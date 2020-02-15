package com.jobs.ikokazike;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Qualif extends RecyclerView.Adapter<Qualif.MyViewHolder> {

    private ArrayList<String> mQualificationsList = new ArrayList<>();
    private Activity mActivity;

    public Qualif(JobDetail activity,ArrayList<String> mQualificationsList){
        this.mActivity = activity;
        this.mQualificationsList = mQualificationsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_qualifications;

        public MyViewHolder(View view) {
            super(view);
            tv_qualifications = (TextView) view.findViewById(R.id.qualification);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.qualif_data, parent, false);

        return new Qualif.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {

        holder.tv_qualifications.setText(mQualificationsList.get(position));

    }

    @Override
    public int getItemCount() {
        return mQualificationsList.size();
    }



}
