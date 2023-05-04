package com.example.alkgame;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;


public class NotificationHandler {
    private static final String CHANNEL_ID = "GameTime";
    private final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;
    private Context mContext;


    public NotificationHandler(Context context) {
        this.mContext = context;
        this.mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationChannel channel = new NotificationChannel
                (CHANNEL_ID, "Game Notification", NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableLights(true);
        channel.enableVibration(true);

        mNotifyManager.createNotificationChannel(channel);
    }

    public void send(String message) {
        Intent intent = new Intent(mContext, MenuActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle("Hahó")
                .setContentText(message)
                .setSmallIcon(R.drawable.red_dot)
                .setContentIntent(pendingIntent);

        mNotifyManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void cancel() {
        mNotifyManager.cancel(NOTIFICATION_ID);
    }
}
