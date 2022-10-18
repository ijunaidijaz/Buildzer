package com.tradesk.appCode.profileModule.proposalsModule.addProposalModule


import android.Manifest
import android.R.attr
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.tradesk.R
import com.tradesk.appCode.activities.PaymentSchduleActivity
import com.tradesk.appCode.adapters.AttachedDocsAdapter
import com.tradesk.appCode.adapters.DocsAdapter
import com.tradesk.appCode.home.customersModule.CustomersActivity
import com.tradesk.appCode.jobsModule.jobDetailModule.jobChatModule.ImagesPagerAdapter
import com.tradesk.appCode.profileModule.proposalsModule.IProposalsView
import com.tradesk.appCode.profileModule.proposalsModule.PropsoalsPresenter
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.listeners.AttachedDocListener
import com.tradesk.listeners.DocListener
import com.tradesk.listeners.OnItemRemove
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.utils.CommonUtil
import com.tradesk.utils.ConstUtils
import com.tradesk.utils.ConstUtils.Companion.REQUEST_UPLOAD_DOC
import com.tradesk.utils.DatePickerHelper
import com.tradesk.utils.extension.customCenterDialog
import com.tradesk.utils.extension.customFullDialog
import com.tradesk.utils.extension.toast
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_add_proposal.*
import kotlinx.android.synthetic.main.activity_add_proposal.dots
import kotlinx.android.synthetic.main.activity_add_proposal.mBtnPreview
import kotlinx.android.synthetic.main.activity_add_proposal.mBtnSave
import kotlinx.android.synthetic.main.activity_add_proposal.mEtExtraInfo
import kotlinx.android.synthetic.main.activity_add_proposal.mEtTaxes
import kotlinx.android.synthetic.main.activity_add_proposal.mIvAddImage
import kotlinx.android.synthetic.main.activity_add_proposal.mIvAddSig
import kotlinx.android.synthetic.main.activity_add_proposal.mIvBack
import kotlinx.android.synthetic.main.activity_add_proposal.mIvCalendar
import kotlinx.android.synthetic.main.activity_add_proposal.mIvChangeDefault
import kotlinx.android.synthetic.main.activity_add_proposal.mIvMainImage
import kotlinx.android.synthetic.main.activity_add_proposal.mLAddClient
import kotlinx.android.synthetic.main.activity_add_proposal.mLItemAdd
import kotlinx.android.synthetic.main.activity_add_proposal.mTvClientName
import kotlinx.android.synthetic.main.activity_add_proposal.mTvDates
import kotlinx.android.synthetic.main.activity_add_proposal.mTvSubTotal
import kotlinx.android.synthetic.main.activity_add_proposal.mTvTax
import kotlinx.android.synthetic.main.activity_add_proposal.mTvTotals
import kotlinx.android.synthetic.main.activity_add_proposal.rvInflateItem
import kotlinx.android.synthetic.main.activity_add_sales_person.*
import kotlinx.android.synthetic.main.activity_invoices.*
import kotlinx.android.synthetic.main.activity_job_detail.*
import kotlinx.android.synthetic.main.add_estimate_item.*
import kotlinx.android.synthetic.main.docs_dialog.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


data class AddItemDataProposal(
    var name: String,
    var desc: String,
    var qty: Int = 0,
    var cost: Int = 0,
    var total: Int = 0,
    var tax: Int = 0
)


public data class AddItemDataUpdateProposal(
    var title: String,
    var description: String,
    var quantity: String,
    var amount: String,
    var tax: String,
)


class AddProposalActivity : BaseActivity(), IProposalsView, SingleItemCLickListener, OnItemRemove,
        () -> Unit,
    AttachedDocListener, DocListener {


    var users = ""
    var date_pass = ""
    var clients_id = ""
    var clients_name = ""
    var clients_email = ""
    var clients_address = ""
    var clients_phone = ""
    var sales = ""
    var taxRate = 10
    var lat = ""
    var lng = ""
    var city = ""
    var state = ""
    var post_code = ""
    var type = "invoice"
    var todaydate = ""
    var status = "pending"

    var myImageUri = ""
    var mySignatureUri = ""
    var mFile: File? = null
    var mDocFile: File? = null
    lateinit var viewPager: ViewPager

    var touch_pad_client = false
    var selectedimage_key = ""
    var contract_started_text = ""
    var selectedimage_client = ""
    var mail_sent = "true"
    var estimate = ""
    var selectedDoc = ""
    var estimate_start = "00000"
    var isClientSign = false;
    var isMySign = false;
    var isEditMode = false;
    var isUrl = true;
    var isUploadingDoc = false;

    var filesList = mutableListOf<File>()
    var selectedDocList = mutableListOf<String>()
    lateinit var proposalDetailModel: ProposalDetailModel;
    val mAddItemData = arrayListOf<AddItemDataProposal>()
    val imagesAdapter by lazy { ImagesPagerAdapter(this, imagesList, this) }
    var imagesList = mutableListOf<String>()
    val docsAdapter by lazy { DocsAdapter(this, this, selectedDocList) }

    lateinit var mySignSwitch: Switch;
    lateinit var clientSignSwitch: Switch;

    lateinit var datePicker: DatePickerHelper
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
    lateinit var presenter: PropsoalsPresenter<IProposalsView>
    val mProSelectedItemsAdapter by lazy {
        ProSelectedItemsAdapter(
            this,
            this,
            this,
            mAddItemDataUpdate
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_proposal)
        presenter.onAttach(this)
        mySignSwitch = findViewById(R.id.switchMySign)
        viewPager = findViewById(R.id.mIvMainImage);
        clientSignSwitch = findViewById(R.id.clientSignSwitch)
        if (intent.hasExtra("is_proposal")) {
            mBtnSave.visibility = View.GONE
            type = "proposal"
        }

        mySignSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            isMySign = isChecked
        }
        clientSignSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            isClientSign = isChecked
        }
        val formatter_amount = DecimalFormat("#,###,###")


        if (intent.hasExtra("count")) {
            findViewById<LinearLayout>(R.id.proposalNumber).visibility = View.VISIBLE
            estimateNoEdit.setText(intent.getStringExtra("count").toString())
            estimate = intent.getStringExtra("count").toString();
        }
        mPrefs.setKeyValue("myTitle", intent.getStringExtra("title").toString());


        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadCastReceiver, IntentFilter("itemupdated"))
        if (intent.hasExtra("client_name")) {
            mTvClientName.setText("Client : " + intent.getStringExtra("client_name"))
            clients_id = intent.getStringExtra("client_id").toString()
            clients_email = intent.getStringExtra("client_email").toString()
            mLAddClient.visibility = View.GONE
            mTvClientName.visibility = View.VISIBLE
        }

        datePicker = DatePickerHelper(this, true)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        todaydate = sdf.format(Date())
        mAddItemDataUpdate.clear()

        rvInflateItem.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rvInflateItem.adapter = mProSelectedItemsAdapter

        mIvBack.setOnClickListener { finish() }
        addPaymentScedule.setOnClickListener { startActivity(Intent(this,PaymentSchduleActivity::class.java)) }
        attachDoc.setOnClickListener { presenter.getProfile() }
        crossDoc.setOnClickListener {
            selectedDoc = ""
            selectedDocLayout.visibility = View.GONE
            atttachDocLayout.visibility = View.VISIBLE
            uploadDoc.visibility = View.VISIBLE
            docName.text = ""
            if (mDocFile != null) {
                mDocFile = null
            }
        }
        findViewById<EditText>(R.id.mEtTaxes).setOnClickListener {
            showTaxDialog()
        }
