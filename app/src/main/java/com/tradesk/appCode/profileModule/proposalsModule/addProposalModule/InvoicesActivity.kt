package com.tradesk.appCode.profileModule.proposalsModule.addProposalModule

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.tradesk.R
import com.tradesk.appCode.home.customersModule.CustomersActivity
import com.tradesk.appCode.profileModule.proposalsModule.IProposalsView
import com.tradesk.appCode.profileModule.proposalsModule.PropsoalsPresenter
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.listeners.OnItemRemove
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.utils.CommonUtil
import com.tradesk.utils.ConstUtils
import com.tradesk.utils.DatePickerHelper
import com.tradesk.utils.extension.customCenterDialog
import com.tradesk.utils.extension.customFullDialog
import com.tradesk.utils.extension.loadWallImage
import com.tradesk.utils.extension.toast
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_add_proposal.*
import kotlinx.android.synthetic.main.activity_invoices.*
import kotlinx.android.synthetic.main.activity_invoices.mBtnPreview
import kotlinx.android.synthetic.main.activity_invoices.mBtnSave
import kotlinx.android.synthetic.main.activity_invoices.mEtExtraInfo
import kotlinx.android.synthetic.main.activity_invoices.mEtTaxes
import kotlinx.android.synthetic.main.activity_invoices.mIvAddImage
import kotlinx.android.synthetic.main.activity_invoices.mIvAddSig
import kotlinx.android.synthetic.main.activity_invoices.mIvBack
import kotlinx.android.synthetic.main.activity_invoices.mIvCalendar
import kotlinx.android.synthetic.main.activity_invoices.mIvChangeDefault
import kotlinx.android.synthetic.main.activity_invoices.mIvMainImage
import kotlinx.android.synthetic.main.activity_invoices.mLAddClient
import kotlinx.android.synthetic.main.activity_invoices.mLItemAdd
import kotlinx.android.synthetic.main.activity_invoices.mTvClientName
import kotlinx.android.synthetic.main.activity_invoices.mTvDates
import kotlinx.android.synthetic.main.activity_invoices.mTvSubTotal
import kotlinx.android.synthetic.main.activity_invoices.mTvTax
import kotlinx.android.synthetic.main.activity_invoices.mTvTotals
import kotlinx.android.synthetic.main.activity_invoices.rvInflateItem
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class AddItemDataInvoice(
    var name: String,
    var desc: String,
    var qty: Int = 0,
    var cost: Int = 0,
    var total: Int = 0
)


public data class AddItemDataUpdateInvoice(
    var title: String,
    var description: String,
    var quantity: String,
    var amount: String,
)

class InvoicesActivity : BaseActivity(), IProposalsView, SingleItemCLickListener, OnItemRemove {


    var users = ""
    var date_pass = ""
    var clients_id = ""
    var clients_name = ""
    var clients_email = ""
    var clients_address = ""
    var clients_phone = ""
    var sales = ""
    var lat = ""
    var lng = ""
    var city = ""
    var state = ""
    var post_code = ""
    var type = ""
    var todaydate = ""
    var status = "pending"
    var myImageUri = ""
    var taxRate = 10
    var mySignatureUri = ""
    var mFile: File? = null

    var touch_pad_client = false
    var selectedimage_key = ""
    var contract_started_text = ""
    var selectedimage_client = ""
    var mail_sent = "true"
    var estimate_start = "00000"

    var select_old = ""
    var select_old_title = ""
    var select_old_description = ""
    var isEditMode = false;
    lateinit var proposalDetailModel: ProposalDetailModel;
    val mAddItemData = arrayListOf<AddItemDataProposal>()


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
        setContentView(R.layout.activity_invoices)
        presenter.onAttach(this)

        val formatter_amount = DecimalFormat("#,###,###")

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
        mIvChangeDefault.setOnClickListener { mEtTaxes.setText("10") }

