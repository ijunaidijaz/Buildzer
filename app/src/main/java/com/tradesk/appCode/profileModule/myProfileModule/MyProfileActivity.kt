package com.tradesk.appCode.profileModule.myProfileModule

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.PhoneTextFormatter
import com.tradesk.data.entity.ProfileModel
import com.tradesk.data.entity.SuccessModel
import com.tradesk.filemanager.FileExplorerActivity
import com.tradesk.filemanager.openApp
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.utils.ConstUtils
import com.tradesk.utils.ConstUtils.Companion.REQUEST_CODE_DOCS
import com.tradesk.utils.extension.loadWallImage
import com.tradesk.utils.extension.toast
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_my_profile.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class MyProfileActivity : BaseActivity(), IMyProfileView, SingleItemCLickListener {

    var myImageUri = ""
    var image_type = ""
    var lat = ""
    var lng = ""
    var city = ""
    var state = ""
    var post_code = ""
    var licenseimage = ""
    var mFile: File? = null
    var exploreFile: File? = null
    val mListImages = mutableListOf<String>()
    var isBack = false;
    val mUserDocumentsAdapter by lazy { UserDocumentsAdapter(this, this, mListImages) }
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
    lateinit var presenter: ProfilePresenter<IMyProfileView>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        presenter.onAttach(this)
        exploreFile = Environment.getExternalStorageDirectory()
        mRvDocs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mRvDocs.adapter = mUserDocumentsAdapter

        mIvAddressEdit.setOnFocusChangeListener { view, b ->
            if (b)
                openPlaceDialog()
        }
        if (isInternetConnected()) {
            presenter.getProfile()
        }

        mIvEdit.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                showImagePop()
            }
        }

        mIvEditCompanyLogo.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                image_type = "logo"
                showImagePop()
            }
        }
        mIvLicenseImage.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                licenseimage = "1"
                showImagePop()
            }
        }
        mIvAddDocImage.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                licenseimage = "2"
                showImagePop()
            }
        }
        mIvBack.setOnClickListener {
            isBack
            if (mIvNameEdit.text.toString().isEmpty()) {
                toast("Please enter your company name")
            } else if (mIvUNameEdit.text.toString().isEmpty()) {
                toast("Please enter your user name")
            } else if (mIvAddressEdit.text.toString().trim().isEmpty()) {
                toast("Please enter address")
            } else {
                if (isInternetConnected())
                    hashMapOf<String, RequestBody>().also {
                        it.put(
                            "name",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mIvUNameEdit.text.toString()
                            )
                        )

                        if (mIvNameEdit.text.toString().isNotEmpty()) {

                            it.put(
                                "company_name",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    mIvNameEdit.text.toString()
                                )
                            )
                        }

                        if (mIvSeccEmailEdit.text.toString().isNotEmpty()) {

                            it.put(
                                "company_email",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    mIvSeccEmailEdit.text.toString()
                                )
                            )
                        }


                        it.put(
                            "phone_no",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                if (mIvPhoneEdit.text.toString()
                                        .isEmpty() || mIvPhoneEdit.text.toString().trim()
                                        .equals("N/A")
                                ) {
                                    ""
                                } else {
                                    mIvPhoneEdit.text.toString().replace(" ", "").replace("(", "")
                                        .replace(")", "").replace("-", "").trim()
                                }
                            )
                        )

                        it.put(
                            "type",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                "sales"
                            )
                        )

                        it.put(
                            "address",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mIvAddressEdit.text.toString()
                            )
                        )

                        it.put(
                            "city",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                city
                            )
                        )

                        it.put(
                            "state",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                state
                            )
                        )

                        it.put(
                            "postal_code",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                post_code
                            )
                        )

                        it.put(
                            "latLong",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                lat + "," + lng
                            )
                        )

                        it.put(
                            "trade",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mIvTradeEditCom.text.toString().trim()
                            )
                        )

