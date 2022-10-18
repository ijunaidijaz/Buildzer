package com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.addClientModule

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.tradesk.R
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.ClientSalesModelNew
import com.tradesk.data.entity.ClientsListModel
import com.tradesk.data.entity.SuccessModel
import com.tradesk.utils.ConstUtils
import com.tradesk.utils.extension.toast
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.tradesk.appCode.home.customersModule.CustomersActivity
import kotlinx.android.synthetic.main.activity_add_client.*
import kotlinx.android.synthetic.main.activity_add_client.mBtnSubmit
import kotlinx.android.synthetic.main.activity_add_client.mEtAddress
import kotlinx.android.synthetic.main.activity_add_client.mEtEmail
import kotlinx.android.synthetic.main.activity_add_client.mEtName
import kotlinx.android.synthetic.main.activity_add_client.mIvBack
import kotlinx.android.synthetic.main.activity_add_client.mTvNumber
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.util.*
import javax.inject.Inject
import com.tradesk.data.entity.PhoneTextFormatter
import com.tradesk.utils.CommonUtil
import java.io.IOException


class AddClientActivity : BaseActivity(), IAddClientView {


    var lat = ""
    var lng = ""
    var city = ""
    var state = ""
    var post_code = ""
    lateinit var dialog:Dialog;

