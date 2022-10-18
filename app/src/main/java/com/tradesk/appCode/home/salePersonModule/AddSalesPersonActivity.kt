package com.tradesk.appCode.home.salePersonModule

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.tradesk.R
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.utils.ConstUtils
import com.tradesk.utils.extension.loadWallImage
import com.tradesk.utils.extension.toast
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.tradesk.appCode.MainActivity
import com.tradesk.appCode.home.customersModule.CustomersActivity
import com.tradesk.appCode.profileModule.usersContModule.UsersContrActivity
import com.tradesk.utils.CommonUtil
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_add_client.*
import kotlinx.android.synthetic.main.activity_add_sales_person.*
import kotlinx.android.synthetic.main.activity_add_sales_person.imageView9
import kotlinx.android.synthetic.main.activity_add_sales_person.mBtnSubmit
import kotlinx.android.synthetic.main.activity_add_sales_person.mEtAddress
import kotlinx.android.synthetic.main.activity_add_sales_person.mEtEmail
import kotlinx.android.synthetic.main.activity_add_sales_person.mEtName
import kotlinx.android.synthetic.main.activity_add_sales_person.mIvBack
import kotlinx.android.synthetic.main.activity_add_sales_person.mTvNumber
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject

class AddSalesPersonActivity : BaseActivity(), IAddSalesView {
    var lat = ""
    var lng = ""
    var city = ""
    var state = ""
    var post_code = ""
    var selectedTrade = ""
    var designation = ""
    var myImageUri = ""
    var mFile: File? = null
    lateinit var dialog: Dialog

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
    lateinit var presenter: AddSalesPresenter<IAddSalesView>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sales_person)
        presenter.onAttach(this)

        if(intent.hasExtra("addUser")){
            findViewById<TextView>(R.id.textView6).text="Add User"
        }
        mIvBack.setOnClickListener { finish() }
        mEtTrade.setOnClickListener { showTradeMenu(mEtTrade, 1) }
        imageView9.setOnClickListener { showTradeMenu(mEtTrade, 1) }
        mIvSelectPic.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                showImagePop()
            }
        }

        mEtAddress.setOnFocusChangeListener { view, b ->
            if (b)
                openPlaceDialog()
        }

        if (intent.hasExtra("id")) {
            presenter.getDetails(
                intent.getStringExtra("id").toString(),
                "1",
                "10",
                intent.getStringExtra("type").toString(),
                ""
            )
        } else {
            mEtNumber.addTextChangedListener(PhoneTextFormatter(mEtNumber, "(###) ###-####"))
        }

        mBtnSubmit.setOnClickListener {

            if (intent.hasExtra("id")) {

                if (mFile == null) {
                    toast(getString(R.string.pleaseuploadpic), false)
                } else if (mEtName.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_name), false)
                } else if (mEtEmail.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_email_address), false)
                } else if (!Patterns.EMAIL_ADDRESS.matcher(mEtEmail.text.toString().trim())
                        .matches()
                ) {
                    toast(getString(R.string.enter_valid_address), false)
                } else if (mEtNumber.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_phone), false)
                } else if (mEtNumber.text.toString().trim().length < 14) {
                    toast(getString(R.string.enter_valid_phone), false)
                } else if (mEtAddress.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_address), false)
                } else if (selectedTrade.isEmpty()) {
                    toast(getString(R.string.select_trade), false)
                } else {
                    designation=mEtDesignation.text.toString();
                    if (isInternetConnected())
                        hashMapOf<String, RequestBody>().also {
                            it.put(
                                "id",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    intent.getStringExtra("id")
                                )
                            )

                            it.put(
                                "name",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    mEtName.text.toString().trim()
                                )
                            )

                            it.put(
                                "email",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    mEtEmail.text.toString().trim()
                                )
                            )

                            it.put(
                                "phone_no",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    mEtNumber.text.toString().trim().replace(" ", "")
                                        .replace("(", "").replace(")", "").replace("-", "")
                                )
                            )

                            it.put(
                                "type",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    intent.getStringExtra("type")
                                )
                            )

                            it.put(
                                "address",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    mEtAddress.text.toString()
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
                                "zipcode",
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
                                    selectedTrade
                                )
                            )
                            it.put(
                                "designation",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    designation
                                )
                            )

                            if (mFile != null) {
                                it.put(
                                    "image\"; filename=\"image.jpg",
                                    RequestBody.create(MediaType.parse("image/*"), mFile!!)
                                )
                            }
                            presenter.updatesaleclient(it)
                        }
                }

            } else {

                if (mEtName.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_name), false)
                } else if (mEtEmail.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_email_address), false)
                } else if (!Patterns.EMAIL_ADDRESS.matcher(mEtEmail.text.toString().trim())
                        .matches()
                ) {
                    toast(getString(R.string.enter_valid_address), false)
                } else if (mEtNumber.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_phone), false)
                } else if (mEtNumber.text.toString().trim().length < 14) {
                    toast(getString(R.string.enter_valid_phone), false)
                } else if (mEtAddress.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_address), false)
                } else if (selectedTrade.isEmpty()) {
                    toast(getString(R.string.select_trade), false)
                } else {
                    designation=mEtDesignation.text.toString();
                    if (isInternetConnected())
                        hashMapOf<String, RequestBody>().also {
                            it.put(
                                "name",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    mEtName.text.toString()
                                )
                            )

                            it.put(
                                "email",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    mEtEmail.text.toString()
                                )
                            )

                            it.put(
                                "phone_no",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    mEtNumber.text.toString().trim().replace(" ", "")
                                        .replace("(", "").replace(")", "").replace("-", "")
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
                                    mEtAddress.text.toString()
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
                                "zipcode",
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
                                    selectedTrade
                                )
                            )
                            it.put(
                                "designation",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    designation
                                )
                            )
                            if (mFile != null) {

                                it.put(
                                    "image\"; filename=\"image.jpg",
                                    RequestBody.create(MediaType.parse("image/*"), mFile!!)
                                )
                            }


                            presenter.add_sales(it)
                        }
                }
            }
        }
    }

    override fun onAddSales(it: SuccessModel) {
        Handler().postDelayed({
            CommonUtil.showSuccessDialog(UsersContrActivity.context,"User Added")
        }, 1500)
        toast("Sales person added successfully")
        finish()
    }

    override fun onUpdateSales(it: SuccessModel) {
        toast("Updated successfully")
        finish()
    }

    override fun onDetails(it: ClientSalesModelNew) {
        if (it.data.client.image.isNotEmpty()) {
            mIvPic.loadWallImage(it.data.client.image)
        }

        mEtName.setText(it.data.client.name)
        mEtEmail.setText(it.data.client.email)
        mEtTrade.text = it.data.client.trade.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }

        mTvNumber.text = insertString(it.data.client.phone_no, "", 0)
        mTvNumber.text = insertString(mTvNumber.text.toString(), ")", 2)
        mTvNumber.text = insertString(mTvNumber.text.toString(), " ", 3)
        mTvNumber.text = insertString(mTvNumber.text.toString(), "-", 7)
        mTvNumber.text = "(" + mTvNumber.text.toString()

