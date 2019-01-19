package com.android.moven.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.moven.ConfigURL;
import com.android.moven.Drawer;
import com.android.moven.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "FCM Service";

    static void updateMyActivity(Context context, String message, String type, String fare) {

        Intent intent = new Intent(ConfigURL.PUSH_NOTIFICATION);
        //put whatever data you want to send, if any
        intent.putExtra("message", message);
        intent.putExtra("type", type);
        intent.putExtra("fare", fare);
        //send broadcast
        context.sendBroadcast(intent);
    }

    public static void cancelNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.v(TAG, "From: " + remoteMessage.getData().toString());
        try {
            JSONObject notificationObject = new JSONObject(remoteMessage.getData().toString());
            addNotification(notificationObject.getJSONObject("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addNotification(JSONObject notificationObject) throws JSONException {
        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent ii = new Intent(getApplicationContext(), Drawer.class);

        String message = notificationObject.getString("message");
        String type = notificationObject.getString("senderName");
        String fare = notificationObject.getString("fare");
        ii.putExtra("message", message);
        ii.putExtra("type", type);
        ii.putExtra("fare", fare);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle(notificationObject.getString("senderName"));
        mBuilder.setContentText(notificationObject.getString("message"));
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        // mBuilder.setStyle(bigText);

        mBuilder.setSound(Settings.System.DEFAULT_RINGTONE_URI);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }
        mNotificationManager.notify(0, mBuilder.build());
        if (type.equals("PICKEDUP")) {
            updateMyActivity(this, message, type, fare);

        } else if (type.equals("FINISHED")) {
            updateMyActivity(this, message, "FINISHED", fare);
        }

    }
}