//        findViewById<EditText>(R.id.estimateNoEdit).addTextChangedListener(textWatcher)
        findViewById<EditText>(R.id.mEtTaxes).doAfterTextChanged {

        }
        mIvChangeDefault.setOnClickListener {
            mEtTaxes.setText("10")
            taxRate = 10
            try {
                if (mAddItemDataUpdate != null && mAddItemDataUpdate.isNotEmpty()) {
                    setTotals()
                }
            } catch (nfe: java.lang.NumberFormatException) {
                nfe.printStackTrace()
                toast("Exception")
            }
        }

        mIvCalendar.setOnClickListener {
            showDatePickerDialog()
        }
        mIvAddImage.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                showImagePop()
            }
        }
        uploadDoc.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                filePicker()
            }
        }


        mLAddClient.setOnClickListener {
            val i = Intent(this, CustomersActivity::class.java)
            i.putExtra("from", "Add")
            startActivityForResult(i, 3333)
        }

        mIvAddSig.setOnClickListener {

//            sign.visibility=View.VISIBLE
//            buttons_container.visibility=View.VISIBLE
            val i = Intent(this, SignatureActivity::class.java)
            i.putExtra("from", "Add")
            startActivityForResult(i, 5555)
        }

        mLItemAdd.setOnClickListener {
            startActivity(
                Intent(this, ProposaltemsActivity::class.java).putExtra(
                    "taxRate",
                    mEtTaxes.text.toString()
                )
            )
        }

        if (intent.hasExtra("is_EditMode")) {
            isEditMode = true
            mBtnPreview.setText("Update and Review")
            mBtnSave.setText("Update")
            val builder = GsonBuilder()
            val gson = builder.create()
            var projectJsonString = intent.getStringExtra("proposalData");
            proposalDetailModel = gson.fromJson(projectJsonString, ProposalDetailModel::class.java)
            setData()
        }

