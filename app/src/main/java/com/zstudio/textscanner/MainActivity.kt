package com.zstudio.textscanner

i
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.zstudio.textscanner.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageViewCamera.setOnClickListener {
            // Open Camera
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(intent.resolveActivity(packageManager) != null){
                startActivityForResult(intent, 1)
            }
            else{
                Toast.makeText(this, "There is something wrong with camera", Toast.LENGTH_SHORT).show()
            }
        }

        binding.imageViewClear.setOnClickListener {
            binding.result.setText("")
        }

        binding.imageViewCopy.setOnClickListener {
            val text = binding.result.text.toString()
            if(text.isNotEmpty()){
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("text", text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Text Copied", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == RESULT_OK){
            val image = data?.extras?.get("data")
            val bitmap = image as Bitmap
            val matrix = Matrix().apply { postRotate(-90f) }
            val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            detectTextUinsgMLKit(rotatedBitmap)
        }
    }

    private fun detectTextUinsgMLKit(bitmap: Bitmap) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val image = InputImage.fromBitmap(bitmap, 0)

        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                // ...
                binding.result.setText(visionText.text)
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }
    }
}
