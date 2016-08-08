package com.example.bhargav.buyhatkesmsapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bhargav.buyhatkesmsapp.R;
import com.example.bhargav.buyhatkesmsapp.datamodels.SMSData;

import java.util.ArrayList;

/**
 * Created by Bhargav on 8/6/2016.
 */
public class SmsRecyclerAdapter extends RecyclerView.Adapter<SmsRecyclerAdapter.SmsViewHolder>  {

    ArrayList<SMSData> dataArrayList;
    Context mContext;
    public SmsRecyclerAdapter(Context context, ArrayList<SMSData> smsDatas){
        dataArrayList = smsDatas;
        mContext = context;
    }

    @Override
    public SmsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_sms,parent,false);
        return new SmsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SmsViewHolder holder, int position) {
        holder.smsHeader.setText(dataArrayList.get(position).getNumber());
        holder.smsBody.setText(dataArrayList.get(position).getBody());
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    class SmsViewHolder extends RecyclerView.ViewHolder {
        TextView smsHeader, smsBody;
        public SmsViewHolder(View itemView) {
            super(itemView);
            smsHeader = (TextView) itemView.findViewById(R.id.tv_sms_header);
            smsBody = (TextView) itemView.findViewById(R.id.tv_sms_body);
        }
    }

    public void setSmsData(ArrayList<SMSData> smsDataArrayList) {
        dataArrayList = smsDataArrayList;
        notifyDataSetChanged();
    }
}
