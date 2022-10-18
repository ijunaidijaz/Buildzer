package com.tradesk.appCode.profileModule.proposalsModule.addProposalModule


import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.github.barteksc.pdfviewer.PDFView
import com.tradesk.R
import com.tradesk.appCode.profileModule.proposalsModule.IProposalsView
import com.tradesk.appCode.profileModule.proposalsModule.ProposalsActivity
import com.tradesk.appCode.profileModule.proposalsModule.PropsoalsPresenter
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.utils.CommonUtil
import com.tradesk.utils.extension.sendEmail
import com.tradesk.utils.extension.toast
import kotlinx.android.synthetic.main.activity_pdfview.*
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

class PDFViewNewActivity : BaseActivity(), IProposalsView {
    //creating a variable for PDF view.
    var pdfView: PDFView? = null
    lateinit var title: TextView;
    lateinit var markAsOngoing: Button;
    lateinit var markAsComplete: Button;
    var mIvEmail: ImageView? = null
    var mIvBack: ImageView? = null
    var pb_loadingpdf: ProgressBar? = null

    var alertDialog: AlertDialog? = null
    var type: String = "";

    @Inject
    lateinit var presenter: PropsoalsPresenter<IProposalsView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdfview)
        presenter.onAttach(this)
        title = findViewById(R.id.textView6)
        //initializing our pdf view.
        if (intent.getStringExtra("title").equals("proposals",true)) {
            type = "Proposal"
            title.text = "Estimate PDF"
        } else {
            type = "invoice"
            title.text="Invoice PDF"
//            findViewById<LinearLayout>(R.id.buttons).visibility=View.GONE
        }
        pdfView = findViewById(R.id.idPDFView)
        mIvEmail = findViewById(R.id.mIvEmailSend)
        mIvBack = findViewById(R.id.mIvBack)
        markAsComplete = findViewById(R.id.markAsComplete)
        markAsOngoing = findViewById(R.id.markAsOngoing)

        pb_loadingpdf = findViewById(R.id.pb_loadingpdf)
        mIvBack!!.setOnClickListener(View.OnClickListener { finish() })
        RetrivePDFfromUrl().execute(
            intent.getStringExtra("pdfurl")
        )
        if (intent.hasExtra("status") && intent.getStringExtra("status").equals("pending", true)) {
            findViewById<LinearLayout>(R.id.buttons).visibility = View.VISIBLE
        }
        if (type.equals("invoice")){
            findViewById<LinearLayout>(R.id.buttons).visibility = View.GONE
            if (intent.hasExtra("status") && !intent.getStringExtra("status").equals("completed", true)) {
                findViewById<Button>(R.id.paidButton).visibility = View.VISIBLE
            }
        }
        mIvEmail!!.setOnClickListener(View.OnClickListener {
            if (intent.hasExtra("id")) {
                alertDialog =
                    sendEmail("Email Estimate", intent.getStringExtra("email").toString()) {
                        if (isInternetConnected()) {
                            presenter.sendProposal(
                                intent.getStringExtra("id").toString(),
                                it.toString()
                            )
                        }
                    }

            } else {
                val stringArray1 = arrayOf(intent.getStringExtra("email"))
                composeEmail(stringArray1, "Proposal", intent.getStringExtra("pdfurl"))
            }
        })

        markAsComplete.setOnClickListener {
            presenter.changeStatus("completed", type, intent.getStringExtra("id").toString())
        }
        markAsOngoing.setOnClickListener {
            presenter.changeStatus("Inprocess", type, intent.getStringExtra("id").toString())

        }
        paidButton.setOnClickListener {
            presenter.changeStatus("completed", type, intent.getStringExtra("id").toString())
        }
    }

    override fun onAdd(it: AddProposalsModel) {

    }

    override fun onList(it: PorposalsListModel) {

    }

    override fun onDefaultList(it: DefaultItemsModel) {

    }

    override fun onDetails(it: ProposalDetailModel) {

    }

    override fun onSend(it: SuccessModel) {
        if (alertDialog != null) {
            if (alertDialog!!.isShowing) alertDialog!!.dismiss()
        }
        if (ProposalsActivity.context != null) {
            Handler().postDelayed({
                CommonUtil.showSuccessDialog(ProposalsActivity.context, "Email Sent")
            }, 1500)
        }
        toast("Sent successfully")
        finish()
    }

    override fun onChangeStatus(it: ChangeProposalStatus) {
       toast(it.message)
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

    fun composeEmail(addresses: Array<String?>?, subject: String?, content: String?) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.type = "text/plain"
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "Hi,\n\nHere I am sharing proposal for your construction work. \n\nLink :- $content"
        )
        //        intent.setType("message/rfc822");
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    //create an async task class for loading pdf file from URL.
    inner class RetrivePDFfromUrl : AsyncTask<String?, Void?, InputStream?>() {

        override fun doInBackground(vararg p0: String?): InputStream? {
            //we are using inputstream for getting out PDF.
            pb_loadingpdf!!.visibility = View.VISIBLE
            var inputStream: InputStream? = null
            try {
                val url = URL(p0[0])
                // below is the step where we are creating our connection.
                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection
                if (urlConnection.responseCode == 200) {
                    //response is success.
                    //we are getting input stream from url and storing it in our variable.
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            } catch (e: IOException) {
                //this is the method to handle errors.
                e.printStackTrace()
                return null
            }
            return inputStream
        }

        override fun onPostExecute(inputStream: InputStream?) {
            // after the execution of our async task we are loading our pdf in our pdf view.
            pb_loadingpdf!!.visibility = View.GONE
            pdfView!!.fromStream(inputStream).load()
        }
    }
}