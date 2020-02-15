package com.jobs.ikokazike;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Next extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private String url = "https://jobwebkenya.com/jobs/page/";
    private ArrayList<String> mJobTitleList = new ArrayList<>();
    private ArrayList<String> mBriefDescList = new ArrayList<>();
    private ArrayList<String> mDateList = new ArrayList<>();
    private ArrayList<String> mJobLink = new ArrayList<>();
    private TextView button;
    RecyclerView mRecyclerView;
    private Integer numb;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        Intent i = getIntent();
       numb = i.getExtras().getInt("page");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        button = (TextView) findViewById(R.id.next);
        mRecyclerView = (RecyclerView)findViewById(R.id.act_recyclerview1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = numb +1;
                Intent intent = new Intent(Next.this,More.class);
                intent.putExtra("pag",i);
                startActivity(intent);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView mRecyclerView, int newState) {
                super.onScrollStateChanged(mRecyclerView, newState);

                if (!mRecyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    button.setVisibility(View.VISIBLE);

                }
            }
        });

        new Description().execute();
    }

    public class Description extends AsyncTask<Void, Void, Void> {
        String desc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(Next.this);
            mProgressDialog.setMessage("Loading Data...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                String urll = url + numb;
                // Connect to the web site
                Document mBlogDocument = Jsoup.connect(urll).get();
                // Using Elements to get the Meta data
                Elements mElementDataSize = mBlogDocument.select("ol[class=jobs]").select("li");
                // Locate the content attribute
                int mElementSize = mElementDataSize.size();

                for (int i = 0; i < mElementSize; i++) {

                    Elements mJobsLink = mBlogDocument.select("div[id=titlo]").select("a");

                    for (Element link : mJobsLink) {
                        String mlink = link.attr("href");
                        mJobLink.add(mlink);



                    }

                    Elements mElementBriefDesc = mBlogDocument.select("div[id=exc]").eq(i);
                    String mAuthorName = mElementBriefDesc.text();

                    Elements mElementDate = mBlogDocument.select("span[class=year]").eq(i);
                    String mBlogUploadDate = mElementDate.text();

                    Elements mElementJobTitle = mBlogDocument.select("div[id=titlo]").select("a").eq(i);
                    String mBlogTitle = mElementJobTitle.text();

                    mBriefDescList.add(mAuthorName);
                    mDateList.add(mBlogUploadDate);
                    mJobTitleList.add(mBlogTitle);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set description into TextView


            DataAdapter mDataAdapter = new DataAdapter(Next.this, mJobTitleList, mBriefDescList, mDateList, mJobLink);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mDataAdapter);

            mProgressDialog.dismiss();
        }
    }
}
