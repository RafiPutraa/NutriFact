package com.dicoding.nutrifact.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.dicoding.nutrifact.databinding.FragmentScanBinding
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private val scanViewModel: ScanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }

        binding.btnImage.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener {
            binding.previewImageView.visibility = View.GONE
            binding.previewImageView.setImageDrawable(null)
            binding.cameraPreview.visibility = View.VISIBLE
            binding.btnCamera.visibility = View.GONE
        }

        scanViewModel.currentImageUri.observe(viewLifecycleOwner, Observer { uri ->
            if (uri != null) {
                binding.previewImageView.visibility = View.VISIBLE
                binding.previewImageView.setImageURI(uri)
                binding.cameraPreview.visibility = View.GONE
                binding.btnCamera.visibility = View.VISIBLE

                try {
                    val inputImage = InputImage.fromFilePath(requireContext(), uri)

                    val barcodeScanner = BarcodeScanning.getClient()
                    barcodeScanner.process(inputImage)
                        .addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                Log.d(TAG, "Detected barcode from image: ${barcode.rawValue}")
                                binding.barcodeResult.text = barcode.rawValue ?: "No barcode found"
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error detecting barcode: ${e.localizedMessage}", e)
                        }
                } catch (e: Exception) {
                    Log.e(TAG,"Failed to load image for barcode scanning: ${e.localizedMessage}", e)
                }
            } else {
                binding.previewImageView.visibility = View.GONE
                binding.previewImageView.setImageDrawable(null)
                binding.cameraPreview.visibility = View.VISIBLE
                binding.btnCamera.visibility = View.GONE
            }
        })

        return binding.root
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scanViewModel.setCurrentImageUri(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            val barcodeScanner = BarcodeScanning.getClient()

            val imageAnalyzer = ImageAnalysis.Builder().build().also { imageAnalysis ->
                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->
                    val inputImage = imageProxy.image?.let { image ->
                        InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
                    }

                    if (inputImage != null) {
                        barcodeScanner.process(inputImage)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    Log.d(TAG, "Detected barcode: ${barcode.rawValue}")
                                    barcode.rawValue?.let {
                                        binding.barcodeResult.text = it
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Barcode processing error: ${e.localizedMessage}", e)
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        Log.e(TAG, "Input image is null")
                        imageProxy.close()
                    }
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Camera binding error: ${exc.localizedMessage}", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Log.e(TAG, "Camera permission denied")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
        private const val TAG = "ScanFragment"
    }
}