    var mFile: File? = null
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
    lateinit var presenter: AddClientPresenter<IAddClientView>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_client)
        presenter.onAttach(this)

        if (intent.hasExtra("title")){
            textView6.setText("Edit Client")
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
        }else{

            mEtPhoneNumber.addTextChangedListener(PhoneTextFormatter(mEtPhoneNumber, "(###) ###-####"))
            mEtHomeNumber.addTextChangedListener(PhoneTextFormatter(mEtHomeNumber, "(###) ###-####"))

        }

        mIvBack.setOnClickListener { finish() }
        mBtnSubmit.setOnClickListener {

            if(intent.hasExtra("id")){

                if (mEtName.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_name), false)
                } else if (mEtEmail.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_email_address), false)
                } else if (!Patterns.EMAIL_ADDRESS.matcher(mEtEmail.text.toString().trim())
                        .matches()
                ) {
                    toast(getString(R.string.enter_valid_address), false)
                } else if (mEtPhoneNumber.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_phone), false)
                }else if (mEtPhoneNumber.text.toString().length<14) {
                    toast("Enter valid phone number", false)
                }/* else if (mEtHomeNumber.text.toString().trim().isEmpty()) {
                toast(getString(R.string.enter_home_phone), false)
            }*/ else if (mEtAddress.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_address), false)
                } else {


                    if (isInternetConnected()) hashMapOf<String, RequestBody>().also {
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
                                mEtPhoneNumber.text.toString().trim().replace(" ","").replace("(","").replace(")","").replace("-","")
                            )
                        )

                        if (mEtHomeNumber.text.toString().trim().isNotEmpty()) {
                            it.put(
                                "home_phone_number",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    mEtHomeNumber.text.toString().trim().replace(" ", "")
                                        .replace("(", "").replace(")", "").replace("-", "")
                                )
                            )
                        }

                        it.put(
                            "type",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                "client"
                            )
                        )

                        it.put(
                            "privatenotes",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mEtNotes.text.toString().trim()
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
                                ""
                            )
                        )

                        presenter.updatesaleclient(it)
                    }
                }
            }else {
                if (mEtName.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_name), false)
                } else if (mEtEmail.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_email_address), false)
                } else if (!Patterns.EMAIL_ADDRESS.matcher(mEtEmail.text.toString().trim())
                        .matches()
                ) {
                    toast(getString(R.string.enter_valid_address), false)
                } else if (mEtPhoneNumber.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_phone), false)
                }else if (mEtPhoneNumber.text.toString().length<14) {
                    toast("Enter valid phone number", false)
                }/* else if (mEtHomeNumber.text.toString().trim().isEmpty()) {
                toast(getString(R.string.enter_home_phone), false)
            }*/ else if (mEtAddress.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_address), false)
                } else {


                    if (isInternetConnected()) hashMapOf<String, RequestBody>().also {
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
                                mEtPhoneNumber.text.toString().trim().replace(" ","").replace("(","").replace(")","").replace("-","")
                            )
                        )

                        if (mEtHomeNumber.text.toString().trim().isNotEmpty()) {
                            it.put(
                                "home_phone_number",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    mEtHomeNumber.text.toString().trim().replace(" ", "")
                                        .replace("(", "").replace(")", "").replace("-", "")
                                )
                            )
                        }
                        it.put(
                            "type",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                "client"
                            )
                        )

                        it.put(
                            "privatenotes",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mEtNotes.text.toString().trim()
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
                                ""
                            )
                        )

                        presenter.add_client(it)
                    }
                }
            }
        }
    }


    override fun onAdd(it: SuccessModel) {
        Handler().postDelayed({
            CommonUtil.showSuccessDialog(CustomersActivity.context,"Client Added")
        }, 1500)
        toast(it.message)
        finish()
    }

    override fun onUpdateSales(it: SuccessModel) {

        toast("Updated successfully")
        finish()




    }

    override fun onDetails(it: ClientSalesModelNew) {
        mEtName.setText(it.data.client.name)
        mEtEmail.setText(it.data.client.email)

        mTvNumber.setText(insertString(it.data.client.phone_no,"",0))
        mTvNumber.setText(insertString(mTvNumber.text.toString(), ")", 2))
        mTvNumber.setText(insertString(mTvNumber.text.toString(), " ", 3))
        mTvNumber.setText(insertString(mTvNumber.text.toString(), "-", 7))
        mTvNumber.setText("("+mTvNumber.text.toString())

        mEtPhoneNumber.setText(mTvNumber.text.toString())

        if (it.data.client.home_phone_number.isNotEmpty()) {
            mTvphoneNumber.setText(insertString(it.data.client.home_phone_number, "", 0))
            mTvphoneNumber.setText(insertString(mTvphoneNumber.text.toString(), ")", 2))
            mTvphoneNumber.setText(insertString(mTvphoneNumber.text.toString(), " ", 3))
            mTvphoneNumber.setText(insertString(mTvphoneNumber.text.toString(), "-", 7))
            mTvphoneNumber.setText("(" + mTvphoneNumber.text.toString())

            mEtHomeNumber.setText(mTvphoneNumber.text.toString())
        }



        mEtAddress.setText(it.data.client.address.street)
        mEtNotes.setText(it.data.client.privatenotes)

        lat = it.data.client.address.location.coordinates[0].toString()
        lng = it.data.client.address.location.coordinates[1].toString()
        city = it.data.client.address.city
        state = it.data.client.address.state
        post_code = it.data.client.address.zipcode

        mEtPhoneNumber.addTextChangedListener(PhoneTextFormatter(mEtPhoneNumber, "(###) ###-####"))
        mEtHomeNumber.addTextChangedListener(PhoneTextFormatter(mEtHomeNumber, "(###) ###-####"))

    }

    override fun onGetClients(it: ClientsListModel) {

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
            .build(this@AddClientActivity)
        startActivityForResult(intent, ConstUtils.PLACE_AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun setPlaceData(placeData: Place) {
        try {
            if (placeData != null) {
                mEtAddress.setText("")
                city=""
                state=""
                post_code=""

                if (!placeData.getName()!!.toLowerCase().isEmpty()) {
                    mEtAddress.setText(placeData.getName())
                    mEtAddress.setSelection(mEtAddress.getText()!!.length)
                } else mEtAddress.setError("Not available")

                for (i in 0 until placeData.getAddressComponents()!!.asList().size) {
                    val place: AddressComponent =
                        placeData.getAddressComponents()!!.asList().get(i)

                    if (city.trim().isEmpty()) {
                        if (place.types.contains("neighborhood") && place.types.contains("political")) {
                            city=place.name
                        }
                    }

                    if (city.trim().isEmpty()) {
                        if (place.types.contains("locality") && place.types.contains("political")) {
                            city=place.name
                        }
                    }

                    if (place.types.contains("administrative_area_level_1")) {
                        state=place.name
                    }
                    if (place.types.contains("postal_code")) {
                        post_code=place.name
                    }
                    var address: Address = getAddressFromLocation(
                        placeData.latLng!!.latitude,
                        placeData.latLng!!.longitude
                    )
                    if (address.postalCode == null) {
                        address.postalCode="unknown"
                    }
                    if (address.locality == null) {
                        address.locality="unknown"
                    }
                    if (address.adminArea == null) {
                        address.adminArea="unknown"
                    }
                    post_code = getZipCodeFromLocation(address);
                    state=address.adminArea
                    city=address.locality
                }


                lng = placeData.latLng!!.longitude.toString();
                lat = placeData.latLng!!.latitude.toString();
            }
        } catch (e: java.lang.Exception) {
            Log.e("Exce......", e.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            /*if (requestCode == ConstUtils.REQUEST_TAKE_PHOTO) {
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
            } else*/ if (requestCode == ConstUtils.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                try {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    setPlaceData(place)
                } catch (e: java.lang.Exception) {
                }
            }
            /*if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    saveCaptureImageResults(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }*/
        }
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