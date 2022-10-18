package com.tradesk.appCode.addExpensesModule

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.tradesk.R
import com.tradesk.base.BaseActivity
import com.tradesk.utils.ConstUtils
import com.tradesk.utils.extension.loadWallImage
import com.tradesk.utils.extension.toast
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.google.gson.GsonBuilder
import com.socialgalaxyApp.util.extension.loadLikeImage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.tradesk.appCode.ImageActivity
import com.tradesk.data.entity.*
import com.tradesk.utils.extension.loadUserImage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_add_expense.*
import kotlinx.android.synthetic.main.activity_add_expense.mIvBack
import kotlinx.android.synthetic.main.activity_add_sales_person.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.lang.NumberFormatException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

class AddExpenseActivity : BaseActivity(), IExpensesView {
    var myImageUri = ""
    var mFile: File? = null
    var isViewMode=false;
    @Inject
    lateinit var presenter: ExpensesPresenter<IExpensesView>
lateinit var expenses: Expenses;

    val options by lazy {
        Options.init()
            .setRequestCode(1010) //Request code for activity results
            .setCount(3) //Number of images to restict selection count
            .setFrontfacing(false) //Front Facing camera on start
            .setPreSelectedUrls(arrayListOf()) //Pre selected Image Urls
            .setSpanCount(4) //Span count for gallery min 1 & max 5
            .setMode(Options.Mode.All) //Option to select only pictures or videos or both
            .setVideoDurationLimitinSeconds(30) //Duration for video recording
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT) //Orientaion
            .setPath("/pix/images") //Custom Path For media Storage

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)
        presenter.onAttach(this)
        if (intent.hasExtra("expenses")){
            findViewById<TextView>(R.id.textView6).setText("View Expense")
            isViewMode=true
            val builder = GsonBuilder()
            val gson = builder.create()
            var projectJsonString = intent.getStringExtra("expenses");
            expenses = gson.fromJson(projectJsonString, Expenses::class.java)
            mEtExpenseTitle.setText(expenses.title)
            mEtExpenseTitle.isEnabled =false
            mEtAmount.setText(expenses.amount)
            mEtAmount.isEnabled =false
            mIvProof.loadWallImage(expenses.image)
            mIvProofEdit.visibility=View.GONE
            mBtnAdd.visibility=View.GONE
           mIvProof.setOnClickListener{
               startActivity(Intent(this, ImageActivity::class.java)
                   .putExtra("expense","Expenses")
                   .putExtra("image",expenses.image)
               )
           }

        }
        mEtAmount.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                mEtAmount.removeTextChangedListener(this)

                try {
                    var originalString = s.toString()
                    val longval: Long
                    if (originalString.contains(",")) {
                        originalString = originalString.replace(",".toRegex(), "")
                    }
                    longval = originalString.toLong()
                    val formatter: DecimalFormat =
                        NumberFormat.getInstance(Locale.US) as DecimalFormat
                    formatter.applyPattern("#,###,###,###")
                    val formattedString: String = formatter.format(longval)

                    //setting text after format to EditText
                    mEtAmount.setText(formattedString)
                    mEtAmount.setSelection(mEtAmount.getText()!!.length)
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                }

                mEtAmount.addTextChangedListener(this)
            }
        })

        mIvBack.setOnClickListener { finish() }
        mIvProofEdit.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                showImagePop()
            }
        }
        mBtnAdd.setOnClickListener {
            if (mEtExpenseTitle.text.isEmpty()){
                toast("Please enter expense title")
            }else if (mEtAmount.text.isEmpty()){
                toast("Please enter expense amount")
            } else if (mFile==null){
                toast("Please add expense receipt as proof by taking picture")
            }else{
                if (isInternetConnected())
                    hashMapOf<String, RequestBody>().also {
                        it.put(
                            "job_id",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                intent.getStringExtra("job_id").toString()
                            )
                        )

                        it.put(
                            "title",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mEtExpenseTitle.text.toString()
                            )
                        )

                        it.put(
                            "amount",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mEtAmount.text.toString().replace(",","")
                            )
                        )

                        it.put(
                            "image\"; filename=\"image.jpg",
                            RequestBody.create(MediaType.parse("image/*"), mFile!!)
                        )


                        presenter.addexpense(it)
                    }
            }
        }
    }

    override fun onAdd(it: AddExpenseModel) {
        toast(it.message)
        finish()
    }

    override fun onList(it: ExpensesListModel) {

    }

    override fun onDelete(it: SuccessModel) {

    }

    override fun onerror(data: String) {
        toast(data)
    }

    override fun onGeneratedToken(lastAction: String) {

    }



    val mBuilder: Dialog by lazy { Dialog(this@AddExpenseActivity) }

    fun showImagePop() {
        Pix.start(this@AddExpenseActivity, options)
//        mBuilder.setContentView(R.layout.camera_dialog);
//        mBuilder.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation;
//        mBuilder.window!!.setGravity(Gravity.BOTTOM)
//        mBuilder.window!!.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        mBuilder.findViewById<TextView>(R.id.titleCamera)
//            .setOnClickListener {
//                mBuilder.dismiss()
//                dispatchTakePictureIntent()
//            }
//        mBuilder.findViewById<TextView>(R.id.titleGallery)
//            .setOnClickListener {
//                mBuilder.dismiss()
//                dispatchTakeGalleryIntent()
//            }
//        mBuilder.findViewById<TextView>(R.id.titleCancel)
//            .setOnClickListener { mBuilder.dismiss() }
//        mBuilder.show();
    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(this@AddExpenseActivity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    ConstUtils.createImageFile(this@AddExpenseActivity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this@AddExpenseActivity,
                        "${this@AddExpenseActivity.packageName}.provider",
                        it
                    )
                    myImageUri = photoURI.toString()
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, ConstUtils.REQUEST_TAKE_PHOTO)
                }
            }

        }
    }

    private fun dispatchTakeGalleryIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        if (intent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(intent, ConstUtils.REQUEST_IMAGE_GET)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1010) {
            data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.let {
                if (it.isNotEmpty()) saveCaptureImageResults(it[0])
            }
            Log.e("RESULT","RESULT")
//                saveCaptureImageResults()
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ConstUtils.REQUEST_TAKE_PHOTO) {
                CropImage.activity(Uri.parse(myImageUri))
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setMinCropWindowSize(1600,1600)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(this)
            } else if (requestCode == ConstUtils.REQUEST_IMAGE_GET) {
                val uri: Uri = data?.data!!
                CropImage.activity(uri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setAspectRatio(2, 1)
                    .setMinCropWindowSize(1600,1600)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(this)
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    saveCaptureImageResults(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }
        }
    }

    private fun saveCaptureImageResults(data: Uri) = try {
        val file = File(data.path!!)
        mFile = Compressor(this@AddExpenseActivity)
            .setMaxHeight(1000).setMaxWidth(1000)
            .setQuality(99)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .compressToFile(file)

        if (intent.getStringExtra("type").equals("soldier")) {
            mIvProof.loadWallImage(mFile!!.absolutePath)
        } else {
            mIvProof.loadWallImage(mFile!!.absolutePath)
        }


    } catch (e: Exception) {
    }

    private fun saveCaptureImageResults(path: String) = try {
        val file = File(path!!)
        mFile = Compressor(this@AddExpenseActivity)
            .setMaxHeight(4000).setMaxWidth(4000)
            .setQuality(99)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .compressToFile(file)

        if (intent.getStringExtra("type").equals("soldier")) {
            mIvProof.loadWallImage(mFile!!.absolutePath)
        } else {
            mIvProof.loadWallImage(mFile!!.absolutePath)
        }

    } catch (e: Exception) {
    }
}