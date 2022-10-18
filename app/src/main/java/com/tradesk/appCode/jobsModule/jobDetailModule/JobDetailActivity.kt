package com.tradesk.appCode.jobsModule.jobDetailModule

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.appCode.addExpensesModule.ExpensesListActivity
import com.tradesk.appCode.home.addJobsModule.AddJobActivity
import com.tradesk.appCode.home.leadsDetail.CustomerDetailActivity
import com.tradesk.appCode.home.leadsDetail.LeadDetailPresenter
import com.tradesk.appCode.home.leadsDetail.leadsNotesModule.ILeadDetailView
import com.tradesk.appCode.home.leadsDetail.leadsNotesModule.LeadNotesActivity
import com.tradesk.appCode.home.salePersonModule.SalesPersonActivity
import com.tradesk.appCode.jobsModule.jobDetailModule.jobChatModule.ChatActivity
import com.tradesk.appCode.profileModule.gallaryModule.GallaryActivity
import com.tradesk.appCode.profileModule.proposalsModule.ProposalsActivity
import com.tradesk.appCode.profileModule.timesheetModule.jobTimesheetModule.JobTimeSheetActivity
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.utils.ConstUtils
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.convertDateFormat
import com.tradesk.utils.extension.loadWallImage
import com.tradesk.utils.extension.toast
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.tradesk.utils.convertDateFormatWithTime
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_job_detail.*
import kotlinx.android.synthetic.main.activity_job_detail.dots
import kotlinx.android.synthetic.main.activity_job_detail.imageView42
import kotlinx.android.synthetic.main.activity_job_detail.imageView43
import kotlinx.android.synthetic.main.activity_job_detail.inner
import kotlinx.android.synthetic.main.activity_job_detail.mIvAddImage
import kotlinx.android.synthetic.main.activity_job_detail.mIvAddImageIcon
import kotlinx.android.synthetic.main.activity_job_detail.mIvAddUsers
import kotlinx.android.synthetic.main.activity_job_detail.mIvBack
import kotlinx.android.synthetic.main.activity_job_detail.mIvCallIcon
import kotlinx.android.synthetic.main.activity_job_detail.mIvChat
import kotlinx.android.synthetic.main.activity_job_detail.mIvEmail
import kotlinx.android.synthetic.main.activity_job_detail.mIvEmailIcon
import kotlinx.android.synthetic.main.activity_job_detail.mIvEmailSendButton
import kotlinx.android.synthetic.main.activity_job_detail.mIvMainImageBanner
import kotlinx.android.synthetic.main.activity_job_detail.mIvMenus
import kotlinx.android.synthetic.main.activity_job_detail.mIvNotes
import kotlinx.android.synthetic.main.activity_job_detail.mIvView
import kotlinx.android.synthetic.main.activity_job_detail.mTvDesc
import kotlinx.android.synthetic.main.activity_job_detail.rvContractUsers
import kotlinx.android.synthetic.main.activity_job_detail.textView7
import kotlinx.android.synthetic.main.activity_job_detail.viewPager
import kotlinx.android.synthetic.main.activity_leads.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject

