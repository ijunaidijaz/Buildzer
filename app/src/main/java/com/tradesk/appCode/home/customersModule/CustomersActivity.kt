package com.tradesk.appCode.home.customersModule

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.addClientModule.AddClientActivity
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.addClientModule.AddClientPresenter
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.addClientModule.IAddClientView
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.Client
import com.tradesk.data.entity.ClientSalesModelNew
import com.tradesk.data.entity.ClientsListModel
import com.tradesk.data.entity.SuccessModel
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.extension.addWatcher
import com.tradesk.utils.extension.toast
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_customers.*
import kotlinx.android.synthetic.main.activity_sales_person.mIvBack
import javax.inject.Inject


class CustomersActivity : BaseActivity(), IAddClientView, SingleListCLickListener {

    val mList = mutableListOf<Client>()
    val mCustomersAdapter by lazy { CustomersAdapter(this, mList, mList, this) }

    companion object{
        lateinit var context: CustomersActivity
    }
    @Inject
    lateinit var presenter: AddClientPresenter<IAddClientView>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customers)
        presenter.onAttach(this)
        context=this;

        rvCustomers.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rvCustomers.adapter = mCustomersAdapter

        if (isInternetConnected()) {
            presenter.get_clients("1", "50")
        }

        mEtSearch.addWatcher {
            mCustomersAdapter.filter.filter(it)
        }

        mIvBack.setOnClickListener { finish() }
        mIvAdd.setOnClickListener { startActivity(Intent(this, AddClientActivity::class.java)) }
    }

    override fun onAdd(it: SuccessModel) {

    }

    override fun onUpdateSales(it: SuccessModel) {

    }

    override fun onDetails(it: ClientSalesModelNew) {

    }

    override fun onGetClients(it: ClientsListModel) {
        mList.clear()
        mList.addAll(it.data.client)
        mCustomersAdapter.notifyDataSetChanged()
    }

    override fun onerror(data: String) {
        toast(data)
    }

    override fun onGeneratedToken(lastAction: String) {

    }

    override fun onResume() {
        super.onResume()
        if (isInternetConnected()) {
            presenter.get_clients("1", "10")
        }
    }

    override fun onSingleListClick(item: Any, position: Int) {

        if (intent.hasExtra("from")) {
            val builder = GsonBuilder()
            val gson = builder.create()
            val returnIntent = Intent()
            returnIntent.putExtra("result", mList[position]._id)
            returnIntent.putExtra("name", mList[position].name)
            returnIntent.putExtra("email", mList[position].email)
            returnIntent.putExtra("image", mList[position].image)
            returnIntent.putExtra("client", gson.toJson(mList[position]))
            returnIntent.putExtra(
                "address",
                mList[position].address.street + ", " + mList[position].address.city + ", " + mList[position].address.state + ", " + mList[position].address.zipcode
            )
            returnIntent.putExtra("phonenumber", mList[position].phone_no)
            setResult(RESULT_OK, returnIntent)
            finish()
        } else if (item.equals("Edit")) {
            startActivity(
                Intent(this, AddClientActivity::class.java)
                    .putExtra("title", "Edit Client").putExtra("id", mList.get(position)._id)
                    .putExtra("type", mList.get(position).type)
            )
        }
    }
}