//        mEtNumber.setText(it.data.client.phone_no)
        mEtNumber.setText(mTvNumber.text.toString())



        mEtAddress.setText(it.data.client.address.street)

        lat = it.data.client.address.location.coordinates[0].toString()
        lng = it.data.client.address.location.coordinates[1].toString()
        city = it.data.client.address.city
        state = it.data.client.address.state
        post_code = it.data.client.address.zipcode
        selectedTrade = it.data.client.trade


        mEtNumber.addTextChangedListener(PhoneTextFormatter(mEtNumber, "(###) ###-####"))
    }

    override fun onTradesDetails(it: TradesModel) {

    }

    override fun onAddJobSales(it: SuccessModel) {

    }

    override fun onAddSubUsers(it: SuccessModel) {
        toast("Sub user added successfully")
        finish()
    }

    override fun onSalesList(it: ClientsListModel) {

    }

    override fun onerror(data: String) {
        toast(data)
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
            .build(this@AddSalesPersonActivity)
        startActivityForResult(intent, ConstUtils.PLACE_AUTOCOMPLETE_REQUEST_CODE)
    }


    private fun setPlaceData(placeData: Place) {
        try {
            if (placeData != null) {
                mEtAddress.setText("")
                city = ""
                state = ""
                post_code = ""

                if (!placeData.name!!.lowercase(Locale.getDefault()).isEmpty()) {
                    mEtAddress.setText(placeData.name)
                    mEtAddress.setSelection(mEtAddress.text!!.length)
                } else mEtAddress.error = "Not available"

                for (i in 0 until placeData.addressComponents!!.asList().size) {
                    val place: AddressComponent =
                        placeData.addressComponents!!.asList().get(i)

                    if (city.trim().isEmpty()) {
                        if (place.types.contains("neighborhood") && place.types.contains("political")) {
                            city = place.name
                        }
                    }

                    if (city.trim().isEmpty()) {
                        if (place.types.contains("locality") && place.types.contains("political")) {
                            city = place.name
                        }
                    }

                    if (place.types.contains("administrative_area_level_1")) {
                        state = place.name
                    }
                    if (place.types.contains("postal_code")) {
                        post_code = place.name
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
                    post_code = getZipCodeFromLocation(address)
                    state = address.adminArea
                    city = address.locality
                }


                lng = placeData.latLng!!.longitude.toString()
                lat = placeData.latLng!!.latitude.toString()
            }
        } catch (e: java.lang.Exception) {
            Log.e("Exce......", e.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ConstUtils.REQUEST_TAKE_PHOTO) {
                CropImage.activity(Uri.parse(myImageUri))
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setMinCropWindowSize(1000,1200)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(this)
            } else if (requestCode == ConstUtils.REQUEST_IMAGE_GET) {
                val uri: Uri = data?.data!!
                CropImage.activity(uri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
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

    private fun saveCaptureImageResults(data: Uri) = try {
        val file = File(data.path!!)
        mFile = Compressor(this@AddSalesPersonActivity)
            .setMaxHeight(1000).setMaxWidth(1000)
            .setQuality(99)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .compressToFile(file)

        if (intent.getStringExtra("type").equals("soldier")) {
            mIvPic.loadWallImage(mFile!!.absolutePath)
        } else {
            mIvPic.loadWallImage(mFile!!.absolutePath)
        }


    } catch (e: Exception) {
    }

    val mBuilder: Dialog by lazy { Dialog(this@AddSalesPersonActivity) }

    fun showImagePop() {
        mBuilder.setContentView(R.layout.camera_dialog)
        mBuilder.window!!.attributes.windowAnimations = R.style.DialogAnimation
        mBuilder.window!!.setGravity(Gravity.BOTTOM)
        mBuilder.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
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
        mBuilder.show()
    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(this@AddSalesPersonActivity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    ConstUtils.createImageFile(this@AddSalesPersonActivity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this@AddSalesPersonActivity,
                        "${this@AddSalesPersonActivity.packageName}.provider",
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

    fun showTradeMenu(anchor: View, position: Int): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.menu.add("Admins") // menus items
        popup.menu.add("Managers") // menus items
        popup.menu.add("Employees")
        popup.menu.add("Custom")
        popup.setOnMenuItemClickListener {

            if (it.title.equals("Custom")) {
                mEtTrade.hint = "Click here to select..."
                showInformationTradePop(0)
            } else {
                mEtTrade.text = it.title.toString()
                selectedTrade = it.title.toString().lowercase(Locale.getDefault())
            }
//            if (it.title.equals("Admins")) {
//                selectedTrade = "admin"
//            } else if (it.title.equals("Managers")) {
//                selectedTrade = "manager"
//            } else if (it.title.equals("Employees")) {
//                selectedTrade = "employee"
//            } else if (it.title.equals("Custom")) {
//
//            }
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }


    val mInfoBuilderTrade: Dialog by lazy { Dialog(this@AddSalesPersonActivity) }
    fun showInformationTradePop(id: Int) {
        mInfoBuilderTrade.setContentView(R.layout.popup_dynamic_salestrade)
        val displayMetrics = DisplayMetrics()
        mInfoBuilderTrade.window!!.attributes.windowAnimations = R.style.DialogAnimationNew
        mInfoBuilderTrade.window!!.setGravity(Gravity.BOTTOM)
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        mInfoBuilderTrade.window!!.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
            (displayMetrics.widthPixels * 0.99).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )




        mInfoBuilderTrade.findViewById<TextView>(R.id.tvDone).setOnClickListener {
            if (mInfoBuilderTrade.findViewById<EditText>(R.id.editTextTextPersonName).text.toString()
                    .trim().isNotEmpty()
            ) {
                selectedTrade =
                    mInfoBuilderTrade.findViewById<EditText>(R.id.editTextTextPersonName).text.toString()
                        .trim()
                mEtTrade.text = selectedTrade
                mInfoBuilderTrade.dismiss()
            } else {
                toast("Enter custom trade")
            }
        }

        mInfoBuilderTrade.findViewById<TextView>(R.id.mTvCancel).setOnClickListener {
            selectedTrade = ""
            mInfoBuilderTrade.dismiss()
        }



        mInfoBuilderTrade.show()
    }
    private fun showNoteDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.add_trade_dialog)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        val window: Window? = dialog.getWindow()
        val wlp = window?.attributes
        if (wlp != null) {
            wlp.gravity = Gravity.CENTER
        }
        dialog.setCancelable(false)
        if (wlp != null) {
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        }
        if (window != null) {
            window.attributes = wlp
        }
        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
        val note_edit: EditText = dialog.findViewById<EditText>(R.id.note_edit)
        val add: Button = dialog.findViewById<Button>(R.id.add)
        val cancel: Button = dialog.findViewById<Button>(R.id.cancel)
        add.setOnClickListener { v: View? ->
            val note = note_edit.text.toString()
            if (note != null && !note.equals("", ignoreCase = true)) {
                dialog.dismiss()

            } else toast("Please write trade")
        }
        cancel.setOnClickListener { v: View? ->
            dialog.dismiss()
        }
    }

    private fun getZipCodeFromLocation(addr: Address): String {

        return if (addr.postalCode == null) "" else addr.postalCode
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