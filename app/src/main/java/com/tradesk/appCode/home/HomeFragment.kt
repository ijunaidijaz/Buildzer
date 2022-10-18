package com.tradesk.appCode.home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.appCode.MainActivity
import com.tradesk.appCode.home.addLeadsModule.AddLeadActivity
import com.tradesk.appCode.home.leadsDetail.LeadsActivity
import com.tradesk.appCode.profileModule.settingsModule.SettingsActivity
import com.tradesk.base.BaseFragment
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.*
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_lead_notes.*
import kotlinx.android.synthetic.main.activity_leads.*
import kotlinx.android.synthetic.main.activity_userscontract.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.mEtSearchName
import kotlinx.android.synthetic.main.fragment_home.mIvAdd
import kotlinx.android.synthetic.main.fragment_home.simpleTabLayout
import java.util.*
import javax.inject.Inject


class HomeFragment : BaseFragment(), SingleListCLickListener, IHomeView, SingleItemCLickListener {
    /////////////
    private val TRIGGER_AUTO_COMPLETE: Int = 100
    private val AUTO_COMPLETE_DELAY: Long = 500
    private var handler: Handler? = null

    var mHomeImage = true
    var CheckVersion = true
    var clicked = ""
    val isPortalUser by lazy {
        mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).contains("charity").not()
    }


    val mList = mutableListOf<LeadsData>()
    val mHomeLeadsAdapter by lazy { HomeLeadsAdapter(requireActivity(), this, mList,mList) }

    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainActivity)
            mainActivity = context
    }

    //////////////
    val searchDrugData by lazy { arrayListOf<String>() }

    @Inject
    lateinit var presenter: HomePresenter<IHomeView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setUp(view: View) {
        presenter.onAttach(this)

        if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")) {
            mIvAdd.visibility = View.VISIBLE
        } else {
            mIvAdd.visibility = View.GONE
        }

        val firstTab: TabLayout.Tab = simpleTabLayout.newTab()
        firstTab.text = "All Leads"
        simpleTabLayout.addTab(firstTab)


        val secTab: TabLayout.Tab = simpleTabLayout.newTab()
        secTab.text = "Pending"
        simpleTabLayout.addTab(secTab)


        val thirdTab: TabLayout.Tab = simpleTabLayout.newTab()
        thirdTab.text = "Follow Up"
        simpleTabLayout.addTab(thirdTab)


        val forthTab: TabLayout.Tab = simpleTabLayout.newTab()
        forthTab.text = "Sale"
        simpleTabLayout.addTab(forthTab)


        val decodedBytes = Base64.getDecoder().decode(
            "UGVybWl0cyAmIEluc3BlY3Rpb25zIApDb21wYW55IHRvIG9idGFpbiBuZWNlc3NhcnkgY2l0eSBwZXJtaXRzIGFzIHdlbGwgYXMgc2NoZWR1bGUgYWxsIGNpdHkgaW5zcGVjdGlvbnMgcmVxdWlyZWQgZm9yIGJhdGhyb29tIHJlbW9kZWw7CsKgCsKgCuKXjyBBcmNoaXRlY3R1cmFsIGFuZCBlbmdpbmVlcmluZyBwbGFucyAoQmx1ZXByaW50cyk6IApbT3B0aW9uYWxdIENvbXBhbnkgd2lsbCBwcmVwYXJlIGFuZCBwcm92aWRlIHRoZSBjdXN0b21lciB3aXRoIGFyY2hpdGVjdHVyYWwgYW5kIGVuZ2luZWVyaW5nIHBsYW5zIChCbHVlcHJpbnRzKSBmb3IgYSBsb2FkIGJlYXJpbmcgd2FsbCByZW1vdmFsIHRvIGJlIHN1Ym1pdHRlZCB0byB0aGUgY2l0eSB0byBvYnRhaW4gYSBwZXJtaXQgKENvbXBhbnkgZ3VhcmFudGVlIG9idGFpbmluZyBwZXJtaXRzKS4gQW55IGNvcnJlY3Rpb25zIG5lZWRlZCB3aWxsIG5vdCBjYXJyeSBhZGRpdGlvbmFsIGNoYXJnZS4gCsKgCsKgCsKgCsKgCkRlbW9saXRpb24gCkRlbW9saXNoIGFuZCByZW1vdmUgZXhpc3RpbmcgY2FiaW5ldHMsIGNvdW50ZXJ0b3BzLCBmbG9vciwgZml4dHVyZXMsIGV0Yy4gClNhbHZhZ2UgdGhlIGZvbGxvd2luZyBpdGVtcwrCoArCoAril48gbW9sZC9hc2Jlc3Rvcy9MZWFkIHRlc3QKY29tcGFueSBpcyByZWNvbW1lbmRhdGlvbjogCmN1c3RvbWVyIHRvIGhpcmVkIGEgM3JkIHBhcnR5IGxpY2Vuc2UgY29tcGFueSB0byBwcm92aWRlIG1vbGQgdGVzdCAKaWYgaG91c2UgYnVpbHQgYmVmb3JlIDE5NzggcmVxdWlyZWQgYnkgRVBBIGxhdzoKY3VzdG9tZXIgdG8gaGlyZWQgYSAzcmQgcGFydHkgbGljZW5zZSBjb21wYW55IHRvIHByb3ZpZGUgQXNiZXN0b3MgdGVzdCAKY3VzdG9tZXIgdG8gaGlyZWQgYSAzcmQgcGFydHkgbGljZW5zZSBjb21wYW55IHRvIHByb3ZpZGUgTEVBRCB0ZXN0IArCoArCoEZyYW1pbmcgCldhbGstaW4gc2hvd2VyIGZyYW1pbmc6IFtTdGFuZGFyZCA24oCdIGN1cmJlZCAvIFN0ZXBsZXNzXQpTaGFtcG9vIG5pY2hlOiBbU2VsZWN0OiBTdGFuZGFydCBiZXR3ZWVuIHN0dWRzIC8gQ3VzdG9tIHNpemVdIApTaG93ZXIgQmVuY2g6IFtTZWxlY3Q6IEZsb2F0aW5nIC8gTW91bnRlZCA7IFNlbGVjdDogQ29ybmVyIC8gUmVjdGFuZ3VsYXJdIApUdWIgZnJhbWluZzogW1NlbGVjdDogUmVndWxhciB0dWIgLyBGcmVlc3RhbmRpbmcgdHViIC8gRHJvcC1pbiB0dWIgZnJhbWVdCkFkZGl0aW9uYWwgZnJhbWluZyBpdGVtczogW1NlbGVjdDogUGV0aXRpb24gd2FsbHMgLyBQb255IHdhbGxzIC8gRG9vcnMgLyBXaW5kb3dzIC8gU29mZml0XSAKQ2FuY2VsYXRpb24gb2YgZXhpc3RpbmcgZnJhbWluZzogW1NlbGVjdDogU29mZml0cyAvIFdhbGxzIChwZXRpdGlvbi9sb2FkIGJlYXJpbmcpIC8gT3BlbmluZ3MgLyBXaW5kb3dzIC8gU29mZml0IC8gUmVjZXNzZWQgY2FiaW5ldF0uIArCoMKgwqAKUm91Z2ggZWxlY3RyaWNhbCAKVXBncmFkZSBhbmQgcHJlcGFyZSBlbGVjdHJpY2l0eSB1cCB0byBjb2RlIChOb3QgaW5jbHVkZXMgcmV3aXJlIC8gcGFuZWwgYm94IHVwZ3JhZGUpLiBDb21wYW55IHdpbGwgcHJvdmlkZSBhbmQgaW5zdGFsbCB0aGUgZm9sbG93aW5nIGl0ZW1zOiAKR0ZDSSByZWNlcHRhY2xlcyBbUXVhbnRpdHk6IF9dClN3aXRjaGVzIFtTZWxlY3Q6IHJlZ3VsYXIgLyBkaW1tZXIgOyBRdWFudGl0eTogX10KTEVEIHJlY2Vzc2VkIGxpZ2h0cyBbU2VsZWN0OiBTaXplIDTigJ0gLyA24oCdIC8gOOKAnSA7IFNlbGVjdDogQ29sZCAvIE5vcm1hbCAvIFdhcm0gV2hpdGUgOyBRdWFudGl0eTogX10KQWRkaXRpb25hbCBsaWdodCBwb3dlciBzcG90IFtRdWFudGl0eTogX107IApFeGhhdXN0IGZhbiAtIENvbXBhbnkgc2VsZWN0aW9uIFtRdWFudGl0eTogX107CkFkZGl0aW9uYWwgcm91Z2ggZWxlY3RyaWNhbCBpdGVtcyAoaS5nLiBVU0Igb3V0bGV0cywgRWxlY3RyaWNhbCBUb2lsZXQsIGV0Yy4pOgrCoApSb3VnaCBwbHVtYmluZyAKVXBncmFkZSBhbmQgcHJlcGFyZSBleGlzdGluZyBiYXRocm9vbSByb3VnaCBwbHVtYmluZyB1cCB0byBjb2RlIChyZXBsYWNlIOKAnG91dCBvZiB0aGUgd2FsbOKAnSBwaXBlcyB0byBuZXcgY29wcGVyL1BFWCBwaXBpbmcuIE5vdCBpbmNsdWRpbmcgcmUtcGlwZSAvIHJlLWRyYWluIC8gbmV3IGRyYWluKToKSW5zdGFsbCBTaG93ZXIgLyBUdWIgdmFsdmVzIHN5c3RlbSBbU2VsZWN0OiBTaW5nbGUgdmFsdmUgLyBEdWFsIHZhbHZlIC8gT3RoZXI6X10gCkluc3RhbGwgVHViIFtTZWxlY3Q6IHJlZ3VsYXIgdHViIC8gZnJlZXN0YW5kaW5nIHR1YiAvIGRyb3AtaW4gdHViXSAtIEN1c3RvbWVyIHByb3ZpZGVzCkluc3RhbGwgc2hvd2VyIGRyYWluIHN5c3RlbSBbU2VsZWN0OiBDZW50ZXJlZCBkcmFpbiAvIFR1YiBkcmFpbi8g4oCcaW5maW5pdHnigJ0gZHJhaW5dOyAKSW5zdGFsbCBiYXRocm9vbSB2YW5pdHkgcm91Z2ggcGx1bWJpbmcgW1NlbGVjdDogU2luZ2xlIHZhbml0eSAvIERvdWJsZSB2YW5pdHkgLyBPdGhlcjpfXTsKQWRkaXRpb25hbCByb3VnaCBwbHVtYmluZyBpdGVtcyAoaWYgYW55KSAKwqAKwqAKV2F0ZXJwcm9vZmluZyAmIHRpbGUgcHJlcGFyYXRpb25zCkluc3RhbGwgbGF0aCBwYXBlciBhbmQgd2lyZSBtZXNoIG9uIHNob3dlciB3YWxscy4KQXBwbHkgYSBob3QgbW9wIG9uIHNob3dlciBwYW4gYW5kIGZpbGwgd2l0aCB3YXRlciAoMjQtNDggaG91cnMpLiAKQXBwbHkgZmxvYXRpbmcgY2VtZW50L0NlbWVudCBib2FyZHMgKHNjcmF0Y2ggYW5kIGNlbWVudCBjb2F0KSBvbiBzaG93ZXIgd2FsbHMgdG8gcHJlcGFyZSBmb3IgdGlsZSBpbnN0YWxsYXRpb24uIApQb3VyIGNvbmNyZXRlIG9uIHNob3dlciBwYW5lLiAKwqAKwqAKVGlsZSBpbnN0YWxsYXRpb24KSW5zdGFsbCB0aWxlcyBpbiB0aGUgZm9sbG93aW5nIGFyZWFzIFtTcGVjaWZ5IGFweC4gU0YgZm9yIGVhY2ggaXRlbV06IApTaG93ZXIgd2FsbHM7IApTaG93ZXIgcGFuZTsgCkJhdGhyb29tIGZsb29yIFtPcHRpb25hbDogTFZQIGZsb29yaW5nIGluc3RlYWQgb2YgVGlsZV0KQWRkaXRpb25hbCB0aWxlIGFyZWFzIFtTZWxlY3Q6IFNoYW1wb28gbmljaGUgLyBCZW5jaCAvIFNob3dlciB0aHJlc2hvbGQgLyBCYXRocm9vbSB3YWxsc10gClRpbGUgZWRnZXIgW1NlbGVjdDogQnVsbG5vc2UgLyBNZXRhbCAvIE90aGVyXQpDdXN0b21lcnMgc2hhbGwgcHJvdmlkZSB0aWxlcyBhbmQgYWRkaXRpb25hbCBpdGVtczsgCkFwcGx5IGdyb3V0IGluIHNwYWNlcnMgYW5kIHNlYWxlciBvbiB0aWxlcyAoQ29tcGFueSBwcm92aWRlIGdyb3V0IOKAkyBmcm9tIGNvbXBhbnkgc2VsZWN0aW9uKQrCoApEcnl3YWxsICYgUGFpbnQgClByZXBhcmUgYW5kIGluc3RhbGwgZHJ5d2FsbCBhcyBuZWVkZWQgaW4gdGhlIHdvcmtpbmcgYXJlYXM7CkFwcGx5IHByaW1lciBhbmQgcGFpbnQgKFRleHR1cmUsIGNvbG9yIGFuZCBmaW5pc2ggY29hdCAtIFRCRCk7Ckluc3RhbGwgYmFzZWJvYXJkcyBbU2VsZWN0OiBTdGFuZGFyZCBQVkMgLyBNREYgdXAgdG8gNOKAnS8gT3RoZXI6IF9dOwrCoMKgCkluc3RhbGxhdGlvbiBvZiBmaXh0dXJlcyAKU2hvd2VyIGZpeHR1cmVzIFtTaG93ciBoZWFkIGFuZCBoYW5kbGVzXTsKQmF0aHJvb20gZml4dHVyZXM6IFtTZWxlY3Q6IFZhbml0eSAtIHN0b2NrL3ByZWZhYiAoc2luZ2xlL2RvdWJsZSkgLyBtaXJyb3IgLyBTaW5rICYgRmF1Y2V0IC8gVG9pbGV0IC8gQmF0aHJvb20gaGFyZHdhcmUgc2V0IC8gT3RoZXIgOl9dCkFkZGl0aW9uYWwgZml4dHVyZXMsIGhhcmR3YXJlIG9yIGFwcGxpYW5jZXMgdG8gYmUgaW5zdGFsbGVkOiAKQ3VzdG9tZXJzIHNoYWxsIHByb3ZpZGUgYWxsIGl0ZW1zLiAKwqAKwqAKU2hvd2VyIGdsYXNzCkdsYXNzIGRvb3IgcHJvdmlkZWQgYnk6IFtTZWxlY3Q6IEN1c3RvbWVyIC1pbmNsdWRpbmcgaW5zdGFsbGF0aW9uLyBDb21wYW55IC0gZnJvbSBjb21wYW55IHNlbGVjdGlvbiB1bmRlciBhbGxvd2FuY2VdIApJZiBwcm92aWRlZCBieSB0aGUgY29tcGFueSDigJMgZ2xhc3MgZG9vciBkZXRhaWxzOiBbU2VsZWN0OiBHbGFzcyAtIEZyYW1lZCAvIEZyYW1lbGVzcyA7IFNlbGVjdDogSGFyZHdhcmUgLSBDaHJvbWUgLyBCcnVzaGVkIG5pY2tlbCAvIEJyb256ZSAvIEN1c3RvbSA7IFNlbGVjdDogSGFuZGxlIC0gU3F1YXJlIC8gUm91bmQgLyBPdGhlcjogXykKwqAKwqAKVHJhc2ggJiBDbGVhbmluZyAKQ292ZXIgd29ya2luZyBhcmVhczsKSGF1bCBhd2F5IGFsbCBjb25zdHJ1Y3Rpb24gZGVicmlzOwpDb21wbGV0ZSBmaW5hbCBjbGVhbi11cAo="
        )
        val decodedString = String(decodedBytes)

        de.text = decodedString
        mEtSearchName.addWatcher {
            mHomeLeadsAdapter.filter.filter(it)
//
        }
        simpleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        clicked = "0"
                        mList.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.getLeads("1", "30", "all")
                        }
                    }
                    1 -> {
                        clicked = "1"
                        mList.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.getLeads("1", "30", "pending")
                        }
                    }
                    2 -> {
                        clicked = "2"
                        mList.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.getLeads("1", "30", "follow_up")
                        }
                    }
                    3 -> {
                        clicked = "3"
                        mList.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.getLeads("1", "30", "sale")
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        mIvAdd.setOnClickListener { startActivity(Intent(activity, AddLeadActivity::class.java)) }
        mIvSettingsLead.setOnClickListener {
            startActivity(
                Intent(
                    requireActivity(),
                    SettingsActivity::class.java
                )
            )
        }
        tvStatuses.setOnClickListener { showDropDownMenu(tvStatuses, 1) }
        rvLeads.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        rvLeads.adapter = mHomeLeadsAdapter

        if (mPrefs.getKeyValue(PreferenceConstants.USER_NAME).isEmpty()) {
            if (isInternetConnected()) {
                presenter.getProfile()
            }
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

    override fun onLeads(it: LeadsModel) {
        mList.clear()
        mList.addAll(it.data.leadsData)
        mHomeLeadsAdapter.notifyDataSetChanged()
    }

    override fun onDrugSearched(it: ProfileModel) {
        mPrefs.setKeyValue(PreferenceConstants.USER_NAME, it.data.name)
        mPrefs.setKeyValue(PreferenceConstants.USER_ID, it.data._id)
        mPrefs.setKeyValue(PreferenceConstants.USER_LOGO, it.data.image)
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
                            val i = Intent(android.content.Intent.ACTION_VIEW)
                            i.data = Uri.parse("https://play.google.com/store/apps/details?id=com.buildzer")
                            startActivity(i)
                        }
                    })
            }
        } catch (e: Exception) {

        }
    }

    private fun checkVersionCode(): Float {
        try {
            val pInfo = requireActivity().packageManager
                .getPackageInfo(requireActivity().packageName, 0)
            return pInfo.versionCode.toFloat()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0.0f
    }

    override fun onResume() {
        super.onResume()
        if (clicked.equals("0")) {
            mList.clear()
            mHomeLeadsAdapter.notifyDataSetChanged()
            if (isInternetConnected()) {
                presenter.getLeads("1", "30", "all")
            }

        } else if (clicked.equals("1")) {
            mList.clear()
            mHomeLeadsAdapter.notifyDataSetChanged()
            if (isInternetConnected()) {
                presenter.getLeads("1", "30", "pending")
            }
        } else if (clicked.equals("2")) {
            mList.clear()
            mHomeLeadsAdapter.notifyDataSetChanged()
            if (isInternetConnected()) {
                presenter.getLeads("1", "30", "follow_up")
            }
        } else if (clicked.equals("3")) {
            mList.clear()
            mHomeLeadsAdapter.notifyDataSetChanged()
            if (isInternetConnected()) {
                presenter.getLeads("1", "30", "sale")
            }
        } else {
            mList.clear()
            mHomeLeadsAdapter.notifyDataSetChanged()
            if (isInternetConnected()) {
                presenter.getLeads("1", "30", "all")
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        clicked = "0"
    }

    override fun onSingleItemClick(item: Any, position: Int) {
        if (item.equals("2")) {
            val uri = java.lang.String.format(
                Locale.ENGLISH,
                "geo:%f,%f",
                mList[position].address.location.coordinates[0],
                mList[position].address.location.coordinates[1]
            )
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        } else if (item.equals("3")) {
//            val emailIntent = Intent(
//                Intent.ACTION_SENDTO, Uri.fromParts(
//                    "mailto", mList[position].client[0].email, null
//                )
//            )
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to lead")
//            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi "+mList[position].client[0].name)
//            startActivity(Intent.createChooser(emailIntent, "Send email..."))

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("smsto:" + "+1 " + mList[position].client[0].phone_no)
            intent.putExtra("sms_body", "")
            startActivity(intent)
        } else if (item.equals("4")) {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "+1 " + mList[position].client[0].phone_no)
            startActivity(dialIntent)
        } else {
            requireActivity().startActivity(
                Intent(
                    requireActivity(),
                    LeadsActivity::class.java
                ).putExtra("id", mList.get(position)._id)
            )
        }
    }

    fun showDropDownMenu(anchor: View, position: Int): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.menuInflater.inflate(R.menu.leadshomedropdown_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            tvStatuses.text = it.title
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

}

