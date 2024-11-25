package com.dicoding.nutrifact.ui.scan

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import cn.pedant.SweetAlert.SweetAlertDialog
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.databinding.FragmentScanBinding
import com.dicoding.nutrifact.ui.ViewModelFactory
import com.dicoding.nutrifact.ui.result.ResultActivity
import com.dicoding.nutrifact.ui.result.notfound.NotFoundActivity
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.TimeUnit

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private val scanViewModel: ScanViewModel by viewModels{
        ViewModelFactory.getInstance()
    }
    private var loadingDialog: SweetAlertDialog? = null

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
                    val options = BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                            Barcode.FORMAT_CODE_128,
                            Barcode.FORMAT_EAN_13,
                            Barcode.FORMAT_EAN_8
                        )
                        .build()

                    val barcodeScanner = BarcodeScanning.getClient(options)
                    barcodeScanner.process(inputImage)
                        .addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                Log.d(TAG, "Detected barcode: ${barcode.rawValue}")
                                barcode.rawValue?.let {
                                    scanViewModel.getProductByBarcode(it)

                                    scanViewModel.productResponse.observe(viewLifecycleOwner, Observer { result ->
                                        when (result) {
                                            is ResultState.Loading -> {
                                                showLoading(true)
                                            }
                                            is ResultState.Success -> {
                                                showLoading(false)
                                                val product = result.data.data
                                                if (product != null) {
                                                    val intent = Intent(requireContext(), ResultActivity::class.java)
                                                    intent.putExtra("PRODUCT_DATA", product)
                                                    startActivity(intent)
                                                }
                                            }
                                            is ResultState.Error -> {
                                                showLoading(false)
                                                Log.e(TAG, "Error: ${result.error}")
                                                val intent = Intent(requireContext(),NotFoundActivity::class.java)
                                                intent.putExtra("BARCODE_VALUE", barcode.rawValue)
                                                startActivity(intent)
                                            }
                                        }
                                    })
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error detecting barcode: ${e.localizedMessage}", e)
                            Toast.makeText(requireContext(), "Failed to detect barcode from image", Toast.LENGTH_SHORT).show()
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


    @SuppressLint("ClickableViewAccessibility")
    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_CODE_128,
                    Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_EAN_8
                )
                .build()

            val barcodeScanner = BarcodeScanning.getClient(options)

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
                                        scanViewModel.getProductByBarcode(it)

                                        scanViewModel.productResponse.observe(viewLifecycleOwner, Observer { result ->
                                            when (result) {
                                                is ResultState.Loading -> {
                                                    showLoading(true)
                                                }
                                                is ResultState.Success -> {
                                                    showLoading(false)
                                                    val product = result.data.data
                                                    if (product != null) {
                                                        val intent = Intent(requireContext(), ResultActivity::class.java)
                                                        intent.putExtra("PRODUCT_DATA", product)
                                                        startActivity(intent)
                                                    }
                                                }
                                                is ResultState.Error -> {
                                                    showLoading(false)
                                                    Log.e(TAG, "Error: ${result.error}")
                                                    val intent = Intent(requireContext(),NotFoundActivity::class.java)
                                                    intent.putExtra("BARCODE_VALUE", barcode.rawValue)
                                                    startActivity(intent)
                                                }
                                            }
                                        })
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
                val camera = cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
                binding.cameraPreview.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        val factory = binding.cameraPreview.meteringPointFactory
                        val point = factory.createPoint(event.x, event.y)

                        val action = FocusMeteringAction.Builder(
                            point,
                            FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE
                        )
                            .setAutoCancelDuration(3, TimeUnit.SECONDS)
                            .build()

                        camera.cameraControl.startFocusAndMetering(action)
                        Log.d(TAG, "Focus started at: x=${event.x}, y=${event.y}")
                    }
                    true
                }
            } catch (exc: Exception) {
                Log.e(TAG, "Camera binding error: ${exc.localizedMessage}", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            if (loadingDialog == null) {
                loadingDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE).apply {
                    titleText = "Loading"
                    setCancelable(false)
                    show()
                }
            } else {
                loadingDialog?.show()
            }
        } else {
            loadingDialog?.dismissWithAnimation()
        }
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
