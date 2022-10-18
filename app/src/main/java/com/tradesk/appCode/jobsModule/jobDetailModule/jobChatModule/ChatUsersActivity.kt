package com.tradesk.appCode.jobsModule.jobDetailModule.jobChatModule

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.addWatcher
import com.tradesk.utils.extension.makeEmpty
import kotlinx.android.synthetic.main.activity_chat_users.*
import javax.inject.Inject

class ChatUsersActivity : BaseActivity(), IChatView {
    @Inject
    lateinit var presenter: ChatPresenter<IChatView>

    var chatUserListFilter = arrayListOf<LeadsData>()
    var chatUserList = arrayListOf<LeadsData>()
    val mChatUserAdapter by lazy {
        ChatUserAdapter(
            this,
            mPrefs.getKeyValue(PreferenceConstants.USER_TYPE),
            chatUserListFilter
        )
    }
    var loadChat = true
    var page = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_users)
        presenter.onAttach(this)
        setAdapter()
        if (isInternetConnected()) {
            presenter.getChatUsers(page.toString())
        }
        mIvBack.setOnClickListener { onBackPressed() }
        mIvSearch.setOnClickListener {
            edtSearch.isVisible = edtSearch.isVisible.not()
            edtSearch.makeEmpty()
        }
        edtSearch.addWatcher { str ->
            val leadsChat = arrayListOf<LeadsData>()
            if (str.isNotEmpty()) {
                val filter = chatUserList.filter { it.project_name.contains(str, true) }
                leadsChat.addAll(filter)
            } else leadsChat.addAll(chatUserList)
            chatUserListFilter.clear()
            chatUserListFilter.addAll(leadsChat)
            mChatUserAdapter.notifyDataSetChanged()
        }
    }

    private fun setAdapter() {
        rvChatting.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvChatting.itemAnimator = null
        rvChatting.adapter = mChatUserAdapter
        rvChatting.addOnScrollListener(listener)
    }

    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            (rvChatting.layoutManager as LinearLayoutManager).also {
                if (loadChat && it.findLastCompletelyVisibleItemPosition() == chatUserList.size - 1) {
                    page = page++
                    presenter.getChatUsers(page.toString())
                }
            }
        }
    }

    override fun onChatUsersList(it: LeadsModel) {
        edtSearch.makeEmpty()
        edtSearch.isVisible = false
        loadChat = it.data.totalPages > page
        chatUserList.addAll(it.data.leadsData)
        chatUserListFilter.clear()
        chatUserListFilter.addAll(chatUserList)
        mChatUserAdapter.notifyDataSetChanged()
    }


    override fun onAppVersionResp(it: AppVersionEntity) = Unit
    override fun onAddMsgResp(it: AddConversationModel) = Unit
    override fun onGetChatResp(data: GetConversationData) = Unit
    override fun onGetRecentChatResp(data: GetConversationData) = Unit
    override fun onGeneratedToken(lastAction: String) = Unit
}