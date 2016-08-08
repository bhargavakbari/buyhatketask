package com.example.bhargav.buyhatkesmsapp.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.bhargav.buyhatkesmsapp.activity.MainActivity;
import com.example.bhargav.buyhatkesmsapp.R;

/**
 * Created by Bhargav on 8/6/2016.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "SmsBroadcastReceiver";
    public static final String SMS_BUNDLE = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();

                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";
            }
            Log.d(TAG,"sms received onReceived() method");
            generateNotification(context, smsMessageStr);
            Toast.makeText(context, smsMessageStr, Toast.LENGTH_LONG).show();
            sendLocalBroadcast(context);
        }
    }

    private void generateNotification(Context context,String messageString) {
        int notificationID = 100;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_sms);
        mBuilder.setContentTitle("New Sms");
        mBuilder.setContentText(messageString);

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    private void sendLocalBroadcast(Context context) {
        Intent intent = new Intent("sms_received");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
