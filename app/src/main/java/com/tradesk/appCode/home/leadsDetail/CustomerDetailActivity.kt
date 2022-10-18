package com.tradesk.appCode.home.leadsDetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.utils.extension.loadWallImage
import kotlinx.android.synthetic.main.activity_customer_detail.*
import java.util.*

class CustomerDetailActivity : AppCompatActivity(), SingleItemCLickListener {

    val mCustomerDocsAdapter by lazy { CustomerDocsAdapter(this, this) }
    val mCustomerCompletedJobsAdapter by lazy { CustomerCompletedJobsAdapter(this, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_detail)

        if (intent.getStringExtra("image").toString().isNotEmpty()) {
            mTvImage.loadWallImage(intent.getStringExtra("image").toString())
        }

        if (intent.getStringExtra("phone").toString().isEmpty()) {
            mTvPhone.text = "N/A"
        } else {

            mTvPhone.text = insertString(intent.getStringExtra("phone").toString(), "", 0)
            mTvPhone.text = insertString(mTvPhone.text.toString(), ")", 2)
            mTvPhone.text = insertString(mTvPhone.text.toString(), " ", 3)
            mTvPhone.text = insertString(mTvPhone.text.toString(), "-", 7)
            mTvPhone.text = "+1 (" + mTvPhone.text.toString()

        }

        mTvName.text = intent.getStringExtra("name")
        mNameTv.text = intent.getStringExtra("name")
        mTvTrade.text = intent.getStringExtra("trade").toString()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        textTrade.text = intent.getStringExtra("trade").toString()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        mTvCompany.text = intent.getStringExtra("name")
        mTvEmail.text = intent.getStringExtra("email")
        mTvAddress.text = intent.getStringExtra("address")


        mIvBack.setOnClickListener { finish() }

        rvDocu.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvDocu.adapter = mCustomerDocsAdapter

        rcv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rcv.adapter = mCustomerCompletedJobsAdapter

    }

    override fun onSingleItemClick(item: Any, position: Int) {

    }

    // Function to insert string
    open fun insertString(
        originalString: String,
        stringToBeInserted: String?,
        index: Int
    ): String? {
        // Create a new string
        var newString: String? = String()
        for (i in 0 until originalString.length) {
            // Insert the original string character
            // into the new string
            newString += originalString[i]
            if (i == index) {
                // Insert the string to be inserted
                // into the new string
                newString += stringToBeInserted
            }
        }
        // return the modified String
        return newString
    }
}