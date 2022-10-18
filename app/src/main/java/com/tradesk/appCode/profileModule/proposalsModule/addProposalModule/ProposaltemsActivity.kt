package com.tradesk.appCode.profileModule.proposalsModule.addProposalModule

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.appCode.profileModule.proposalsModule.IProposalsView
import com.tradesk.appCode.profileModule.proposalsModule.PropsoalsPresenter
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.AddProposalActivity.Companion.mAddItemDataUpdate
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.AddProposalActivity.Companion.sub_total_amount
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.AddProposalActivity.Companion.tax_amount
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.AddProposalActivity.Companion.total_amount
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.utils.extension.customFullDialog
import com.tradesk.utils.extension.toast
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_proposaltems.*
import java.lang.NumberFormatException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject
import android.widget.CompoundButton




class ProposaltemsActivity : BaseActivity(), IProposalsView, SingleItemCLickListener {

    var select_old = ""
    var select_old_title = ""
    var select_old_description = ""
    var taxRate=10;

    val mList = arrayListOf<ItemsData>()
    val mDefaultItemsAdapter by lazy { DefaultItemsAdapter(this, this, mList) }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onAttach(this)
        setContentView(R.layout.activity_proposaltems)

        rvDefaultItems.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rvDefaultItems.adapter = mDefaultItemsAdapter

        if (isInternetConnected()) {
            presenter.getProposalItemslist()
        }

        mIvAddNew.setOnClickListener {
            createEditItem {
                addItem(it)
            }
        }

        mIvBack.setOnClickListener {
            finish()
        }
        if (intent.hasExtra("taxRate")){
            taxRate=intent.getStringExtra("taxRate")!!.toInt()
        }
    }

    override fun onAdd(it: AddProposalsModel) {

    }

    override fun onList(it: PorposalsListModel) {

    }

    override fun onDefaultList(it: DefaultItemsModel) {
        mList.clear()
        mList.addAll(it.data.items_data)
        mDefaultItemsAdapter.notifyDataSetChanged()
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

    }

    override fun onProfile(it: ProfileModel) {
        TODO("Not yet implemented")
    }

    override fun onGeneratedToken(lastAction: String) {

    }

    override fun onSingleItemClick(item: Any, position: Int) {
        select_old_title = mList[position].title
        select_old_description = mList[position].description
        select_old = "1"
        createEditItem {
            addItem(it)
        }
    }

    fun createEditItem(data: AddItemDataProposal? = null, onSuccess: (AddItemDataProposal) -> Unit) {
        customFullDialog(R.layout.add_estimate_item) { v, d ->
            val etName = v.findViewById<TextInputEditText>(R.id.etName)
            val etDesc = v.findViewById<TextInputEditText>(R.id.etDesc)
            val etQty = v.findViewById<TextInputEditText>(R.id.etQty)
            val etCost = v.findViewById<TextInputEditText>(R.id.etCost)
            val subTotal = v.findViewById<TextView>(R.id.subTotal)
            val itemTax = v.findViewById<TextView>(R.id.itemTax)
            val total = v.findViewById<TextView>(R.id.total)
            val isTaxable = v.findViewById<Switch>(R.id.isTaxable)

            if (select_old.isNotEmpty()) {
                etName.setText(select_old_title)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    etDesc.setText(Html.fromHtml(select_old_description.replace("\\n","\n"), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    etDesc.setText(Html.fromHtml(select_old_description));
                }
            }


            isTaxable.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if(!etCost.text.isNullOrEmpty()&&!etQty.text.isNullOrEmpty()) {
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


                        sub_total_amount = sub_total.toString()
                        total_amount = totalamount.toString()
                        tax_amount = tax.toString()
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
                }
                // do something, the isChecked will be
                // true if the switch is in the On position
            })
            etCost.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (etCost.text.toString().isNotEmpty()) {
                        if (etCost.text.toString().isNotEmpty()) {

                            var sub_total =
                                etCost.text.toString().replace(",", "")
                                    .toInt() * etQty.text.toString()
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







                            sub_total_amount = sub_total.toString()
                            total_amount = totalamount.toString()
                            tax_amount = tax.toString()

                        }
                    }else toast("Please enter quantity to continue")

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
                       if (etCost.text.toString().isNotEmpty()){

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

                           sub_total_amount = sub_total.toString()
                           total_amount = totalamount.toString()
                           tax_amount = tax.toString()
                       }
                    } catch (nfe: NumberFormatException) {
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


                    mAddItemDataUpdate.add(
                        AddItemDataUpdateProposal(
                            etName.text.toString(),
                            etDesc.text.toString(),
                            etQty.text.toString(),
                            etCost.text.toString(),
                            if (isTaxable.isChecked) "1" else "0"
                        )

                    )

                    select_old = ""
                    finish()

                    val intent = Intent("itemupdated")
                    intent.putExtra("itemupdated", "itemupdated")
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

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

    fun String.addFormatDecimal(currency: Boolean = true, twoDecimal: Boolean = false): String {
        var v = "0"
        if (isEmpty()) v = "0" else v = this
        var format = "%.2f"
        if (twoDecimal.not()) {
            if (contains(".")) {
                format = if (substringAfter(".").length > 2) "%.3f" else "%.2f"
            } else format = "%.2f"
        }
        val amount = String.format(Locale.getDefault(), format, (v.replace("%","")).toDouble())
        val currencyInstance = NumberFormat.getCurrencyInstance(Locale.US);
        currencyInstance.currency = Currency.getInstance("USD")
        return if (currency) currencyInstance.format(amount.toDouble()) else amount
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



}