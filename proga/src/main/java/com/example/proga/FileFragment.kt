package com.example.proga

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.proga.databinding.FragmentFileBinding
import java.io.ByteArrayOutputStream
import java.io.IOException

class FileFragment : Fragment() {

    private var _binding: FragmentFileBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {
            showPermissionDeniedMessage()
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    convertImage(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFileBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnConvert.setOnClickListener {
            Log.d("FileFragment", "Нажата кнопка Convert")
            requestReadStoragePermission()
        }

        Log.d("FileFragment", "pickImageLauncher зарегистрирован")

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestReadStoragePermission() {
        Log.d("FileFragment", "Запрос разрешения на чтение внешнего хранилища")
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openGallery()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }


    private fun openGallery() {
        Log.d("FileFragment", "Открытие галереи")
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun convertImage(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val pngByteArray = outputStream.toByteArray()

        val convertedBitmap = BitmapFactory.decodeByteArray(pngByteArray, 0, pngByteArray.size)
        binding.imageResult.setImageBitmap(convertedBitmap)
    }

    private fun showPermissionDeniedMessage() {
        // Можете настроить это сообщение в соответствии с дизайном вашего приложения
        Toast.makeText(requireContext(), "Разрешение на доступ к галерее не предоставлено", Toast.LENGTH_SHORT).show()
    }
}

