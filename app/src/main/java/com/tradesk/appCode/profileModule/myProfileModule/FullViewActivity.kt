package com.tradesk.appCode.profileModule.myProfileModule

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.tradesk.R
import com.tradesk.utils.extension.loadOrigImage
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_full_view.*

class FullViewActivity : AppCompatActivity() {
    val url_file by lazy { intent.getStringExtra("url_file")?:"" }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_view)
        mIvBack.setOnClickListener { onBackPressed() }
        if (isInternetConnected().not()||url_file.isEmpty()) onBackPressed()
        ivWallpaper.apply {
           loadOrigImage(url_file)
        }
    }

    fun isInternetConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnectedOrConnecting)
            return true
        else {
            Snackbar.make(findViewById<View>(android.R.id.content), "You are Offline!", 2000).show()
            return false
        }
    }
}
