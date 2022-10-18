package com.tradesk.appCode.profileModule.usersContModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.appCode.home.leadsDetail.CustomerDetailActivity
import com.tradesk.appCode.home.salePersonModule.AddSalesPersonActivity
import com.tradesk.appCode.home.salePersonModule.AddSalesPresenter
import com.tradesk.appCode.home.salePersonModule.IAddSalesView
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.extension.addWatcher
import com.google.android.material.tabs.TabLayout
import com.tradesk.utils.ListerReverser
import kotlinx.android.synthetic.main.activity_userscontract.*
import java.util.ArrayList
import javax.inject.Inject

class UsersContrActivity : BaseActivity(), IAddSalesView, SingleListCLickListener {

    var tab_click = "All"
    var mList = mutableListOf<Client>()
    var mListTrade = mutableListOf<DataTradesOld>()
    val mListTradeOld = mutableListOf<DataTradesOld>()
    val mUsersContractAdapter by lazy { UsersContractAdapter(this, mList, mList,this) }
    val mUsersContractTradeAdapter by lazy { UsersContractTradeAdapter(this, mListTradeOld, this) }

    companion object{
        lateinit var context: UsersContrActivity
    }

    @Inject
    lateinit var presenter: AddSalesPresenter<IAddSalesView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userscontract)
        presenter.onAttach(this)
        context=this
        val zeroTab: TabLayout.Tab = simpleTabLayout.newTab()
        zeroTab.text = "All"
        simpleTabLayout.addTab(zeroTab)


        val firstTab: TabLayout.Tab = simpleTabLayout.newTab()
        firstTab.text = "Admins"
        simpleTabLayout.addTab(firstTab)


        val secTab: TabLayout.Tab = simpleTabLayout.newTab()
        secTab.text = "Managers"
        simpleTabLayout.addTab(secTab)


        val thirdTab: TabLayout.Tab = simpleTabLayout.newTab()
        thirdTab.text = "Employees"
        simpleTabLayout.addTab(thirdTab)

        rvUsersContract.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rvUsersContract.adapter = mUsersContractAdapter


        rvUsersTrades.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        rvUsersTrades.adapter = mUsersContractTradeAdapter


        if (isInternetConnected()) {
            presenter.getTradeDetails("sales", "1", "50")
        }



        if (isInternetConnected()) {
            presenter.get_all_sales("1", " 20", "")
        }


        mEtSearchName.addWatcher {
             mUsersContractAdapter.filter.filter(it)
//
        }

        /* simpleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
             override fun onTabSelected(tab: TabLayout.Tab?) {
                 when (tab?.position) {
                     0 -> {
                         tab_click=0
                         mList.clear()
                         mUsersContractAdapter.notifyDataSetChanged()
                         if (isInternetConnected()) {
                             presenter.get_all_sales("1", " 20","")
                         }
                     }
                     1 -> {
                         tab_click=1
                         mList.clear()
                         mUsersContractAdapter.notifyDataSetChanged()
                         if (isInternetConnected()) {
                             presenter.get_all_sales("1", " 20","admin")
                         }
                     }
                     2 -> {
                         tab_click=2
                         mList.clear()
                         mUsersContractAdapter.notifyDataSetChanged()
                         if (isInternetConnected()) {
                             presenter.get_all_sales("1", " 20","manager")
                         }
                     }
                     3 -> {
                         tab_click=3
                         mList.clear()
                         mUsersContractAdapter.notifyDataSetChanged()
                         if (isInternetConnected()) {
                             presenter.get_all_sales("1", " 20","employee")
                         }
                     }
                 }
             }

             override fun onTabUnselected(tab: TabLayout.Tab?) {
             }

             override fun onTabReselected(tab: TabLayout.Tab?) {
             }
         })*/

        mIvBack.setOnClickListener { finish() }
        mIvAddUsers.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddSalesPersonActivity::class.java
                )
            )
        }
        mIvAdd.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddSalesPersonActivity::class.java
                ).putExtra("addUser",true)
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
        mListTrade.clear()
        mListTradeOld.clear()
        tab_click = "All"
        mListTradeOld.add(DataTradesOld("All", true))
        for (i in 0 until it.data.tradeData.size) {
            mListTrade.add(DataTradesOld(it.data.tradeData[i], false))
        }

        mListTradeOld.addAll(mListTrade.groupBy { it.name }.entries.map { it.value[0] })
        mUsersContractTradeAdapter.notifyDataSetChanged()
    }

    override fun onAddJobSales(it: SuccessModel) {

    }

    override fun onAddSubUsers(it: SuccessModel) {

    }

    override fun onSalesList(it: ClientsListModel) {
        mList.clear()
        mUsersContractAdapter.notifyDataSetChanged()
        mList.addAll(it.data.client)
        ListerReverser.reverse(mList as ArrayList<Client>?)
        mUsersContractAdapter.notifyDataSetChanged()
    }

    override fun onerror(data: String) {

    }

    override fun onGeneratedToken(lastAction: String) {

    }

    override fun onResume() {
        super.onResume()
        if (tab_click.equals("All")) {
            if (isInternetConnected()) {
                presenter.get_all_sales("1", " 20", "")
            }
        } else {
            if (isInternetConnected()) {
                presenter.get_all_sales("1", " 20", tab_click.toString().lowercase())
            }
        }
    }

    override fun onSingleListClick(item: Any, position: Int) {
        if (item.equals("Edit")) {
            startActivity(
                Intent(this, AddSalesPersonActivity::class.java)
                    .putExtra("id", mList.get(position)._id)
                    .putExtra("type", mList.get(position).type)
            )
        }else  if (item.equals("main")) {
            startActivity(
                Intent(this, CustomerDetailActivity::class.java)
                    .putExtra("id", mList[position]._id)
                    .putExtra("name", mList[position].name)
                    .putExtra("email", mList[position].email)
                    .putExtra("phone", mList[position].phone_no)
                    .putExtra("trade", mList[position].trade)
                    .putExtra(
                        "address", if (mList[position].address.street.isNotEmpty()) {
                            mList[position].address.street + ", " + mList[position].address.city + ", " + mList[position].address.state + ", " + mList[position].address.zipcode
                        } else {
                            mList[position].address.street + ", " + mList[position].address.city + ", " + mList[position].address.state + ", " + mList[position].address.zipcode
                        }
                    )
                    .putExtra("image", mList[position].image)
                    .putExtra("trade", mList[position].trade)

            )
        }  else if (item.equals("Click")) {
            if (isInternetConnected()) {
                tab_click = mListTradeOld[position].name
                if (mListTradeOld[position].name.equals("All")) {
                    presenter.get_all_sales("1", " 20", "")
                } else {
                    presenter.get_all_sales(
                        "1",
                        " 20",
                        mListTradeOld[position].name.toString().toLowerCase()
                    )
                }

            }
        }
    }
        fun setUsers(clientsListModel: ClientsListModel){
            mList.clear()
            mList.addAll(clientsListModel.data.client)
            mUsersContractAdapter.notifyDataSetChanged()
        }
}