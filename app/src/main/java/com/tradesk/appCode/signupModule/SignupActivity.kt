package com.tradesk.appCode.signupModule

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.tradesk.R
import com.tradesk.appCode.loginModule.LoginActivity
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.SignupModel
import com.tradesk.data.entity.SuccessModel
import com.tradesk.utils.AppConstants.PATTERN
import com.tradesk.utils.ConstUtils
import com.tradesk.utils.extension.toast
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.iid.FirebaseInstanceId
import com.tradesk.appCode.MainActivity
import com.tradesk.data.entity.LoginModel
import com.tradesk.utils.PreferenceConstants
import kotlinx.android.synthetic.main.activity_add_sales_person.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.etEmail
import kotlinx.android.synthetic.main.activity_signup.etPassword
import java.io.IOException
import java.util.*
import javax.inject.Inject

class SignupActivity : BaseActivity(), ISignUpView,LocationListener {
    var token = ""
    var lat = ""
    var lng = ""
    var city = ""
    var state = ""
    var selectedTrade = ""
    var post_code = "unknown"
    var RC_SIGN_IN = 444
    lateinit var callbackManager: CallbackManager;
    private val EMAIL = "email"
    var mGoogleSignInClient: GoogleSignInClient? = null
    var addresses: List<Address>? = null
    var locationManager: LocationManager? = null
    private var userLocation: Location? = null
    var address = "unknown"
    var accessToken = "";
    var loginType=""
    lateinit var mEtTrade:TextView
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
    lateinit var presenter: SignupPresenter<ISignUpView>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        mEtTrade=findViewById(R.id.mEtTrade)
        presenter.onAttach(this)
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // 3
                token = task.result?.token.toString()
//                Toast.makeText(baseContext, token, Toast.LENGTH_LONG).show()
            })

        etAddress.setOnFocusChangeListener { view, b ->
            if (b)
                openPlaceDialog()
        }
        openTrade.setOnClickListener { showTradeMenu(mEtTrade, 1) }
        findViewById<TextView>(R.id.textView2).setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }
        ivSignUp.setOnClickListener {
            if (etName.text.toString().trim().isEmpty()) {
                toast("Enter your name", false)
            } else if (etEmail.text.toString().trim().isEmpty()) {
                toast("Enter your email", false)
            }else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString().trim()).matches()) {
                toast("Enter valid email", false)
            } else if (etAddress.text.toString().trim().isEmpty()) {
                toast("Enter your address", false)
            } else if (selectedTrade.trim().isEmpty()) {
                toast("Please select trade", false)
            } else if (etCPassword.text.toString().trim().isEmpty()) {
                toast("Enter password", false)
            } else if (!PATTERN.matcher(etCPassword.text.toString().trim()).matches()) {
                toast("Password must contain upper and lower letters, numerics, and a special character with a min of 8 characters", false)
            } else if (etPassword.text.toString().trim().isEmpty()) {
                toast("Confirm password", false)
            } else if (!etPassword.text.toString().trim().equals(etCPassword.text.toString().trim(),false)) {
                toast("Password does not match", false)
            }else if ( checkBox.isChecked.not()) {
                toast("Please accept phone number terms and conditions", false)
            }else{
                if (isInternetConnected())
                    presenter.signup(etName.text.toString().trim(),etEmail.text.toString().trim(),
                        etAddress.text.toString().trim(),etPassword.text.toString().trim(),"1",city,state,post_code,lat+","+lng,selectedTrade)
            }
        }
        //google SSO
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .requestId()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        callbackManager = CallbackManager.Factory.create();
        googleLoginS.setOnClickListener {
            googleSignIn()
        }
        fbLoginS.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(
                    this,
                    Arrays.asList("email", "public_profile", "user_friends")
                )

            // If you are using in a fragment, call loginButton.setFragment(this);

            // Callback registration
            // If you are using in a fragment, call loginButton.setFragment(this);

            // Callback registration
            LoginManager.getInstance()
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        accessToken = loginResult.accessToken.token
                        loginType="fb"
                        getLocation()

                    }

                    override fun onCancel() {
                        toast("cancel")
                    }

                    override fun onError(error: FacebookException) {
                        toast("error")
                    }
                })
        }
    }

    override fun onSignup(data: SignupModel) {
        toast("Your account is created successfully. Please check your email to verify the email.")
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
    fun signOut() {
        mGoogleSignInClient!!.signOut()
        LoginManager.getInstance().logOut()
    }
    override fun onSocialSignup(data: LoginModel) {
        toast(data.message, true)
        mPrefs.setUserLoggedIn(PreferenceConstants.USER_LOGGED_IN, true)
        mPrefs.setKeyValue(PreferenceConstants.USER_TYPE, data.data.userType.toString())
//        mPrefs.setKeyValue(PreferenceConstants.USER_UNIT, data.body.unit ?: "")
//        mPrefs.setKeyValue(PreferenceConstants.USER_GOVT, data.body.contractor_name ?: "")
//        mPrefs.setKeyValue(PreferenceConstants.USER_COMMAND, data.body.command_name ?: "")
//        mPrefs.setKeyValue(PreferenceConstants.USER_GENDER, data.body.gender.toString() ?: "")
//        mPrefs.setKeyValue(PreferenceConstants.USER_ID, data.body.id.toString() ?: "")
//        mPrefs.setKeyValue(PreferenceConstants.USER_URL, data.body.profile_image ?: "")
        mPrefs.setKeyValue(PreferenceConstants.USER_TOKEN, data.data.token ?: "")

        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }

    override fun onForgot(data: SuccessModel) {

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
            .build(this@SignupActivity)
        startActivityForResult(intent, ConstUtils.PLACE_AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun setPlaceData(placeData: Place) {
        try {
            if (placeData != null) {
                etAddress.setText("")
                city=""
                state=""
                post_code=""

                if (!placeData.getName()!!.toLowerCase().isEmpty()) {
                    etAddress.setText(placeData.getName())
                    etAddress.setSelection(etAddress.getText()!!.length)
                } else etAddress.setError("Not available")

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
                    post_code=getZipCodeFromLocation(address);
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
            callbackManager.onActivityResult(requestCode, resultCode, data)
            // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
            if (resultCode == RESULT_OK && requestCode == RC_SIGN_IN) {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
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
    val mInfoBuilderTrade: Dialog by lazy { Dialog(this) }
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
    private fun googleSignIn() {
        val signInIntent: Intent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            accessToken = account.idToken.toString();
            loginType="google"
            getLocation()
            signOut()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(
                ContentValues.TAG,
                "signInResult:failed code=" + e.statusCode
            )
            // updateUI(null);
        }
    }



    private fun geoCodeLocation() {
        val geocoder = Geocoder(this)
        try {
            addresses =
                geocoder.getFromLocation(
                    userLocation!!.getLatitude(),
                    userLocation!!.getLongitude(),
                    1
                )
            try {
                if ((addresses as MutableList<Address>?)!![0].postalCode != null) {
                    post_code = (addresses as MutableList<Address>?)!![0].postalCode
                }
                if ((addresses as MutableList<Address>?)!![0].countryName != null) {
                    state = (addresses as MutableList<Address>?)!![0].countryName
                }
                if ((addresses as MutableList<Address>?)!![0].locality != null) {
                    city = (addresses as MutableList<Address>?)!![0].locality
                }
                if ((addresses as MutableList<Address>?)!![0].getAddressLine(0)!=null) {
                    address = (addresses as MutableList<Address>?)!![0].getAddressLine(0)
                }
                lat = userLocation!!.latitude.toString()
                lng = userLocation!!.longitude.toString()
                presenter.socialLogin(address,accessToken,token, "android",post_code,city,state,lat+","+lng,loginType);
            } catch (e: java.lang.Exception) {
                toast("Something went wrong")
            }
        } catch (e: IOException) {
            e.printStackTrace()
//            Helper.hideProgressBar(progressBar)
        }
    }

    fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    10
                )
            }
            .setNegativeButton("No") { dialog, id ->
                buildAlertMessageNoGps()
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()
    }

    fun getLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }
        locationManager = this
            .getSystemService(
                LOCATION_SERVICE
            ) as LocationManager
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        userLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        geoCodeLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                PackageManager.PERMISSION_DENIED -> buildAlertMessageNoGps() //Tell to user the need of grant permission
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }

    override fun onLocationChanged(p0: Location) {
//        geoCodeLocation()

    }

}