package com.jobs.ikokazike;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog mProgressDialog;
    private String url = "https://jobwebkenya.com/jobs/page/";
    private int no = 1;
    private ArrayList<String> mJobTitleList = new ArrayList<>();
    private ArrayList<String> mBriefDescList = new ArrayList<>();
    private ArrayList<String> mDateList = new ArrayList<>();
    private ArrayList<String> mJobLink = new ArrayList<>();
    private TextView button;
    RecyclerView mRecyclerView;
    String appPackageName;
    private String TAG;
    private static InterstitialAd mInterstitialAd;

    private AdView mAdView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
        else{
            mProgressDialog = new ProgressDialog(MainActivity.this);
            // mProgressDialog.setTitle("Bet Predictions");
            mProgressDialog.setMessage("Loading data");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(this.getResources().getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });




        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        button = (TextView) findViewById(R.id.next);
        mRecyclerView = (RecyclerView)findViewById(R.id.act_recyclerview);

        appPackageName = "https://play.google.com/store/apps/details?id=" + this.getPackageName();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 1 +1;
              Intent intent = new Intent(MainActivity.this,Next.class);
              intent.putExtra("page",i);
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

        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(4)
                .session(7)
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {

                    }
                }).build();

        ratingDialog.show();




        new Description().execute();
    }




    public class Description extends AsyncTask<Void, Void, Void> {
        String desc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                String urll = url + no;
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



            DataAdapter mDataAdapter = new DataAdapter(MainActivity.this, mJobTitleList, mBriefDescList, mDateList, mJobLink);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mDataAdapter);

            mProgressDialog.dismiss();


        }
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            comingSoon();

        } else if (id == R.id.nav_slideshow) {
            comingSoon();

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {
            shareApp();

        } else if (id == R.id.nav_send) {

           showDialog();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showDialog(){



        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(4)
                .ratingBarColor(R.color.career)
                .playstoreUrl(appPackageName)
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        Log.i(TAG,"Feedback:" + feedback);
                    }
                })
                .build();


        ratingDialog.show();

    }

    private void comingSoon(){
        final FlatDialog flatDialog = new FlatDialog(MainActivity.this);
        flatDialog.setIcon(R.drawable.point)
                .setTitle("Coming Soon")
                .setSubtitle("This feature is still under development, our engineers will let you know once it is available")
                .setSecondButtonText("EXIT")
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        flatDialog.dismiss();
                    }
                })
                .show();
    }

    public void shareApp()
    {
        final String appPackageName = this.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out Iko Kazi Kenya App at: https://play.google.com/store/apps/details?id=" + appPackageName);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }

    public static void ShowAd()
    {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }




}
