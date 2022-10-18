package com.tradesk.appCode.profileModule.proposalsModule.addProposalModule

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.gcacace.signaturepad.views.SignaturePad
import com.tradesk.R
import com.tradesk.utils.FilePath
import kotlinx.android.synthetic.main.activity_add_proposal.*
import kotlinx.android.synthetic.main.activity_signature.*
import kotlinx.android.synthetic.main.activity_signature.mIvBack
import okhttp3.MediaType
import java.io.*


class SignatureActivity : AppCompatActivity() {


    lateinit var  signature_pad:SignaturePad
    lateinit var  mClearButton:Button
    lateinit var  mSaveButton:Button

    var selectedimage_client=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature)
        verifyStoragePermissions(this)
        signature_pad  =findViewById(R.id.signature_pad) as SignaturePad

        mClearButton = findViewById<View>(R.id.clear_button) as Button
        mSaveButton = findViewById<View>(R.id.save_button) as Button

        signature_pad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                //Event triggered when the pad is touched
            }

            override fun onSigned() {
                //Event triggered when the pad is signed
                mSaveButton.setEnabled(true)
                mClearButton.setEnabled(true)
            }

            override fun onClear() {
                //Event triggered when the pad is cleared

                mSaveButton.setEnabled(false)
                mClearButton.setEnabled(false)
            }
        })

        mIvBack.setOnClickListener { finish() }

        mClearButton.setOnClickListener { signature_pad.clear() }

        mSaveButton.setOnClickListener {
            val signatureBitmap: Bitmap = signature_pad.getSignatureBitmap()


            if (signature_pad.getSignatureBitmap() != null) {
                selectedimage_client = FilePath.getPath(this@SignatureActivity, saveBitmap(this, signature_pad.getSignatureBitmap(),System.currentTimeMillis().toString(),"Signatures")!!).toString()

                val MEDIA_TYPE =
                    if (selectedimage_client.endsWith("png")) MediaType.parse("image/png") else MediaType.parse(
                        "image/jpeg"
                    )

//                val MEDIA_TYPE =
//                    if (selectedimage_client.endsWith("png")) MediaType.parse("image/png") else MediaType.parse(
//                        "image/jpeg"
//                    )
//
//                val file = File(selectedimage_client)
//                formBuilder.addFormDataPart(
//                    Parameters.CLIENT_SIGN,
//                    file.name,
//                    RequestBody.create(MEDIA_TYPE, file)
//                )
                AddProposalActivity.mFileSignature = File(selectedimage_client)

                val returnIntent = Intent()
                returnIntent.putExtra("result", selectedimage_client)
                returnIntent.putExtra("mySignatureUri", selectedimage_client)
                setResult(RESULT_OK, returnIntent)
                finish()
            }
//            if (addJpgSignatureToGallery(signatureBitmap)) {
//                Toast.makeText(
//                    this@SignatureActivity,
//                    "Signature saved into the Gallery",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                Toast.makeText(
//                    this@SignatureActivity,
//                    "Unable to store the signature",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            if (addSvgSignatureToGallery(signature_pad.getSignatureSvg())) {
//                Toast.makeText(
//                    this@SignatureActivity,
//                    "SVG Signature saved into the Gallery",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                Toast.makeText(
//                    this@SignatureActivity,
//                    "Unable to store the SVG signature",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size <= 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        this@SignatureActivity,
                        "Cannot write images to external storage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun getAlbumStorageDir(albumName: String?): File {
        // Get the directory for the user's public pictures directory.
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created")
        }
        return file
    }

    @Throws(IOException::class)
    fun saveBitmapToJPG(bitmap: Bitmap, photo: File?) {
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val stream: OutputStream = FileOutputStream(photo)
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        stream.close()
    }

    fun addJpgSignatureToGallery(signature: Bitmap): Boolean {
        var result = false
        try {
            val photo = File(
                getAlbumStorageDir("SignaturePad"),
                String.format("Signature_%d.jpg", System.currentTimeMillis())
            )
            saveBitmapToJPG(signature, photo)
            scanMediaFile(photo)
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun scanMediaFile(photo: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(photo)
        mediaScanIntent.data = contentUri
        this@SignatureActivity.sendBroadcast(mediaScanIntent)
    }

    fun addSvgSignatureToGallery(signatureSvg: String?): Boolean {
        var result = false
        try {
            val svgFile = File(
                getAlbumStorageDir("SignaturePad"),
                String.format("Signature_%d.svg", System.currentTimeMillis())
            )
            val stream: OutputStream = FileOutputStream(svgFile)
            val writer = OutputStreamWriter(stream)
            writer.write(signatureSvg)
            writer.close()
            stream.flush()
            stream.close()
            scanMediaFile(svgFile)
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        /**
         * Checks if the app has permission to write to device storage
         *
         *
         * If the app does not has permission then the user will be prompted to grant permissions
         *
         * @param activity the activity from which permissions are checked
         */
        fun verifyStoragePermissions(activity: Activity?) {
            // Check if we have write permission
            val permission = ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        }
    }


    fun getAbsolutePath(activity: Context, uri: Uri): String? {
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf("_data")
            var cursor: Cursor? = null
            try {
                cursor = activity.contentResolver.query(uri, projection, null, null, null)
                val column_index = cursor!!.getColumnIndexOrThrow("_data")
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: java.lang.Exception) {
                // Eat it
                e.printStackTrace()
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
    @NonNull
    @Throws(IOException::class)
    private fun saveBitmap(
        @NonNull context: Context, @NonNull bitmap: Bitmap,
        @NonNull displayName: String, @Nullable subFolder: String
    ): Uri? {
        var relativeLocation = Environment.DIRECTORY_PICTURES
        if (!TextUtils.isEmpty(subFolder)) {
            relativeLocation += File.separator.toString() + subFolder
        }
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
        val resolver = context.contentResolver
        var stream: OutputStream? = null
        var uri: Uri? = null
        return try {
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            uri = resolver.insert(contentUri, contentValues)
            if (uri == null) {
                throw IOException("Failed to create new MediaStore record.")
            }
            stream = resolver.openOutputStream(uri)
            if (stream == null) {
                throw IOException("Failed to get output stream.")
            }
            if (bitmap.compress(CompressFormat.JPEG, 95, stream) == false) {
                throw IOException("Failed to save bitmap.")
            }
            uri
        } catch (e: IOException) {
            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null)
            }
            throw e
        } finally {
            stream?.close()
        }
    }
}