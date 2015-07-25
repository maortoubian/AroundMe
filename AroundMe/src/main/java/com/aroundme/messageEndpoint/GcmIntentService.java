package com.aroundme.messageEndpoint;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.appspot.enhanced_cable_88320.aroundmeapi.Aroundmeapi;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;
import com.aroundme.EndpointApiCreator;
import com.aroundme.MainActivity;
import com.aroundme.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by nezer14 on 4/27/15.
 */

public class GcmIntentService extends IntentService {
    static final String TAG = "------------------------";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    //Intent myIntent = new Intent(GcmIntentService.this, Chat.class);

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Log.i(TAG , "kk");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        SharedPreferences sharedPref = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String user_email = sharedPref.getString("email","");

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
              //  for (int i=0; i<5; i++) {
                //    Log.i(TAG, "Working... " + (i+1)
                  //          + "/5 @ " + SystemClock.elapsedRealtime());
                    //try {
                      //  Thread.sleep(5000);
                    //} catch (InterruptedException e) {
                   // }
              //  }


                String newMatch = intent.getStringExtra("message");
                if (newMatch != null) {
                    sendNotification("Received New message : " + newMatch);
                }
                String messageId = intent.getStringExtra("newMessage");
                if (messageId != null) {
                    try {
                        Aroundmeapi api = EndpointApiCreator
                                .getApi(Aroundmeapi.class);

                        Message m = api.getMessage(Long.parseLong(messageId)).execute();

                        String messageDetailes = m.getContnet() + ", from:"
                                + m.getFrom();
                        long timeStampInMills = m.getTimestamp().getValue();
                        String themessage = m.getContnet();
                        String from = m.getFrom(); //return mail adress
                        String to = m.getTo();

                        Log.i("^^^^^^^",to);
                        Log.i("^^^^^^^",user_email);
                     if(to.equals(user_email)){


                            Log.i("^^^^^^^","THIS IS THE USER'S EMAIL!!!");

                            ChatDBHandler cdb = new ChatDBHandler(this);
                            cdb.open(ChatDBHandler.OPEN_DB_FOR.WRITE);


                            cdb.saveUser(from,"unknown"); //
                            cdb.saveMessage(from,themessage, ChatDBHandler.WHO_SEND.OTHER); // from == sender's email
                            cdb.close();

                         sendNotification("Message from "+from+" : "+themessage);



                            //Intent dialogIntent = new Intent(this, Chat.class);
                           // dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //dialogIntent.putExtra("message",themessage);
                            //dialogIntent.putExtra("from",from);
                            //startActivity(dialogIntent);




                        }
                        else{
                            Log.i("^^^^^^^","THIS IS **NOT** USER'S EMAIL!!!");
                        }



                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.

                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.me)
                        .setContentTitle("AroundMe")
                        .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}


