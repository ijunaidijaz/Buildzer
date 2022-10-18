package com.tradesk.appCode.home.leadsDetail

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
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
import com.tradesk.appCode.home.addLeadsModule.AddLeadActivity
import com.tradesk.appCode.home.leadsDetail.leadsNotesModule.ILeadDetailView
import com.tradesk.appCode.home.leadsDetail.leadsNotesModule.LeadNotesActivity
import com.tradesk.appCode.home.salePersonModule.SalesPersonActivity
import com.tradesk.appCode.jobsModule.jobDetailModule.ImageVPagerAdapter
import com.tradesk.appCode.jobsModule.jobDetailModule.JobContractorUsersAdapter
import com.tradesk.appCode.jobsModule.jobDetailModule.JobDetailActivity
import com.tradesk.appCode.profileModule.gallaryModule.GallaryActivity
import com.tradesk.appCode.profileModule.proposalsModule.ProposalsActivity
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.utils.ConstUtils
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.convertDateFormatWithTime
import com.tradesk.utils.extension.*
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.tradesk.utils.convertDateFormat
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_leads.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject


class LeadsActivity : BaseActivity(), ILeadDetailView, SingleItemCLickListener, () -> Unit {

    var status_api = ""
    var converted_job = ""
    var client_id = ""
    var client_name = ""
    var client_email = ""
    var address = ""
    var email_idclient = ""
    var phonenumberclient = ""
    var alertDialog: AlertDialog? = null

    var latitudeJob = ""
    var longitudeJob = ""

    var sales = ""
    var myImageUri = ""
    var mFile: File? = null
    val mList = mutableListOf<AdditionalImageLeadDetail>()
    val mListSubUsers = mutableListOf<Sale>()
    val mJobContractorUsersAdapter by lazy { JobContractorUsersAdapter(this, this, mListSubUsers) }

lateinit var leadDetailModel: LeadDetailModel;
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leads)
        presenter.onAttach(this)
        mIvBack.setOnClickListener { finish() }

        rvContractUsers.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        rvContractUsers.adapter = mJobContractorUsersAdapter

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


        imageView13.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", mTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to lead")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + mTvClientName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }


        textView13.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", mTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to lead")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + mTvClientName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }


        mTvEmail.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", mTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to lead")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + mTvClientName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }


        mIvEmailSendButton.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", mTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to lead")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + mTvClientName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }


        mIvView.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_leads, null)
            val dialog = BottomSheetDialog(this)