        mIvCalendar.setOnClickListener {
            showDatePickerDialog()
        }
        mIvAddImage.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                showImagePop()
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
            createEditItemNew {
                addItem(it)
            }
        }
        findViewById<EditText>(R.id.mEtTaxes).setOnClickListener {
            showTaxDialog()
        }
        if (intent.hasExtra("is_EditMode")) {
            isEditMode = true
            mBtnPreview.setText("Update and Review")
            findViewById<TextView>(R.id.textView6).setText("Update Invoice")
            mBtnSave.setText("Update")
            val builder = GsonBuilder()
            val gson = builder.create()
            var projectJsonString = intent.getStringExtra("proposalData");
            proposalDetailModel = gson.fromJson(projectJsonString, ProposalDetailModel::class.java)
            setData()
        }

        mBtnSave.setOnClickListener {

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
            mail_sent = "false"
            val myJSONObjects: ArrayList<JSONObject> =
                ArrayList<JSONObject>(mAddItemDataUpdate.size)

            for (i in 0 until mAddItemDataUpdate.size) {

                val decoded_data: String =
                    URLEncoder.encode(mAddItemDataUpdate.get(i).description.toString(), "UTF-8")
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
                toast("Please add invoice items")
            } else {


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
                            "client_id",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                clients_id
                            )
                        )

                        it.put(
                            "estimate",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                intent.getStringExtra("count").toString()

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
                            "tax_rate",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                taxRate.toString()
                            )
                        )

                        it.put(
                            "type",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                "invoice"
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


                        if (mFile != null) {
                            it.put(
                                "image\"; filename=\"image.jpg",
                                RequestBody.create(MediaType.parse("image/*"), mFile!!)
                            )
                        }


                        if (mFileSignature != null) {
                            it.put(
                                "client_signature\"; filename=\"image.jpg",
                                RequestBody.create(MediaType.parse("image/*"), mFileSignature!!)
                            )
                        }


                        if (!isEditMode) {
                            presenter.addProposals(it)
                        } else {
                            it.put(
                                "id",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    proposalDetailModel.data.proposal_details._id
                                )
                            )
                            presenter.updateProposal(it)
                        }
                    }

            }
        }
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


