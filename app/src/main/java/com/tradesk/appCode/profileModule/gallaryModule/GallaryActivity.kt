package com.tradesk.appCode.profileModule.gallaryModule

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.appCode.ImageActivity
import com.tradesk.appCode.PermitsImageActivity
import com.tradesk.appCode.home.leadsDetail.LeadDetailPresenter
import com.tradesk.appCode.home.leadsDetail.leadsNotesModule.ILeadDetailView
import com.tradesk.appCode.profileModule.AdditionalGallaryAdapter
import com.tradesk.appCode.profileModule.gallaryModule.subGallaryModule.SubGallaryActivity
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.filemanager.FileExplorerActivity
import com.tradesk.filemanager.openApp
import com.tradesk.filemanager.openAppPermits
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.ConstUtils
import com.tradesk.utils.extension.addWatcher
import com.tradesk.utils.extension.toast
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.google.gson.JsonObject
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_gallary.*
import kotlinx.android.synthetic.main.activity_gallary.mIvBack
import kotlinx.android.synthetic.main.activity_gallary.textView6
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

class GallaryActivity : BaseActivity(), SingleListCLickListener, ILeadDetailView {

    var selected_position=0
    var selected_doc=0
    var myImageUri = ""
    var mFile: File? = null
    val mList = mutableListOf<LeadsDataGallary>()
    val mGallaryAdapter by lazy { GallaryAdapter(this,this) }
    val mListAdditional = mutableListOf<AdditionalImageLeadDetail>()
    val mListAdditionalImage = ArrayList<AdditionalImageLeadDetail>()



    val mListDocuments = ArrayList<String>()


    val mGallaryAdapterUpdated by lazy { GallaryAdapterUpdated(this,this,mListAdditional) }
    /*For gallary adapter*/
    val mListUpdatedAdditionalImage = ArrayList<LeadsDataImageClient>()
    val mAdditionalGallaryAdapter by lazy { AdditionalGallaryAdapter(this,this,mListUpdatedAdditionalImage) }

    val mDocumentsGallaryAdapter by lazy { DocumentsGallaryAdapter(this,this,mListDocuments) }

    val mPermits = ArrayList<AdditionalImageLeadDetail>()
    val mPermitsAdapter by lazy { PermitsAdapter(this,this,mPermits) }