//
            dialog.setContentView(dialogView)

            dialog.findViewById<ImageView>(R.id.mIvNotes)!!.setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        LeadNotesActivity::class.java
                    ).putExtra("id", intent.getStringExtra("id").toString())
                )
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.mIvInvoices)!!.setOnClickListener {
                presenter.junkleads(leadDetailModel.data.leadsData._id);
            }
            dialog.findViewById<ImageView>(R.id.mIvExpense)!!.setOnClickListener {
//                startActivity(
//                    Intent(this, ExpensesListActivity::class.java)
//                        .putExtra("job_id", intent.getStringExtra("id"))
//                )

                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tradesk\nLead Detail")
                var shareMessage = """
                    ${
                    "\n" + mTvTitle.text.toString() + "\n" +
                            mTvClientName.text.toString() + "\n" +
                            mTvEmail.text.toString() + "\n" +
                            mTvPhone.text.toString() + "\n" +
                            address + "\n"

                    /*"\nTradesk App\n\n" + "Lead Detail"+"\n"+ "Project Name - "+mTvTitle.getText().toString()+"\n"+ 
                    "Client Name - "+mTvClientName.getText().toString()+"\n"+ 
                    "Client Email - "+mTvEmail.getText().toString()+"\n"+ 
                    "Client Phone - "+mTvPhone.getText().toString()+"\n"+ 
                    "Client Address - "+address+"\n"*/
                }
               
                    """.trimIndent()
                shareMessage = """
                    $shareMessage${"\nShared by - " + mPrefs.getKeyValue(PreferenceConstants.USER_NAME)}
                    
                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)

                startActivity(Intent.createChooser(shareIntent, "choose one"))
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.mIvProposals)!!
                .setOnClickListener {
                    startActivity(
                        Intent(this, ProposalsActivity::class.java)
                            .putExtra("title", "Proposals")
                            .putExtra("job_id", intent.getStringExtra("id"))
                            .putExtra("client_name", client_name)
                            .putExtra("client_email", email_idclient)
                            .putExtra("client_id", client_id)
                    )
                    dialog.dismiss()
                }
            dialog.findViewById<ImageView>(R.id.mIvProposal)!!.setOnClickListener {
                startActivity(
                    Intent(this, ProposalsActivity::class.java)
                        .putExtra("title", "Proposals")
                        .putExtra("job_id", intent.getStringExtra("id"))
                        .putExtra("client_name", client_name)
                        .putExtra("client_email", email_idclient)
                        .putExtra("client_id", client_id)
                )
                dialog.dismiss()
            }
//            dialog.findViewById<ImageView>(R.id.mIvInvoices)!!.setOnClickListener {
//                startActivity(
//                    Intent(this, ProposalsActivity::class.java)
//                        .putExtra("title", "Invoices")
//                        .putExtra("job_id", intent.getStringExtra("id").toString())
//                        .putExtra("client_name", client_name)
//                        .putExtra("client_id", client_id)
//                        .putExtra("client_email", client_email)
//
//                )
//                dialog.dismiss()
//            }
            dialog.findViewById<ImageView>(R.id.mIvDocuments)!!
                .setOnClickListener { toast("notes") }
            dialog.findViewById<ImageView>(R.id.mIvGallary)!!.setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        GallaryActivity::class.java
                    ).putExtra("id", intent.getStringExtra("id").toString())
                        .putExtra("additionalimages", "yes")
                )
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.mIvCalendars)!!
                .setOnClickListener { toast("notes") }
            dialog.findViewById<ImageView>(R.id.mIvCalendar)!!.setOnClickListener { toast("notes") }
            dialog.findViewById<ImageView>(R.id.mIvEndJob)!!.setOnClickListener {
                if (isInternetConnected()) {
                    status_api = "Completed"
                    presenter.convertleads(
                        intent.getStringExtra("id").toString(),
                        "job",
                        "completed", "false"
                    )
                }
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.mIvCancel)!!.setOnClickListener {
                if (isInternetConnected()) {
                    status_api = "Cancel Job"
                    presenter.convertleads(
                        intent.getStringExtra("id").toString(),
                        "job",
                        "cancel",
                        "false"
                    )
                }
                dialog.dismiss()
            }
            dialog.show()
        }

        mIvCallIcon.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + mTvPhone.text.toString())
            startActivity(dialIntent)
        }


        mIvEmailIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("smsto:" + mTvPhone.text.toString())
            intent.putExtra("sms_body", "")
            startActivity(intent)
        }

        mTvPhone.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + mTvPhone.text.toString())
            startActivity(dialIntent)
        }


        imageView12.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + mTvPhone.text.toString())
            startActivity(dialIntent)
        }


        textView12.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + mTvPhone.text.toString())
            startActivity(dialIntent)
        }


        mIvMenus.setOnClickListener {
//            showDotsMenu(mIvMenus, 1)
            val builder = GsonBuilder()
            val gson = builder.create()
            var string=gson.toJson(leadDetailModel);
            startActivity(Intent(this,AddLeadActivity::class.java).putExtra("edit",true).putExtra("lead",string));
        }
        tvStatus.setOnClickListener {
            if (converted_job == "true") {
                startActivity(
                    Intent(this, JobDetailActivity::class.java).putExtra(
                        "id",
                        intent.getStringExtra("id").toString()
                    )
                )
            } else {
                showLogoutMenu(tvStatus, 1)
            }
        }
        mIvEmail.setOnClickListener { showInformationPop(4) }
        mIvNotes.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    LeadNotesActivity::class.java
                ).putExtra("id", intent.getStringExtra("id").toString())
            )
        }

        mIvNavigates.setOnClickListener {
            val uri = java.lang.String.format(
                Locale.ENGLISH, "geo:%f,%f",
                latitudeJob.toDouble(), longitudeJob.toDouble()
            )
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }

        if (isInternetConnected()) {
            presenter.getLeadDetail(intent.getStringExtra("id").toString())
        }
    }

    override fun onLeadDetail(it: LeadDetailModel) {
        leadDetailModel=it;
        mIvMapImage.loadWallImage("https://maps.googleapis.com/maps/api/staticmap?zoom=13&size=600x500&maptype=roadmap&markers=color:red%7Clabel:S%7C" + it.data.leadsData.address.location.coordinates[0] + "," + it.data.leadsData.address.location.coordinates[1] + "&key=AIzaSyAweeG9yxU6nQulKdyN6nIIBtSPak1slfo")
//        mTvTitleTop.setText(it.data.leadsData.project_name.capitalize())
        mTvTitleTop.text = "Lead"
        mTvTitle.text = it.data.leadsData.project_name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
        mTvClientName.text = it.data.leadsData.client[0].name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
        mTvPhone.text = insertString(it.data.leadsData.client[0].phone_no, "", 0)
        mTvPhone.text = insertString(mTvPhone.text.toString(), ")", 2)
        mTvPhone.text = insertString(mTvPhone.text.toString(), " ", 3)
        mTvPhone.text = insertString(mTvPhone.text.toString(), "-", 7)
        mTvPhone.text = "+1 (" + mTvPhone.text.toString()

        mTvEmail.text = it.data.leadsData.client[0].email
        client_id = it.data.leadsData.client[0]._id
        client_name = it.data.leadsData.client[0].name
        email_idclient = it.data.leadsData.client[0].email
        phonenumberclient = mTvPhone.text.toString()
        mTvAddress.text = it.data.leadsData.address.street + ", " + it.data.leadsData.address.city
        address =
            it.data.leadsData.address.street + ", " + it.data.leadsData.address.city + ", " + it.data.leadsData.address.state + ", " + it.data.leadsData.address.zipcode
//       if (it.data.leadsData.start_date!=null){
//           mTvDate.setText(convertDateFormat(it.data.leadsData.start_date.toString()))
//       }else{
//           mTvDate.setText("N/A")
//       }
        if(leadDetailModel.data.leadsData.source!=null){
            mTvSource.text=(leadDetailModel.data.leadsData.source)
        }
        latitudeJob = it.data.leadsData.address.location.coordinates[0].toString()
        longitudeJob = it.data.leadsData.address.location.coordinates[1].toString()
        if (it.data.leadsData.createdAt.isNotEmpty()) {
            mTvDate.text = convertDateFormatWithTime(it.data.leadsData.startDate.toString())
        } else {
            mTvDate.text = "N/A"
        }


        if (it.data.leadsData.description.isNotEmpty()) {
            mTvDesc.text = it.data.leadsData.description
        } else {
            mTvDesc.text = "N/A"
        }


        if (it.data.leadsData.converted_to_job == true) {
            converted_job = "true"
            tvStatus.text = "Job"
            tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrowtoforward, 0)
        } else {
            if (it.data.leadsData.status.equals("follow_up")) {
                tvStatus.text = "Follow Up"
            } else {
                tvStatus.text = it.data.leadsData.status.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
            }
            tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
        }


        status_api = it.data.leadsData.status

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
        if (status_api.equals("Convert to Job")) {
            finish()
        } else if (status_api.equals("follow_up")) {
            toast("Lead converted to Follow Up")
        } else if (status_api.equals("sale")) {
            toast("Lead converted to Sale")
        } else {
            toast(it.message)
        }
    }

    override fun onLeadDeleteStatus(it: SuccessModel) {
        toast("Lead added to junk successfully")
        finish()
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
        toast(it.message)
        status_api = "follow_up"

        if (status_api.equals("follow_up")) {
            presenter.convertleads(
                intent.getStringExtra("id").toString(),
                "lead",
                "follow_up",
                "false"
            )
        }
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


    val mInfoBuilder: Dialog by lazy { Dialog(this) }
    val mInfoBuilderTwo: Dialog by lazy { Dialog(this) }
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
        mInfoBuilder.findViewById<TextView>(R.id.mTvSpam).text = "Send Proposal"
        mInfoBuilder.findViewById<TextView>(R.id.mTvNude).visibility = View.GONE
        mInfoBuilder.findViewById<TextView>(R.id.mTvHate).visibility = View.GONE

        mInfoBuilder.findViewById<TextView>(R.id.mTvSpam).setOnClickListener {

            startActivity(
                Intent(this, ProposalsActivity::class.java)
                    .putExtra("title", "Proposals")
                    .putExtra("job_id", intent.getStringExtra("id"))
                    .putExtra("client_name", client_name)
                    .putExtra("client_email", email_idclient)
                    .putExtra("client_id", client_id)
            )

//            startActivity(
//                Intent(this, PDFActivity::class.java)
//                    .putExtra("clientname", mTvClientName.text.toString())
//                    .putExtra("address", address)
//                    .putExtra("phone", phonenumberclient)
//                    .putExtra("email", email_idclient)
//            )


//            startActivity(Intent(this, ProposalsActivity::class.java))

            mInfoBuilder.dismiss()
        }

        mInfoBuilder.findViewById<TextView>(R.id.mTvNude).setOnClickListener {

            startActivity(
                Intent(this, ProposalsActivity::class.java)
                    .putExtra("title", "Invoices")
                    .putExtra("job_id", intent.getStringExtra("id"))
                    .putExtra("client_name", client_name)
                    .putExtra("client_email", email_idclient)
                    .putExtra("client_id", client_id)
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
            popup.menu.add("Follow Up") // menus items
            popup.menu.add("Sale")
        } else if (status_api.equals("follow_up")) {
            popup.menu.add("Pending") // menus items
            popup.menu.add("Follow Up") // menus items
            popup.menu.add("Sale")
        } else if (status_api.equals("sale")) {
            popup.menu.add("Pending") // menus items
            popup.menu.add("Follow Up")
            popup.menu.add("Convert to Job")
        }


//        popup.getMenuInflater().inflate(R.menu.leadsmain_menu, popup.getMenu())
        popup.setOnMenuItemClickListener {
            tvStatus.text = it.title
            if (it.title.equals("Follow Up")) {
                showFollowUpPop(0)
            } else if (it.title.equals("Pending")) {
                status_api = tvStatus.text.toString().trim().lowercase()
                presenter.convertleads(
                    intent.getStringExtra("id").toString(),
                    "lead",
                    "pending",
                    "false"
                )
            } else if (it.title.equals("Sale")) {
                status_api = tvStatus.text.toString().trim().lowercase()
//                if (status_api.equals(tvStatus.text.toString().trim().lowercase())) {
//                    AllinOneDialog(getString(R.string.app_name),
//                        "Would you like to convert this lead to a new job?",
//                        btnLeft = "No",
//                        btnRight = "Yes",
//                        onLeftClick = {},
//                        onRightClick = {
//                            presenter.convertleads(
//                                intent.getStringExtra("id").toString(),
//                                "job",
//                                "pending"
//                            )
//                        })
//                } else {
                presenter.convertleads(
                    intent.getStringExtra("id").toString(),
                    "lead",
                    "sale",
                    "false"
                )
//                }
            } else if (it.title.equals("Convert to Job")) {
                status_api = "Convert to Job"
                AllinOneDialog(getString(R.string.app_name),
                    "Would you like to convert this lead to a new job?",
                    btnLeft = "No",
                    btnRight = "Yes",
                    onLeftClick = {},
                    onRightClick = {
                        presenter.convertleads(
                            intent.getStringExtra("id").toString(),
                            "job",
                            "pending", "true"
                        )
                    })

            }
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

    fun showDotsMenu(anchor: View, position: Int): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.menuInflater.inflate(R.menu.leadsdot_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            if (isInternetConnected()) {
                presenter.junkleads(intent.getStringExtra("id").toString())
            }
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

    fun showFollowUpPop(id: Int) {
        mInfoBuilderTwo.setContentView(R.layout.popup_followup)
        val displayMetrics = DisplayMetrics()
        mInfoBuilderTwo.window!!.attributes.windowAnimations = R.style.DialogAnimationNew
        mInfoBuilderTwo.window!!.setGravity(Gravity.BOTTOM)
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        mInfoBuilderTwo.window!!.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
            (displayMetrics.widthPixels * 0.99).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        mInfoBuilderTwo.findViewById<TextView>(R.id.mTvCallReminder).setOnClickListener {

            val c: Calendar = Calendar.getInstance()
            val dialog = DatePickerDialog(
                this@LeadsActivity,
                { view, year, month, dayOfMonth ->
                    val _year = year.toString()
                    val _month = if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                    val _date = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                    val _pickedDate = "$_year-$_month-$_date"
                    Log.e("PickedDate: ", "Date: $_pickedDate") //2019-02-12

                    val c2 = Calendar.getInstance()
                    var mHour = c2[Calendar.HOUR_OF_DAY]
                    var mMinute = c2[Calendar.MINUTE]

                    // Launch Time Picker Dialog

                    // Launch Time Picker Dialog
                    val timePickerDialog = TimePickerDialog(
                        this,
                        { view, hourOfDay, minute ->
                            mHour = hourOfDay
                            mMinute = minute
                            var _pickedTime = ""
                            if (hourOfDay.toString().length == 1 && minute.toString().length == 1) {
                                _pickedTime =
                                    "0" + hourOfDay.toString() + ":" + "0" + minute.toString()
                            } else if (hourOfDay.toString().length == 1) {
                                _pickedTime = "0" + hourOfDay.toString() + ":" + minute.toString()
                            } else if (minute.toString().length == 1) {
                                _pickedTime = hourOfDay.toString() + ":" + "0" + minute.toString()
                            } else {
                                _pickedTime = hourOfDay.toString() + ":" + minute.toString()
                            }
                            Log.e(
                                "PickedTime: ",
                                "Date: $_pickedDate" + _pickedTime
                            ) //2019-02-12
//                            et_show_date_time.setText(date_time.toString() + " " + hourOfDay + ":" + minute)

                            val tz = TimeZone.getDefault()
                            presenter.addreminder(
                                intent.getStringExtra("id").toString(),
                                client_id,
                                "phone",
                                "lead",
                                _pickedDate + " " + _pickedTime + ":00", tz.id
                            )
                            mInfoBuilderTwo.dismiss()

                        },
                        mHour,
                        if (mMinute == 60) 0 + 2 else if (mMinute == 59) 0 + 1 else mMinute + 2,
                        false
                    )
                    timePickerDialog.show()

                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.MONTH)
            )
            dialog.datePicker.minDate = System.currentTimeMillis() - 1000
            dialog.show()

        }

        mInfoBuilderTwo.findViewById<TextView>(R.id.mTvAppointReminder).setOnClickListener {

            val c: Calendar = Calendar.getInstance()
            val dialog = DatePickerDialog(
                this@LeadsActivity,
                { view, year, month, dayOfMonth ->
                    val _year = year.toString()
                    val _month = if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                    val _date = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                    val _pickedDate = "$_year-$_month-$_date"
                    Log.e("PickedDate: ", "Date: $_pickedDate") //2019-02-12

                    val c2 = Calendar.getInstance()
                    var mHour = c2[Calendar.HOUR_OF_DAY]
                    var mMinute = c2[Calendar.MINUTE]

                    // Launch Time Picker Dialog

                    // Launch Time Picker Dialog
                    val timePickerDialog = TimePickerDialog(
                        this,
                        { view, hourOfDay, minute ->
                            mHour = hourOfDay
                            mMinute = minute
                            var _pickedTime = ""

                            if (hourOfDay.toString().length == 1 && minute.toString().length == 1) {
                                _pickedTime =
                                    "0" + hourOfDay.toString() + ":" + "0" + minute.toString()
                            } else if (hourOfDay.toString().length == 1) {
                                _pickedTime = "0" + hourOfDay.toString() + ":" + minute.toString()
                            } else if (minute.toString().length == 1) {
                                _pickedTime = hourOfDay.toString() + ":" + "0" + minute.toString()
                            } else {
                                _pickedTime = hourOfDay.toString() + ":" + minute.toString()
                            }
                            Log.e(
                                "PickedTime: ",
                                "Date: $_pickedDate" + _pickedTime
                            ) //2019-02-12
//                            et_show_date_time.setText(date_time.toString() + " " + hourOfDay + ":" + minute)
                            val tz = TimeZone.getDefault()
                            presenter.addreminder(
                                intent.getStringExtra("id").toString(),
                                client_id,
                                "appointment",
                                "lead",
                                _pickedDate + " " + _pickedTime + ":00", tz.id
                            )
                            mInfoBuilderTwo.dismiss()
                        },
                        mHour,
                        if (mMinute == 60) 0 + 2 else if (mMinute == 59) 0 + 1 else mMinute + 2,
                        false
                    )
                    timePickerDialog.show()

                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.MONTH)
            )
            dialog.datePicker.minDate = System.currentTimeMillis() - 1000
            dialog.show()

        }
        mInfoBuilderTwo.findViewById<TextView>(R.id.tvDone)
            .setOnClickListener {
                mInfoBuilderTwo.dismiss()
            }
        mInfoBuilderTwo.show()
    }

    val mBuilder: Dialog by lazy { Dialog(this@LeadsActivity) }

    fun showImagePop() {
        Pix.start(this@LeadsActivity, options)
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
            takePictureIntent.resolveActivity(this@LeadsActivity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    ConstUtils.createImageFile(this@LeadsActivity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this@LeadsActivity,
                        "${this@LeadsActivity.packageName}.provider",
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
        mFile = Compressor(this@LeadsActivity)
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
        mFile = Compressor(this@LeadsActivity)
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

    override fun onSingleItemClick(item: Any, position: Int) {
        startActivity(
            Intent(this, CustomerDetailActivity::class.java)
                .putExtra("id", mListSubUsers[position]._id)
                .putExtra("name", mListSubUsers[position].name)
                .putExtra("email", mListSubUsers[position].email)
                .putExtra("phone", mListSubUsers[position].phone_no)
                .putExtra("trade", mListSubUsers[position].trade)
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

    override fun invoke() {

    }

    override fun onResume() {
        presenter.getLeadDetail(intent.getStringExtra("id").toString())
        super.onResume()

    }
}