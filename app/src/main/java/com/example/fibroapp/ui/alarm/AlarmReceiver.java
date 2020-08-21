package com.example.fibroapp.ui.alarm;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.example.fibroapp.R;
import com.example.fibroapp.ui.login.LoginActivity;
import com.example.fibroapp.ui.medicine.MedicinesEditFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "Notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean sound = true;
        Log.d("DEBUG", "AlarmService lanzado");
        if (intent != null){
            Log.d("DEBUG", "Intent distinto de null");

            //Check if expired the alarm
            boolean limited = intent.getBooleanExtra("limited", false);
            if (limited){
                int code = intent.getIntExtra("code", 0);
                String finishDate = intent.getStringExtra("finishDate") + " 23:59";

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date = null;
                try{
                    date = format.parse(finishDate);
                }catch (ParseException e){
                    return;
                }


                if (System.currentTimeMillis() > date.getTime()){
                    //Expired
                    sound = false;

                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    PendingIntent pIntent = PendingIntent.getBroadcast(MedicinesEditFragment.instance.getContext(), code, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    manager.cancel(pIntent);
                }
            }

            if (sound){
                //Show a notification
                String name = intent.getStringExtra("name");

                CreateNotificationChannel(context);

                //Intent to touch the notification
                Intent activityIntent = new Intent(context, LoginActivity.class);
                activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_medicine_icon)
                        .setContentTitle(context.getResources().getString(R.string.tittle_notification))
                        .setContentText(context.getResources().getString(R.string.message_notification) + " " + name)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000})
                        .setLights(Color.RED, 2000, 2000)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                int id = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
                notificationManagerCompat.notify(id, builder.build());
            }
        }
    }

    private void CreateNotificationChannel(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String name = context.getResources().getString(R.string.name_channel_notification);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name,importance);
            notificationChannel.setDescription(context.getResources().getString(R.string.description_channel_notification));
            notificationChannel.setVibrationPattern(new long[] { 1000, 1000, 1000, 1000});
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            AudioAttributes att = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build();
            Uri uriSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationChannel.setSound(uriSound, att);


            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
