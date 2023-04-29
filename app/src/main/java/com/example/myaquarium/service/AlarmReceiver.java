package com.example.myaquarium.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myaquarium.R;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String content = intent.getStringExtra("content");

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_action);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "1")
                        .setSmallIcon(R.drawable.other_calendar)
                        .setLargeIcon(icon)
                        .setContentTitle("Новое уведомление")
                        .setContentText(content);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());

    }
}
