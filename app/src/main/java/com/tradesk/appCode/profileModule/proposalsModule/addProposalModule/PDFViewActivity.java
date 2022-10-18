package com.tradesk.appCode.profileModule.proposalsModule.addProposalModule;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.tradesk.R;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class PDFViewActivity extends AppCompatActivity {

    //creating a variable for PDF view.
    PDFView pdfView;
    ImageView mIvEmail,mIvBack;
    ProgressBar pb_loadingpdf;
    //url of our PDF file.
//    String pdfurl="https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        //initializing our pdf view.
        pdfView = findViewById(R.id.idPDFView);
        mIvEmail = findViewById(R.id.mIvEmailSend);
        mIvBack = findViewById(R.id.mIvBack);
        pb_loadingpdf = findViewById(R.id.pb_loadingpdf);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        new RetrivePDFfromUrl().execute(getIntent().getStringExtra("pdfurl"));


        mIvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().hasExtra("id")){

                }else {
                    String[] stringArray1 = new String[] {getIntent().getStringExtra("email")};

                    composeEmail(stringArray1,"Proposal",getIntent().getStringExtra("pdfurl"));
                }
            }
        });



    }

    public void composeEmail(String[] addresses, String subject,String content) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, "Hi,\n\nHere I am sharing proposal for your construction work. \n\nLink :- "+content);
//        intent.setType("message/rfc822");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //create an async task class for loading pdf file from URL.
    class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            //we are using inputstream for getting out PDF.
            pb_loadingpdf.setVisibility(View.VISIBLE);
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // below is the step where we are creating our connection.
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    //response is success.
                    //we are getting input stream from url and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }

            } catch (IOException e) {
                //this is the method to handle errors.
                e.printStackTrace();
                return null;
            }

            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // after the execution of our async task we are loading our pdf in our pdf view.
            pb_loadingpdf.setVisibility(View.GONE);
            pdfView.fromStream(inputStream).load();

        }
    }

}