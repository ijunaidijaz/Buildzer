package com.tradesk.appCode.home.salePersonModule

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.extension.toast
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_sales_person.*
import javax.inject.Inject

class SalesPersonActivity : BaseActivity(), IAddSalesView, SingleListCLickListener {

    val mList = mutableListOf<Client>()
    val mSalesPersonsLeadsAdapter by lazy { SalesPersonsLeadsAdapter(this, mList, this) }

    @Inject
    lateinit var presenter: AddSalesPresenter<IAddSalesView>


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_person)
        presenter.onAttach(this)
        rvSalesPersons.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rvSalesPersons.adapter = mSalesPersonsLeadsAdapter
        if (isInternetConnected()) {
            presenter.get_sales("1", "20")
        }

        mIvBack.setOnClickListener { finish() }
        mBtnAddNew.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddSalesPersonActivity::class.java
                )
            )
        }
    }

    override fun onAddSales(it: SuccessModel) {

    }

    override fun onUpdateSales(it: SuccessModel) {

    }

    override fun onDetails(it: ClientSalesModelNew) {
    }

    override fun onTradesDetails(it: TradesModel) {

    }

    override fun onAddJobSales(it: SuccessModel) {
        toast("Users assigned for job successfully.")
        val returnIntent = Intent()
        setResult(RESULT_OK, returnIntent)
        finish()

    }

    override fun onAddSubUsers(it: SuccessModel) {

    }

    override fun onSalesList(it: ClientsListModel) {
        mList.clear()
        mSalesPersonsLeadsAdapter.notifyDataSetChanged()
        mList.addAll(it.data.client)
        mSalesPersonsLeadsAdapter.notifyDataSetChanged()
    }

    override fun onerror(data: String) {
        toast(data)
    }

    override fun onResume() {
        super.onResume()
        if (isInternetConnected()) {
            presenter.get_sales("1", "20")
        }
    }


    override fun onGeneratedToken(lastAction: String) {

    }

    override fun onSingleListClick(item: Any, position: Int) {
        if (intent.hasExtra("from")) {
            val builder = GsonBuilder()
            val gson = builder.create()
            val returnIntent = Intent()
            returnIntent.putExtra("result", mList[position]._id)
            returnIntent.putExtra("image", mList[position].image)
            returnIntent.putExtra("client", gson.toJson(mList[position]))
            setResult(RESULT_OK, returnIntent)
            finish()
        } else if (intent.hasExtra("fromjob")) {

            if (isInternetConnected())
                presenter.addjob_sales(
                    mList[position]._id.toString(),
                    intent.getStringExtra("job_id").toString()
                )
        }
    }


}