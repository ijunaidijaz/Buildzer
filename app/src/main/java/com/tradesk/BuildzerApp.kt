package com.tradesk

import android.app.Activity
import android.content.Context
import android.os.StrictMode
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.facebook.FacebookSdk
import com.tradesk.di.component.DaggerAppComponent
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*
import javax.inject.Inject


class BuildzerApp : MultiDexApplication(), /*HasServiceInjector,*/ HasActivityInjector {

    companion object{
        var ON_CLICK_DELAY=1*1000
        var lastTimeClicked=0L
        var primary_Color: String="#BE2233"
//        var mSavePharmacyFavData: SavePharmacyFavData?=null
        var secondary_Color: String="#BE2233"
        var ternary_color: String="#EC505E"
        val pound = Currency.getInstance("GBP").symbol
        lateinit  var appContext: Context
//        lateinit var  context:Context;

    }

//    @Inject
//    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    @Inject
    internal lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector() = activityDispatchingAndroidInjector

//    override fun serviceInjector(): AndroidInjector<Service> = dispatchingServiceInjector

    operator fun get(context: Context): BuildzerApp {
        return context.applicationContext as BuildzerApp
    }
//     var sAnalytics: GoogleAnalytics? = null
//     var sTracker: Tracker? = null

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        BuildzerApp.appContext = applicationContext
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
//        FirebaseAnalytics.getInstance(this)
//        FirebaseCrashlytics.getInstance()
        FirebaseApp.initializeApp(this)
        Places.initialize(applicationContext, "AIzaSyAweeG9yxU6nQulKdyN6nIIBtSPak1slfo")
//        sAnalytics = GoogleAnalytics.getInstance(this);
        // Create a new PlacesClient instance
//        val placesClient = Places.createClient(this)

        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)



    }
//    @Synchronized
//    fun getDefaultTracker(): Tracker? {
//        var tracker: Tracker? = null
////        sAnalytics = GoogleAnalytics.getInstance(this) // here pass your activity instance
//        sAnalytics?.let {
//            tracker = it.newTracker(R.xml.global_tracker)
//        }
//        return tracker
//    }
//
//    fun googleTracker(category: String, action: String, label: String) {
//        val tracker: Tracker? = getDefaultTracker()
//        tracker?.send(
//            HitBuilders.EventBuilder()
//                .setCategory(category)
//                .setAction(action)
//                .setLabel(label)
//                .build()
//        )
//
//    }

    data class StateModel(var state: String, var code: String)


    fun addStates(activity: Activity): MutableList<StateModel> {
        val listingJson=loadJSONFromAsset(activity)
        val list= mutableListOf<StateModel>()
        val json= JSONArray(listingJson)
        for (i in 0 until json.length()) {
            val jsonObj= JSONObject(json.get(i).toString())
            list.add(
                StateModel(
                    jsonObj.getString("name"),
                    jsonObj.getString("abbreviation")
                )
            )
        }
        return list
    }

    fun loadJSONFromAsset(context: Context): String? {
        var json: String? = null
        json = try {
            val `is`: InputStream = context.assets.open("statesjson")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

}