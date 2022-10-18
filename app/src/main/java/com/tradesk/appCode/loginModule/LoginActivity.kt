package com.tradesk.appCode.loginModule

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.tradesk.R
import com.tradesk.appCode.MainActivity
import com.tradesk.appCode.forgotPassword.ForgotActivity
import com.tradesk.appCode.signupModule.SignupActivity
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.LoginModel
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.toast
import kotlinx.android.synthetic.main.activity_login.*
import java.io.IOException
import java.util.*
import javax.inject.Inject
import android.location.LocationManager








class LoginActivity : BaseActivity(), ILoginView, LocationListener {
    var token = ""
    var RC_SIGN_IN = 444
    lateinit var callbackManager: CallbackManager;
    private val EMAIL = "email"
    var mGoogleSignInClient: GoogleSignInClient? = null
    var addresses: List<Address>? = null
    var locationManager: LocationManager? = null
    private var userLocation: Location? = null
    var postalCode = "unknown";
    var city = "unknown";
    var state = "unknown";
    var lat = ""
    var lng = ""
    var address = "unknown"
    var accessToken = "";
    var loginType=""
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
    lateinit var presenter: LoginPresenter<ILoginView>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
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




        mTvSignUp.setOnClickListener { startActivity(Intent(this, SignupActivity::class.java)) }
        mTvForgot.setOnClickListener { startActivity(Intent(this, ForgotActivity::class.java)) }

        mIvLogin.setOnClickListener {
            if (token.isEmpty()) {
                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }
                        // 3
                        token = task.result?.token.toString()
//                Toast.makeText(baseContext, token, Toast.LENGTH_LONG).show()
                    })

            }
            if (etEmail.text.toString().trim().isEmpty()) {
                toast(getString(R.string.enter_email_address), false)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString().trim()).matches()) {
                toast(getString(R.string.enter_valid_address), false)
            } else if (etPassword.visibility == View.VISIBLE && etPassword.text.toString().trim()
                    .isEmpty()
            ) {
                toast(getString(R.string.enter_password), false)
            } /*else if (!PATTERN.matcher(etPassword.text.toString().trim()).matches()) {
                    toast(getString(R.string.password_length_message), false)
                } */ else {
                if (isInternetConnected())
                    presenter.login(
                        etEmail.text.toString().trim(),
                        etPassword.text.toString().trim(),
                        token,
                        "android"
                    )
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
        googleLogin.setOnClickListener {
            googleSignIn()
        }
        fbLogin.setOnClickListener {
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

    override fun onLogin(data: LoginModel) {
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

    override fun onerror(data: String) {
        toast(data)
    }

    override fun onGeneratedToken(lastAction: String) {

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
                TAG,
                "signInResult:failed code=" + e.statusCode
            )
            // updateUI(null);
        }
    }
    fun signOut() {
        mGoogleSignInClient!!.signOut()
        LoginManager.getInstance().logOut()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (resultCode == RESULT_OK && requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        val providers: List<String> = locationManager!!.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l: Location = locationManager!!.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        return bestLocation
    }
    private fun geoCodeLocation() {
        val geocoder = Geocoder(this)
        try {
            addresses =
                geocoder.getFromLocation(
                    userLocation!!.latitude,
                    userLocation!!.longitude,
                    1
                )
            try {
                if ((addresses as MutableList<Address>?)!![0].postalCode != null) {
                    postalCode = (addresses as MutableList<Address>?)!![0].postalCode
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
                presenter.socialLogin(address,accessToken,token, "android",loginType)
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
            != PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        userLocation = getLastKnownLocation();
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