//                    val encoded_data: String = URLEncoder.encode(mAddItemDataUpdate.get(i).description, "UTF-8")
//                    val decoded_data: String = URLDecoder.decode(encoded_data.replace("+"," "), "UTF-8")
//                    toast(decoded_data)


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
                obj.put("description", mAddItemDataUpdate.get(i).description)
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
            }else if (mEtExtraInfo.text.isEmpty()) {
                toast("Please add extra info")
            } /*else if (mEtEstimates.text.isEmpty()) {
                    toast("Please enter estimate")
                }*/ else if (mAddItemDataUpdate.isEmpty()) {
                toast("Please add invoice items")
            } else {


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
                            "client_id",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                clients_id
                            )
                        )

                        it.put(
                            "estimate",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                intent.getStringExtra("count").toString()

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
                                "invoice"
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


                        if (mFile != null) {
                            it.put(
                                "image\"; filename=\"image.jpg",
                                RequestBody.create(MediaType.parse("image/*"), mFile!!)
                            )
                        }


                        if (mFileSignature != null) {
                            it.put(
                                "client_signature\"; filename=\"image.jpg",
                                RequestBody.create(MediaType.parse("image/*"), mFileSignature!!)
                            )
                        }


                        if (!isEditMode) {
                            presenter.addProposals(it)
                        } else {
                            it.put(
                                "id",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    proposalDetailModel.data.proposal_details._id
                                )
                            )
                            presenter.updateProposal(it)
                        }

                    }

            }
        }

    }
    fun showTaxDialog() {
        customCenterDialog(R.layout.addtax_dialog, true) { v, d ->
            val taxRateEdit = v.findViewById<EditText>(R.id.mEtTaxes)
            val button:Button =v.findViewById(R.id.done_button);
            button.setOnClickListener() {
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
        mTvDates.setText(CommonUtil.getFormatedDateString(proposalDetail.date))
        date_pass = (proposalDetail.date)
        type = (proposalDetail.type)
        status = (proposalDetail.status)
        for (item in proposalDetail.items) {
            var taxAble="0";
            if (item.textAble){
                taxAble="1"
            }
            var actualItem = AddItemDataUpdateProposal(
                item.title,
                AddProposalActivity.getDecodedString(item.description),
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
            mIvMainImage.loadWallImage(
                proposalDetail.images[0]
            )
        }
        mEtExtraInfo.setText(proposalDetail.extra_info)
        if (proposalDetail.my_signature != null && proposalDetail.my_signature.isNotEmpty()) {
            switchMySign.isChecked = true;
        }
        if (proposalDetail.client_signature != null && proposalDetail.client_signature.isNotEmpty()) {
            clientSignSwitch.isChecked = true;
        }
    }

    override fun onAdd(it: AddProposalsModel) {
        if (mail_sent.equals("true")) {
            toast(it.message)
            startActivity(
                Intent(
                    this@InvoicesActivity,
                    PDFViewNewActivity::class.java
                )
                    .putExtra("pdfurl", it.data.pdfLink)
                    .putExtra("id", it.data._id)
                    .putExtra("email", clients_email)
            )
            finish()
        } else {
            toast("Invoice Saved Successfully")
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

    override fun onProfile(it: ProfileModel) {
        TODO("Not yet implemented")
    }

    override fun onGeneratedToken(lastAction: String) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ConstUtils.REQUEST_TAKE_PHOTO) {
                CropImage.activity(Uri.parse(myImageUri))
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

    val mBuilder: Dialog by lazy { Dialog(this@InvoicesActivity) }

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
                dispatchTakePictureIntent()
            }
        mBuilder.findViewById<TextView>(R.id.titleGallery)
            .setOnClickListener {
                mBuilder.dismiss()
                dispatchTakeGalleryIntent()
            }
        mBuilder.findViewById<TextView>(R.id.titleCancel)
            .setOnClickListener { mBuilder.dismiss() }
        mBuilder.show();
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(this@InvoicesActivity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    ConstUtils.createImageFile(this@InvoicesActivity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this@InvoicesActivity,
                        "${this@InvoicesActivity.packageName}.provider",
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
        mFile = Compressor(this@InvoicesActivity)
            .setMaxHeight(1000).setMaxWidth(1000)
            .setQuality(99)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .compressToFile(file)

        mIvMainImage.loadWallImage(mFile!!.absolutePath)


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
        customFullDialog(R.layout.add_estimate_item_invoices) { v, d ->
            val etName = v.findViewById<TextInputEditText>(R.id.etName)
            val etDesc = v.findViewById<TextInputEditText>(R.id.etDesc)
            val etQty = v.findViewById<TextInputEditText>(R.id.etQty)
            val etCost = v.findViewById<TextInputEditText>(R.id.etCost)
            val subTotal = v.findViewById<TextView>(R.id.subTotal)
            val itemTax = v.findViewById<TextView>(R.id.itemTax)
            val total = v.findViewById<TextView>(R.id.total)
            val isTaxable = v.findViewById<Switch>(R.id.isTaxable)

            if (tax.equals("1")) {
                isTaxable.isChecked = true
            } else {
                isTaxable.isChecked = false
            }

            etName.setText(title)
            etQty.setText(qty)
            etCost.setText(amount)
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
                        tax = sub_total * 10 / 100
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
                // do something, the isChecked will be
                // true if the switch is in the On position
            })
            etCost.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (etCost.text.toString().isNotEmpty()) {

                        var sub_total =
                            etCost.text.toString().replace(",", "").toInt() * etQty.text.toString()
                                .toInt()

                        var tax = 0
                        if (isTaxable.isChecked) {
                            tax = sub_total * 10 / 100
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
//                                subtotal = java.lang.String.valueOf(mAddItemDataUpdate.get(i).getId())
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



                        tax_loop = subtotal_loop * 10 / 100
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
                    d.dismiss()
                } else toast("All fields are required!")
            }

        }
    }


    companion object {
        var mFileSignature: File? = null
        val mAddItemDataUpdate = arrayListOf<AddItemDataUpdateProposal>()
        var sub_total_amount = ""
        var total_amount = ""
        var tax_amount = ""
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



                            tax_loop = subtotal_loop * 10 / 100
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
                        setTotals()
                    }

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiver)
    }

    override fun onSingleItemClick(item: Any, position: Int) {

        createEditItem(
            mAddItemDataUpdate[position].title,
            mAddItemDataUpdate[position].description,
            mAddItemDataUpdate[position].amount,
            mAddItemDataUpdate[position].quantity,
            position,
            mAddItemDataUpdate[position].tax.toInt()
        )

    }


    fun createEditItemNew(
        data: AddItemDataProposal? = null,
        onSuccess: (AddItemDataProposal) -> Unit
    ) {
        customCenterDialog(R.layout.additem_dialog,true) { v, d ->
            val etName = v.findViewById<EditText>(R.id.etName)
            val etDesc = v.findViewById<EditText>(R.id.etDesc)
            val etQty = v.findViewById<TextInputEditText>(R.id.etQty)
            val etCost = v.findViewById<EditText>(R.id.etCost)
            val subTotal = v.findViewById<TextView>(R.id.subTotal)
            val itemTax = v.findViewById<TextView>(R.id.itemTax)
            val total = v.findViewById<TextView>(R.id.total)
            val isTaxable = v.findViewById<Switch>(R.id.isTaxable)

            isTaxable.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked && !etCost.text.isNullOrEmpty()) {

                    var sub_total =
                        etCost.text.toString().replace(",", "").toInt() * etQty.text.toString()
                            .toInt()

                    var tax = 0
                    if (isTaxable.isChecked) {
                        tax = sub_total * 10 / 100
                    } else {
                        tax = 0
                    }

                    var totalamount = sub_total + tax


                    val formatter = DecimalFormat("#,###,###")


                    subTotal.setText("$ " + formatter.format(sub_total))
                    itemTax.setText("$ " + formatter.format(tax))
                    total.setText("$ " + formatter.format(totalamount))


                   sub_total_amount = sub_total.toString()
                    total_amount = totalamount.toString()
                    tax_amount = tax.toString()
                }
                // do something, the isChecked will be
                // true if the switch is in the On position
            })
            etCost.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (etCost.text.toString().isNotEmpty()) {

                        var sub_total =
                            etCost.text.toString().replace(",", "").toInt() * etQty.text.toString()
                                .toInt()

                        var tax = 0
                        if (isTaxable.isChecked) {
                            tax = sub_total * 10 / 100
                        } else {
                            tax = 0
                        }

                        var totalamount = sub_total + tax


                        val formatter = DecimalFormat("#,###,###")


                        subTotal.setText("$ " + formatter.format(sub_total))
                        itemTax.setText("$ " + formatter.format(tax))
                        total.setText("$ " + formatter.format(totalamount))




                        InvoicesActivity.sub_total_amount = sub_total.toString()
                        InvoicesActivity.total_amount = totalamount.toString()
                        InvoicesActivity.tax_amount = tax.toString()


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
//            v.findViewById<ImageView>(R.id.mIvBack).setOnClickListener {
//                d.dismiss()
//            }
            v.findViewById<Button>(R.id.btnDone).setOnClickListener {
                if (etName.text!!.isNotEmpty() && etDesc.text!!.isNotEmpty() && etQty.text!!.isNotEmpty() && etCost.text!!.isNotEmpty()) {

                    InvoicesActivity.mAddItemDataUpdate.add(
                        AddItemDataUpdateProposal(
                            etName.text.toString(),
                            etDesc.text.toString(),
                            etQty.text.toString(),
                            etCost.text.toString(),
                            if (isTaxable.isChecked) "1" else "0"
                        )

                    )

                    select_old = ""

                    var subtotal_loop = 0
                    var tax_loop = 0
                    var total_loop = 0

                    for (i in mAddItemDataUpdate.indices) {
                        if (subtotal_loop == 0) {
//                                subtotal = java.lang.String.valueOf(mAddItemDataUpdate.get(i).getId())
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



                        tax_loop = subtotal_loop * 10 / 100
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
                    setTotals()
                    d.dismiss()
                } else toast("All fields are required!")
            }

            data?.let {
                etName.setText(it.name)
                etDesc.setText(it.desc)
                etQty.setText(it.qty)
                etCost.setText(it.cost.toString())
            }
        }
    }

    fun addItem(addItemData: AddItemDataProposal) {
//        val view = LayoutInflater.from(this).inflate(R.layout.added_estimate_item, null, false)
//        view.findViewById<TextView>(R.id.tvName).text = addItemData.name
//        view.findViewById<TextView>(R.id.tvDesc).text = addItemData.desc
//        view.findViewById<TextView>(R.id.tvPrice).text = "$ " + addItemData.cost.toString()
//        view.setOnClickListener {
//            createEditItem(addItemData) {
//                view.findViewById<TextView>(R.id.tvName).text = it.name
//                view.findViewById<TextView>(R.id.tvDesc).text = it.desc
//                view.findViewById<TextView>(R.id.tvPrice).text = "$ " + it.cost.toString()
//            }
//        }


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

    override fun onRemove(item: Any, position: Int) {
        if (mAddItemDataUpdate.isNotEmpty()) {
            mAddItemDataUpdate.removeAt(position)
            mProSelectedItemsAdapter.notifyItemRemoved(position)
            mProSelectedItemsAdapter.notifyItemRangeChanged(position, mAddItemDataUpdate.size)
            setTotals()
        }
    }


}