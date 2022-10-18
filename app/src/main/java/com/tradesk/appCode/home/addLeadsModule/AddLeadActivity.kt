package com.tradesk.appCode.home.addLeadsModule

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.BuildzerApp
import com.tradesk.R
import com.tradesk.appCode.home.customersModule.CustomersActivity
import com.tradesk.appCode.home.salePersonModule.SalesPersonActivity
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.databinding.ActivityAddLeadBinding
import com.tradesk.listeners.AddClientListener
import com.tradesk.listeners.AddSalesListener
import com.tradesk.utils.CommonUtil
import com.tradesk.utils.CommonUtil.createPartFromArray
import com.tradesk.utils.ConstUtils
import com.tradesk.utils.DatePickerHelper
import com.tradesk.utils.extension.toast
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.GsonBuilder
import com.tradesk.appCode.MainActivity
import com.tradesk.utils.convertDateFormatWithTime
import kotlinx.android.synthetic.main.activity_add_jobs.*
import kotlinx.android.synthetic.main.activity_add_lead.*
import kotlinx.android.synthetic.main.activity_add_lead.clientsRv
import kotlinx.android.synthetic.main.activity_add_lead.mIvAddSales
import kotlinx.android.synthetic.main.activity_add_lead.mIvBack
import kotlinx.android.synthetic.main.activity_add_lead.mIvCustomer
import kotlinx.android.synthetic.main.activity_add_lead.salesRv
import kotlinx.android.synthetic.main.activity_signature.*
import kotlinx.android.synthetic.main.activity_userscontract.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.IOException
import java.util.*
import java.util.function.Predicate
import javax.inject.Inject
import kotlin.collections.ArrayList


class AddLeadActivity : BaseActivity(), IAddLeadsView, AddClientListener, AddSalesListener {

    //    var users = mutableListOf<String>();
    var users = "";
    var clients = ""
    var sales = ""
    var lat = ""
    var lng = ""
    var city = ""
    var state = ""
    var post_code = ""
    var zip_code = ""
    var Phone_Number_Count = 0
    var mList = mutableListOf<Client>()
    var salesList = mutableListOf<Client>()
    val clientAdapter by lazy { AddClientAdapter(this, mList, mList, this) }
    val salesAdapter by lazy { AddSalesAdapter(this, salesList, salesList, this) }
    lateinit var clientDummyImageView: ImageView;
    lateinit var salesDummyImageView: ImageView;
    lateinit var datePicker: DatePickerHelper
    lateinit var leadDetailModel: LeadDetailModel;
    lateinit var binding: ActivityAddLeadBinding;
    var isEditMode = false;
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
    lateinit var presenter: AddLeadsPresenter<IAddLeadsView>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLeadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.hasExtra("edit")) {
            val builder = GsonBuilder()
            val gson = builder.create()
            var projectJsonString = intent.getStringExtra("lead");
            leadDetailModel = gson.fromJson(projectJsonString, LeadDetailModel::class.java)
            if (leadDetailModel != null) {
                setLeadData()
            }
        }
        clientDummyImageView = findViewById(R.id.imageView16);
        salesDummyImageView = findViewById(R.id.mIvSalesImage);
        presenter.onAttach(this)
        datePicker = DatePickerHelper(this, true)
        mEtAddress.setOnFocusChangeListener { view, b ->
            if (b)
                openPlaceDialog()
        }
        mIvBack.setOnClickListener { finish() }

        mIvDate.setOnClickListener {
            showDatePickerDialog()
        }
        clientsRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        salesRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        clientsRv.adapter = clientAdapter
        salesRv.adapter = salesAdapter
        mBtnSubmit.setOnClickListener {
            if (mEtName.text.toString().trim().isEmpty()) {
                toast(getString(R.string.enter_project_name), false)
            }/* else if (mEtDesc.text.toString().trim().isEmpty()) {
                toast(getString(R.string.enter_desc), false)
            }*/ else if (mEtAddress.text.toString().trim().isEmpty()) {
                toast(getString(R.string.enter_address), false)
            } else if (mList.isEmpty()) {
                toast(getString(R.string.select_client), false)
            } else if (salesList.isEmpty()) {
                toast(getString(R.string.select_sales), false)
            } else if (mTvDate.text.toString().trim().isEmpty()) {
                toast(getString(R.string.enter_startdate), false)
            } else {
                clients = ""
                var list = mutableListOf<String>()
                for (client: Client in mList) {
                    list.add(client._id);
                }
                for (client: Client in salesList) {
                    client._id = client._id.replace("\"", "")
                    list.add(client._id);
                }
                users = list.toString();

                if (isInternetConnected()) hashMapOf<String, RequestBody>().also {
                    it.put(
                        "project_name",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            mEtName.text.toString()
                        )
                    )

                    if (mEtDesc.text.toString().isNotEmpty()) {
                        it.put(
                            "description",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mEtDesc.text.toString()
                            )
                        )
                    }

                    if (mEtName2.text.toString().isNotEmpty()) {
                        it.put(
                            "source",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                mEtName2.text.toString()
                            )
                        )
                    }

                    it.put(
                        "startDate",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            mTvDate.text.toString()
                        )
                    )

                    it.put(
                        "type",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            "lead"
                        )
                    )

                    it.put(
                        "address",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            mEtAddress.text.toString()
                        )
                    )

                    it.put(
                        "city",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            city
                        )
                    )

                    it.put(
                        "state",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            state
                        )
                    )

                    it.put(
                        "zipcode",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            zip_code
                        )
                    )

                    it.put(
                        "latLong",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            lat + "," + lng
                        )
                    )