//        mBtnSave.setOnClickListener {
//
//            var subTotal = 0
//            var total_sub = 0
//            var totalTax = 0
//            var item_tax = 0
//            var item_total = 0
//            for (i in mAddItemDataUpdate.indices) {
//                var item_sub = 0;
//                if (subTotal == 0) {
//                    var quantity = 1
////                                subtotal = java.lang.String.valueOf(mAddItemDataUpdate.get(i).getId())
//                    item_sub =
//                        mAddItemDataUpdate.get(i).amount.replace("$", "").replace(",", "").toInt()
//                    quantity =
//                        mAddItemDataUpdate.get(i).quantity.replace("$", "").replace(",", "").toInt()
//                    item_sub = item_sub * quantity;
//                } else {
//                    var quantity = 1
//                    item_sub = mAddItemDataUpdate.get(i).amount.replace("$", "")
//                        .replace(",", "").toInt()
//                    quantity =
//                        mAddItemDataUpdate.get(i).quantity.replace("$", "").replace(",", "").toInt()
//                    item_sub = item_sub * quantity;
//                }
//
//                if (mAddItemDataUpdate.get(i).tax.equals("1")) {
//                    item_tax = subTotal * taxRate / 100
//                    totalTax += item_tax;
//                } else {
//                    item_tax = 0
//                }
//                item_total += item_sub + item_tax;
//                subTotal += item_sub
//            }
//            mail_sent = "false"
//            val myJSONObjects: ArrayList<JSONObject> =
//                ArrayList<JSONObject>(mAddItemDataUpdate.size)
//
//            for (i in 0 until mAddItemDataUpdate.size) {
//
//                val decoded_data: String =
//                    URLEncoder.encode(mAddItemDataUpdate.get(i).description.toString(), "UTF-8")
//                val obj = JSONObject()
//                obj.put("title", mAddItemDataUpdate.get(i).title.toString())
//                obj.put("description", decoded_data)
//                obj.put("quantity", mAddItemDataUpdate.get(i).quantity.toString())
//                obj.put("amount", mAddItemDataUpdate.get(i).amount.toString())
//                myJSONObjects.add(obj)
//            }
//
//            Log.e("Array data", myJSONObjects.toString())
//
//            if (clients_id.isEmpty()) {
//                toast("Please select client")
//            } else if (mTvDates.text.isEmpty()) {
//                toast("Please select date")
//            } /*else if (mEtEstimates.text.isEmpty()) {
//                    toast("Please enter estimate")
//                }*/ else if (mAddItemDataUpdate.isEmpty()) {
//                toast("Please add proposal items")
//            } else {
//
//
//                if (isInternetConnected())
//                    hashMapOf<String, RequestBody>().also {
//
//                        if (intent.hasExtra("job_id")) {
//                            it.put(
//                                "contract_id",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    intent.getStringExtra("job_id")
//                                )
//                            )
//                        }
//                        it.put(
//                            "client_id",
//                            RequestBody.create(
//                                MediaType.parse("multipart/form-data"),
//                                clients_id
//                            )
//                        )
//
//                        it.put(
//                            "estimate",
//                            RequestBody.create(
//                                MediaType.parse("multipart/form-data"),
//                                estimate
//
//                            )
//                        )
//
//                        it.put(
//                            "subtotal",
//                            RequestBody.create(
//                                MediaType.parse("multipart/form-data"),
////                                (formatter_amount.format(sub_total_amount.toInt())).toString()
//                                subTotal.toString().replace(",", "")
//                            )
//                        )
//
//                        it.put(
//                            "tax",
//                            RequestBody.create(
//                                MediaType.parse("multipart/form-data"),
////                                (formatter_amount.format(item_tax.toInt()).toString())
//                                totalTax.toString().replace(",", "")
//                            )
//                        )
//
//                        it.put(
//                            "total",
//                            RequestBody.create(
//                                MediaType.parse("multipart/form-data"),
////                                (formatter_amount.format(total_amount.toInt())).toString()
//                                item_total.toString().replace(",", "")
//                            )
//                        )
//
//
//
//                        it.put(
//                            "extra_info",
//                            RequestBody.create(
//                                MediaType.parse("multipart/form-data"),
//                                mEtExtraInfo.text.toString().trim()
//                            )
//                        )
//
//                        it.put(
//                            "items",
//                            RequestBody.create(
//                                MediaType.parse("multipart/form-data"),
//                                myJSONObjects.toString()
//                            )
//                        )
//
//                        it.put(
//                            "status",
//                            RequestBody.create(
//                                MediaType.parse("multipart/form-data"),
//                                status
//                            )
//                        )
//
//
//                        it.put(
//                            "type",
//                            RequestBody.create(
//                                MediaType.parse("multipart/form-data"),
//                                type
//                            )
//                        )
//
//
//                        it.put(
//                            "mail_sent",
//                            RequestBody.create(
//                                MediaType.parse("multipart/form-data"),
//                                "false"
//                            )
//                        )
//
//
//                        it.put(
//                            "date",
//                            RequestBody.create(
//                                MediaType.parse("multipart/form-data"),
//                                date_pass
//                            )
//                        )
//
//
//                        if (mFile != null) {
//                            it.put(
//                                "image\"; filename=\"image.jpg",
//                                RequestBody.create(MediaType.parse("image/*"), mFile!!)
//                            )
//                        }
//
//
//                        if (mDocFile != null) {
//                            it.put(
//                                "document\"; filename=\"image.jpg",
//                                RequestBody.create(MediaType.parse("image/*"), mDocFile!!)
//                            )
//
//                        } else {
//                            it.put(
//                                "doc_url",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    selectedDoc
//                                )
//                            )
//                            it.put(
//                                "isUrl",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    "true"
//                                )
//                            )
//                        }
//
//                        if (mFileSignature != null) {
//                            it.put(
//                                "client_signature\"; filename=\"image.jpg",
//                                RequestBody.create(MediaType.parse("image/*"), mFileSignature!!)
//                            )
//                        }
//
//
//                        if (!isEditMode) {
////                            presenter.addProposals(it)
//                        } else {
//                            it.put(
//                                "id",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    proposalDetailModel.data.proposal_details._id
//                                )
//                            )
////                            presenter.updateProposal(it)
//                        }
////                        presenter.addProposalsrwp(it,myJSONObjects)
//
//                    }
//
//            }
//        }
        mBtnPreview.setOnClickListener {


            var subTotal = 0
            var total_sub = 0
            var totalTax = 0
            var item_tax = 0
            var item_total = 0
            for (i in mAddItemDataUpdate.indices) {
                var item_sub = 0;
                if (subTotal == 0) {
                    var quantity = 1
//                                subtotal = java.lang.String.valueOf(mAddItemDataUpdate.get(i).getId())
                    item_sub =
                        mAddItemDataUpdate.get(i).amount.replace("$", "").replace(",", "").toInt()
                    quantity =
                        mAddItemDataUpdate.get(i).quantity.replace("$", "").replace(",", "").toInt()
                    item_sub = item_sub * quantity;
                } else {
                    var quantity = 1
                    item_sub = mAddItemDataUpdate.get(i).amount.replace("$", "")
                        .replace(",", "").toInt()
                    quantity =
                        mAddItemDataUpdate.get(i).quantity.replace("$", "").replace(",", "").toInt()
                    item_sub = item_sub * quantity;
                }

                if (mAddItemDataUpdate.get(i).tax.equals("1")) {
                    item_tax = item_sub * taxRate / 100
                    totalTax += item_tax;
                } else {
                    item_tax = 0
                }
                item_total += item_sub + item_tax;
                subTotal += item_sub
            }

            val myJSONObjects: ArrayList<JSONObject> =
                ArrayList<JSONObject>(mAddItemDataUpdate.size)

            for (i in 0 until mAddItemDataUpdate.size) {
                val encodedString: String = Base64.getEncoder()
                    .encodeToString(mAddItemDataUpdate.get(i).description.toByteArray())

                val decodedBytes = Base64.getDecoder().decode(encodedString)
                val decodedString = String(decodedBytes)

                Log.d("encoded", encodedString)
                Log.d("decoded", decodedString)

                val encodedString2: String = Base64.getUrlEncoder()
                    .encodeToString(mAddItemDataUpdate.get(i).description.toByteArray())
                val decodedBytes2 = Base64.getUrlDecoder().decode(encodedString2)
                val decodedString2 = String(decodedBytes2)
                Log.d("encoded2", encodedString2)
                Log.d("decoded2", decodedString2)


                val obj = JSONObject()
                obj.put("title", mAddItemDataUpdate.get(i).title.toString())
                obj.put("description", mAddItemDataUpdate.get(i).description.toString())
                if (mAddItemDataUpdate.get(i).tax.equals("1")) {
                    obj.put("textAble", true)
                }else{
                    obj.put("textAble", false)
                }
                obj.put("quantity", mAddItemDataUpdate.get(i).quantity.toString())
                obj.put("amount", mAddItemDataUpdate.get(i).amount.toString())
                myJSONObjects.add(obj)
            }
            Log.e("Array data", myJSONObjects.toString())

            if (clients_id.isEmpty()) {
                toast("Please select client")
            } else if (mTvDates.text.isEmpty()) {
                toast("Please select date")
            } else if (mEtExtraInfo.text.isEmpty()) {
                toast("Please add extra info")
            } /*else if (mEtEstimates.text.isEmpty()) {
                    toast("Please enter estimate")
                }*/ else if (mAddItemDataUpdate.isEmpty()) {
                toast("Please add proposal items")
            } else {

                var docsList = arrayListOf<RequestBody>()
                var jsonArray: JSONArray? = JSONArray()
                var filesParts = arrayListOf<MultipartBody.Part>()
                if (isInternetConnected())

                    hashMapOf<String, RequestBody>().also {

                        if (intent.hasExtra("job_id")) {
                            it.put(
                                "contract_id",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    intent.getStringExtra("job_id")
                                )
                            )
                        }

                        it.put(
                            "estimate",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                estimate

                            )
                        )

                        it.put(
                            "client_id",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                clients_id
                            )
                        )
                        it.put(
                            "tax_rate",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                taxRate.toString()
                            )
                        )

                        it.put(
                            "subtotal",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
//                                (formatter_amount.format(sub_total_amount.toInt())).toString()
                                subTotal.toString().replace(",", "")
                            )
                        )

                        it.put(
                            "tax",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
//                                (formatter_amount.format(item_tax.toInt()).toString())
                                totalTax.toString().replace(",", "")
                            )
                        )

                        it.put(
                            "total",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
//                                (formatter_amount.format(total_amount.toInt())).toString()
                                item_total.toString().replace(",", "")
                            )
                        )




                        it.put(
                            "extra_info",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mEtExtraInfo.text.toString().trim()
                            )
                        )

                        it.put(
                            "items",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                myJSONObjects.toString()
                            )
                        )


                        it.put(
                            "status",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                status
                            )
                        )


                        it.put(
                            "type",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                type
                            )
                        )


                        it.put(
                            "mail_sent",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                "false"
                            )
                        )


                        it.put(
                            "date",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                date_pass
                            )
                        )