class JobDetailActivity : BaseActivity(), SingleItemCLickListener, ILeadDetailView, () -> Unit {
    var client_name = ""
    var client_id = ""
    var receiver_id = ""
    var sales_id = ""
    var client_email = ""
    var myImageUri = ""
    var status_api = ""
    var sales = ""
    var address = ""
    var latitudeJob = ""
    var longitudeJob = ""
    var converted_job = "false"
    var mFile: File? = null
    val mList = mutableListOf<AdditionalImageLeadDetail>()
    val mListSubUsers = mutableListOf<Sale>()
    val mJobContractorUsersAdapter by lazy { JobContractorUsersAdapter(this, this, mListSubUsers) }
    lateinit var leadDetailModel: LeadDetailModel;
    var alertDialog: AlertDialog? = null

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
    lateinit var presenter: LeadDetailPresenter<ILeadDetailView>

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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_detail)
        presenter.onAttach(this)
        rvContractUsers.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        rvContractUsers.adapter = mJobContractorUsersAdapter

        if (isInternetConnected()) {
            presenter.getLeadDetail(intent.getStringExtra("id").toString())
        }
        mIvBack.setOnClickListener { finish() }
        mIvNavigate.setOnClickListener {
            val uri = java.lang.String.format(
                Locale.ENGLISH, "geo:%f,%f",
                latitudeJob.toDouble(), longitudeJob.toDouble()
            )
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }
        mIvView.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(dialogView)

            dialog.findViewById<ImageView>(R.id.mIvShare)!!.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tradesk\nJob Detail")
                var shareMessage = """
                    ${
                    "\n" + "Job Detail" + "\n" + etTvTitle.text.toString() + "\n" +
                            etTvName.text.toString() + "\n" +
                            etTvEmail.text.toString() + "\n" +
                            etTvPhone.text.toString() + "\n" +
                            address + "\n"
//                         "Job Detail"+"\n"+ "Project Name - "+etTvTitle.getText().toString()+"\n"+
//                            "Client Name - "+etTvName.getText().toString()+"\n"+
//                            "Client Email - "+etTvEmail.getText().toString()+"\n"+
//                            "Client Phone - "+etTvPhone.getText().toString()+"\n"+
//                            "Client Address - "+address+"\n"
                }
               
                    """.trimIndent()
                shareMessage = """
                    $shareMessage${"\nShared by - " + mPrefs.getKeyValue(PreferenceConstants.USER_NAME)}
                    
                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)

                startActivity(Intent.createChooser(shareIntent, "choose one"))
                dialog.dismiss()
            }

            dialog.findViewById<ImageView>(R.id.mIvNote)!!.setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        LeadNotesActivity::class.java
                    ).putExtra("id", intent.getStringExtra("id").toString())
                )
                dialog.dismiss()
            }

            dialog.findViewById<ImageView>(R.id.mIvChat)!!.setOnClickListener {
                startActivity(
                    Intent(this, ChatActivity::class.java)
                        .putExtra("job_id", intent.getStringExtra("id").toString())
                        .putExtra("receiver_id", receiver_id)
                        .putExtra("sales_id", sales_id)
                        .putExtra("job_title", etTvTitle.text.toString())

                )
                dialog.dismiss()
            }

            dialog.findViewById<ImageView>(R.id.mIvExpensesJob)!!.setOnClickListener {
                startActivity(
                    Intent(this, ExpensesListActivity::class.java)
                        .putExtra("job_id", intent.getStringExtra("id"))
                )
                dialog.dismiss()
            }
//            dialog.findViewById<ImageView>(R.id.mIvProposalsJobs)!!
//                .setOnClickListener {
//                    startActivity(
//                        Intent(this, ProposalsActivity::class.java)
//                            .putExtra("title", "Proposals")
//                            .putExtra("job_id", intent.getStringExtra("id").toString())
//                            .putExtra("client_name", client_name)
//                            .putExtra("client_id", client_id)
//                            .putExtra("client_email", client_email)
//
//                    )
//                    dialog.dismiss()
//                }
            dialog.findViewById<ImageView>(R.id.mIvChatsJobs)!!.setOnClickListener {
                startActivity(
                    Intent(this, ProposalsActivity::class.java)
                        .putExtra("title", "Proposals")
                        .putExtra("job_id", intent.getStringExtra("id").toString())
                        .putExtra("client_name", client_name)
                        .putExtra("client_id", client_id)
                        .putExtra("client_email", client_email)

                )
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.mIvInovicesJobs)!!.setOnClickListener {
                startActivity(
                    Intent(this, ProposalsActivity::class.java)
                        .putExtra("title", "Invoices")
                        .putExtra("job_id", intent.getStringExtra("id").toString())
                        .putExtra("client_name", client_name)
                        .putExtra("client_id", client_id)
                        .putExtra("client_email", client_email)

                )
                dialog.dismiss()
            }

            dialog.findViewById<ImageView>(R.id.mIvGallaries)!!.setOnClickListener {
                startActivity(
                    Intent(this, GallaryActivity::class.java)
                        .putExtra("id", intent.getStringExtra("id").toString())
                        .putExtra("additionalimages", "yes")
                )
                dialog.dismiss()
            }

            dialog.findViewById<ImageView>(R.id.mIvPermitsJobs)!!.setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        GallaryActivity::class.java
                    ).putExtra("id", intent.getStringExtra("id").toString())
                        .putExtra("permits", "yes")
                )
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.mIvTimesheet)!!.setOnClickListener {
                startActivity(
                    Intent(this, JobTimeSheetActivity::class.java)
                        .putExtra("id", intent.getStringExtra("id"))
                )
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.mIvCancelJobs)!!.setOnClickListener {
                if (isInternetConnected()) {
                    status_api = "Cancel Job"
                    presenter.convertleads(
                        intent.getStringExtra("id").toString(),
                        "job",
                        "cancel",
                        converted_job
                    )
                }
                dialog.dismiss()
            }

            dialog.show()
        }
        mIvAddImage.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                showImagePop()
            }
        }

        mIvAddImageIcon.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                showImagePop()
            }
        }
        tvStatuses.setOnClickListener { showLogoutMenu(tvStatuses, 1) }
        mIvMenus.setOnClickListener { showSideMenu(mIvMenus, 1) }
        mIvEmail.setOnClickListener { showInformationPop(4) }
        mIvNotes.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    LeadNotesActivity::class.java
                ).putExtra("id", intent.getStringExtra("id").toString())
            )
        }
        mIvChat.setOnClickListener {
            startActivity(
                Intent(this, ExpensesListActivity::class.java)
                    .putExtra("job_id", intent.getStringExtra("id"))
            )
        }

//        mIvAddUsers.setOnClickListener {
//            startActivity(
//                Intent(
//                    this,
//                    AddSalesPersonActivity::class.java
//                ).putExtra("job_id", intent.getStringExtra("id").toString())
//            )
//        }


        if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")) {
            mIvAddUsers.visibility = View.VISIBLE
        } else {
            mIvAddUsers.visibility = View.GONE
        }
        mIvAddUsers.setOnClickListener {
            val i = Intent(this, SalesPersonActivity::class.java)
            i.putExtra("fromjob", "Job")
            i.putExtra("job_id", intent.getStringExtra("id"))
            startActivityForResult(i, 2222)
        }

        imageView2.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", etTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to job")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + etTvName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }


        mIvEmailSendButton.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", etTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to job")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + etTvName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }


        textView2.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", etTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to job")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + etTvName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }


        etTvEmail.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", etTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to job")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + etTvName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }

        mIvCallIcon.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + etTvPhone.text.toString())
            startActivity(dialIntent)
        }


        mIvEmailIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("smsto:" + etTvPhone.text.toString())
            intent.putExtra("sms_body", "")
            startActivity(intent)
        }


        etTvPhone.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + etTvPhone.text.toString())
            startActivity(dialIntent)
        }


        imageView4.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + etTvPhone.text.toString())
            startActivity(dialIntent)
        }


        textView3.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + etTvPhone.text.toString())
            startActivity(dialIntent)
        }
        edit_icon.setOnClickListener {
//            showDotsMenu(mIvMenus, 1)
            val builder = GsonBuilder()
            val gson = builder.create()
            var string=gson.toJson(leadDetailModel);
            startActivity(Intent(this, AddJobActivity::class.java).putExtra("edit",true).putExtra("lead",string));
        }
    }

    override fun onLeadDetail(it: LeadDetailModel) {
        leadDetailModel=it
        etIvMapView.loadWallImage("https://maps.googleapis.com/maps/api/staticmap?zoom=13&size=600x500&maptype=roadmap&markers=color:red%7Clabel:S%7C" + it.data.leadsData.address.location.coordinates[0] + "," + it.data.leadsData.address.location.coordinates[1] + "&key=AIzaSyAweeG9yxU6nQulKdyN6nIIBtSPak1slfo")
//        etTvTopTitle.setText(it.data.leadsData.project_name.capitalize())
        etTvTopTitle.text = "Job"
        etTvTitle.text = it.data.leadsData.project_name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
        etTvName.text = it.data.leadsData.client[0].name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }

        client_name = it.data.leadsData.client[0].name
        client_id = it.data.leadsData.client[0]._id
        client_email = it.data.leadsData.client[0].email
        etTvPhone.text = insertString(it.data.leadsData.client[0].phone_no, "", 0)
        etTvPhone.text = insertString(etTvPhone.text.toString(), ")", 2)
        etTvPhone.text = insertString(etTvPhone.text.toString(), " ", 3)
        etTvPhone.text = insertString(etTvPhone.text.toString(), "-", 7)
        etTvPhone.text = "+1 (" + etTvPhone.text.toString()

        etTvEmail.text = it.data.leadsData.client[0].email
        etTvAddress.text = it.data.leadsData.address.street + ", " + it.data.leadsData.address.city
        address =
            it.data.leadsData.address.street + ", " + it.data.leadsData.address.city + ", " + it.data.leadsData.address.state + ", " + it.data.leadsData.address.zipcode
        latitudeJob = it.data.leadsData.address.location.coordinates[0].toString()
        longitudeJob = it.data.leadsData.address.location.coordinates[1].toString()
        if (it.data.leadsData.startDate != null) {
            etTvDate.text = convertDateFormatWithTime(it.data.leadsData.startDate.toString())
        } else {
            etTvDate.text = "N/A"
        }

        if (it.data.leadsData.description.isNotEmpty()) {
            mTvDesc.text = it.data.leadsData.description
        } else {
            mTvDesc.text = "N/A"
        }

        if (it.data.leadsData.converted_to_job == true) {
            converted_job = "true"
        } else {
            converted_job = "false"
        }

        if (it.data.leadsData.status.equals("completed")) {
            tvStatuses.text = "Complete"
        } else {
            tvStatuses.text = it.data.leadsData.status.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
        }

        if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE) == "1") {
            receiver_id = it.data.leadsData.sales[0]._id
        } else {
            receiver_id = it.data.leadsData.created_by
        }
        sales_id = it.data.leadsData.sales[0]._id

        status_api = it.data.leadsData.status
        if (status_api.equals("cancel")) {
            textView7.visibility = View.INVISIBLE
            mIvAddImage.visibility = View.INVISIBLE
            mIvMenus.visibility = View.INVISIBLE
            mIvAddUsers.visibility = View.INVISIBLE
        } else {
            textView7.visibility = View.VISIBLE
            mIvMenus.visibility = View.INVISIBLE
            mIvAddImage.visibility = View.VISIBLE
            if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")) {
                mIvAddUsers.visibility = View.VISIBLE
            } else {
                mIvAddUsers.visibility = View.GONE
            }
        }
        mList.clear()
        if (!it.data.leadsData.additional_images.isNullOrEmpty()) {
            mList.addAll(it.data.leadsData.additional_images.filter { it.status!!.equals("image") })
        }

        if (mList.isNotEmpty()) {
            viewPager.visibility = View.VISIBLE
            dots.visibility = View.VISIBLE
            imageView42.visibility = View.INVISIBLE
            imageView43.visibility = View.INVISIBLE

            mIvMainImageBanner.visibility = View.INVISIBLE
            mIvAddImageIcon.visibility = View.INVISIBLE
        } else {
            viewPager.visibility = View.GONE
            dots.visibility = View.GONE
            imageView42.visibility = View.GONE
            imageView43.visibility = View.GONE

            mIvMainImageBanner.visibility = View.VISIBLE
            mIvAddImageIcon.visibility = View.VISIBLE
        }
        viewPager.adapter = ImageVPagerAdapter(mList, this)
        dots.attachViewPager(viewPager)

        mListSubUsers.clear()
        mListSubUsers.addAll(it.data.leadsData.sales)
        mJobContractorUsersAdapter.notifyDataSetChanged()

        inner.visibility = View.VISIBLE
    }

    override fun onDeleteImage(it: SuccessModel) {

    }

    override fun onStatus(it: SuccessModel) {
        if (status_api.equals("Completed")) {
            toast("Job completed successfully")
            finish()
        } else if (status_api.equals("Cancel Job")) {
            toast("Job cancelled successfully")
            finish()
        } else {
            toast("Job status updated successfully")
        }
    }

    override fun onLeadDeleteStatus(it: SuccessModel) {

    }

    override fun onDocDeleteStatus(it: SuccessModel) {

    }

    override fun onAddImage(it: SuccessModel) {
        toast(it.message)
        if (isInternetConnected()) {
            presenter.getLeadDetail(intent.getStringExtra("id").toString())
        }
    }

    override fun onAddReminder(it: SuccessModel) {

    }

    override fun onProfileData(it: ProfileModel) {

    }

    override fun onAdditionalImagesData(it: AdditionalImagesWithClientModel) {

    }


    override fun onerror(data: String) {
        toast(data)
    }

    override fun onGeneratedToken(lastAction: String) {

    }

    override fun onSingleItemClick(item: Any, position: Int) {
        startActivity(
            Intent(this, CustomerDetailActivity::class.java)
                .putExtra("id", mListSubUsers[position]._id)
                .putExtra("name", mListSubUsers[position].name)
                .putExtra("email", mListSubUsers[position].email)
                .putExtra("phone", mListSubUsers[position].phone_no)
                .putExtra(
                    "address", if (mListSubUsers[position].address.street.isNotEmpty()) {
                        mListSubUsers[position].address.street + ", " + mListSubUsers[position].address.city + ", " + mListSubUsers[position].address.state + ", " + mListSubUsers[position].address.zipcode
                    } else {
                        mListSubUsers[position].address.street + ", " + mListSubUsers[position].address.city + ", " + mListSubUsers[position].address.state + ", " + mListSubUsers[position].address.zipcode
                    }
                )
                .putExtra("image", mListSubUsers[position].image)
                .putExtra("trade", mListSubUsers[position].trade)

        )
    }


    val mInfoBuilder: Dialog by lazy { Dialog(this) }
    fun showInformationPop(id: Int) {
        mInfoBuilder.setContentView(R.layout.popup_dynamic)
        val displayMetrics = DisplayMetrics()
        mInfoBuilder.window!!.attributes.windowAnimations = R.style.DialogAnimationNew
        mInfoBuilder.window!!.setGravity(Gravity.BOTTOM)
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        mInfoBuilder.window!!.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
            (displayMetrics.widthPixels * 0.99).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        mInfoBuilder.findViewById<TextView>(R.id.mTvSpam).setOnClickListener {
            startActivity(
                Intent(this, ProposalsActivity::class.java)
                    .putExtra("title", "Estimates")
                    .putExtra("job_id", intent.getStringExtra("id").toString())
                    .putExtra("client_name", client_name)
                    .putExtra("client_id", client_id)
                    .putExtra("client_email", client_email)

            )
            mInfoBuilder.dismiss()
        }

        mInfoBuilder.findViewById<TextView>(R.id.mTvNude).setOnClickListener {
            startActivity(
                Intent(this, ProposalsActivity::class.java)
                    .putExtra("title", "Invoices")
                    .putExtra("job_id", intent.getStringExtra("id").toString())
                    .putExtra("client_name", client_name)
                    .putExtra("client_id", client_id)
                    .putExtra("client_email", client_email)

            )

            mInfoBuilder.dismiss()
        }

        mInfoBuilder.findViewById<TextView>(R.id.mTvHate).setOnClickListener {


            mInfoBuilder.dismiss()
        }


        mInfoBuilder.findViewById<TextView>(R.id.tvDone)
            .setOnClickListener {
                mInfoBuilder.dismiss()
            }
        mInfoBuilder.show()
    }

    fun showLogoutMenu(anchor: View, position: Int): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        if (status_api.equals("pending")) {
            popup.menu.add("Pending") // menus items
            popup.menu.add("Ongoing") // menus items
            popup.menu.add("Complete")
        } else if (status_api.equals("ongoing")) {
            popup.menu.add("Pending")
            popup.menu.add("Ongoing")
            popup.menu.add("Complete")
        } else if (status_api.equals("completed")) {
            popup.menu.add("Pending")
            popup.menu.add("Ongoing")
            popup.menu.add("Complete")
        }
//        val popup = PopupMenu(anchor.context, anchor)
//        popup.getMenuInflater().inflate(R.menu.jobsstatus_menu, popup.getMenu())
        popup.setOnMenuItemClickListener {
            tvStatuses.text = it.title
            if (isInternetConnected()) {
                status_api = tvStatuses.text.toString().trim().lowercase()

                if (tvStatuses.text.toString().trim().equals("Completed")) {
                    presenter.convertleads(
                        intent.getStringExtra("id").toString(),
                        "job",
                        "completed", converted_job
                    )
                } else {
                    presenter.convertleads(
                        intent.getStringExtra("id").toString(),
                        "job",
                        tvStatuses.text.toString().trim().lowercase(), converted_job
                    )
                }
            }
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

    fun showSideMenu(anchor: View, position: Int): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.menuInflater.inflate(R.menu.jobsidemenu, popup.menu)
        popup.setOnMenuItemClickListener {
            if (it.title.equals("End Job")) {
                if (isInternetConnected()) {
                    status_api = "Completed"
                    presenter.convertleads(
                        intent.getStringExtra("id").toString(),
                        "job",
                        "completed", converted_job
                    )
                }
            } else if (it.title.equals("Cancel Job")) {
                if (isInternetConnected()) {
                    status_api = "Cancel Job"
                    presenter.convertleads(
                        intent.getStringExtra("id").toString(),
                        "job",
                        "cancel",
                        converted_job
                    )
                }
            } else if (it.title.equals("Gallary")) {
                startActivity(
                    Intent(
                        this,
                        GallaryActivity::class.java
                    ).putExtra("id", intent.getStringExtra("id").toString())
                        .putExtra("additionalimages", "yes")
                )
            }

//            mIvMenus.setText(it.title)
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

    val mBuilder: Dialog by lazy { Dialog(this@JobDetailActivity) }

    fun showImagePop() {
        Pix.start(this@JobDetailActivity, options)

//        mBuilder.setContentView(R.layout.camera_dialog);
//        mBuilder.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation;
//        mBuilder.window!!.setGravity(Gravity.BOTTOM)
//        mBuilder.window!!.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        mBuilder.findViewById<TextView>(R.id.titleCamera)
//            .setOnClickListener {
//                mBuilder.dismiss()
//                dispatchTakePictureIntent()
//            }
//        mBuilder.findViewById<TextView>(R.id.titleGallery)
//            .setOnClickListener {
//                mBuilder.dismiss()
//                dispatchTakeGalleryIntent()
//            }
//        mBuilder.findViewById<TextView>(R.id.titleCancel)
//            .setOnClickListener { mBuilder.dismiss() }
//        mBuilder.show();
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(this@JobDetailActivity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    ConstUtils.createImageFile(this@JobDetailActivity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this@JobDetailActivity,
                        "${this@JobDetailActivity.packageName}.provider",
                        it
                    )
                    myImageUri = photoURI.toString()
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, ConstUtils.REQUEST_TAKE_PHOTO)
                }
            }

        }
    }

    private fun dispatchTakeGalleryIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        if (intent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(intent, ConstUtils.REQUEST_IMAGE_GET)
        }
    }

    private fun saveCaptureImageResults(data: Uri) = try {
        val file = File(data.path!!)
        mFile = Compressor(this@JobDetailActivity)
            .setMaxHeight(1000).setMaxWidth(1000)
            .setQuality(99)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .compressToFile(file)

        hashMapOf<String, RequestBody>().also {
            it.put(
                "image\"; filename=\"image.jpg",
                RequestBody.create(MediaType.parse("image/*"), mFile!!)
            )
            it.put(
                "_id",
                RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    intent.getStringExtra("id").toString()
                )
            )
            it.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "image"))
            presenter.addImgaes(it)
        }

    } catch (e: Exception) {
    }

    private fun saveCaptureImageResults(path: String) = try {
        val file = File(path)
        mFile = Compressor(this@JobDetailActivity)
            .setMaxHeight(4000).setMaxWidth(4000)
            .setQuality(99)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .compressToFile(file)

        hashMapOf<String, RequestBody>().also {
            it.put(
                "image\"; filename=\"image.jpg",
                RequestBody.create(MediaType.parse("image/*"), mFile!!)
            )
            it.put(
                "_id",
                RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    intent.getStringExtra("id").toString()
                )
            )
            it.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "image"))
            presenter.addImgaes(it)
        }

    } catch (e: Exception) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1010) {
            data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.let {
                if (it.isNotEmpty()) saveCaptureImageResults(it[0])
            }
            Log.e("RESULT", "RESULT")
//                saveCaptureImageResults()
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ConstUtils.REQUEST_TAKE_PHOTO) {
                CropImage.activity(Uri.parse(myImageUri))
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setMinCropWindowSize(8000, 8000)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(this)
            } else if (requestCode == ConstUtils.REQUEST_IMAGE_GET) {
                val uri: Uri = data?.data!!
                CropImage.activity(uri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setAspectRatio(2, 1)
                    .setMinCropWindowSize(8000, 8000)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(this)
            } else if (requestCode == 2222) {
                if (resultCode == Activity.RESULT_OK) {
                    sales = ""
                    sales = data!!.getStringExtra("result").toString()
//                    if (data!!.getStringExtra("image").toString().isNotEmpty()){
//                        mIvSalesImage.loadWallImage(data!!.getStringExtra("image").toString())
//                    }
                    if (isInternetConnected()) {
                        presenter.getLeadDetail(intent.getStringExtra("id").toString())
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    // Write your code if there's no result
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    saveCaptureImageResults(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }
        }
    }

    override fun invoke() {

    }
    override fun onResume() {
        presenter.getLeadDetail(intent.getStringExtra("id").toString())
        super.onResume()

    }
}