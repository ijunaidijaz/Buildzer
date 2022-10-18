package com.tradesk.appCode.profileModule.proposalsModule

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.gson.GsonBuilder
import com.tradesk.R
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.AddProposalActivity
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.InvoicesActivity
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.PDFViewNewActivity
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.extension.AllinOneDialog
import com.tradesk.utils.extension.addWatcher
import com.tradesk.utils.extension.toast
import kotlinx.android.synthetic.main.activity_proposals.*
import javax.inject.Inject

class ProposalsActivity : BaseActivity(), IProposalsView, SingleListCLickListener {
    val mList = mutableListOf<Proposal>()
    val mProposalsAdapter by lazy { ProposalsAdapter(this, mList, mList, this) }
    var selected_position = 0
    var clicked = "0"
    var proposal_count = ""
    var mTitle = ""
    lateinit var proposalDetail: ProposalDetailModel
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

    companion object {
        lateinit var context: ProposalsActivity
    }

    @Inject
    lateinit var presenter: PropsoalsPresenter<IProposalsView>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proposals)
        presenter.onAttach(this)
        context = this
        textView6.setText(intent.getStringExtra("title"))
        mTitle = intent.getStringExtra("title").toString();
        if(mTitle.equals("proposals",true)){
            textView6.setText("Estimates")
        }
        val firstTab: TabLayout.Tab = simpleTabLayout.newTab()
        firstTab.text = "Pending"
        simpleTabLayout.addTab(firstTab)

        if (!mTitle.equals("Invoices", true)) {
            val secTab: TabLayout.Tab = simpleTabLayout.newTab()
            secTab.text = "Ongoing"
            simpleTabLayout.addTab(secTab)

        }
        if (!mTitle.equals("Invoices", true)) {
            val thirdTab: TabLayout.Tab = simpleTabLayout.newTab()
            thirdTab.text = "Complete"
            simpleTabLayout.addTab(thirdTab)
        } else {
            val thirdTab: TabLayout.Tab = simpleTabLayout.newTab()
            thirdTab.text = "Paid"
            simpleTabLayout.addTab(thirdTab)
        }
        mEtSearchName.visibility = View.VISIBLE
        mEtSearchName.addWatcher {
            mProposalsAdapter.filter.filter(it)
        }
        mIvAddProposal.visibility=View.VISIBLE
        simpleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        clicked = "0"
                        mIvAddProposal.visibility=View.VISIBLE
                        mList.clear()
                        mProposalsAdapter.notifyDataSetChanged()
                        if (intent.getStringExtra("title").equals("Proposals")) {
                            if (isInternetConnected()) {
                                if (intent.hasExtra("job_id")) {
                                    presenter.getProposals(
                                        "1",
                                        "30",
                                        "pending",
                                        "proposal",
                                        intent.getStringExtra("job_id").toString()
                                    )
                                } else {
                                    presenter.getProposals("1", "30", "pending", "proposal", "")
                                }

                            }
                        } else {
                            if (isInternetConnected()) {
                                if (intent.hasExtra("job_id")) {
                                    presenter.getProposals(
                                        "1",
                                        "30",
                                        "pending",
                                        "invoice",
                                        intent.getStringExtra("job_id").toString()
                                    )
                                } else {
                                    presenter.getProposals("1", "30", "pending", "invoice", "")
                                }
                            }
                        }
                    }
                    1 -> {
                        clicked = "1"
                        mIvAddProposal.visibility=View.GONE
                        mList.clear()
                        mProposalsAdapter.notifyDataSetChanged()
                        if (intent.getStringExtra("title").equals("Proposals")) {
                            if (isInternetConnected()) {
                                if (intent.hasExtra("job_id")) {
                                    presenter.getProposals(
                                        "1",
                                        "30",
                                        "Inprocess",
                                        "proposal",
                                        intent.getStringExtra("job_id").toString()
                                    )
                                } else {
                                    presenter.getProposals("1", "30", "Inprocess", "proposal", "")
                                }

                            }
                        } else {
                            if (isInternetConnected()) {
                                if (intent.hasExtra("job_id")) {
                                    presenter.getProposals(
                                        "1",
                                        "30",
                                        "completed",
                                        "invoice",
                                        intent.getStringExtra("job_id").toString()
                                    )
                                } else {
                                    presenter.getProposals("1", "30", "Inprocess", "invoice", "")
                                }
                            }
                        }
                    }
                    2 -> {
                        clicked = "2"
                        mIvAddProposal.visibility=View.GONE
                        mList.clear()
                        mProposalsAdapter.notifyDataSetChanged()
                        if (intent.getStringExtra("title").equals("Proposals")) {
                            if (isInternetConnected()) {
                                if (intent.hasExtra("job_id")) {
                                    presenter.getProposals(
                                        "1",
                                        "30",
                                        "completed",
                                        "proposal",
                                        intent.getStringExtra("job_id").toString()
                                    )
                                } else {
                                    presenter.getProposals("1", "30", "completed", "proposal", "")
                                }

                            }
                        } else {
                            if (isInternetConnected()) {
                                if (intent.hasExtra("job_id")) {
                                    presenter.getProposals(
                                        "1",
                                        "30",
                                        "completed",
                                        "invoice",
                                        intent.getStringExtra("job_id").toString()
                                    )
                                } else {
                                    presenter.getProposals("1", "30", "completed", "invoice", "")
                                }
                            }
                        }
                    }

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        rvProposals.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rvProposals.adapter = mProposalsAdapter

        mIvBack.setOnClickListener { finish() }
        mIvAddProposal.setOnClickListener {

            if (mList.isNotEmpty()) {
                var get_proposal_count = mList.size + 1
                if (get_proposal_count.toString().length == 1) {
                    proposal_count = "0000" + get_proposal_count.toString()
                } else if (get_proposal_count.toString().length == 1) {
                    proposal_count = "000" + get_proposal_count.toString()
                } else if (get_proposal_count.toString().length == 2) {
                    proposal_count = "00" + get_proposal_count.toString()
                } else if (get_proposal_count.toString().length == 3) {
                    proposal_count = "0" + get_proposal_count.toString()
                } else if (get_proposal_count.toString().length == 4) {
                    proposal_count = get_proposal_count.toString()
                }
            } else {
                proposal_count = "00001"
            }

            if (intent.getStringExtra("title").equals("Proposals")) {
                if (intent.hasExtra("job_id")) {
                    startActivity(
                        Intent(this, AddProposalActivity::class.java)
                            .putExtra("job_id", intent.getStringExtra("job_id"))
                            .putExtra("client_name", intent.getStringExtra("client_name"))
                            .putExtra("client_id", intent.getStringExtra("client_id"))
                            .putExtra("client_email", intent.getStringExtra("client_email"))
                            .putExtra("is_proposal", true)
                            .putExtra("count", proposal_count)
                    )
                } else {
                    startActivity(
                        Intent(this, AddProposalActivity::class.java).putExtra(
                            "count",
                            proposal_count
                        )
                    )
                }
            } else {
                if (intent.hasExtra("job_id")) {
                    startActivity(
                        Intent(this, InvoicesActivity::class.java)
                            .putExtra("job_id", intent.getStringExtra("job_id"))
                            .putExtra("client_name", intent.getStringExtra("client_name"))
                            .putExtra("client_id", intent.getStringExtra("client_id"))
                            .putExtra("client_email", intent.getStringExtra("client_email"))
                            .putExtra("count", proposal_count)
                    )
                } else {
                    startActivity(
                        Intent(this, InvoicesActivity::class.java).putExtra(
                            "count",
                            proposal_count
                        )
                    )
                }
            }


        }


