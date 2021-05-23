package com.example.imagelabel

//import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import java.io.*


class MainActivity : AppCompatActivity() {
    private val SELECT_PICTURE: Int = 200;
    private lateinit var uri: Uri
    private lateinit var image: ImageView
    private lateinit var textView: TextView
    private  val  localModel = LocalModel.Builder()
    .setAssetFilePath("mobilenet_v1_1.0_224_quant.tflite")
    // or .setAbsoluteFilePath(absolute file path to model file)
    // or .setUri(URI to model file)
    .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image = findViewById(R.id.imageView)
        textView = findViewById(R.id.textView)

    }





    //ImageView, Detect Button, Select Image Button, TextView with text.
    //Choose Image from phone
    fun selectImage(view: View) {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT

        // pass the constant to compare it
        // with the returned requestCode

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)


    }

    //Analyze Image
    fun analyzeImage(view: View) {
        var image: InputImage? = null
        try {
            image = InputImage.fromFilePath(this, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // To use default options:
        val customImageLabelerOptions = CustomImageLabelerOptions.Builder(localModel)
            .setConfidenceThreshold(0.5f)
            .setMaxResultCount(5)
            .build()
        val labeler = ImageLabeling.getClient(customImageLabelerOptions)

        if (image != null)
            labeler.process(image)
                .addOnSuccessListener { labels ->
                    // Task completed successfully
                    // ...
                    //Put text in TextView
                    //textView.text = labels.get(0).index// represents the text of the images in the picture


//Create a function that recive a integer and returns a string
//The function should read the TXT file and return the text in the line of the Integer that was reciverd
//Good Luck
                    var indexFound :Int = 0
                    for (label in labels) {
                        val text = label.text
                        val confidence = label.confidence
                        val index = label.index
                        indexFound = index
                        Log.d("boris", text + ":" + confidence + "-" + index)
                    }
                    textView.text = getNameFromFile(indexFound)
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                }
    }

fun getNameFromFile(index: Int) :String
{
    Log.d("StackOverflow","The index is " + index);
    var counter = 0
    val reader: BufferedReader

        reader =  application.assets.open("labels_mobilenet_quant_v1_224.txt").bufferedReader()
        var line: String = reader.readLine()
        while (line != null) {
            if(index == counter)
                return line
            line = reader.readLine()
            Log.d("StackOverflow", line + " " + counter)
            counter++
        }
    return "not found"
   // }
}


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode === SELECT_PICTURE) {
                // Get the url of the image from data
                val selectedImageUri: Uri? = data?.data
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    image.setImageURI(selectedImageUri)
                    uri=selectedImageUri
                }
            }
        }

    }
}