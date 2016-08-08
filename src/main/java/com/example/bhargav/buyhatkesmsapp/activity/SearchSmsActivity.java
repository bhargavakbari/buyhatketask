package com.example.bhargav.buyhatkesmsapp.activity;

import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bhargav.buyhatkesmsapp.R;
import com.example.bhargav.buyhatkesmsapp.adapters.SmsRecyclerAdapter;
import com.example.bhargav.buyhatkesmsapp.databasemanager.DataBaseHelper;
import com.example.bhargav.buyhatkesmsapp.datamodels.SMSData;

import java.util.ArrayList;

/**
 * Created by Bhargav on 8/7/2016.
 */
public class SearchSmsActivity extends AppCompatActivity {

    private DataBaseHelper dataBaseHelper;
    private ArrayList<SMSData> smsDataList = new ArrayList<>();
    private ArrayList<SMSData> smsDataOriginalList;
    private String searchString;
    private SmsRecyclerAdapter smsRecyclerAdapter;

    private RecyclerView filterContactList;
    private EditText searchStringEditText;
    private TextView noSmsTv;
    private Button searchSms;
    private RelativeLayout progressBarContainer;
    private ProgressBar progressBar;
    private ImageView clearSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sms);

        dataBaseHelper = new DataBaseHelper(this);
        progressBarContainer = (RelativeLayout) findViewById(R.id.rl_progressbar_container);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor
                (this, R.color.green), PorterDuff.Mode.MULTIPLY);

        noSmsTv = (TextView) findViewById(R.id.tv_no_sms_found);
        filterContactList = (RecyclerView) findViewById(R.id.rv_filter_contact_list);
        searchStringEditText = (EditText) findViewById(R.id.et_search_sms);
        searchSms = (Button) findViewById(R.id.btn_search_sms);
        searchSms.setOnClickListener(searchSmsClickListner);
        clearSearch = (ImageView) findViewById(R.id.iv_clear_search);
        clearSearch.setOnClickListener(clearSearchClickListner);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        filterContactList.setLayoutManager(layoutManager);

        loadAllSMSFromDataBase();

        searchStringEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(TextUtils.isEmpty(editable.toString())) {
                    noSmsTv.setVisibility(View.GONE);
                    filterContactList.setVisibility(View.VISIBLE);
                    smsRecyclerAdapter.setSmsData(smsDataOriginalList);
                    smsRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    View.OnClickListener clearSearchClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            searchStringEditText.getText().clear();
            noSmsTv.setVisibility(View.GONE);
            filterContactList.setVisibility(View.VISIBLE);
            smsRecyclerAdapter.setSmsData(smsDataOriginalList);
            smsRecyclerAdapter.notifyDataSetChanged();
        }
    };


    private void loadAllSMSFromDataBase() {
        new AsyncTask<Void, Void, ArrayList<SMSData>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressBar();
            }

            @Override
            protected ArrayList<SMSData> doInBackground(Void... voids) {
                smsDataOriginalList = dataBaseHelper.getAllContacts();
                return smsDataOriginalList;
            }

            @Override
            protected void onPostExecute(ArrayList<SMSData> smsDataList) {
                super.onPostExecute(smsDataList);
                hideProgressBar();
                filterContactList.setVisibility(View.VISIBLE);
                smsRecyclerAdapter = new SmsRecyclerAdapter(SearchSmsActivity.this,smsDataList);
                filterContactList.setAdapter(smsRecyclerAdapter);
            }
        }.execute();
    }

    View.OnClickListener searchSmsClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            searchString = searchStringEditText.getText().toString();
            findContactAndSetListView(searchString);
        }
    };

    private void findContactAndSetListView(String searchString) {
        smsDataList.clear();
        for (SMSData smsData: smsDataOriginalList) {
            String smsBody = smsData.getBody().toLowerCase();
            if(smsBody.contains(searchString.toLowerCase())) {
                smsDataList.add(smsData);
            }
        }
        if(smsDataList.size() > 0) {
            filterContactList.setVisibility(View.VISIBLE);
            smsRecyclerAdapter.setSmsData(smsDataList);
            filterContactList.setAdapter(smsRecyclerAdapter);
        } else {
            noSmsTv.setVisibility(View.VISIBLE);
            filterContactList.setVisibility(View.GONE);
        }
    }

    private void showProgressBar() {
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBarContainer.setVisibility(View.GONE);
    }
}
