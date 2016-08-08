package com.example.bhargav.buyhatkesmsapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bhargav.buyhatkesmsapp.R;

/**
 * Created by Bhargav on 8/6/2016.
 */
public class SendNewSmsActivity extends AppCompatActivity {

    private EditText smsBodyEditText,receiverNumber;
    private Button sendButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_new_sms);
        smsBodyEditText = (EditText) findViewById(R.id.et_message);
        receiverNumber = (EditText) findViewById(R.id.et_phone_number);
        sendButton = (Button) findViewById(R.id.btn_send_sms);
        sendButton.setOnClickListener(sendSms);
    }

    View.OnClickListener sendSms = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String phoneNo = receiverNumber.getText().toString();
            String message = smsBodyEditText.getText().toString();
            if(phoneNo.length() >= 10 || phoneNo.length() <= 12) {
                receiverNumber.setError("Enter valid Phone Number");
            }
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, message, null, null);
                Toast.makeText(getApplicationContext(), "Message Sent",
                        Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }
    };
}
