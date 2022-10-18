package com.tradesk.appCode.jobsModule.jobDetailModule.jobChatModule

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.appCode.loginModule.LoginActivity
import com.tradesk.data.entity.*
import com.tradesk.data.preferences.PreferenceHelper
import com.tradesk.filemanager.openApp
import com.tradesk.utils.*
import com.tradesk.utils.extension.makeEmpty
import com.tradesk.utils.extension.toast
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class ChatActivity : AppCompatActivity(), IChatView {
    private var mProgressDialog: ProgressDialog? = null

    @Inject
    lateinit var mPrefs: PreferenceHelper

    @Inject
    lateinit var permissionFile: PermissionFile

    @Inject
    lateinit var mAppUtils: AppUtils

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    @Inject
    lateinit var mImageUtility: ImageUtility

    companion object {
        var activeJobId = ""
    }

    //    val mList = mutableListOf<DataShelters>()
    val job_id by lazy { intent.getStringExtra("job_id") ?: "" }
    val receiver_id by lazy { intent.getStringExtra("receiver_id") ?: "" }
    val sales_id by lazy { intent.getStringExtra("sales_id") ?: "" }
    val job_title by lazy { intent.getStringExtra("job_title") ?: "" }
    var page = 1
    var loadChat = true
    val chatList = arrayListOf<AddConversationData>()
    val mChattingAdapter by lazy {
        ChattingAdapter(
            this,
            mPrefs.getKeyValue(PreferenceConstants.USER_ID),
            chatList
        ) { type, pos ->
            if (type == "hide") hideKeyboard(window.decorView.rootView)
            else {
                if (chatList[pos].message.image.isNotEmpty()) openApp(
                    this@ChatActivity,
                    chatList[pos].message.image
                )
            }
        }
    }

    val handler by lazy { Handler() }
    val runnable by lazy {
        {
            getRecentchat()
        }
    }

    @Inject
    lateinit var presenter: ChatPresenter<IChatView>

    val options by lazy {
        Options.init()
            .setRequestCode(1010) //Request code for activity results
            .setCount(3) //Number of images to restict selection count
            .setFrontfacing(false) //Front Facing camera on start
            .setPreSelectedUrls(arrayListOf()) //Pre selected Image Urls
            .setSpanCount(4) //Span count for gallery min 1 & max 5
            .setMode(Options.Mode.All) //Option to select only pictures or videos or both
            .setVideoDurationLimitinSeconds(30) //Duration for video recording
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT) //Orientaion
            .setPath("/pix/images") //Custom Path For media Storage

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        performDI()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        presenter.onAttach(this)

        activeJobId = job_id
        textView6.text = job_title
        getchat()
        setAdapter()
        mIvBack.setOnClickListener { finish() }
        imageView45.setOnClickListener {
            Pix.start(this@ChatActivity, options)
        }
        imageView50.setOnClickListener {
            if (textView42.text.trim().isNotEmpty()) {
                addMsg(textView42.text.trim().toString())
            }
        }
    }

    private fun setAdapter() {

        rvChatting.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvChatting.itemAnimator = null
        rvChatting.adapter = mChattingAdapter
        rvChatting.addOnScrollListener(listener)

    }

    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            (rvChatting.layoutManager as LinearLayoutManager).also {
                if (loadChat && it.findLastCompletelyVisibleItemPosition() == chatList.size - 1) {
                    page++
                    getchat()
                }
            }
        }
    }

    fun isInternetConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnectedOrConnecting)
            return true
        else {
            Snackbar.make(findViewById<View>(android.R.id.content), "You are Offline!", 2000).show()
            return false
        }
    }


    fun getchat() {
//        sales_user_id=62384fef62e6e74b135e7641&job_id=623c60d216c682c148d58951&limit=10&page=1
        if (isInternetConnected()) {
            hashMapOf<String, String>().also {
                it.put("sales_user_id", sales_id)
                it.put("job_id", job_id)
                it.put("limit", "20")
                it.put("page", page.toString())
                presenter.getChat(it)
            }
        }
    }

    fun getRecentchat() {
//        sales_user_id=62384fef62e6e74b135e7641&job_id=623c60d216c682c148d58951&limit=10&page=1
        if (isInternetConnected() && chatList.isNotEmpty()) {
            hashMapOf<String, String>().also {
                it.put("sales_user_id", sales_id)
                it.put("job_id", job_id)
                it.put("limit", "20")
                it.put("page", "1")
                it.put("dateTime", chatList[chatList.size - 1].createdAt)
                presenter.getChat(it)
            }
        }
    }

    fun addMsg(msg: String) {
        if (isInternetConnected()) {
            val map = hashMapOf<String, String>().also {
                it.put("sales_user_id", sales_id)
                it.put("job_id", job_id)
                it.put("receiver_id", receiver_id)
                it.put("sender_id", mPrefs.getKeyValue(PreferenceConstants.USER_ID))
                it.put("message", msg)
//            it.put("image","")
            }
            presenter.addMsg(map)
            textView42.makeEmpty()
        }
    }

    override fun onAddMsgResp(it: AddConversationModel) {
        textView42.makeEmpty()
        chatList.add(it.data)
        mChattingAdapter.notifyDataSetChanged()
        if (chatList.isNotEmpty())
            rvChatting.scrollToPosition(chatList.size - 1)
    }

    override fun onGetChatResp(data: GetConversationData) {
        loadChat = page < data.totalPages
        val filter = data.conversation.filter { chatList.map { it._id }.contains(it._id).not() }
        chatList.addAll(filter)
        chatList.sortBy { it.createdAt }
        mChattingAdapter.notifyDataSetChanged()
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 10 * 1000)
    }

    override fun onGetRecentChatResp(data: GetConversationData) {
        val filter = data.conversation.filter { chatList.map { it._id }.contains(it._id).not() }
        chatList.addAll(filter)
        chatList.sortBy { it.createdAt }
        mChattingAdapter.notifyDataSetChanged()
        val first = (rvChatting.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (first <= 11 && filter.isNotEmpty()) {
            rvChatting.removeOnScrollListener(listener)
            rvChatting.scrollToPosition(chatList.size - 1)
            rvChatting.addOnScrollListener(listener)
        }
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 10 * 1000)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1010) {
            data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.let {
                if (it.isNotEmpty()) saveCaptureImageResults(it[0])
            }
            Log.e("RESULT", "RESULT")
//                saveCaptureImageResults()
        }
    }

    private fun saveCaptureImageResults(path: String) = try {
        val file = File(path)
        val mFile = Compressor(this@ChatActivity)
            .setMaxHeight(4000).setMaxWidth(4000)
            .setQuality(60)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .compressToFile(file)
        val map = hashMapOf<String, RequestBody>().also {
            it.put(
                "sales_user_id", RequestBody.create(
                    MediaType.parse("multipart/form-data"), sales_id
                )
            )
            it.put(
                "job_id", RequestBody.create(
                    MediaType.parse("multipart/form-data"), job_id
                )
            )
            it.put(
                "receiver_id", RequestBody.create(
                    MediaType.parse("multipart/form-data"), receiver_id
                )
            )
            it.put(
                "sender_id", RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    mPrefs.getKeyValue(PreferenceConstants.USER_ID)
                )
            )
            it.put(
                "image\"; filename=\"image.jpg",
                RequestBody.create(MediaType.parse("image/*"), mFile!!)
            )
            presenter.addImage(it)
        }
    } catch (e: Exception) {
    }

    override fun onAppVersionResp(it: AppVersionEntity) {

    }

    override fun onGeneratedToken(lastAction: String) {
    }

    override fun showLoading() {
        hideLoading()
        mProgressDialog = CommonUtil.initProgressDialog(this)

    }

    override fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.cancel()
        }
    }

    override fun openActivityOnTokenExpire() {

    }


    override fun onError(message: String) {
        toast(message, false)
    }

    override fun setCustomDialog(isSuccess: Boolean, message: String, btn: String) {

    }


    fun hideAllTypeKB(ibinder: IBinder?) {
        val imm = getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.hideSoftInputFromWindow(ibinder!!, 0)
    }

    override fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    fun hideKeyboardDialog(view: View? = null) {
        try {
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (view != null)
                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun enableButton() {

    }

    override fun disableButton() {

    }


    override fun onDestroy() {
        hideLoading()
        super.onDestroy()
        activeJobId = ""
        handler.removeCallbacks(runnable)
    }

    override fun onLogout(message: String) {
        mAppUtils.showToast("Autherization failed")
        mPrefs.logoutUser()
        Intent(this, LoginActivity::class.java).putExtra(
            "logout",
            "true"
        ).apply {
            startActivity(this)
        }
        finishAffinity()
    }


    override fun onError(resId: Int) {
        onError(getString(resId))
//        show404Error()
    }

    override fun showErrorMessage(message: String) {
        toast(message, false)
    }

    override fun showMessage(message: String) {
        if (true)
            toast(message, true)
        else
            toast("getString(R.string.err)", false)
    }

    override fun showCustomMessage(message: String) {
        CommonUtil.showNoInternet(this, findViewById<View>(android.R.id.content), message)
    }

    override fun showMessage(resId: Int) {
        showMessage(getString(resId))
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    private fun performDI() = AndroidInjection.inject(this)

    override fun onChatUsersList(it: LeadsModel) = Unit
}