//                    it.put(
//                        "users_assigned[]",
//                        RequestBody.create(
//                            MediaType.parse("multipart/form-data"),
//                            convertToBytes(list)
//                        )
//                    )

//                    it.put(
//                        "image\"; filename=\"image.jpg",
//                        RequestBody.create(MediaType.parse("image/*"), mFile!!)
//                    )

                    if (!isEditMode) {
                        presenter.add_leads(it, createPartFromArray(list as ArrayList<String>))
                    } else {
                        it.put(
                            "id",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                leadDetailModel.data.leadsData._id
                            )
                        )
                        presenter.updateLeads(it, createPartFromArray(list as ArrayList<String>))
                    }
                }
            }
        }
        mIvAddSales.setOnClickListener {

            val i = Intent(this, SalesPersonActivity::class.java)
            i.putExtra("from", "Add")
            startActivityForResult(i, 2222)
        }

        mIvCustomer.setOnClickListener {
            val i = Intent(this, CustomersActivity::class.java)
            i.putExtra("from", "Add")
            startActivityForResult(i, 1111)
        }
    }



    private fun setLeadData() {
        isEditMode = true;
        binding.textView6.text = "Edit Lead"
        binding.mEtName.setText(leadDetailModel.data.leadsData.project_name)
        binding.mEtDesc.setText(leadDetailModel.data.leadsData.description)
        binding.mEtName2.setText(leadDetailModel.data.leadsData.source)
        binding.mEtAddress.setText(leadDetailModel.data.leadsData.address.city)
        binding.mTvDate.text = convertDateFormatWithTime(leadDetailModel.data.leadsData.startDate)
        city = leadDetailModel.data.leadsData.address.city
        lat = leadDetailModel.data.leadsData.address.location.coordinates[0].toString();
        lng = leadDetailModel.data.leadsData.address.location.coordinates[1].toString();
        post_code = leadDetailModel.data.leadsData.address.zipcode
        zip_code = leadDetailModel.data.leadsData.address.zipcode
        state = leadDetailModel.data.leadsData.address.state
        binding.mBtnSubmit.setText("Update")

        if (leadDetailModel.data.leadsData.client != null && leadDetailModel.data.leadsData.client.isNotEmpty()) {
            getClients()
        }
        if (leadDetailModel.data.leadsData.sales != null && leadDetailModel.data.leadsData.sales.isNotEmpty()) {
            getSalesUsers()
        }
    }

    private fun getClients() {
        var list = mutableListOf<Client>()
        var clientList = mutableListOf<Client>()
        var salesList = mutableListOf<Client>()
        for (clientDetail: ClientLeadDetail in leadDetailModel.data.leadsData.client) {
            var locationClients = LocationClients(
                if (clientDetail.address.location != null && clientDetail.address.location.location != null && clientDetail.address.location.location._id != null) clientDetail.address.location.location._id else "",
                if (clientDetail.address.location != null && clientDetail.address.location.location != null && clientDetail.address.location.location.coordinates != null) clientDetail.address.location.location.coordinates else ArrayList(),
                clientDetail.type
            )
            var addressClient = AddressClient(
                clientDetail.address.city,
                locationClients,
                clientDetail.address.state,
                clientDetail.address.street,
                clientDetail.address.zipcode
            )
            var client = Client(
                clientDetail.__v,
                clientDetail._id,
                clientDetail.active,
                addressClient,
                clientDetail.createdAt,
                clientDetail.created_by,
                clientDetail.deleted,
                clientDetail.email,
                clientDetail.image,
                clientDetail.name,
                clientDetail.phone_no,
                "",
                clientDetail.trade,
                clientDetail.type,
                clientDetail.updatedAt
            );
            list.add(client)

        }
        if (list.isNotEmpty()) {
            mList.clear()
            mList.addAll(list)
            clientAdapter.notifyDataSetChanged()
        }
    }

    private fun getSalesUsers() {
        var list = mutableListOf<Client>()
        var clientList = mutableListOf<Client>()
        var salesList = mutableListOf<Client>()
        for (clientDetail: Sale in leadDetailModel.data.leadsData.sales) {
            var locationClients = LocationClients(
                if (clientDetail.address.location != null && clientDetail.address.location.coordinates != null) clientDetail.address.location._id else "",
                if (clientDetail.address.location != null && clientDetail.address.location.coordinates != null) clientDetail.address.location.coordinates else ArrayList(),
                clientDetail.type
            )
            var addressClient = AddressClient(
                clientDetail.address.city,
                locationClients,
                clientDetail.address.state,
                clientDetail.address.street,
                clientDetail.address.zipcode
            )
            var client = Client(
                clientDetail.__v,
                clientDetail._id,
                clientDetail.active,
                addressClient,
                clientDetail.createdAt,
                clientDetail.created_by,
                clientDetail.deleted,
                clientDetail.email,
                clientDetail.image,
                clientDetail.name,
                clientDetail.phone_no,
                "",
                clientDetail.trade,
                clientDetail.type,
                clientDetail.updatedAt
            );
            list.add(client)

        }
        if (list.isNotEmpty()) {
            this.salesList.clear()
            this.salesList.addAll(list)
            salesAdapter.notifyDataSetChanged()
        }
    }

    override fun onAddLeads(it: SuccessModel) {
        Handler().postDelayed({
            CommonUtil.showSuccessDialog(MainActivity.context,"Lead Added")
        }, 1500)
        toast(it.message)
        finish()
    }

    override fun onUpdateLeads(it: SuccessModel) {
        toast(it.message)
        finish()
    }

    override fun onerror(data: String) {
        toast(data)
    }

    override fun onGeneratedToken(lastAction: String) {

    }

    fun openPlaceDialog() {
        val fields = Arrays.asList(
            Place.Field.PHONE_NUMBER,
            Place.Field.BUSINESS_STATUS,
            Place.Field.ADDRESS,
            Place.Field.NAME,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.ID,
            Place.Field.LAT_LNG
        )
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this@AddLeadActivity)
        startActivityForResult(intent, ConstUtils.PLACE_AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun setPlaceData(placeData: Place) {
        try {
            if (placeData != null) {
                mEtAddress.setText("")
                city = ""
                state = ""
                post_code = ""

                if (!placeData.name!!.lowercase(Locale.getDefault()).isEmpty()) {
                    mEtAddress.setText(placeData.name)
                    mEtAddress.setSelection(mEtAddress.text!!.length)
                } else mEtAddress.error = "Not available"

                for (i in 0 until placeData.addressComponents!!.asList().size) {
                    val place: AddressComponent =
                        placeData.addressComponents!!.asList().get(i)

                    if (city.trim().isEmpty()) {
                        if (place.types.contains("neighborhood") && place.types.contains("political")) {
                            city = place.name
                        }
                    }

                    if (city.trim().isEmpty()) {
                        if (place.types.contains("locality") && place.types.contains("political")) {
                            city = place.name
                        }
                    }

                    if (place.types.contains("administrative_area_level_1")) {
                        state = place.name
                    }
                    if (place.types.contains("postal_code")) {
                        post_code = place.name
                    }
                    var address: Address = getAddressFromLocation(
                        placeData.latLng!!.latitude,
                        placeData.latLng!!.longitude
                    )
                    if (address.postalCode == null) {
                        address.postalCode = "unknown"
                    }
                    if (address.locality == null) {
                        address.locality = "unknown"
                    }
                    if (address.adminArea == null) {
                        address.adminArea = "unknown"
                    }
                    zip_code = getZipCodeFromLocation(address)
                    state = address.adminArea
                    city = address.locality
                }


                lng = placeData.latLng!!.longitude.toString()
                lat = placeData.latLng!!.latitude.toString()
            }
        } catch (e: java.lang.Exception) {
            Log.e("Exce......", e.toString())
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            /*if (requestCode == ConstUtils.REQUEST_TAKE_PHOTO) {
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
            } else*/ if (requestCode == ConstUtils.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                try {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    setPlaceData(place)
                } catch (e: java.lang.Exception) {
                }
            } else if (requestCode == 1111) {
                if (resultCode == Activity.RESULT_OK) {
                    clients = ""
                    clients = data!!.getStringExtra("result").toString()
                    var projectJsonString = data!!.getStringExtra("client").toString()
                    val builder = GsonBuilder()
                    val gson = builder.create()
                    val client = gson.fromJson(projectJsonString, Client::class.java)
                    if (mList != null && mList.isNotEmpty() && !containsId(mList, client._id)) {
                        mList.add(client)
                    } else if (mList.isEmpty()) {
                        mList.add(client)

                    } else {
                        toast("Already added")
                    }
                    if (mList.isNotEmpty()) {
//                        clientsRv.isVisible = true
                        clientDummyImageView.visibility = View.INVISIBLE
                        clientAdapter.notifyDataSetChanged()
                    } else clientDummyImageView.visibility = View.VISIBLE
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    // Write your code if there's no result
                }
            } else if (requestCode == 2222) {
                if (resultCode == Activity.RESULT_OK) {
                    sales = ""
                    sales = data!!.getStringExtra("result").toString()
//                    if (data.getStringExtra("image").toString().isNotEmpty()) {
//                        mIvSalesImage.loadWallImage(data.getStringExtra("image").toString())
//                    }
                    var projectJsonString = data!!.getStringExtra("client").toString()
                    val builder = GsonBuilder()
                    val gson = builder.create()
                    val client = gson.fromJson(projectJsonString, Client::class.java)
                    if (salesList != null && salesList.isNotEmpty() && !containsId(salesList, client._id)) {
                        salesList.add(client)
                    } else if (salesList.isEmpty()) {
                        salesList.add(client)

                    } else {
                        toast("Already added")
                    }
                    if (salesList.isNotEmpty()) {
//                        clientsRv.isVisible = true
                        salesDummyImageView.visibility = View.INVISIBLE
                        salesAdapter.notifyDataSetChanged()
                    } else
                        salesDummyImageView.visibility = View.VISIBLE
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    // Write your code if there's no result
                }
            }
            /*if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    saveCaptureImageResults(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }*/
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun containsId(list: List<Client>, id: String?): Boolean {
        return list.stream().filter(Predicate<Client> { o: Client ->
            o._id.equals(id)
        }).findFirst().isPresent()
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

                mTvDate.text = "${monthStr}-${dayStr}-${year}"+" "+ CommonUtil.getCurrentTimeOnly()


            }
        })
    }

    companion object {
        private fun getZipCodeFromLocation(addr: Address): String {

            return if (addr.postalCode == null) "" else addr.postalCode
        }

        private fun getAddressFromLocation(lat: Double, long: Double): Address {
            val geocoder = Geocoder(BuildzerApp.appContext)
            var address = Address(Locale.getDefault())
            try {
                val addr: List<Address> =
                    geocoder.getFromLocation(lat, long, 1)
                if (addr.size > 0) {
                    address = addr[0]
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return address
        }
    }

    override fun onAddClientClick(item: Client, position: Int) {
        if (mList.isNotEmpty()) {
            mList.remove(item)
            clientAdapter.notifyItemRemoved(position)
            clientAdapter.notifyItemRangeChanged(position, mList.size)
            if (mList.isEmpty()) {
                clientDummyImageView.visibility = View.VISIBLE
            }
        }
    }

    override fun onAddSalesClick(item: Client, position: Int) {
        if (salesList.isNotEmpty()) {
            salesList.remove(item)
            salesAdapter.notifyItemRemoved(position)
            salesAdapter.notifyItemRangeChanged(position, salesList.size)
            if (mList.isEmpty()) {
                salesDummyImageView.visibility = View.VISIBLE
            }
        }
    }


}