    package com.example.proga

    import android.Manifest
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.graphics.Bitmap
    import android.os.Bundle
    import android.provider.MediaStore
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import androidx.fragment.app.Fragment

    class CameraFragment : Fragment() {

        private lateinit var imageView1: ImageView
        private lateinit var imageView2: ImageView
        private lateinit var imageViewWide: ImageView
        private var selectedView: ImageView? = null
        private var isWork = false

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_camera, container, false)
            imageView1 = view.findViewById(R.id.imageView1)
            imageView2 = view.findViewById(R.id.imageView2)
            imageViewWide = view.findViewById(R.id.imageViewWide)

            val cameraPermissionStatus = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            )
            val storagePermissionStatus = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (cameraPermissionStatus == PackageManager.PERMISSION_GRANTED &&
                storagePermissionStatus == PackageManager.PERMISSION_GRANTED
            ) {
                isWork = true
            } else {
                val REQUEST_CODE_PERMISSION = 200
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), REQUEST_CODE_PERMISSION
                )
            }

            imageView1.setOnClickListener {
                selectedView = imageView1
                dispatchTakePictureIntent()
            }

            imageView2.setOnClickListener {
                selectedView = imageView2
                dispatchTakePictureIntent()
            }

            imageViewWide.setOnClickListener {
                selectedView = imageViewWide
                dispatchTakePictureIntent()
            }

            return view
        }

        private val takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == android.app.Activity.RESULT_OK) {
                    val data = result.data
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    selectedView?.setImageBitmap(imageBitmap)
                }
            }

        private fun dispatchTakePictureIntent() {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureLauncher.launch(takePictureIntent)
            }
        }
    }

