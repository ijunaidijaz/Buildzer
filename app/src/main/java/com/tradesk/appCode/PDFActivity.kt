package com.tradesk.appCode

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.print.PDFPrint.OnPDFPrintListener
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.isVisible
import com.tradesk.R
import com.tradesk.utils.extension.customFullDialog
import com.tradesk.utils.extension.toast
import com.google.android.material.textfield.TextInputEditText
import com.tejpratapsingh.pdfcreator.utils.FileManager
import com.tejpratapsingh.pdfcreator.utils.PDFUtil
import kotlinx.android.synthetic.main.activity_pdfactivity.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


data class AddItemData(
    var name: String,
    var desc: String,
    var qty: Int = 0,
    var cost: Float = 0f,
    var total: Float = 0f
)

class PDFActivity : AppCompatActivity() {
    var mFile: File? = null
    val displayMetrics by lazy { DisplayMetrics() }
    val mAddItemData = arrayListOf<AddItemData>()

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdfactivity)

        btnPreviewPDF.setOnClickListener {

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val todaydate = sdf.format(Date())

            val userinfo =
                createUserInfo(intent.getStringExtra("clientname").toString(), intent.getStringExtra("address").toString(), intent.getStringExtra("phone").toString(),todaydate)
            val iteminfo = createItemInfo()
            var subtotal = 0f
            mAddItemData.map { it.total }.forEach { subtotal += it }
            val subtotalinfo = createSubtotalInfo(intent.getStringExtra("clientname").toString(), "$subtotal")
            generatePDF(userinfo, iteminfo, subtotalinfo)
        }
        btnAddItem.setOnClickListener {
            createEditItem {
                addItem(it)
            }
        }
        btnEditPDF.setOnClickListener {
            pdfContainer.isVisible = false
            contentView.isVisible = true
        }
        btnShare.setOnClickListener {
            composeMail()
        }
    }

    fun createEditItem(data: AddItemData? = null, onSuccess: (AddItemData) -> Unit) {
        customFullDialog(R.layout.add_estimate_item) { v, d ->
            val etName = v.findViewById<TextInputEditText>(R.id.etName)
            val etDesc = v.findViewById<TextInputEditText>(R.id.etDesc)
            val etQty = v.findViewById<TextInputEditText>(R.id.etQty)
            val etCost = v.findViewById<TextInputEditText>(R.id.etCost)
            v.findViewById<ImageView>(R.id.mIvBack).setOnClickListener {
                d.dismiss()
            }
            v.findViewById<TextView>(R.id.btnDone).setOnClickListener {
                if (etName.text!!.isNotEmpty() && etDesc.text!!.isNotEmpty() && etQty.text!!.isNotEmpty() && etCost.text!!.isNotEmpty()) {
                    onSuccess.invoke(
                        AddItemData(
                            etName.text.toString(),
                            etDesc.text.toString(),
                            etQty.text.toString().toInt(),
                            etCost.text.toString().toFloat(),
                            etQty.text.toString().toInt().times(etCost.text.toString().toFloat())
                        )
                    )


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

    fun addItem(addItemData: AddItemData) {
        val view = LayoutInflater.from(this).inflate(R.layout.added_estimate_item, null, false)
        view.findViewById<TextView>(R.id.tvName).text = addItemData.name
        view.findViewById<TextView>(R.id.tvDesc).text = addItemData.desc
        view.findViewById<TextView>(R.id.tvPrice).text = addItemData.cost.toString()
        view.setOnClickListener {
            createEditItem(addItemData) {
                view.findViewById<TextView>(R.id.tvName).text = it.name
                view.findViewById<TextView>(R.id.tvDesc).text = it.desc
                view.findViewById<TextView>(R.id.tvPrice).text = it.cost.toString()
            }
        }
        llItemInflater.addView(view)
    }

    fun createUserInfo(name: String, address: String, phone_number: String, dates: String) = """
        <h1 align="center"><font color="#454545">Estimate</font></h1>
        <table width="100%">
        <tr>
        <td>
        <img src="fb.jpg" width="100" height="100"/>
        </td>
        <td>
        <p align="right">
        	<font color="#454545"><b>$name</b>
        	</br>
        	$address
        	</br>
        	$phone_number
        	</br></br>
        	Estimate #:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3
        	</br>
        	Date :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$dates
        	</font>
        </p>
        </td>
        </tr>
        <tr>
        <td>
        <h3>Construction</h3>
        </td>
        <td></td>
        </tr>
        </table>
    """.trimIndent()

    fun createItemInfo(): String {
        val items = StringBuilder("")
        llItemInflater.children.forEach {
            items.append(
                """
                     <tr>
                <td width="70%"><h4>${it.findViewById<TextView>(R.id.tvName).text}</br>${
                    it.findViewById<TextView>(
                        R.id.tvDesc
                    ).text
                }</h4></td>
        <td ><h4 align="right">1</h4></td>
        <td ><h4 align="right">${
                    it.findViewById<TextView>(R.id.tvPrice).text.toString().twoDecimalFormat()
                }</h4></td>
        <td ><h4 align="right">$${
                    it.findViewById<TextView>(R.id.tvPrice).text.toString().twoDecimalFormat()
                }</h4></td>
         </tr>
        """
            )
        }
        return """
        <table width="100%">
        <tr>
        <td width="70%"><h4>Item</h4></td>
        <td ><h4>Quantity</h4></td>
        <td ><h4>Cost</h4></td>
        <td ><h4 align="right">Subtotal</h4></td>
        </tr>
       
        $items
       
        </table>
    """.trimIndent()
    }

    fun createSubtotalInfo(name: String, subtotal: String) = """
<td>
	<table align="right">
	<tr>
	<td>
	<p align="right">
	<b>Subtotal</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ${'$'}${subtotal.twoDecimalFormat()}
	</br>
	<b>Tax</b> &nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 	${'$'}0.00
	</br>
	<b>Amount Due</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ${'$'}0.00
	</p>
	</td>
	</tr>
	</table>
</br></br></br></br></br>

<p> By signing this document the customer agrees to the service and conditions outlined in this document.</p>

<p align="right"> ____________________________</br> $name &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
    """.trimIndent()

    fun generatePDF(userinfo: String, iteminfo: String, subtotalinfo: String) {
        // Create Temp File to save Pdf To
        // Create Temp File to save Pdf To
        val savedPDFFile =
            FileManager.getInstance().createTempFile(applicationContext, "pdf", false)
// Generate Pdf From Html
// Generate Pdf From Html
        PDFUtil.generatePDFFromHTML(
            applicationContext, savedPDFFile, """ <!DOCTYPE html>
<html>
<body>
$userinfo$iteminfo$subtotalinfo
</body>
</html> """, object : OnPDFPrintListener {
                override fun onSuccess(file: File) {
                    // Open Pdf Viewer
                    mFile = file
                    
//                    val pdfUri: Uri = Uri.fromFile(savedPDFFile)
//                    pdfView.fromUri(pdfUri)
                    pdfView.fromFile(mFile)
                        .swipeHorizontal(false)
                        .load()
                    pdfContainer.isVisible = true
                    contentView.isVisible = false
                }

                override fun onError(exception: java.lang.Exception) {
                    mFile = null
                    exception.printStackTrace()
                }
            })
    }

    private fun composeMail() {
        if (mFile != null) {
            val path = Uri.fromFile(mFile)
            val selectorIntent = Intent(Intent.ACTION_SENDTO)
            selectorIntent.data = Uri.parse("mailto:")
            var isFound = false

            val shareIntent = Intent(Intent.ACTION_SEND)
//            shareIntent.putExtra(Intent.EXTRA_EMAIL, "email")
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Sent by ${getString(R.string.app_name)}")
            shareIntent.putExtra(Intent.EXTRA_STREAM, path)
            shareIntent.selector = selectorIntent

            try {
                val pm = packageManager;
                val activityList = pm.queryIntentActivities(shareIntent, 0);
                for (app in activityList) {
                    if ((app.activityInfo.name).contains("google.android.gm") || (app.activityInfo.name).contains(
                            "gmail"
                        )
                    ) {
                        isFound = true
                        val activity = app.activityInfo
                        val name =
                            ComponentName(activity.applicationInfo.packageName, activity.name)
                        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                        shareIntent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                        shareIntent.component = name
                        startActivity(shareIntent)
                    }
                }
                if (!isFound) {
                    try {
                        val chooserIntent = Intent.createChooser(shareIntent, "Select email app:")
                        startActivity(chooserIntent)
                    } catch (e: java.lang.Exception) {
                        Toast.makeText(this@PDFActivity, e.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                try {
                    val chooserIntent = Intent.createChooser(shareIntent, "Select email app:")
                    startActivity(chooserIntent)
                } catch (e: java.lang.Exception) {
                    Toast.makeText(this@PDFActivity, e.toString(), Toast.LENGTH_LONG).show()
                }
            }
        } else toast("Something went wrong, Please try later!")
    }
}

fun String.twoDecimalFormat(): String {
    var v = "0"
    if (isEmpty()) v = "0" else v = this
    val format = "%.2f"
    val amount = String.format(Locale.getDefault(), format, (v.replace("%", "")).toDouble())
    return amount
}

