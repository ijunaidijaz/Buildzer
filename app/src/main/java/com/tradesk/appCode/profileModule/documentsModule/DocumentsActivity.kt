package com.tradesk.appCode.profileModule.documentsModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tradesk.R
import com.tradesk.appCode.profileModule.proposalsModule.ProposalsActivity
import com.tradesk.utils.extension.addWatcher
import kotlinx.android.synthetic.main.activity_documents.*

class DocumentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents)

//        mCvContracts.setOnClickListener { startActivity(Intent(this, OnGoingJobsActivity::class.java).putExtra("from","Documents"))}
        mCvProposals.setOnClickListener { startActivity(Intent(this, ProposalsActivity::class.java).putExtra("title","Proposals"))}
        mCvInvoices.setOnClickListener { startActivity(Intent(this, ProposalsActivity::class.java).putExtra("title","Invoices")) }
//        mCvReceipts.setOnClickListener { startActivity(Intent(this, ProposalsActivity::class.java).putExtra("title","Receipts")) }
//        mCvPermits.setOnClickListener { startActivity(Intent(this, GallaryActivity::class.java).putExtra("title","Permits").putExtra("permit","true")) }
//        mCvOthers.setOnClickListener { startActivity(Intent(this, GallaryActivity::class.java).putExtra("other","Other Documents")) }
        mIvBack.setOnClickListener { finish() }

        editTextTextPersonName6.addWatcher {
//            mHomeLeadsAdapter.filter.filter(it)
        }
    }
}