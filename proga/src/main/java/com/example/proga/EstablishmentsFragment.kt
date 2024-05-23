package com.example.proga

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.proga.databinding.FragmentEstablishmentsBinding
import com.google.android.material.snackbar.Snackbar
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class EstablishmentsFragment : Fragment() {
    companion object {
        fun newInstance() = EstablishmentsFragment()

        @RequiresApi(Build.VERSION_CODES.R)
        private var requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        )

        private val PLACES = arrayOf(
            Place(
                title = "Вернадка",
                address = "Москва, Проспект Вернадского, д. 78",
                location = GeoPoint(55.669956, 37.480225),
            ),
            Place(
                title = "Стромынка",
                address = "Москва, ул. Стромынка, д.20",
                location = GeoPoint(55.794229, 37.700772),
            ),
            Place(
                title = "Пироговка",
                address = "Москва, улица Малая Пироговская, д. 1, стр. 5",
                location = GeoPoint(55.731582, 37.574840),
            ),
            Place(
                title = "Дом",
                address = "Дом милый дом",
                location = GeoPoint(55.850474, 38.436396),
            ),
            Place(
                title = "Домик",
                address = "Домик в деревне",
                location = GeoPoint(55.850884, 38.388489),
            ),
        )
    }

    data class Place(
        val title: String,
        val address: String,
        val location: GeoPoint,
    )

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all {
            it.value
        }
        permissions.entries.forEach {
            Log.e("LOG_TAG", "${it.key} = ${it.value}")
        }

        if (!granted) {
            Snackbar.make(
                requireView(),
                "Our app needs access to your device's location. " +
                        "Please grant this permission in your device settings.",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Go to settings") {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }.show()
        } else {
            onPermissionsGranted()
        }
    }

    private lateinit var _binding: FragmentEstablishmentsBinding
    private val binding get() = _binding

    private lateinit var mapView: MapView
    private lateinit var locationNewOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEstablishmentsBinding.inflate(inflater, container, false)

        mapView = binding.mapView
        mapView.setZoomRounding(true)
        mapView.setMultiTouchControls(true)

        val mapController = mapView.controller
        mapController.setZoom(15.0)
        val startPoint = GeoPoint(55.794229, 37.700772)
        mapController.setCenter(startPoint)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart() {
        super.onStart()

        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        mapView.onResume()
        requestPermissionsLauncher.launch(requiredPermissions)
    }

    override fun onStop() {
        super.onStop()

        Configuration.getInstance().save(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        mapView.onPause()
    }

    private fun onPermissionsGranted() {
        locationNewOverlay =
            MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView)
        locationNewOverlay.enableMyLocation()
        mapView.overlays.add(this.locationNewOverlay)

        val compassOverlay = CompassOverlay(
            requireContext(), InternalCompassOrientationProvider(
                requireContext()
            ), mapView
        )
        compassOverlay.enableCompass()
        mapView.overlays.add(compassOverlay)

        val dm = requireContext().resources.displayMetrics
        val scaleBarOverlay = ScaleBarOverlay(mapView)
        scaleBarOverlay.setCentred(true)
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10)
        mapView.overlays.add(scaleBarOverlay)

        PLACES.forEach {
            val marker = Marker(mapView)
            marker.position = it.location
            marker.setOnMarkerClickListener { _, _ ->
                Toast.makeText(requireContext(), it.address, Toast.LENGTH_SHORT).show()
                true
            }

            marker.setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                override fun onMarkerDragStart(marker: Marker) {}
                override fun onMarkerDrag(marker: Marker) {}
                override fun onMarkerDragEnd(marker: Marker) {
                    startNavigation(marker.position)
                }
            })

            mapView.overlays.add(marker)
            marker.icon = ResourcesCompat.getDrawable(
                resources,
                org.osmdroid.library.R.drawable.osm_ic_follow_me_on,
                null
            )
            marker.title = it.title
        }
    }

    private fun startNavigation(destination: GeoPoint) {
        val uri = Uri.parse("google.navigation:q=${destination.latitude},${destination.longitude}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Google Maps is not installed", Toast.LENGTH_LONG).show()
        }
    }
}
