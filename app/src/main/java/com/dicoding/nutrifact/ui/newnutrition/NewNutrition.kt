package com.dicoding.nutrifact.ui.newnutrition

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.nutrifact.databinding.ActivityNewNutritionBinding
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

class NewNutrition : AppCompatActivity() {
    private lateinit var binding: ActivityNewNutritionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNutritionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val options = GmsDocumentScannerOptions.Builder()
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_BASE)
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
                    }
                }
            } else {
                Toast.makeText(this, "Scan failed or cancelled", Toast.LENGTH_SHORT).show()
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

    private fun showScannedImage(uri: Uri) {
        binding.imageNutrition.apply {
            setImageURI(uri)
            visibility = android.view.View.VISIBLE
        }
        binding.tvMessage.visibility = android.view.View.GONE
    }
}