    val mPermitsModel = ArrayList<LeadsDataImageClient>()
    val mPermitsAdapterGallary by lazy { PermitsAdapterGallary(this,this,mPermitsModel) }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass.name.startsWith(
                "android.webkit."
            )
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x = ev.rawX + view.left - scrcoords[0]
            val y = ev.rawY + view.top - scrcoords[1]
            if (x < view.left || x > view.right || y < view.top || y > view.bottom)
            //  mAppUtils.hideSoftKeyboard(window.decorView.rootView)
                hideKeyboard(window.decorView.rootView)
        }
        return super.dispatchTouchEvent(ev)
    }

    @Inject
    lateinit var presenter: LeadDetailPresenter<ILeadDetailView>


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
        setContentView(R.layout.activity_gallary)
        presenter.onAttach(this)
        callApi()

        mIvBack.setOnClickListener { finish() }
        mIvAddImage.setOnClickListener {  if (permissionFile.checkLocStorgePermission(this)) {
            showImagePop()
        } }
    }

    private fun callApi() {
        if (intent.hasExtra("additionalimages")){
            mIvAddImage.visibility=View.VISIBLE
            textView6.setText("Images")
            GridLayoutManager(this,   2,  RecyclerView.VERTICAL,   false).apply {
                rvGallary.layoutManager = this
            }
            rvGallary.adapter = mGallaryAdapterUpdated
            if (isInternetConnected())
                presenter.getLeadDetail(intent.getStringExtra("id").toString())
        }else if (intent.hasExtra("other")){
            textView6.setText("Other Documents")
            GridLayoutManager(this,   2,  RecyclerView.VERTICAL,   false).apply {
                rvGallary.layoutManager = this
            }
            rvGallary.adapter = mDocumentsGallaryAdapter

            if (isInternetConnected()){
                presenter.getProfile()
            }

        }else if (intent.hasExtra("permits")){
            textView6.setText("Permits")
            GridLayoutManager(this,   2,  RecyclerView.VERTICAL,   false).apply {
                rvGallary.layoutManager = this
            }
            rvGallary.adapter = mPermitsAdapter
            mIvAddImage.visibility=View.VISIBLE

            if (isInternetConnected()){
                presenter.getLeadDetail(intent.getStringExtra("id").toString())
            }

        }else if (intent.hasExtra("permit")){
            textView6.setText("Permits")
            GridLayoutManager(this,   2,  RecyclerView.VERTICAL,   false).apply {
                rvGallary.layoutManager = this
            }
            rvGallary.adapter = mPermitsAdapterGallary

            if (isInternetConnected()){
                presenter.getadditionalimagesjobs("1","30","permit")
            }

        }
        else{
            textView6.setText("Gallary")
            GridLayoutManager(this,   2,  RecyclerView.VERTICAL,   false).apply {
                rvGallary.layoutManager = this
            }
            rvGallary.adapter= mAdditionalGallaryAdapter

            mEtSearchName.visibility=View.VISIBLE
            mEtSearchName.addWatcher {
                mAdditionalGallaryAdapter.filter.filter(it)}

            if (isInternetConnected()){
                presenter.getadditionalimagesjobs("1","30","image")
            }
        }
    }

    override fun onSingleListClick(item: Any, position: Int) {
        if (item.equals("Click")){
            if (intent.hasExtra("additionalimages")) {
                startActivity(
                    Intent(this, ImageActivity::class.java)
                        .putExtra("image", mListAdditional.get(position).image)
                        .putParcelableArrayListExtra(
                            "imagelist",
                            mListAdditionalImage as ArrayList<AdditionalImageLeadDetail>
                        )
                )
            } else if (intent.hasExtra("other")){

                openApp(this@GallaryActivity,mListDocuments[position])
//                startActivity(
//                    Intent(this, ImageActivity::class.java)
//                        .putExtra("image", mListDocuments.get(position))
//                        .putExtra("title", "other")
//                        .putStringArrayListExtra(
//                            "imagelist",
//                            mListDocuments as ArrayList<String>
//                        )
//                )
            }

        }else if (item.equals("Multi")) {
            startActivity(
                Intent(this, SubGallaryActivity::class.java)
                    .putExtra("title", "Job Images")
                    .putExtra("job_id", mListUpdatedAdditionalImage.get(position)._id)
                    .putExtra("title", (mListUpdatedAdditionalImage.get(position).client_details.name +" " +mListUpdatedAdditionalImage.get(position).project_name))
                    .putParcelableArrayListExtra(
                        "imagelist", mListUpdatedAdditionalImage[position].additional_images.filter { it.status.equals("image") } as ArrayList<AdditionalImageImageClient>
                    )
            )
        }else if (item.equals("PermitAdapter")) {
            startActivityForResult(
                Intent(this, PermitsImageActivity::class.java)
                    .putExtra("title", "Permits")
                    .putExtra("id", mPermitsModel[position]._id.toString())
                    .putParcelableArrayListExtra(
                        "imagelist", mPermitsModel[position].additional_images as ArrayList<AdditionalImageImageClient>
                    )
            ,10201)
        }else if (item.equals("Permit")) {

            openAppPermits(this@GallaryActivity,mPermits[position].image,mPermits.get(position)._id)


//            startActivity(
//                Intent(this, ImageActivity::class.java)
//                    .putExtra("title", "Permits")
//                    .putExtra("job_id", mPermits.get(position)._id)
//                    .putParcelableArrayListExtra(
//                        "imagelist", mPermits[position] as ArrayList<AdditionalImageLeadDetail>
//                    )
//            )
        }else {
            showMenu(item as View, position)
        }
    }

    fun showMenu(anchor: View, position: Int ): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.getMenuInflater().inflate(R.menu.gallary_menu, popup.getMenu())
        popup.setOnMenuItemClickListener{
            if (textView6.text.equals("Documents")){
                if (isInternetConnected()) {
                    selected_doc = position
                    presenter.deleteprofiledocs(
                        mListDocuments.get(position)
                    )
                }
            }else if (textView6.text.equals("Other Documents")){
                if (isInternetConnected()) {
                    selected_doc = position
                    presenter.deleteprofiledocs(
                        mListDocuments.get(position)
                    )
                }
            }else {
                if (isInternetConnected()) {
                    selected_position = position
                    with(JsonObject()) {
                        addProperty("job_id", intent.getStringExtra("id").toString())
                        addProperty("image_id",mListAdditional[position]._id)

                        presenter.deleteimages(this)
                    }


                }
            }
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

    override fun onLeadDetail(it: LeadDetailModel) {
        if (intent.hasExtra("additionalimages")){
            if (it.data.leadsData.additional_images!=null) {
                mListAdditional.clear()
                mListAdditionalImage.clear()
                mListAdditionalImage.addAll(it.data.leadsData.additional_images.filter { it.status!!.equals("image") })
                mListAdditional.addAll(it.data.leadsData.additional_images.filter { it.status!!.equals("image") })
                mGallaryAdapterUpdated.notifyDataSetChanged()
            }
        }else if (intent.hasExtra("permits")){
            mPermits.clear()
            mPermits.addAll(it.data.leadsData.additional_images!!.filter { it.status!!.equals("permit") })
            mPermitsAdapter.notifyDataSetChanged()
        }
    }

    override fun onDeleteImage(it: SuccessModel) {
        toast("Image deleted successfully")
        mListAdditionalImage.removeAt(selected_position)
        mGallaryAdapterUpdated.notifyDataSetChanged()

    }

    override fun onStatus(it: SuccessModel) {

    }

    override fun onLeadDeleteStatus(it: SuccessModel) {

    }

    override fun onDocDeleteStatus(it: SuccessModel) {
        toast("Deleted Successfully")
        mListDocuments.removeAt(selected_doc)
        mDocumentsGallaryAdapter.notifyDataSetChanged()

    }

    override fun onAddImage(it: SuccessModel) {
        toast(it.message)
        if (isInternetConnected()) {
            presenter.getLeadDetail(intent.getStringExtra("id").toString())
        }
    }

    override fun onAddReminder(it: SuccessModel) {

    }

    override fun onProfileData(it: ProfileModel) {
        mListDocuments.clear()
        mListDocuments.addAll(it.data.license_and_ins.docs_url)
        mDocumentsGallaryAdapter.notifyDataSetChanged()
    }

    override fun onAdditionalImagesData(it: AdditionalImagesWithClientModel) {
        if (intent.hasExtra("permit")){
            mPermitsModel.clear()
            mPermitsModel.addAll(it.data.leadsData)
            mPermitsAdapterGallary.notifyDataSetChanged()
        }else{
            mListUpdatedAdditionalImage.clear()
            mListUpdatedAdditionalImage.addAll(it.data.leadsData)
            mAdditionalGallaryAdapter.notifyDataSetChanged()
        }

    }

    override fun onerror(data: String) {
        toast(data)
    }

    override fun onGeneratedToken(lastAction: String) {

    }


    val mBuilder: Dialog by lazy { Dialog(this@GallaryActivity) }

    fun showImagePop() {
        mBuilder.setContentView(R.layout.camera_dialog);
        mBuilder.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation;
        mBuilder.window!!.setGravity(Gravity.BOTTOM)
        mBuilder.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mBuilder.findViewById<TextView>(R.id.titleCamera).also {
            if (intent.hasExtra("permits")) {
                it.isVisible = false
            } else {
                it.isVisible = true
            }
            it.setOnClickListener {
                mBuilder.dismiss()
                dispatchTakePictureIntent()
            }
        }
        mBuilder.findViewById<TextView>(R.id.titleCameraGallary).also {
            if (intent.hasExtra("permits")){it.isVisible=true} else {it.isVisible=false}
            it.setOnClickListener {
                mBuilder.dismiss()
                Pix.start(this@GallaryActivity, options)
            }
        }
        if (intent.hasExtra("permits")){mBuilder.findViewById<View>(R.id.view11).isVisible=true}else {mBuilder.findViewById<View>(R.id.view11).isVisible=false}

        mBuilder.findViewById<TextView>(R.id.titleDoc).also {
            if (intent.hasExtra("permits")){it.isVisible=true} else {it.isVisible=false}
            it.setOnClickListener {
                mBuilder.dismiss()
                browseDocuments()
            }
        }
        if (intent.hasExtra("permits")){mBuilder.findViewById<View>(R.id.view11).isVisible=true}else {mBuilder.findViewById<View>(R.id.view11).isVisible=false}

        mBuilder.findViewById<TextView>(R.id.titleGallery).also {if (intent.hasExtra("permits")) {
            it.isVisible = false } else { it.isVisible = true }
            it.setOnClickListener {
                mBuilder.dismiss()
                dispatchTakeGalleryIntent()
            }
        }
        mBuilder.findViewById<TextView>(R.id.titleCancel)
            .setOnClickListener { mBuilder.dismiss() }
        mBuilder.show();
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(this@GallaryActivity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    ConstUtils.createImageFile(this@GallaryActivity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this@GallaryActivity,
                        "${this@GallaryActivity.packageName}.provider",
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

    private fun browseDocuments() {
        startActivityForResult(Intent(this, FileExplorerActivity::class.java),
            ConstUtils.REQUEST_CODE_DOCS
        )
    }


    private fun saveCaptureImageResults(path: String) = try {
        val file = File(path!!)
        mFile = Compressor(this@GallaryActivity)
            .setMaxHeight(4000).setMaxWidth(4000)
            .setQuality(99)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .compressToFile(file)

        hashMapOf<String, RequestBody>().also {
            it.put(
                "image\"; filename=\"image.jpg",
                RequestBody.create(MediaType.parse("image/*"), mFile!!)
            )
            it.put("_id", RequestBody.create(MediaType.parse("multipart/form-data"), intent.getStringExtra("id").toString()))
            it.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "permit"))

            presenter.addImgaes(it)
        }

    } catch (e: Exception) {
    }

    private fun saveCaptureImageResults(data: Uri) = try {
        val file = File(data.path!!)
        mFile = Compressor(this@GallaryActivity)
            .setMaxHeight(1000).setMaxWidth(1000)
            .setQuality(99)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .compressToFile(file)

//        if (intent.getStringExtra("type").equals("soldier")) {
//            mIvPic.loadWallImage(mFile!!.absolutePath)
//
//        } else {
//            mIvPic.loadWallImage(mFile!!.absolutePath)
//        }

//         if (isInternetConnected()) {
        hashMapOf<String, RequestBody>().also {
            it.put(
                "image\"; filename=\"image.jpg",
                RequestBody.create(MediaType.parse("image/*"), mFile!!)
            )
            it.put("_id", RequestBody.create(MediaType.parse("multipart/form-data"), intent.getStringExtra("id").toString()))
            it.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "permit"))

            presenter.addImgaes(it)
//             }
        }

    } catch (e: Exception) { }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1010) {
            data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.let {
                if (it.isNotEmpty()) saveCaptureImageResults(it[0])
            }
            Log.e("RESULT","RESULT")
//                saveCaptureImageResults()
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10201) {
                callApi()
            }
            else if (requestCode == ConstUtils.REQUEST_CODE_DOCS) {
                val filePath = data?.getStringExtra("_data") ?: ""
                if (filePath.isNotEmpty()) {
                    mFile = File(filePath)
                    hashMapOf<String, RequestBody>().also {
                        it.put("image\"; filename=\"image.${mFile!!.extension}", RequestBody.create(MediaType.parse("application/${mFile!!.extension}"), mFile!!))
                        it.put("_id", RequestBody.create(MediaType.parse("multipart/form-data"), intent.getStringExtra("id").toString()))
                        it.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "permit"))
                        presenter.addImgaes(it)
                    }
                }
                Log.e("data file", data?.getStringExtra("_data") ?: "")
            }
            else if (requestCode == ConstUtils.REQUEST_TAKE_PHOTO) {
                CropImage.activity(Uri.parse(myImageUri))
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setMinCropWindowSize(1000, 1200)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(this)
            } else if (requestCode == ConstUtils.REQUEST_IMAGE_GET) {
                val uri: Uri = data?.data!!
                CropImage.activity(uri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setAspectRatio(2, 1)
                    .setMinCropWindowSize(1000, 1200)
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
}