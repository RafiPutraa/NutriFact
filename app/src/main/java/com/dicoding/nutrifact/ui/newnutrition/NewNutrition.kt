package com.dicoding.nutrifact.ui.newnutrition

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.data.local.HistoryRepository
import com.dicoding.nutrifact.data.local.entity.HistoryEntity
import com.dicoding.nutrifact.data.local.room.HistoryDatabase
import com.dicoding.nutrifact.databinding.ActivityNewNutritionBinding
import com.dicoding.nutrifact.ui.MainActivity
import com.dicoding.nutrifact.ui.result.NotFoundActivity
import com.dicoding.nutrifact.ui.result.ResultActivity
import com.dicoding.nutrifact.util.reduceFileImage
import com.dicoding.nutrifact.util.uriToFile
import com.dicoding.nutrifact.viewmodel.ScanViewModel
import com.dicoding.nutrifact.viewmodel.ViewModelFactory
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody

class NewNutrition : AppCompatActivity() {
    private lateinit var binding: ActivityNewNutritionBinding
    private val viewModel: ScanViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private var loadingDialog: SweetAlertDialog? = null
    private lateinit var historyRepository: HistoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNutritionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAnalyze.setOnClickListener {
            validateInputs()
        }

        val historyDatabase = HistoryDatabase.getInstance(this)
        historyRepository = HistoryRepository.getInstance(historyDatabase)

        val options = GmsDocumentScannerOptions.Builder()
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_BASE_WITH_FILTER)
            .setPageLimit(1)
            .setGalleryImportAllowed(true)
            .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
            .build()

        val scanner = GmsDocumentScanning.getClient(options)

        val scannerLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val scanningResult =
                    GmsDocumentScanningResult.fromActivityResultIntent(result.data)

                scanningResult?.pages?.let { pages ->
                    if (pages.isNotEmpty()) {
                        val imageUri = pages[0].imageUri
                        showScannedImage(imageUri)
                        viewModel.setCurrentImageUri(imageUri)
                    }
                }
            }
        }

        binding.btnTakePhoto.setOnClickListener {
            scanner.getStartScanIntent(this)
                .addOnSuccessListener { intentSender ->
                    scannerLauncher.launch(
                        IntentSenderRequest.Builder(intentSender).build()
                    )
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                }
        }

        val barcodeValue = intent.getStringExtra("BARCODE_VALUE")
        binding.tvBarcode.text = "Barcode: $barcodeValue"
    }

    private fun validateInputs() {
        var isMerkValid = binding.etProduct.validate()
        var isVarianValid = binding.etVariant.validate()
        val uri = viewModel.currentImageUri.value

        if (isMerkValid && isVarianValid && uri != null) {
            submitProduct()
        } else {
            Toast.makeText(this, "Image must be provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showScannedImage(uri: Uri) {
        binding.imageNutrition.apply {
            setImageURI(uri)
            visibility = android.view.View.VISIBLE
        }
        binding.tvMessage.visibility = android.view.View.GONE
    }

    private fun submitProduct() {
        val barcodeValue = intent.getStringExtra("BARCODE_VALUE") ?: return
        val merk = binding.etProduct.text.toString()
        val varian = binding.etVariant.text.toString()
        val uri = viewModel.currentImageUri.value ?: return

        val file = uriToFile(uri,this@NewNutrition).reduceFileImage()
        val barcodeRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), barcodeValue)
        val merkRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), merk)
        val varianRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), varian)
        val filePart = MultipartBody.Part.createFormData("imageNutri", file.name, file.asRequestBody("image/jpeg".toMediaType()))

        viewModel.postNewProduct(barcodeRequest, merkRequest, varianRequest, filePart).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Success!")
                        .setContentText("Thank you for adding a new product. You've earned +5 Points!")
                        .setConfirmText("OK")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                            val barcode = intent.getStringExtra("BARCODE_VALUE") ?: ""
                            viewModel.getProductByBarcode(barcode)
                            viewModel.productResponse.observe(
                                this,
                                Observer { result ->
                                    when (result) {
                                        is ResultState.Loading -> {
                                            showLoading(true)
                                        }

                                        is ResultState.Success -> {
                                            showLoading(false)
                                            val product = result.data.data
                                            val historyEntity = HistoryEntity(
                                                merk = result.data.data?.merk,
                                                varian = result.data.data?.varian,
                                                sugar = result.data.data?.sugar,
                                                fat = result.data.data?.fat,
                                                healthGrade = result.data.data?.healthGrade
                                            )
                                            lifecycleScope.launch {
                                                historyRepository.insertHistory(
                                                    historyEntity
                                                )
                                            }
                                            if (product != null) {
                                                val intent = Intent(
                                                    this,
                                                    ResultActivity::class.java
                                                )
                                                intent.putExtra("PRODUCT_DATA", product)
                                                startActivity(intent)
                                                finishAffinity()
                                            }
                                        }

                                        is ResultState.Error -> {
                                            showLoading(false)
                                            Log.e("NewNutrition", "Error: ${result.error}")
                                            startActivity(Intent(
                                                this,
                                                MainActivity::class.java
                                            ))
                                            finishAffinity()
                                        }
                                    }
                                })
                        }
                        .show()
                }

                is ResultState.Error -> {
                    showLoading(false)
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Something Went Wrong")
                        .setContentText(result.error)
                        .setConfirmText("OK")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                        }
                        .show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            if (loadingDialog == null || !loadingDialog!!.isShowing) {
                loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).apply {
                    titleText = "Loading"
                    setCancelable(false)
                    show()
                }
            }
        } else {
            loadingDialog?.dismissWithAnimation()
            loadingDialog = null
        }
    }
}