package com.tradesk.appCode.notificationsModule

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.appCode.MainActivity
import com.tradesk.appCode.home.leadsDetail.LeadsActivity
import com.tradesk.appCode.profileModule.settingsModule.SettingsActivity
import com.tradesk.base.BaseFragment
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.*
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_notifications.*

class NotificationFragment : BaseFragment(), SingleListCLickListener, INotificationView {
    /////////////
    private val TRIGGER_AUTO_COMPLETE: Int = 100
    private val AUTO_COMPLETE_DELAY: Long = 500
    private var handler: Handler? = null

    var mHomeImage = true
    var CheckVersion = true
    val isPortalUser by lazy {
        mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).contains("charity").not()
    }


    val mList = mutableListOf<NotificationDetail>()
    val mHomeLeadsAdapter by lazy { NotificationsAdapter(requireActivity(),this,mList) }

    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainActivity)
            mainActivity = context
    }

    //////////////
    val searchDrugData by lazy { arrayListOf<String>() }


    @Inject
    lateinit var presenter: NotificationsPresenter<INotificationView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_notifications, container, false)

    override fun setUp(view: View) {
        presenter.onAttach(this)
        rvNotifications.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        rvNotifications.adapter = mHomeLeadsAdapter

        if (isInternetConnected()){
            presenter.notifications("1","20","")
        }
        ivSettings.setOnClickListener {  startActivity(Intent(activity, SettingsActivity::class.java)) }

    }

    override fun onDrugSearched(it: NotifiactionsModel) {
        mList.clear()
        mList.addAll(it.data.notification_details)
        mHomeLeadsAdapter.notifyDataSetChanged()

    }


    override fun onGeneratedToken(lastAction: String) {

    }

    override fun enableButton() {

    }

    override fun disableButton() {

    }

    override fun onSingleListClick(item: Any, position: Int) {
        if (mList[position].type.equals("lead")){
            requireActivity().startActivity(Intent(requireActivity(), LeadsActivity::class.java).putExtra("id",mList[position].refId))
        }else if (mList[position].type.equals("reminder")){
            if (isInternetConnected()){
                presenter.reminderdetail(mList[position].refId)
            }
        }

    }

    override fun onAppVersionResp(it: AppVersionEntity) {
//        it.data.version="2"
        CheckVersion = false
        try {
            if (checkVersionCode() < it.data.version.toDouble() && checkVersionCode() > 0) {
                AllinOneDialog(getString(R.string.app_name),
                    "New version of givebackRx is available on store. Please update your app with latest version.",
                    btnLeft = "Cancel",
                    btnRight = "Update",
                    onLeftClick = {},
                    onRightClick = {
                        if (isInternetConnected()) {
                            val i = Intent(android.content.Intent.ACTION_VIEW);
                            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.buildzer"));
                            startActivity(i);
                        }
                    })
            }
        } catch (e: Exception) {

        }
    }

    override fun onReminderDetail(it: ReminderDetailNewModel) {
        AllinOneDialogWithThreeBtn(getString(R.string.app_name),
            "Description - "+it.data.reminder_details.description+"\n\n"+"Date/Time - "+it.data.reminder_details.dateTime,
            btnLeft = "",
            btnRight = "",
            btnBottom = "Ok",
            onLeftClick = {},
            onRightClick = {},
            onBottomClick = {}
        )
    }

    private fun checkVersionCode(): Float {
        try {
            val pInfo = requireActivity().getPackageManager()
                .getPackageInfo(requireActivity().packageName, 0)
            return pInfo.versionCode.toFloat()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0.0f
    }

    override fun onResume() {
        super.onResume()

    }


}