package com.example.bhargav.buyhatkesmsapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bhargav.buyhatkesmsapp.R;
import com.example.bhargav.buyhatkesmsapp.adapters.SmsRecyclerAdapter;
import com.example.bhargav.buyhatkesmsapp.databasemanager.DataBaseHelper;
import com.example.bhargav.buyhatkesmsapp.datamodels.SMSData;
import com.example.bhargav.buyhatkesmsapp.utils.Constants;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private RecyclerView smsRecyclerView;
    private TextView noSmsTv;
    private FloatingActionButton searchFloatingButton, sendSmsFloatingButton, groupByFloatingButton;
    private RelativeLayout progressBarContainer;
    private ProgressBar progressBar;
    private MyBroadcastReceiver myBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        smsRecyclerView = (RecyclerView) findViewById(R.id.rv_sms_list);
        noSmsTv = (TextView) findViewById(R.id.tv_no_sms);
        progressBarContainer = (RelativeLayout) findViewById(R.id.rl_progressbar_container);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor
                (this, R.color.green), PorterDuff.Mode.MULTIPLY);

        searchFloatingButton = (FloatingActionButton) findViewById(R.id.floating_btn_search);
        sendSmsFloatingButton = (FloatingActionButton) findViewById(R.id.floating_btn_send_sms);
        groupByFloatingButton = (FloatingActionButton) findViewById(R.id.floating_btn_group_by);
        searchFloatingButton.setOnClickListener(searchSmsClickListner);
        sendSmsFloatingButton.setOnClickListener(sendSmsClickListner);
        groupByFloatingButton.setOnClickListener(groupByClickListner);

        showProgressBar();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        smsRecyclerView.setLayoutManager(layoutManager);

        myBroadcastReceiver = new MyBroadcastReceiver();
        loadAllSms();
    }

    View.OnClickListener groupByClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, UploadSmsActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener searchSmsClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, SearchSmsActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener sendSmsClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, SendNewSmsActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).registerReceiver(myBroadcastReceiver, new IntentFilter(Constants.SMS_BROADCAST_RECEIVER));
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myBroadcastReceiver, new IntentFilter(Constants.SMS_BROADCAST_RECEIVER));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myBroadcastReceiver);
    }

    private void loadAllSms() {

        new AsyncTask<Void, Void, ArrayList<SMSData>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressBar();
            }
            @Override
            protected ArrayList<SMSData> doInBackground(Void... voids) {
                Uri uri = Uri.parse("content://sms/inbox");
                Cursor cursor= getContentResolver().query(uri, null, null ,null,null);

                cursor.moveToFirst();
                ArrayList<SMSData> smsList = new ArrayList<>();
                while(cursor.getCount() > 0 && cursor.moveToNext()) {
                    SMSData sms = new SMSData();
                    sms.setBody(cursor.getString(cursor.getColumnIndexOrThrow("body")).toString());
                    sms.setNumber(cursor.getString(cursor.getColumnIndexOrThrow("address")).toString());
                    sms.setId(cursor.getString(cursor.getColumnIndexOrThrow("_id")).toString());
                    smsList.add(sms);
                    cursor.moveToNext();
                }
                arrangeAllSmsGroupBy(smsList);
                insertAllContactIntoDataBase(smsList);
                return smsList;
            }

            @Override
            protected void onPostExecute(ArrayList<SMSData> smsDataArrayList) {
                super.onPostExecute(smsDataArrayList);
                setAdapterToRecyclerView(smsDataArrayList);
                hideProgressBar();
            }
        }.execute();
    }

    private void arrangeAllSmsGroupBy(ArrayList<SMSData> smsList) {
        Collections.sort(smsList, new Comparator<SMSData>() {
            @Override
            public int compare(SMSData smsData1, SMSData smsData2) {
                return smsData1.getNumber().compareTo(smsData2.getNumber());
            }
        });
    }

    private void insertAllContactIntoDataBase(ArrayList<SMSData> smsList) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        dataBaseHelper.deleteAllRecords();
        dataBaseHelper.addAllContacts(smsList);
    }

    private void setAdapterToRecyclerView(ArrayList<SMSData> smsList) {
        if(smsList != null && smsList.size() > 0) {
            smsRecyclerView.setAdapter(new SmsRecyclerAdapter(this, smsList));
        }else {
            noSmsTv.setVisibility(View.VISIBLE);
        }
        Toast.makeText(MainActivity.this,"Please press fab button for menu",Toast.LENGTH_LONG).show();
    }

    class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.this.loadAllSms();
            Log.d(TAG,"local broadcast received onReceived() method");
        }
    }

    private void showProgressBar() {
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBarContainer.setVisibility(View.GONE);
    }
}
