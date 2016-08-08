package com.example.bhargav.buyhatkesmsapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bhargav.buyhatkesmsapp.R;

/**
 * Created by Bhargav on 7/30/2016.
 */
public class NoInternetActivity extends Activity {

    Button okButton, retryButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        okButton = (Button) findViewById(R.id.button_ok);
        retryButton = (Button) findViewById(R.id.button_retry);

        okButton.setOnClickListener(okButtonClickListner);
        retryButton.setOnClickListener(retryButtonClickListner);

    }

    View.OnClickListener okButtonClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    View.OnClickListener retryButtonClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent restartIntent = new Intent(NoInternetActivity.this, MainActivity.class);
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(restartIntent);
        }
    };

}
