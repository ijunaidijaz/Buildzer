package com.tradesk.appCode.home.leadsDetail.leadsNotesModule

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.appCode.home.leadsDetail.leadsNotesModule.addNotesModule.AddNotesActivity
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.Note
import com.tradesk.data.entity.NotesListModel
import com.tradesk.data.entity.SuccessModel
import com.tradesk.listeners.CustomCheckBoxListener
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.utils.extension.AllinOneDialog
import com.tradesk.utils.extension.addWatcher
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_lead_notes.*
import javax.inject.Inject

class LeadNotesActivity : BaseActivity(), SingleItemCLickListener, CustomCheckBoxListener,
    ILeadNotesView {

    val mLeadsNotesAdapter by lazy { LeadsNotesAdapter(this, this, mList) }
    val mLeadsTasksAdapter by lazy { LeadsTasksAdapter(this, this, this) }

    @Inject
    lateinit var presenter: LeadsNotesPresenter<ILeadNotesView>

    val mList = mutableListOf<Note>()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead_notes)
        presenter.onAttach(this)
        val firstTab: TabLayout.Tab = simpleTabLayout.newTab()
        firstTab.text = "Notes"
        simpleTabLayout.addTab(firstTab)

        etSearch.addWatcher {
            mLeadsNotesAdapter.filter.filter(it)
        }

        val secTab: TabLayout.Tab = simpleTabLayout.newTab()
        secTab.text = "Tasks"
        simpleTabLayout.addTab(secTab)

        rvNotesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rvNotesList.adapter = mLeadsNotesAdapter


        simpleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        rvNotesList.adapter = mLeadsNotesAdapter
                    }
                    1 -> {
                        rvNotesList.adapter = mLeadsTasksAdapter
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        if (isInternetConnected()) {
            presenter.leadsnoteslist(intent.getStringExtra("id").toString())
        }

        mIvBack.setOnClickListener { finish() }
        mIvAddNotes.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddNotesActivity::class.java
                ).putExtra("id", intent.getStringExtra("id").toString())
            )
        }
        mIvAdd.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddNotesActivity::class.java
                ).putExtra("id", intent.getStringExtra("id").toString())
            )
        }
    }

    override fun onNotesListSuccess(it: NotesListModel) {
        mList.clear()
        mList.addAll(it.data.notes)
        mLeadsNotesAdapter.notifyDataSetChanged()
    }

    override fun onSuccess(it: SuccessModel) {

    }

    override fun onerror(it: String) {

    }

    override fun onGeneratedToken(lastAction: String) {

    }

    override fun onSingleItemClick(item: Any, position: Int) {

    }

    override fun onCheckBoxClick(item: Int) {
        AllinOneDialog(getString(R.string.app_name),
            "Do you want to complete this task ?",
            btnLeft = "No",
            btnRight = "Yes",
            onLeftClick = {},
            onRightClick = {})
    }

    override fun onResume() {
        super.onResume()
        if (isInternetConnected()) {
            presenter.leadsnoteslist(intent.getStringExtra("id").toString())
        }
    }
}