//                        it.put(
//                            "email",
//                            RequestBody.create(
//                                MediaType.parse("multipart/form-data"),
//                                mIvSecEmailEdit.text.toString().trim()
//                            )
//                        )

                        it.put(
                            "home_phone_no",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),

                                if (mIvHomePhoneEdit.text.toString().trim()
                                        .isEmpty() || mIvHomePhoneEdit.text.toString().trim()
                                        .equals("N/A")
                                ) {
                                    ""
                                } else {
                                    mIvHomePhoneEdit.text.toString().trim().replace("(", "")
                                        .replace(")", "").replace("-", "").trim()
                                }

                            )
                        )

                        it.put(
                            "fax",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                faxEdit.text.toString().trim()
                            )
                        )

                        it.put(
                            "website_link",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mIvWebsiteEdit.text.toString().trim()
                            )
                        )

                        it.put(
                            "license",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mIvLicenseEdit.text.toString().trim()
                            )
                        )

                        it.put(
                            "website",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mIvSocialWebEdit.text.toString().trim()
                            )
                        )
                        it.put(
                            "googleBusiness",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                editGoogleBusiness.text.toString().trim()
                            )
                        )
                        it.put(
                            "yelp",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                editYelp.text.toString().trim()
                            )
                        )

                        it.put(
                            "facebook",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mIvFbEdit.text.toString().trim()
                            )
                        )

                        it.put(
                            "instagram",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mIvInstaEdit.text.toString().trim()
                            )
                        )

                        it.put(
                            "latLong",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                lat + "," + lng
                            )
                        )

                        presenter.updateProfile(it)
                    }
            }
        }

    }

    override fun onProfileSuccess(it: ProfileModel) {
        if (it.data.image.isNotEmpty()) {
            mIvPic.loadWallImage(it.data.image)
        }

        if (it.data.company_logo.isNotEmpty()) {
            mIvLogo.loadWallImage(it.data.company_logo)
        }
        image_type = ""

        mTvName.setText(it.data.name)
        mTvEmail.setText(it.data.email)
        if (it.data.addtional_info.trade.isEmpty()) {
            mTvType.setText("Contractor")
        } else {
            mTvType.setText(it.data.addtional_info.trade)
        }

        if (it.data.phone_no.isEmpty()) {
            mTvPhone.setText("N/A")
        } else {

            mTvPhone.setText(insertString(it.data.phone_no.trim(), "", 0))
            mTvPhone.setText(insertString(mTvPhone.text.toString(), ")", 2))
            mTvPhone.setText(insertString(mTvPhone.text.toString(), "", 3))
            mTvPhone.setText(insertString(mTvPhone.text.toString(), "-", 7))
            mTvPhone.setText("+1 (" + mTvPhone.text.toString())

        }

        if (it.data.license_and_ins.docs_url.isNotEmpty()) {
            mListImages.clear()
            mListImages.addAll(it.data.license_and_ins.docs_url)
            mUserDocumentsAdapter.notifyDataSetChanged()
        }

        if (it.data.license_and_ins.licence_image.isNotEmpty()) {
            mIvLicenseImage.loadWallImage(it.data.image)
        }
        if (it.data.address.postal_code.isNotEmpty()) {
            lat = it.data.address.location.coordinates[0].toString()
            lng = it.data.address.location.coordinates[1].toString()
        } else {
            lat = "0.0"
            lng = "0.0"
        }

        mIvPhoneEdit.removeTextChangedListener(PhoneTextFormatter(mIvPhoneEdit, "(###)###-####"))
        mIvHomePhoneEdit.removeTextChangedListener(
            PhoneTextFormatter(
                mIvHomePhoneEdit,
                "(###)###-####"
            )
        )

        if (!mIvPhoneEdit.text.toString().trim().replace("(", "")
                .replace(")", "").replace("-", "").equals(it.data.phone_no)
        ) {
            if (it.data.phone_no.isEmpty()) {
                mIvPhoneEdit.setHint("N/A")
                mIvPhoneEdit.addTextChangedListener(
                    PhoneTextFormatter(
                        mIvPhoneEdit,
                        "(###)###-####"
                    )
                )
            } else {
//                mIvPhoneEdit.setText(insertString(it.data.phone_no.replace(" ", ""), "", 0))
//                mIvPhoneEdit.setText(insertString(mIvPhoneEdit.text.toString(), ")", 2))
//                mIvPhoneEdit.setText(insertString(mIvPhoneEdit.text.toString(), " ", 3))
//                mIvPhoneEdit.setText(insertString(mIvPhoneEdit.text.toString(), "-", 7))
                mIvPhoneEdit.setText(it.data.phone_no)

                mIvPhoneEdit.addTextChangedListener(
                    PhoneTextFormatter(
                        mIvPhoneEdit,
                        "(###)###-####"
                    )
                )

            }
        }

        if (!mIvHomePhoneEdit.text.toString().trim().replace("(", "")
                .replace(")", "").replace("-", "").equals(it.data.addtional_info.home_phone_no)
        ) {
            if (it.data.addtional_info.home_phone_no.isEmpty()) {
                mIvHomePhoneEdit.setHint("N/A")
                mIvHomePhoneEdit.addTextChangedListener(
                    PhoneTextFormatter(
                        mIvHomePhoneEdit,
                        "(###)###-####"
                    )
                )
            } else {
//
//                mIvHomePhoneEdit.setText(
//                    insertString(
//                        it.data.addtional_info.home_phone_no.replace(
//                            " ",
//                            ""
//                        ), "", 0
//                    )
////                )
//                mIvHomePhoneEdit.setText(insertString(mIvHomePhoneEdit.text.toString(), ")", 2))
//                mIvHomePhoneEdit.setText(insertString(mIvHomePhoneEdit.text.toString(), " ", 3))
//                mIvHomePhoneEdit.setText(insertString(mIvHomePhoneEdit.text.toString(), "-", 7))
                mIvHomePhoneEdit.setText(it.data.addtional_info.home_phone_no.trim())
                mIvHomePhoneEdit.addTextChangedListener(
                    PhoneTextFormatter(
                        mIvHomePhoneEdit,
                        "(###)###-####"
                    )
                )
            }
        }

        mIvNameEdit.setText(it.data.company_name)

        mIvSeccEmailEdit.setText(it.data.company_email)

        mIvUNameEdit.setText(it.data.name)
        mIvSecEmailEdit.setText(it.data.email)


        mTvType.setText(it.data.addtional_info.trade)
        mIvTradeEdit.setText(it.data.addtional_info.trade)
        mIvTradeEditCom.setText(it.data.addtional_info.trade)

        mIvAddressEdit.setText(it.data.address.street+" "+it.data.address.city+" "+it.data.address.postal_code)
        mIvCityEdit.setText(it.data.address.city)
        mIvStateEdit.setText(it.data.address.state)
        mIvPostalEdit.setText(it.data.address.postal_code)


        mIvFaxEdit.setText(it.data.addtional_info.fax)
        mIvWebsiteEdit.setText(it.data.addtional_info.website_link)

        mIvLicenseEdit.setText(it.data.license_and_ins.license)

        mIvSocialWebEdit.setText(it.data.social_media.website)
        mIvFbEdit.setText(it.data.social_media.facebook)
        mIvInstaEdit.setText(it.data.social_media.instagram)
        editGoogleBusiness.setText(it.data.social_media.googleBusiness)
        editYelp.setText(it.data.social_media.yelp)
        faxEdit.setText(it.data.addtional_info.fax);
    }

    override fun onSuccess(it: SuccessModel) {
//        toast(it.message)
        if (isBack) finish()
    }

    override fun onDocSuccess(it: SuccessModel) {
        toast(it.message)
        if (isInternetConnected()) {
            presenter.getProfile()
        }
    }

    override fun onerror(it: String) {
        toast(it)
    }

    override fun onGeneratedToken(lastAction: String) {

    }

    fun openPlaceDialog() {
        val fields = Arrays.asList(
            Place.Field.PHONE_NUMBER,
            Place.Field.BUSINESS_STATUS,
            Place.Field.ADDRESS,
            Place.Field.NAME,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.ID,
            Place.Field.LAT_LNG
        )
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this@MyProfileActivity)
        startActivityForResult(intent, ConstUtils.PLACE_AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun setPlaceData(placeData: Place) {
        try {
            if (placeData != null) {
                mIvAddressEdit.setText("")
                mIvCityEdit.setText("")
                mIvStateEdit.setText("")
                mIvPostalEdit.setText("")
                city = ""
                state = ""
                post_code = ""

                if (!placeData.getName()!!.toLowerCase().isEmpty()) {
                    mIvAddressEdit.setText(placeData.getName())
                    mIvAddressEdit.setSelection(mIvAddressEdit.getText()!!.length)
                } else mIvAddressEdit.setError("Not available")

                for (i in 0 until placeData.getAddressComponents()!!.asList().size) {
                    val place: AddressComponent =
                        placeData.getAddressComponents()!!.asList().get(i)

                    if (city.trim().isEmpty()) {
                        if (place.types.contains("neighborhood") && place.types.contains("political")) {
                            city = place.name
                            mIvCityEdit.setText(city)
                        }
                    }

                    if (city.trim().isEmpty()) {
                        if (place.types.contains("locality") && place.types.contains("political")) {
                            city = place.name
                            mIvCityEdit.setText(city)
                        }
                    }

                    if (place.types.contains("administrative_area_level_1")) {
                        state = place.name
                        mIvStateEdit.setText(state)
                    }
                    if (place.types.contains("postal_code")) {
                        post_code = place.name
                        mIvPostalEdit.setText(post_code)
                    }
                    var address: Address = getAddressFromLocation(
                        placeData.latLng!!.latitude,
                        placeData.latLng!!.longitude
                    )
                    if (address.postalCode == null) {
                        address.postalCode = "unknown"
                    }
                    if (address.locality == null) {
                        address.locality = "unknown"
                    }
                    if (address.adminArea == null) {
                        address.adminArea = "unknown"
                    }
                    post_code = getZipCodeFromLocation(address);
                    mIvPostalEdit.setText(post_code)
                    state = address.adminArea
                    city = address.locality
                }



                lng = placeData.latLng!!.longitude.toString();
                lat = placeData.latLng!!.latitude.toString();
            }
        } catch (e: java.lang.Exception) {
            Log.e("Exce......", e.toString())
        }

    }

    private var photoFile: File? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_DOCS) {
                val filePath = data?.getStringExtra("_data") ?: ""
                if (filePath.isNotEmpty()) {
                    mFile = File(filePath)
                    uploadFileToAPI(true)
                }
                Log.e("data file", data?.getStringExtra("_data") ?: "")
            }/*
            if (requestCode === REQUEST_CODE_DOCS) {

                writePdf(data!!)

            }*/ else if (requestCode == ConstUtils.REQUEST_TAKE_PHOTO) {
                CropImage.activity(Uri.parse(myImageUri))
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setMinCropWindowSize(1000,1200)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(this)
            } else if (requestCode == ConstUtils.REQUEST_IMAGE_GET) {
                val uri: Uri = data?.data!!
                CropImage.activity(uri).setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setAspectRatio(2, 1)
//                    .setMinCropWindowSize(1000,1200)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(this)
            } else if (requestCode == ConstUtils.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                try {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    setPlaceData(place)
                } catch (e: java.lang.Exception) {
                }
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

    private fun writePdf(data: Intent) {
        val dir = getExternalFilesDir("${Environment.DIRECTORY_DOCUMENTS}${File.separator}app")!!
        if (!dir.exists()) dir.mkdirs()
        var uri = data.data
        Log.e(
            "isDocumentUri",
            DocumentsContract.isDocumentUri(this@MyProfileActivity, data.data!!).toString()
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = MediaStore.setRequireOriginal(data.data!!)
        }
        val uriString = uri?.toString()
        val pdfFile: File?
        val myFile = File(uriString ?: "")
        if (uriString?.startsWith("content://") == true) {
            try {
                val v = myFile.name.replace(
                    "[^a-zA-Z]+".toRegex(),
                    "_"
                ) + SimpleDateFormat(
                    "yyyyMMdd_HHmmss",
                    Locale.getDefault()
                ).format(Date()) + ".pdf"
                pdfFile = File(dir, v)
                if (pdfFile.exists().not())
                    Log.e("DirMaking", pdfFile.createNewFile().toString())
                val fos = FileOutputStream(pdfFile)
                val out = BufferedOutputStream(fos)
                val takeFlags: Int = (data.flags
                        and (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))

                try {
                    getContentResolver()
                        .takePersistableUriPermission(data.data!!, takeFlags as Int)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
//                contentResolver?.takePersistableUriPermission(uri!!,
//                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                grantUriPermission(packageName,
//                    uri!!,
//                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val inputStream = contentResolver?.openInputStream(uri!!)
                try {
                    val buffer = ByteArray(8192)
                    var len: Int
                    while (inputStream?.read(buffer).also { len = it ?: 0 } ?: 0 >= 0) {
                        out.write(buffer, 0, len)
                    }
                    out.flush()
                } finally {
                    fos.fd.sync()
                    out.close()
                    inputStream?.close()
                }
                photoFile = pdfFile
                if (photoFile?.isRooted == true) {
                    Log.e("uploadImage", photoFile?.absolutePath ?: "")
                    openFile(photoFile!!)
                }
            } catch (e: Exception) {
                Log.e("uploadImage", e.toString())
            }
        } else if (uriString?.startsWith("file://") == true) {
            photoFile = myFile
            if (photoFile?.isRooted == true) {
                Log.e("uploadImage", photoFile?.absolutePath ?: "")
                openFile(photoFile!!)
            }
        }
    }

    private fun openFile(photoFile: File) {
        Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(photoFile.toUri(), "application/pdf")
            startActivity(this)
        }
    }

    private fun browseDocuments() {
        startActivityForResult(Intent(this, FileExplorerActivity::class.java), REQUEST_CODE_DOCS)
    }


    val mBuilder: Dialog by lazy { Dialog(this@MyProfileActivity) }

    fun showImagePop() {
        mBuilder.setContentView(R.layout.camera_dialog);
        mBuilder.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation;
        mBuilder.window!!.setGravity(Gravity.BOTTOM)
        mBuilder.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mBuilder.findViewById<TextView>(R.id.titleDoc).also {
            it.isVisible = true
            it.setOnClickListener {
                mBuilder.dismiss()
                browseDocuments()
            }
        }
        mBuilder.findViewById<View>(R.id.view11).isVisible = true
        mBuilder.findViewById<TextView>(R.id.titleCamera)
            .setOnClickListener {
                mBuilder.dismiss()
                dispatchTakePictureIntent()
            }
        mBuilder.findViewById<TextView>(R.id.titleGallery)
            .setOnClickListener {
                mBuilder.dismiss()
                dispatchTakeGalleryIntent()
            }
        mBuilder.findViewById<TextView>(R.id.titleCancel)
            .setOnClickListener { mBuilder.dismiss() }
        mBuilder.show();
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(this@MyProfileActivity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    ConstUtils.createImageFile(this@MyProfileActivity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this@MyProfileActivity,
                        "${this@MyProfileActivity.packageName}.provider",
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

    private fun saveCaptureImageResults(data: Uri) = try {
        val file1 = File(data.path!!)
        mFile = Compressor(this@MyProfileActivity)
            .setMaxHeight(1000).setMaxWidth(1000)
            .setQuality(99)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .compressToFile(file1)
        uploadFileToAPI(false)
    } catch (e: Exception) {
    }

    fun uploadFileToAPI(isDoc: Boolean) {

        if (intent.getStringExtra("type").equals("soldier")) {
            mIvPic.loadWallImage(mFile!!.absolutePath)
            when {
                isInternetConnected() -> {
                    hashMapOf<String, RequestBody>().also {
                        it.put(
                            "image\"; filename=\"image.jpg",
                            RequestBody.create(MediaType.parse("image/*"), mFile!!)
                        )

                        presenter.updateProfile(it)
                    }
                }
                else -> {}
            }

        } else if (licenseimage.equals("1")) {
            mIvLicenseImage.loadWallImage(mFile!!.absolutePath)

            when {
                isInternetConnected() -> {
                    hashMapOf<String, RequestBody>().also {
                        it.put(
                            "licence_image\"; filename=\"image.jpg",
                            RequestBody.create(MediaType.parse("image/*"), mFile!!)
                        )

                        presenter.updateProfile(it)
                    }
                }
                else -> {}
            }
        } else if (licenseimage.equals("2")) {
//            mIvLicenseImage.loadWallImage(mFile!!.absolutePath)

            when {
                isInternetConnected() -> {
                    hashMapOf<String, RequestBody>().also {
                        if (isDoc) {
                            it.put(
                                "doc\"; filename=\"doc.${mFile!!.extension}",
                                RequestBody.create(
                                    MediaType.parse("application/${mFile!!.extension}"),
                                    mFile!!
                                )
                            )
                        } else {
                            it.put(
                                "doc\"; filename=\"image.jpg",
                                RequestBody.create(MediaType.parse("image/*"), mFile!!)
                            )
                        }
                        presenter.updateProfileDoc(it)
                    }
                }
                else -> {}
            }
        } else if (image_type.isNotEmpty()) {
            mIvLogo.loadWallImage(mFile!!.absolutePath)
            when {
                isInternetConnected() -> {
                    hashMapOf<String, RequestBody>().also {
                        it.put(
                            "company_logo\"; filename=\"image.jpg",
                            RequestBody.create(MediaType.parse("image/*"), mFile!!)
                        )

                        presenter.updateProfile(it)

                    }
                }
                else -> {}
            }
        } else {
            mIvPic.loadWallImage(mFile!!.absolutePath)
            when {
                isInternetConnected() -> {
                    hashMapOf<String, RequestBody>().also {
                        it.put(
                            "image\"; filename=\"image.jpg",
                            RequestBody.create(MediaType.parse("image/*"), mFile!!)
                        )

                        presenter.updateProfile(it)
                    }
                }
                else -> {}
            }
        }

    }

    override fun onBackPressed() {
        isBack=true
        super.onBackPressed()
        if (mIvNameEdit.text.toString().isEmpty()) {
            toast("Please enter your company name")
        } else if (mIvUNameEdit.text.toString().isEmpty()) {
            toast("Please enter your user name")
        } else if (mIvAddressEdit.text.toString().trim().isEmpty()) {
            toast("Please enter address")
        } else {
            if (isInternetConnected())
                hashMapOf<String, RequestBody>().also {
                    it.put(
                        "name",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            mIvUNameEdit.text.toString()
                        )
                    )
                    if (mIvNameEdit.text.toString().isNotEmpty()) {

                        it.put(
                            "company_name",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mIvNameEdit.text.toString()
                            )
                        )
                    }

                    if (mIvSeccEmailEdit.text.toString().isNotEmpty()) {

                        it.put(
                            "company_email",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mIvSeccEmailEdit.text.toString()
                            )
                        )
                    }
//                    it.put(
//                        "email",
//                        RequestBody.create(
//                            MediaType.parse("multipart/form-data"),
//                            mEtEmail.text.toString()
//                        )
//                    )

                    it.put(
                        "phone_no",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            if (mIvPhoneEdit.text.toString()
                                    .isEmpty() || mIvPhoneEdit.text.toString().trim().equals("N/A")
                            ) {
                                ""
                            } else {
                                mIvPhoneEdit.text.toString().replace("+1 ", "").replace(" ", "")
                                    .replace("(", "")
                                    .replace(")", "").replace("-", "")
                            }
                        )
                    )

                    it.put(
                        "address",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            mIvAddressEdit.text.toString()
                        )
                    )

                    it.put(
                        "city",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            city
                        )
                    )

                    it.put(
                        "state",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            state
                        )
                    )

                    it.put(
                        "postal_code",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            mIvPostalEdit.text.toString()
                        )
                    )

                    it.put(
                        "latLong",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            lat + "," + lng
                        )
                    )

                    it.put(
                        "trade",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            mIvTradeEditCom.text.toString().trim()
                        )
                    )