//                        if (mFile != null) {
//                            it.put(
//                                "image\"; filename=\"image.jpg",
//                                RequestBody.create(MediaType.parse("image/*"), mFile!!)
//                            )
//                        }
                        if (!filesList.isNullOrEmpty()) {
                            filesParts = CommonUtil.getParts(filesList, "image")
                        }

                        if (mDocFile != null) {
                            it.put(
                                "document\"; filename=\"image.jpg",
                                RequestBody.create(MediaType.parse("image/*"), mDocFile!!)
                            )
                            it.put(
                                "isUrl",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    "false"
                                )
                            )

                        } else {
                            if (selectedDocList != null && selectedDocList.isNotEmpty()) {
                                docsList = getSelectedDocList()
                                it.put(
                                    "isUrl",
                                    RequestBody.create(
                                        MediaType.parse("multipart/form-data"),
                                        "true"
                                    )
                                )
                            }
                        }
                        if (isMySign && mFileSignature != null) {
                            it.put(
                                "my_signature\"; filename=\"image.jpg",
                                RequestBody.create(MediaType.parse("image/*"), mFileSignature!!)
                            )
                        }
                        if (isClientSign) {
                            it.put(
                                "client_signature",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    isClientSign.toString()
                                )
                            )
                        }
                        if (!isEditMode) {
                            presenter.addProposals(it, docsList, filesParts)
                        } else {
                            it.put(
                                "id",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    proposalDetailModel.data.proposal_details._id
                                )
                            )
                            if (!proposalDetailModel.data.proposal_details.images.isNullOrEmpty()) {
                                for (item in proposalDetailModel.data.proposal_details.images) {
                                    it.put(
                                        "image",
                                        RequestBody.create(
                                            MediaType.parse("multipart/form-data"),
                                            item
                                        )
                                    )
                                }
                            }
                            var existingDocs = arrayListOf<RequestBody>()
                            if (!proposalDetailModel.data.proposal_details.images.isNullOrEmpty()) {
                                existingDocs = getExistingDocList()
                            }
                            presenter.updateProposal(it, docsList, filesParts, existingDocs)
                        }
                    }

            }
        }

    }

    fun showTaxDialog() {
        customCenterDialog(R.layout.addtax_dialog, true) { v, d ->
            val taxRateEdit = v.findViewById<EditText>(R.id.mEtTaxes)
            val button:Button =v.findViewById(R.id.done_button);
            button.setOnClickListener {
                if (!taxRateEdit.text.isNullOrEmpty()) {
                    taxRate = taxRateEdit.text.toString().toInt()
                    findViewById<EditText>(R.id.mEtTaxes).setText(taxRate.toString())
                    try {
                        if (mAddItemDataUpdate != null && mAddItemDataUpdate.isNotEmpty()) {
                            setTotals()
                        }
                        d.dismiss()
                    } catch (nfe: java.lang.NumberFormatException) {
                        nfe.printStackTrace()
                        toast("Exception")
                    }

                } else toast("Please enter value")

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData() {
        var proposalDetail: ProposalDetails = proposalDetailModel.data.proposal_details
        mTvClientName.setText("Client : " + proposalDetail.client_id.name)
        clients_id = proposalDetail.client_id._id
        clients_email = proposalDetail.client_id.email
        mLAddClient.visibility = View.GONE
        mTvClientName.visibility = View.VISIBLE
        estimateNoEdit.setText(proposalDetail.estimate)
        estimate = (proposalDetail.estimate)
        mEtTaxes.setText(proposalDetail.tax_rate)
        mTvDates.setText(CommonUtil.getFormatedDateString(proposalDetail.date))
        date_pass = (proposalDetail.date)
        type = (proposalDetail.type)
        status = (proposalDetail.status)
        taxRate=proposalDetail.tax_rate.toInt()
        for (item in proposalDetail.items) {
            var taxAble="0";
            if (item.textAble){
                taxAble="1"
            }
            var actualItem = AddItemDataUpdateProposal(
                item.title,
                item.description,
                item.quantity,
                item.amount,
                taxAble

            )
            mAddItemDataUpdate.add(actualItem)

            val intent = Intent("itemupdated")
            intent.putExtra("itemupdated", "itemupdated")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
        if (proposalDetail.images != null && proposalDetail.images.isNotEmpty()) {
//            mIvMainImage.loadWallImage(
//                proposalDetail.images[0]
//            )
            imagesList.clear()
            imagesList.addAll(proposalDetail.images)

            viewPager.adapter = imagesAdapter
            imagesAdapter.notifyDataSetChanged()
            dots.attachViewPager(viewPager)
        }
        mEtExtraInfo.setText(proposalDetail.extra_info)
        if (proposalDetail.my_signature != null && proposalDetail.my_signature.isNotEmpty()) {
            switchMySign.isChecked = true;
        }
        if (proposalDetail.client_signature != null && proposalDetail.client_signature.isNotEmpty()) {
            clientSignSwitch.isChecked = true;
        }
        if (proposalDetail.isUrl && !proposalDetail.doc_url.isNullOrEmpty()) {

            selectedDocList.addAll(proposalDetail.doc_url)
            documentsRV.visibility = View.VISIBLE
            documentsRV.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            documentsRV.adapter = docsAdapter
            docsAdapter.notifyDataSetChanged()

            uploadDoc.visibility = View.GONE
            docName.setOnClickListener { }

            mDocFile = null
        } else if (!proposalDetail.document.isNullOrEmpty()) {
            selectedDocLayout.visibility = View.VISIBLE
            crossDoc.visibility = View.VISIBLE
            atttachDocLayout.visibility = View.GONE
            if (!proposalDetail.document.isNullOrEmpty()) docName.text = proposalDetail.document[0]
            docName.setOnClickListener {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(proposalDetail.document[0]))
                browserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(browserIntent)
            }
            selectedDoc = ""
            selectedDocList.clear()
            isUploadingDoc = false
        }
    }

    fun getSelectedDocList(): ArrayList<RequestBody> {
        var list = arrayListOf<RequestBody>()
        for (it in selectedDocList) {
            list.add(CommonUtil.createBodyString(it.toString()))
        }
        return list
    }

    fun getExistingDocList(): ArrayList<RequestBody> {
        var list = arrayListOf<RequestBody>()
        for (it in proposalDetailModel.data.proposal_details.images) {
            list.add(CommonUtil.createBodyString(it.toString()))
        }
        return list
    }

    override fun onAdd(it: AddProposalsModel) {
        if (isEditMode) {
            toast("Updated Successfully")
            finish()
        } else if (mail_sent.equals("true")) {
            toast(it.message)
            startActivity(
                Intent(
                    this@AddProposalActivity,
                    PDFViewNewActivity::class.java
                ).putExtra("pdfurl", it.data.pdfLink)
                    .putExtra("id", it.data._id)
                    .putExtra("title", "proposals")
                    .putExtra("email", clients_email)
            )
            finish()
        } else {
            toast("Proposal Saved Successfully")
            finish()
        }
    }

    override fun onList(it: PorposalsListModel) {

    }

    override fun onDefaultList(it: DefaultItemsModel) {

    }

    override fun onDetails(it: ProposalDetailModel) {

    }

    override fun onSend(it: SuccessModel) {

    }

    override fun onChangeStatus(it: ChangeProposalStatus) {
        TODO("Not yet implemented")
    }

    override fun onDelete(it: SuccessModel) {

    }

    override fun onerror(data: String) {
        toast(data)
    }

    override fun onGeneratedToken(lastAction: String) {

    }

    override fun onProfile(it: ProfileModel) {
        showDocsPop(it)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ConstUtils.REQUEST_TAKE_PHOTO) {
                isUploadingDoc = false
                if (data != null) {
                    val photo = data.extras?.get("data")
                    var uri:Uri=getUriFromBitmap(this, photo as Bitmap,System.currentTimeMillis().toString(),"Camera");
                    CropImage.activity(uri)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
            //                    .setMinCropWindowSize(1000,1200)
            //                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
            //                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                        .setGuidelinesColor(android.R.color.transparent).start(this)
                }
            } else if (requestCode == ConstUtils.REQUEST_UPLOAD_DOC) {
//                isUploadingDoc = true
                CropImage.activity(Uri.parse(selectedDoc))
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setMinCropWindowSize(1000,1200)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(this)
            } else if (requestCode == ConstUtils.REQUEST_IMAGE_GET) {
                val uri: Uri = data?.data!!
                CropImage.activity(uri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setAspectRatio(2, 1)
//                    .setMinCropWindowSize(1000,1200)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(this)
            } else if (requestCode == ConstUtils.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                try {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
//                    setPlaceData(place)
                } catch (e: java.lang.Exception) {
                }
            } else if (requestCode == ConstUtils.PICKFILE_RESULT_CODE) {

                try {
                    if (data != null) {
                        val filePath =
                            data.data?.let { getRealPathFromUri(it, this) }
                        val parsedpath = filePath?.split("/")?.toTypedArray()
                        //now we will upload the file
                        //lets import okhttp first
                        var fileName = parsedpath?.get(parsedpath.size - 1)
                        var file = File(filePath)
                        mDocFile = file
                        selectedDocLayout.visibility = View.VISIBLE
                        crossDoc.visibility = View.VISIBLE
                        atttachDocLayout.visibility = View.GONE
                        docName.text = fileName
                        selectedDoc = ""
                        selectedDocList.clear()
                        isUploadingDoc = false


                    }

                } catch (e: java.lang.Exception) {
                }
            } else if (requestCode == 3333) {
                if (resultCode == Activity.RESULT_OK) {
                    clients_id = ""
                    clients_name = ""
                    clients_email = ""
                    clients_address = ""
                    clients_phone = ""
                    clients_id = data!!.getStringExtra("result").toString()
                    clients_name = data!!.getStringExtra("name").toString()
                    mTvClientName.setText("Client : " + clients_name)
                    mTvClientName.visibility = View.VISIBLE
                    clients_email = data!!.getStringExtra("email").toString()
                    clients_address = data!!.getStringExtra("address").toString()
                    clients_phone = data!!.getStringExtra("phonenumber").toString()
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    // Write your code if there's no result
                }
            } else if (requestCode == 4444) {
                if (resultCode == Activity.RESULT_OK) {
                    sales = ""
                    sales = data!!.getStringExtra("result").toString()
                    if (data!!.getStringExtra("image").toString().isNotEmpty()) {
//                        imageView223.loadWallImage(data!!.getStringExtra("image").toString())
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    // Write your code if there's no result
                }
            } else if (requestCode == 5555) {
                if (resultCode == Activity.RESULT_OK) {
                    mySignatureUri = ""
                    mySignatureUri = data!!.getStringExtra("result").toString()
                    if (data!!.getStringExtra("mySignatureUri").toString().isNotEmpty()) {
                        Handler().postDelayed({
                            CommonUtil.showSuccessDialog(this, "Signature Added")
                        }, 100)
                        switchMySign.isChecked = true
                        isMySign = true
//                        imageView223.loadWallImage(data!!.getStringExtra("image").toString())
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

    private fun showDatePickerDialog() {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)

        val minDate = Calendar.getInstance()
        minDate.set(Calendar.HOUR_OF_DAY, 0)
        minDate.set(Calendar.MINUTE, 0)
        minDate.set(Calendar.SECOND, 0)

        datePicker.setMinDate(minDate.timeInMillis)
        datePicker.showDialog(d, m, y, object : DatePickerHelper.Callback {
            override fun onDateSelected(dayofMonth: Int, month: Int, year: Int) {
                val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "${dayofMonth}"
                val mon = month + 1
                val monthStr = if (mon < 10) "0${mon}" else "${mon}"

                mTvDates.text = "${monthStr}-${dayStr}-${year}"
                date_pass = "${year}-${monthStr}-${dayStr}"

            }
        })
    }

    val mBuilder: Dialog by lazy { Dialog(this@AddProposalActivity) }

    fun showImagePop() {
        mBuilder.setContentView(R.layout.camera_dialog);
        mBuilder.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation;
        mBuilder.window!!.setGravity(Gravity.BOTTOM)
        mBuilder.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mBuilder.findViewById<TextView>(R.id.titleCamera)
            .setOnClickListener {
                mBuilder.dismiss()
                dispatchTakePictureIntent(ConstUtils.REQUEST_TAKE_PHOTO)
            }
        mBuilder.findViewById<TextView>(R.id.titleGallery)
            .setOnClickListener {
                mBuilder.dismiss()
                dispatchTakeGalleryIntent(ConstUtils.REQUEST_IMAGE_GET)
            }
        mBuilder.findViewById<TextView>(R.id.titleCancel)
            .setOnClickListener { mBuilder.dismiss() }
        mBuilder.show();
    }

    fun showUploadDocPop() {
        mBuilder.setContentView(R.layout.camera_dialog);
        mBuilder.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation;
        mBuilder.window!!.setGravity(Gravity.BOTTOM)
        mBuilder.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mBuilder.findViewById<TextView>(R.id.titleCamera)
            .setOnClickListener {
                mBuilder.dismiss()
                dispatchTakePictureIntent(ConstUtils.REQUEST_UPLOAD_DOC)
            }
        mBuilder.findViewById<TextView>(R.id.titleGallery)
            .setOnClickListener {
                mBuilder.dismiss()
                dispatchTakeGalleryIntent(REQUEST_UPLOAD_DOC)
            }
        mBuilder.findViewById<TextView>(R.id.titleCancel)
            .setOnClickListener { mBuilder.dismiss() }
        mBuilder.show();
    }

    fun showDocsPop(profileModel: ProfileModel) {
        if (profileModel.data.license_and_ins.docs_url != null && profileModel.data.license_and_ins.docs_url.isNotEmpty()) {
            mBuilder.setContentView(R.layout.docs_dialog);
            mBuilder.setCancelable(true)
            mBuilder.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation;
            mBuilder.window!!.setGravity(Gravity.BOTTOM)
            mBuilder.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            var docsRV: RecyclerView = mBuilder.findViewById<RecyclerView>(R.id.docsRV)
            var list = arrayListOf<String>()
            val attachedDocsAdapter by lazy {
                AttachedDocsAdapter(
                    this, this,
                    profileModel.data.license_and_ins.docs_url as MutableList<String>
                )
            }
            docsRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            docsRV.adapter = attachedDocsAdapter;
//            list.addAll(profileModel.data.license_and_ins.docs_url)
//            attachedDocsAdapter.notifyDataSetChanged()

            mBuilder.show();
        } else toast("No documents attached yet, Please upload new one")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun dispatchTakePictureIntent(requestCode: Int) {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            // Ensure that there's a camera activity to handle the intent
//            takePictureIntent.resolveActivity(this@AddProposalActivity.packageManager)?.also {
//                // Create the File where the photo should go
//                val photoFile: File? = try {
//                    ConstUtils.createImageFile(this@AddProposalActivity)
//                } catch (ex: IOException) {
//                    // Error occurred while creating the File
//                    null
//                }
//                // Continue only if the File was successfully created
//                photoFile?.also {
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                        this@AddProposalActivity,
//                        "${this@AddProposalActivity.packageName}.provider",
//                        it
//                    )
//                    myImageUri = photoURI.toString()
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, requestCode)
//                }
//            }
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), requestCode)
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, requestCode)
        }


    }

    private fun dispatchTakeGalleryIntent(requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        if (intent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(intent, requestCode)
        }
    }

    private fun saveCaptureImageResults(data: Uri) = try {
        if (isUploadingDoc) {
            val file = File(data.path!!)
            mDocFile = Compressor(this@AddProposalActivity)
                .setMaxHeight(1000).setMaxWidth(1000)
                .setQuality(99)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .compressToFile(file)
            selectedDocLayout.visibility = View.VISIBLE
            crossDoc.visibility = View.GONE
            atttachDocLayout.visibility = View.GONE
            docName.text = "document uploaded"
            selectedDoc = ""
            isUploadingDoc = false

        } else {
            val file = File(data.path!!)
            mFile = Compressor(this@AddProposalActivity)
                .setMaxHeight(1000).setMaxWidth(1000)
                .setQuality(99)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .compressToFile(file)

            if (!imagesList.contains(mFile!!.absolutePath)) imagesList.add(mFile!!.absolutePath)
            if (!filesList.contains(mFile)) {
                filesList.add(mFile!!)
            }
            viewPager.adapter = imagesAdapter
            imagesAdapter.notifyDataSetChanged()
            dots.attachViewPager(mIvMainImage)
        }
    } catch (e: Exception) {
    }

    fun createEditItem(
        title: String,
        description: String,
        amount: String,
        qty: String,
        position: Int,
        tax: Int
    ) {
        customFullDialog(R.layout.add_estimate_item) { v, d ->
            val etName = v.findViewById<TextInputEditText>(R.id.etName)
            val etDesc = v.findViewById<TextInputEditText>(R.id.etDesc)
            val etQty = v.findViewById<TextInputEditText>(R.id.etQty)
            val etCost = v.findViewById<TextInputEditText>(R.id.etCost)
            val subTotal = v.findViewById<TextView>(R.id.subTotal)
            val itemTax = v.findViewById<TextView>(R.id.itemTax)
            val total = v.findViewById<TextView>(R.id.total)
            val isTaxable = v.findViewById<Switch>(R.id.isTaxable)

            isTaxable.isChecked = tax == 1

            etName.setText(title)
            etQty.setText(qty)
            etCost.setText(amount)
            setItemsTotals(isTaxable, etCost, etQty, itemTax, subTotal, total)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                etDesc.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
            } else {
                etDesc.setText(Html.fromHtml(description));
            }

            isTaxable.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {

                    var sub_total =
                        etCost.text.toString().replace(",", "").toInt() * etQty.text.toString()
                            .toInt()

                    var tax = 0
                    if (isTaxable.isChecked) {
                        tax = sub_total * taxRate / 100
                    } else {
                        tax = 0
                    }

                    var totalamount = sub_total + tax


                    val formatter = DecimalFormat("#,###,###")


                    subTotal.setText("$ " + formatter.format(sub_total))
                    itemTax.setText("$ " + formatter.format(tax))
                    total.setText("$ " + formatter.format(totalamount))


                    AddProposalActivity.sub_total_amount = sub_total.toString()
                    AddProposalActivity.total_amount = totalamount.toString()
                    AddProposalActivity.tax_amount = tax.toString()
                } else {
                    var sub_total =
                        etCost.text.toString().replace(",", "").toInt() * etQty.text.toString()
                            .toInt()

                    var tax = 0
                    if (isTaxable.isChecked) {
                        tax = sub_total * taxRate / 100
                    } else {
                        tax = 0
                    }
                    tax = 0;

                    var totalamount = sub_total


                    val formatter = DecimalFormat("#,###,###")


                    subTotal.setText("$ " + formatter.format(sub_total))
                    itemTax.setText("$ " + formatter.format(tax))
                    total.setText("$ " + formatter.format(totalamount))


                    sub_total_amount = sub_total.toString()
                    total_amount = totalamount.toString()
                    tax_amount = tax.toString()
                }
            })
            etCost.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (etCost.text.toString().isNotEmpty()) {

                        var sub_total =
                            etCost.text.toString().replace(",", "").toInt() * etQty.text.toString()
                                .toInt()

                        var tax = 0
                        if (isTaxable.isChecked) {
                            tax = sub_total * taxRate / 100
                        } else {
                            tax = 0
                        }

                        var totalamount = sub_total + tax


                        val formatter = DecimalFormat("#,###,###")


//                        subTotal.setText("$ " + formatter.format(sub_total))
//                        itemTax.setText("$ " + formatter.format(tax))
//                        total.setText("$ " + formatter.format(totalamount))


                        AddProposalActivity.sub_total_amount = sub_total.toString()
                        AddProposalActivity.total_amount = totalamount.toString()
                        AddProposalActivity.tax_amount = tax.toString()

                    }


                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable) {
                    etCost.removeTextChangedListener(this)

                    try {
                        var originalString = s.toString()
                        val longval: Long
                        if (originalString.contains(",")) {
                            originalString = originalString.replace(",".toRegex(), "")
                        }
                        longval = originalString.toLong()
                        val formatter: DecimalFormat =
                            NumberFormat.getInstance(Locale.US) as DecimalFormat
                        formatter.applyPattern("#,###,###,###")
                        val formattedString: String = formatter.format(longval)

                        //setting text after format to EditText
                        etCost.setText(formattedString)
                        etCost.setSelection(etCost.getText()!!.length)
                    } catch (nfe: NumberFormatException) {
                        nfe.printStackTrace()
                    }

                    etCost.addTextChangedListener(this)
                }
            })
            etQty.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable) {
                    etQty.removeTextChangedListener(this)

                    try {
                        var originalString = s.toString()
                        val longval: Int
                        if (originalString.contains(",")) {
                            originalString = originalString.replace(",".toRegex(), "")
                        }
                        longval = originalString.toInt()
                        if (etCost.text.toString().isNotEmpty()) {

                            var sub_total =
                                etCost.text.toString().replace(",", "")
                                    .toInt() * longval
                                    .toInt()

                            var tax = 0
                            if (isTaxable.isChecked) {
                                tax = sub_total * taxRate / 100
                            } else {
                                tax = 0
                            }

                            var totalamount = sub_total + tax


                            val formatter = DecimalFormat("#,###,###")


                            subTotal.setText("$ " + formatter.format(sub_total))
                            itemTax.setText("$ " + formatter.format(tax))
                            total.setText("$ " + formatter.format(totalamount))







                            AddProposalActivity.sub_total_amount = sub_total.toString()
                            AddProposalActivity.total_amount = totalamount.toString()
                            AddProposalActivity.tax_amount = tax.toString()
                        }
                    } catch (nfe: java.lang.NumberFormatException) {
                        nfe.printStackTrace()
                    }

                    etQty.addTextChangedListener(this)
                }
            })

            v.findViewById<ImageView>(R.id.mIvBack).setOnClickListener {
                d.dismiss()
            }
            v.findViewById<TextView>(R.id.btnDone).setOnClickListener {
                if (etName.text!!.isNotEmpty() && etDesc.text!!.isNotEmpty() && etQty.text!!.isNotEmpty() && etCost.text!!.isNotEmpty()) {

                    mAddItemDataUpdate.removeAt(position)

                    mAddItemDataUpdate.add(
                        AddItemDataUpdateProposal(
                            etName.text.toString(),
                            etDesc.text.toString(),
                            etQty.text.toString(),
                            etCost.text.toString(),
                            if (isTaxable.isChecked) "1" else "0"
                        )
                    )

                    var subtotal_loop = 0
                    var tax_loop = 0
                    var total_loop = 0

                    for (i in mAddItemDataUpdate.indices) {
                        if (subtotal_loop == 0) {
//
                            subtotal_loop =
                                mAddItemDataUpdate.get(i).amount.replace("$", "").replace(",", "")
                                    .toInt()
                        } else {
                            subtotal_loop =
                                subtotal_loop + mAddItemDataUpdate.get(i).amount.replace(
                                    "$",
                                    ""
                                ).replace(",", "").toInt()
                        }

                        val formatter = DecimalFormat("#,###,###")
                        mTvSubTotal.setText("$ " + formatter.format(subtotal_loop))




                        if (mAddItemDataUpdate.get(i).tax.equals("1")) {
                            tax_loop = subtotal_loop * taxRate / 100
                            total_loop = subtotal_loop + tax_loop

                            mTvTax.setText("$ " + formatter.format(tax_loop))
                            mTvTotals.setText("$ " + formatter.format(total_loop))
                        } else {
                            tax_loop = subtotal_loop * taxRate / 100
                            total_loop = subtotal_loop + tax_loop
                            mTvTax.setText("$ " + "00")
                            mTvTotals.setText("$ " + formatter.format(subtotal_loop))
                        }

                    }
                    mProSelectedItemsAdapter.notifyDataSetChanged()
                    setTotals()
                    d.dismiss()
                } else toast("All fields are required!")
            }

        }
    }

    private fun setItemsTotals(
        isTaxable: Switch,
        etCost: TextInputEditText,
        etQty: TextInputEditText,
        itemTax: TextView,
        subTotal: TextView,
        total: TextView
    ) {

        var sub_total =
            etCost.text.toString().replace(",", "").toInt() * etQty.text.toString()
                .toInt()

        var tax = 0
        if (isTaxable.isChecked) {
            tax = sub_total * taxRate / 100
        } else {
            tax = 0
        }

        var totalamount = sub_total + tax


        val formatter = DecimalFormat("#,###,###")


        subTotal.setText("$ " + formatter.format(sub_total))
        itemTax.setText("$ " + formatter.format(tax))
        total.setText("$ " + formatter.format(totalamount))


        AddProposalActivity.sub_total_amount = sub_total.toString()
        AddProposalActivity.total_amount = totalamount.toString()
        AddProposalActivity.tax_amount = tax.toString()
    }


    companion object {
        var mFileSignature: File? = null
        var mAddItemDataUpdate = arrayListOf<AddItemDataUpdateProposal>()
        var sub_total_amount = ""
        var total_amount = ""
        var tax_amount = ""

        @RequiresApi(Build.VERSION_CODES.O)
        fun getDecodedString(text: String): String {
            val decodedBytes =
                Base64.getDecoder().decode(text)
            return String(decodedBytes)
        }
    }


    override fun onResume() {
        super.onResume()

    }

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.action) {
                "itemupdated" -> {
                    if (mAddItemDataUpdate.isNotEmpty()) {

                        var subtotal_loop = 0
                        var tax_loop = 0
                        var total_loop = 0
                        var total = 0
                        var subTotal = 0
                        var Totaltax = 0

                        for (i in mAddItemDataUpdate.indices) {
                            if (subtotal_loop == 0) {
//                                subtotal = java.lang.String.valueOf(mAddItemDataUpdate.get(i).getId())
                                subtotal_loop =
                                    mAddItemDataUpdate.get(i).amount.replace("$", "")
                                        .replace(",", "").toInt()
                            } else {
                                subtotal_loop =
                                    subtotal_loop + mAddItemDataUpdate.get(i).amount.replace(
                                        "$",
                                        ""
                                    ).replace(",", "").toInt()
                            }

                            val formatter = DecimalFormat("#,###,###")

                            mTvSubTotal.setText("$ " + formatter.format(subtotal_loop))



                            tax_loop = subtotal_loop * taxRate / 100
                            total_loop = subtotal_loop + tax_loop

                            if (mAddItemDataUpdate.get(i).tax.equals("1")) {
                                mTvTax.setText("$ " + formatter.format(tax_loop))
                            } else {
                                mTvTax.setText("$ " + "0")
                            }
//

                            if (mAddItemDataUpdate.get(i).tax.equals("1")) {

                                mTvTotals.setText("$ " + formatter.format(total_loop.toInt()))
//                                mTvTotals.setText("$ " +  total_loop.toString())
                            } else {
                                mTvTotals.setText("$ " + formatter.format(subtotal_loop.toInt()))
//                                mTvTotals.setText("$ " +  subtotal_loop.toString())
                            }
                        }
                        mProSelectedItemsAdapter.notifyDataSetChanged()
                        setTotals();
                    }
                }
            }
        }
    }

    fun setTotals() {
        var subTotal = 0
        var total_sub = 0

        var totalTax = 0
        var item_tax = 0
        var item_total = 0
        for (i in mAddItemDataUpdate.indices) {
            var item_sub = 0;
            if (subTotal == 0) {
                var quantity = 1
//                                subtotal = java.lang.String.valueOf(mAddItemDataUpdate.get(i).getId())
                item_sub =
                    mAddItemDataUpdate.get(i).amount.replace("$", "").replace(",", "").toInt()
                quantity =
                    mAddItemDataUpdate.get(i).quantity.replace("$", "").replace(",", "").toInt()
                item_sub = item_sub * quantity;
            } else {
                var quantity = 1
                item_sub = mAddItemDataUpdate.get(i).amount.replace("$", "")
                    .replace(",", "").toInt()
                quantity =
                    mAddItemDataUpdate.get(i).quantity.replace("$", "").replace(",", "").toInt()
                item_sub = item_sub * quantity;
            }

            if (mAddItemDataUpdate.get(i).tax.equals("1")) {
                item_tax = item_sub * taxRate / 100
                totalTax += item_tax;
            } else {
                item_tax = 0
            }
            item_total += item_sub + item_tax;
            subTotal += item_sub
        }
        mTvSubTotal.text = subTotal.toString()
        mTvTax.text = totalTax.toString()
        mTvTotals.text = item_total.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiver)
    }

    override fun onSingleItemClick(item: Any, position: Int) {
//        createEditItem(
//            mAddItemDataUpdate[position].title,
//            mAddItemDataUpdate[position].description,
//            mAddItemDataUpdate[position].amount,
//            mAddItemDataUpdate[position].quantity,
//            position,
//            mAddItemDataUpdate[position].tax.toInt()
//        )

    }

    fun getRandomNumber(): String {
        val r = Random(System.currentTimeMillis())
        return (10000 + r.nextInt(20000)).toString()
    }

    override fun onRemove(item: Any, position: Int) {
        if (mAddItemDataUpdate.isNotEmpty()) {
            mAddItemDataUpdate.removeAt(position)
            mProSelectedItemsAdapter.notifyItemRemoved(position)
            mProSelectedItemsAdapter.notifyItemRangeChanged(position, mAddItemDataUpdate.size)
            setTotals()
        }
    }

    override fun onDocClick(item: Any, position: Int) {
        if (!selectedDocList.contains(item.toString())) {
            selectedDocList.add(item.toString())
        } else return
        mBuilder.hide()
//        selectedDocLayout.visibility = View.VISIBLE
        documentsRV.visibility = View.VISIBLE
        documentsRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        documentsRV.adapter = docsAdapter
        docsAdapter.notifyDataSetChanged()

//        crossDoc.visibility = View.VISIBLE
//        atttachDocLayout.visibility = View.GONE
        uploadDoc.visibility = View.GONE
//        docName.text = item.toString()
//        selectedDoc = item.toString()
        mDocFile = null
    }

    private fun filePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        //  intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        startActivityForResult(intent, ConstUtils.PICKFILE_RESULT_CODE)
        isUploadingDoc = true;
    }

    private fun getDriveFilePath(uri: Uri, context: Context): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val file = File(context.cacheDir, name)
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable = inputStream!!.available()

            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            Log.e("File Size", "Size " + file.length())
            inputStream.close()
            outputStream.close()
            Log.e("File Path", "Path " + file.path)
            Log.e("File Size", "Size " + file.length())
        } catch (e: java.lang.Exception) {
            Log.e("Exception", e.message!!)
        }
        return file.path
    }

    fun getRealPathFromUri(uri: Uri, activity: Activity): String? {
        return getDriveFilePath(uri, activity)
    }

    override fun invoke() {
        TODO("Not yet implemented")
    }

    override fun onCrossClick(item: Any, position: Int) {
        selectedDocList.removeAt(position);
        if (selectedDocList.isEmpty()) {
            uploadDoc.visibility = View.VISIBLE
        }
        docsAdapter.notifyDataSetChanged()

    }

    @NonNull
    @Throws(IOException::class)
    private fun getUriFromBitmap(
        @NonNull context: Context, @NonNull bitmap: Bitmap,
        @NonNull displayName: String, @Nullable subFolder: String
    ): Uri {
        var relativeLocation = Environment.DIRECTORY_PICTURES
        if (!TextUtils.isEmpty(subFolder)) {
            relativeLocation += File.separator.toString() + subFolder
        }
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
        val resolver = context.contentResolver
        var stream: OutputStream? = null
        var uri: Uri? = null
        return try {
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            uri = resolver.insert(contentUri, contentValues)
            if (uri == null) {
                throw IOException("Failed to create new MediaStore record.")
            }
            stream = resolver.openOutputStream(uri)
            if (stream == null) {
                throw IOException("Failed to get output stream.")
            }
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream) == false) {
                throw IOException("Failed to save bitmap.")
            }
            uri
        } catch (e: IOException) {
            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null)
            }
            throw e
        } finally {
            stream?.close()
        }
    }

}