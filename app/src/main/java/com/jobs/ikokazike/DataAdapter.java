package com.jobs.ikokazike;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {

    private ArrayList<String> mJobTitleList = new ArrayList<>();
    private ArrayList<String> mBriefDescList = new ArrayList<>();
    private ArrayList<String> mDateList = new ArrayList<>();
    private ArrayList<String> mJobLink = new ArrayList<>();
    private Activity mActivity;
    private int lastPosition = -1;
    private InterstitialAd mInterstitialAd;

    public DataAdapter(MainActivity activity, ArrayList<String> mJobTitleList, ArrayList<String> mBriefDescList, ArrayList<String> mDateList, ArrayList<String> mJobLink) {
        this.mActivity = activity;
        this.mJobTitleList= mJobTitleList ;
        this.mBriefDescList = mBriefDescList;
        this.mDateList = mDateList;
        this.mJobLink = mJobLink;
    }

    public DataAdapter(Next activity, ArrayList<String> mJobTitleList, ArrayList<String> mBriefDescList, ArrayList<String> mDateList, ArrayList<String> mJobLink) {
        this.mActivity = activity;
        this.mJobTitleList= mJobTitleList ;
        this.mBriefDescList = mBriefDescList;
        this.mDateList = mDateList;
        this.mJobLink = mJobLink;
    }

    public DataAdapter(More activity, ArrayList<String> mJobTitleList, ArrayList<String> mBriefDescList, ArrayList<String> mDateList, ArrayList<String> mJobLink) {
        this.mActivity = activity;
        this.mJobTitleList= mJobTitleList ;
        this.mBriefDescList = mBriefDescList;
        this.mDateList = mDateList;
        this.mJobLink = mJobLink;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_job_title, tv_brief_desc, tv_date, tv_details;


        public MyViewHolder(View view) {
            super(view);
            tv_job_title = (TextView) view.findViewById(R.id.jobTitle);
            tv_brief_desc = (TextView) view.findViewById(R.id.brief_desc);
            tv_date = (TextView) view.findViewById(R.id.date);
            tv_details = (TextView) view.findViewById(R.id.details);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_data, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tv_job_title.setText(mJobTitleList.get(position));
        holder.tv_brief_desc.setText(mBriefDescList.get(position));
        holder.tv_date.setText(mDateList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.ShowAd();
                String JobTitle = mJobTitleList.get(position);
                String JobUrl = mJobLink.get(position);

                Intent intent = new Intent(view.getContext(), JobDetail.class);
                intent.putExtra("title", JobTitle);
                intent.putExtra("link",JobUrl);


                mActivity.startActivity(intent);




            }
        });

        holder.tv_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = mActivity.getPackageName();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mJobTitleList.get(position)+ ": https://play.google.com/store/apps/details?id=" + appPackageName);
                sendIntent.setType("text/plain");
                mActivity.startActivity(sendIntent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return mJobTitleList.size();
    }
}