//        if (intent.getStringExtra("title").equals("Proposals")){
//            if (isInternetConnected()) {
//                if (intent.hasExtra("job_id")){
//                    presenter.getProposals("1", "30", "","proposal",intent.getStringExtra("job_id").toString())
//                }else{
//                    presenter.getProposals("1", "30", "","proposal","")
//                }
//
//            }
//        }else{
//            if (isInternetConnected()) {
//                if (intent.hasExtra("job_id")){
//                    presenter.getProposals("1", "30", "","invoice",intent.getStringExtra("job_id").toString())
//                }else{
//                    presenter.getProposals("1", "30", "","invoice","")
//                }
//            }
//        }

    }

    override fun onAdd(it: AddProposalsModel) {

    }

    override fun onList(it: PorposalsListModel) {
        if (it.data.proposal_list.isNotEmpty()) {
            mList.clear()
            mList.addAll(it.data.proposal_list)
            mProposalsAdapter.notifyDataSetChanged()
        } else {
            if (intent.getStringExtra("title").equals("Proposals")) {
                toast("You have no proposal added yet.")
            } else {
                toast("You have no invoice added yet.")
            }

        }
    }

    override fun onDefaultList(it: DefaultItemsModel) {

    }

    override fun onDetails(it: ProposalDetailModel) {
        proposalDetail = it
        val builder = GsonBuilder()
        val gson = builder.create()
        var string = gson.toJson(it);
        if (intent.getStringExtra("title").equals("Proposals")) {
            if (intent.hasExtra("job_id")) {
                startActivity(
                    Intent(this, AddProposalActivity::class.java)
                        .putExtra("job_id", intent.getStringExtra("job_id"))
                        .putExtra("client_name", intent.getStringExtra("client_name"))
                        .putExtra("client_id", intent.getStringExtra("client_id"))
                        .putExtra("client_email", intent.getStringExtra("client_email"))
                        .putExtra("is_proposal", true)
                        .putExtra("is_EditMode", true)
                        .putExtra("proposalData", string)
                        .putExtra("count", proposal_count)
                )
            } else {
                startActivity(
                    Intent(this, AddProposalActivity::class.java).putExtra(
                        "count",
                        proposal_count
                    ).putExtra("proposalData", string).putExtra("is_EditMode", true)
                )
            }
        } else {
            if (intent.hasExtra("job_id")) {
                startActivity(
                    Intent(this, InvoicesActivity::class.java)
                        .putExtra("job_id", intent.getStringExtra("job_id"))
                        .putExtra("client_name", intent.getStringExtra("client_name"))
                        .putExtra("client_id", intent.getStringExtra("client_id"))
                        .putExtra("client_email", intent.getStringExtra("client_email"))
                        .putExtra("count", proposal_count)
                        .putExtra("proposalData", string)
                        .putExtra("is_EditMode", true)
                )
            } else {
                startActivity(
                    Intent(this, InvoicesActivity::class.java).putExtra(
                        "count",
                        proposal_count
                    ).putExtra("proposalData", string).putExtra("is_EditMode", true)
                )
            }
        }

    }

    override fun onSend(it: SuccessModel) {

    }

    override fun onChangeStatus(it: ChangeProposalStatus) {
        TODO("Not yet implemented")
    }

    override fun onDelete(it: SuccessModel) {
        toast("Deleted Successfully")
        mList.removeAt(selected_position)
        mProposalsAdapter.notifyDataSetChanged()
    }

    override fun onerror(data: String) {
        toast(data)
    }

    override fun onProfile(it: ProfileModel) {
        TODO("Not yet implemented")
    }

    override fun onGeneratedToken(lastAction: String) {
    }

    override fun onSingleListClick(item: Any, position: Int) {
        if (item.equals("Delete")) {
            AllinOneDialog(ttle = "Delete",
                msg = "Are you sure you want to Delete it ?",
                onLeftClick = {/*btn No click*/ },
                onRightClick = {/*btn Yes click*/
                    selected_position = position
                    if (isInternetConnected()) {
                        presenter.deleteproposal(mList.get(position)._id)
                    }
                })
        } else if (item.equals("edit")) {
            presenter.getDetail(mList.get(position)._id)

        } else {
            if (mList[position].invoice_url.isNotEmpty()) {
                startActivity(
                    Intent(this@ProposalsActivity, PDFViewNewActivity::class.java)
                        .putExtra("pdfurl", mList[position].invoice_url)
                        .putExtra("email", mList[position].client_id.email)
                        .putExtra("title", intent.getStringExtra("title"))
                        .putExtra("status", mList[position].status)
                        .putExtra("id", mList[position]._id)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (clicked.equals("0")) {
            if (intent.getStringExtra("title").equals("Proposals")) {
                if (isInternetConnected()) {
                    if (intent.hasExtra("job_id")) {
                        presenter.getProposals(
                            "1",
                            "30",
                            "pending",
                            "proposal",
                            intent.getStringExtra("job_id").toString()
                        )
                    } else {
                        presenter.getProposals("1", "30", "pending", "proposal", "")
                    }

                }
            } else {
                if (isInternetConnected()) {
                    if (intent.hasExtra("job_id")) {
                        presenter.getProposals(
                            "1",
                            "30",
                            "pending",
                            "invoice",
                            intent.getStringExtra("job_id").toString()
                        )
                    } else {
                        presenter.getProposals("1", "30", "pending", "invoice", "")
                    }
                }
            }
        } else if (clicked.equals("1")) {
            if (intent.getStringExtra("title").equals("Proposals")) {
                if (isInternetConnected()) {
                    if (intent.hasExtra("job_id")) {
                        presenter.getProposals(
                            "1",
                            "30",
                            "Inprocess",
                            "proposal",
                            intent.getStringExtra("job_id").toString()
                        )
                    } else {
                        presenter.getProposals("1", "30", "Inprocess", "proposal", "")
                    }

                }
            } else {
                if (isInternetConnected()) {
                    if (intent.hasExtra("job_id")) {
                        presenter.getProposals(
                            "1",
                            "30",
                            "Inprocess",
                            "invoice",
                            intent.getStringExtra("job_id").toString()
                        )
                    } else {
                        presenter.getProposals("1", "30", "Inprocess", "invoice", "")
                    }
                }
            }
        } else if (clicked.equals("2")) {
            if (intent.getStringExtra("title").equals("Proposals")) {
                if (isInternetConnected()) {
                    if (intent.hasExtra("job_id")) {
                        presenter.getProposals(
                            "1",
                            "30",
                            "completed",
                            "proposal",
                            intent.getStringExtra("job_id").toString()
                        )
                    } else {
                        presenter.getProposals("1", "30", "completed", "proposal", "")
                    }

                }
            } else {
                if (isInternetConnected()) {
                    if (intent.hasExtra("job_id")) {
                        presenter.getProposals(
                            "1",
                            "30",
                            "completed",
                            "invoice",
                            intent.getStringExtra("job_id").toString()
                        )
                    } else {
                        presenter.getProposals("1", "30", "completed", "invoice", "")
                    }
                }
            }
        }

    }
}