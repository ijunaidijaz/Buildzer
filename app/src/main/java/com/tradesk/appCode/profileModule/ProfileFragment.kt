package com.tradesk.appCode.profileModule

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.tradesk.R
import com.tradesk.appCode.MainActivity
import com.tradesk.appCode.analyticsModule.MainAnalyticsActivity
import com.tradesk.appCode.home.customersModule.CustomersActivity
import com.tradesk.appCode.jobsModule.jobDetailModule.jobChatModule.ChatUsersActivity
import com.tradesk.appCode.profileModule.documentsModule.DocumentsActivity
import com.tradesk.appCode.profileModule.gallaryModule.GallaryActivity
import com.tradesk.appCode.profileModule.myProfileModule.MyProfileActivity
import com.tradesk.appCode.profileModule.proposalsModule.ProposalsActivity
import com.tradesk.appCode.profileModule.settingsModule.SettingsActivity
import com.tradesk.appCode.profileModule.timesheetModule.TimesheetActivity
import com.tradesk.appCode.profileModule.usersContModule.UsersContrActivity
import com.tradesk.base.BaseFragment
import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.ProfileModel
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.*
import kotlinx.android.synthetic.main.fragment_profile_copy.*
import javax.inject.Inject

class ProfileFragment : BaseFragment(), SingleListCLickListener, IProfileView {
    /////////////
    private val TRIGGER_AUTO_COMPLETE: Int = 100
    private val AUTO_COMPLETE_DELAY: Long = 500
    private var handler: Handler? = null
    var scrollY: Int = 0
    var mHomeImage = true
    var CheckVersion = true
    var scrollstart=false

    private lateinit var scroll : ScrollView

    val isPortalUser by lazy {
        mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).contains("charity").not()
    }



    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainActivity)
            mainActivity = context
    }

    //////////////
    val searchDrugData by lazy { arrayListOf<String>() }


    @Inject
    lateinit var presenter: ProfilePresenter<IProfileView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile_copy, container, false)

    override fun setUp(view: View) {
        scroll = requireActivity().findViewById(R.id.scroll)
        presenter.onAttach(this)
        if (isInternetConnected()){
            presenter.getProfile()
        }

        if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")){
            cvPay.visibility=View.VISIBLE
            mCvContacts.visibility=View.VISIBLE
            mCvDicuments.visibility=View.VISIBLE

        }else{
            cvPay.visibility=View.GONE
            mCvContacts.visibility=View.GONE
            mCvDicuments.visibility=View.GONE
        }



        mCvMyProfile.setOnClickListener { startActivity(Intent(requireActivity(), MyProfileActivity::class.java)) }
        mCvUsers.setOnClickListener { startActivity(Intent(requireActivity(), DocumentsActivity::class.java)) }
        mCvAnalytics.setOnClickListener {  startActivity(Intent(requireActivity(), MainAnalyticsActivity::class.java))  }
        cvPay.setOnClickListener {   }
        mCvDicuments.setOnClickListener { startActivity(Intent(requireActivity(), UsersContrActivity::class.java)) }
        mCvProposals.setOnClickListener { startActivity(Intent(requireActivity(), ProposalsActivity::class.java).putExtra("title","Proposals")) }
        mCvInvoices.setOnClickListener { startActivity(Intent(requireActivity(), ProposalsActivity::class.java).putExtra("title","Invoices")) }
//        cvInvoices.setOnClickListener { startActivity(Intent(requireActivity(), ProposalsActivity::class.java).putExtra("title","Invoices")) }
        clTime.setOnClickListener { startActivity(Intent(requireActivity(), TimesheetActivity::class.java)) }
        mCvContacts.setOnClickListener { startActivity(Intent(requireActivity(), CustomersActivity::class.java)) }
        mCvGallery.setOnClickListener { startActivity(Intent(requireActivity(), GallaryActivity::class.java)) }
        ivSettings.setOnClickListener { startActivity(Intent(requireActivity(), SettingsActivity::class.java)) }
        clPayments.setOnClickListener { startActivity(Intent(requireActivity(), ChatUsersActivity::class.java))}
        techspport.setOnClickListener{toast("Coming soon")}
        financing.setOnClickListener{toast("Coming soon")}
        timeSheet.setOnClickListener{toast("Coming soon")}

    }

    override fun onDrugSearched(it: ProfileModel) {
        if (it.data.image.isNotEmpty()){
            imageView334.loadWallImage(it.data.image)
        }
        if (it.data.company_name.isEmpty()){
            mTvType.setText("")
        }else{
            mTvType.setText(it.data.company_name)
        }
//        if (it.data.addtional_info.trade.isEmpty()) {
//            mTvType.setText("General Contractor")
//        }else{
//            mTvType.setText(it.data.addtional_info.trade)
//        }
        mTvName.setText(it.data.name)
        mTvEmail.setText(it.data.email)

        if (it.data.phone_no.isEmpty()){
            mTvPhone.setText("N/A")
        }else{

            mTvPhone.setText(insertString(it.data.phone_no,"",0))
            mTvPhone.setText(insertString(mTvPhone.text.toString(),")",2))
            mTvPhone.setText(insertString(mTvPhone.text.toString()," ",3))
            mTvPhone.setText(insertString(mTvPhone.text.toString(),"-",7))
            mTvPhone.setText("+1 ("+mTvPhone.text.toString())

        }


    }


    override fun onGeneratedToken(lastAction: String) {

    }

    override fun enableButton() {

    }

    override fun disableButton() {

    }

    override fun onSingleListClick(item: Any, position: Int) {

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
        if (isInternetConnected()){
            presenter.getProfile()
        }
    }


}