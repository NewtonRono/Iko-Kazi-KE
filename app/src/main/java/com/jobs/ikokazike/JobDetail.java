package com.jobs.ikokazike;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class JobDetail extends AppCompatActivity {

    private ArrayList<String> mQualificationsList = new ArrayList<>();


     TextView mTitle, apply, titlo, text, link, hoow;
    private String mLink;
    private ProgressDialog mProgressDialog;
    String rrr = "/cdn-cgi/l/email-protection";

    private AdView mAdView;


    @Override
    public void onBackPressed() {


        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobdetail);

        Intent intent = getIntent();


        String mJobTitle = intent.getExtras().getString("title");
         mLink = intent.getExtras().getString("link");

        mTitle = (TextView) findViewById(R.id.jobTitle);
        apply = (TextView) findViewById(R.id.apply);
        hoow = (TextView) findViewById(R.id.hoow);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        mTitle.setText(mJobTitle);
       new Description().execute();

       apply.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent ii = new Intent();
               ii.setAction(Intent.ACTION_SEND);
               ii.putExtra(Intent.EXTRA_TEXT, apply.getText());
               ii.setType("text/plain");
               startActivity(ii);

           }
       });





    }


    private class Description extends AsyncTask<Void, Void, Void> {
        String Apply;
        String Link;
        String Trial;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(JobDetail.this);
            mProgressDialog.setMessage("Loading Details...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document mBlogDocument = Jsoup.connect(mLink).get();
                // Using Elements to get the Meta data

                Elements list = mBlogDocument.select("font[size=2]");


                int mElementSize = list.size();

               for (int i = 0; i < mElementSize; i++) {

                 mBlogDocument.select("div[class=sharedaddy sd-sharing-enabled]").remove();

                 Elements detail = list.select("ul").select("li");

                    for (Element det : detail) {
                       String qual = det.text();
                       mQualificationsList.add(qual);


                    }


              }



                    Elements app = mBlogDocument.select("font[size=3]").select("a");


                for (Element lin : app) {
                    Apply = lin.attr("data-cfemail");

                    if (!Apply.isEmpty()) {
                        Trial = cfDecodeEmail(Apply);

                    }
                }








               Elements mJobsLink = mBlogDocument.select("font[size=3]").select("a");

                    for (Element link : mJobsLink) {
                        Link = link.attr("href");

                    }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String cfDecodeEmail(final String encodedString) {
            final StringBuilder email = new StringBuilder(50);
            final int r = Integer.parseInt(encodedString.substring(0, 2), 16);
            for (int n = 2; n < encodedString.length(); n += 2) {
                final int i = Integer.parseInt(encodedString.substring(n, n+2), 16) ^ r;
                email.append(Character.toString ((char) i));

            }
            return email.toString();
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set description into TextView



            RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.detail_recycler);

            Qualif mDataAdapter = new Qualif(JobDetail.this, mQualificationsList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mDataAdapter);



            apply.setText(Trial);



            text = (TextView) findViewById(R.id.text);
            titlo = (TextView) findViewById(R.id.applytitlo);
            link = (TextView) findViewById(R.id.link);

            link.setText(Link);






            text.setVisibility(View.VISIBLE);
            titlo.setVisibility(View.VISIBLE);
            hoow.setVisibility(View.VISIBLE);

            mProgressDialog.dismiss();


        }
    }


}