//                    it.put(
//                        "email",
//                        RequestBody.create(
//                            MediaType.parse("multipart/form-data"),
//                            mIvSecEmailEdit.text.toString().trim()
//                        )
//                    )

                    it.put(
                        "home_phone_no",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            mIvHomePhoneEdit.text.toString().trim()
                        )
                    )

                    it.put(
                        "fax",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            faxEdit.text.toString().trim()
                        )
                    )
                    it.put(
                        "googleBusiness",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            editGoogleBusiness.text.toString().trim()
                        )
                    )
                    it.put(
                        "yelp",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            editYelp.text.toString().trim()
                        )
                    )
                    it.put(
                        "website_link",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            mIvWebsiteEdit.text.toString().trim()
                        )
                    )

                    it.put(
                        "license",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            mIvLicenseEdit.text.toString().trim()
                        )
                    )

                    it.put(
                        "website",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            mIvSocialWebEdit.text.toString().trim()
                        )
                    )

                    it.put(
                        "facebook",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            mIvFbEdit.text.toString().trim()
                        )
                    )

                    it.put(
                        "instagram",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            mIvInstaEdit.text.toString().trim()
                        )
                    )

                    it.put(
                        "latLong",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            lat + "," + lng
                        )
                    )

                    presenter.updateProfile(it)
                }
        }
    }

    override fun onSingleItemClick(item: Any, position: Int) {
        openApp(this@MyProfileActivity, mListImages[position])

    }

    private fun getZipCodeFromLocation(addr: Address): String {

        return if (addr.getPostalCode() == null) "" else addr.getPostalCode()
    }

    private fun getAddressFromLocation(lat: Double, long: Double): Address {
        val geocoder = Geocoder(this)
        var address = Address(Locale.getDefault())
        try {
            val addr: List<Address> =
                geocoder.getFromLocation(lat, long, 1)
            if (addr.size > 0) {
                address = addr[0]
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }

}