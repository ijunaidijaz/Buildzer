package com.tradesk.appCode.home.leadsDetail.leadsNotesModule.addNotesModule

import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.tradesk.R
import com.tradesk.appCode.home.leadsDetail.leadsNotesModule.ILeadNotesView
import com.tradesk.appCode.home.leadsDetail.leadsNotesModule.LeadsNotesPresenter
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.NotesListModel
import com.tradesk.data.entity.SuccessModel
import com.tradesk.utils.extension.toast
import kotlinx.android.synthetic.main.activity_add_notes.*
import javax.inject.Inject

class AddNotesActivity : BaseActivity(), ILeadNotesView {

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

    @Inject
    lateinit var presenter: LeadsNotesPresenter<ILeadNotesView>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)
        presenter.onAttach(this)
        mIvBack.setOnClickListener { finish() }
        mBtnSubmit.setOnClickListener {

            if (mEtTitle.text.toString().trim().isEmpty()) {
                toast("Enter note title", false)
            } else if (mEtDescription.text.toString().trim().isEmpty()) {
                toast("Enter note description", false)
            } else {
                if (isInternetConnected()) {
                    presenter.addleadsnotes(
                        intent.getStringExtra("id")!!,
                        mEtTitle.text.toString().trim(),
                        mEtDescription.text.toString().trim()
                    )
                }
            }
        }
    }

    override fun onNotesListSuccess(it: NotesListModel) {

    }

    override fun onSuccess(it: SuccessModel) {
        toast(it.message)
        finish()
    }

    override fun onerror(it: String) {
        toast(it)
    }

    override fun onGeneratedToken(lastAction: String) {
    }
}