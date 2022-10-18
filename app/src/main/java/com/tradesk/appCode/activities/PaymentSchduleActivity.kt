package com.tradesk.appCode.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tradesk.R
import com.tradesk.base.BaseActivity
import com.tradesk.utils.extension.toast
import kotlinx.android.synthetic.main.activity_payment_schdule.*

class PaymentSchduleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_schdule)

        addPayment.setOnClickListener { toast("Coming soon") }
    }


}