package com.example.bhargav.buyhatkesmsapp.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bhargav.buyhatkesmsapp.R;
import com.example.bhargav.buyhatkesmsapp.databasemanager.DataBaseHelper;
import com.example.bhargav.buyhatkesmsapp.datamodels.SMSData;
import com.example.bhargav.buyhatkesmsapp.netwrokconnector.NetworkUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by Bhargav on 8/7/2016.
 */
public class UploadSmsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "UploadSmsActivity";
    private static final String FILE_NAME = "SMS backUp";
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    private GoogleApiClient mGoogleApiClient;
    private DataBaseHelper mDataBaseHelper;
    private ArrayList<SMSData> smsDataArrayList;

    private Button uploadSmsButton;
    private RelativeLayout progressBarContainer;
    private ProgressBar progressBar;
    private TextView progressBarText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_by);
        mDataBaseHelper = new DataBaseHelper(this);

        uploadSmsButton = (Button) findViewById(R.id.btn_upload_sms);
        progressBarContainer = (RelativeLayout) findViewById(R.id.rl_progressbar_container);
        progressBarText = (TextView) findViewById(R.id.tv_loading_message);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor
                (this, R.color.green), PorterDuff.Mode.MULTIPLY);
        uploadSmsButton.setOnClickListener(uploadSmsToGoogleDriveClickListner);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        loadAllSMSFromDataBase();
    }

    private void loadAllSMSFromDataBase() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressBar("Loading SMS");
            }

            @Override
            protected Void doInBackground(Void... voids) {
                smsDataArrayList = mDataBaseHelper.getAllContacts();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                hideProgressBar();
            }
        }.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    View.OnClickListener uploadSmsToGoogleDriveClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(NetworkUtils.isNetworkConnected(UploadSmsActivity.this)){
                Drive.DriveApi.newDriveContents(getGoogleApiClient())
                        .setResultCallback(driveContentsCallback);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected void onPreExecute() {
                            showProgressBar("Uploading SMS to Google Drive");
                            super.onPreExecute();
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);
                            try {
                                for (SMSData smsData: smsDataArrayList) {
                                    writer.write(smsData.getNumber() + "\n" + smsData.getBody() + "\n\n");
                                }
                                writer.close();
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(FILE_NAME)
                                    .setMimeType("text/plain")
                                    .setStarred(true).build();

                            Drive.DriveApi.getRootFolder(getGoogleApiClient())
                                    .createFile(getGoogleApiClient(), changeSet, driveContents)
                                    .setResultCallback(fileCallback);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            hideProgressBar();
                            super.onPostExecute(aVoid);
                        }
                    }.execute();
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    showMessage("Created a file with file Name: " + FILE_NAME);
                }
            };

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showProgressBar(String loadingMessage) {
        progressBarContainer.setVisibility(View.VISIBLE);
        progressBarText.setText(loadingMessage);
    }
    public void hideProgressBar() {
        progressBarContainer.setVisibility(View.GONE);
    }
}
