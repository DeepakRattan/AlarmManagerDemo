package com.example.deepakrattan.alarmmanagerdemo;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    private ToggleButton toggleAlarm;
    private String toastMessage;
    private NotificationManager notificationManager;
    public static final int NOTIFICATION_ID = 0;
    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //findViewById
        toggleAlarm = findViewById(R.id.toggleAlarm);

        // Set up the Notification Broadcast Intent.
        // The AlarmManager is responsible for delivering the PendingIntent at a specified interval.
        // This PendingIntent delivers an intent letting the app know it is time to update the remaining
        // time in the notification.

        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        // To track the state of the alarm, you need a boolean variable that is true
        // if the alarm exists, and false otherwise. To set this boolean, you can call
        // PendingIntent.getBroadcast() with the FLAG_NO_CREATE flag. If a PendingIntent exists,
        // that PendingIntent is returned; otherwise the call returns null.

        boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);

        //This ensures that the toggle is always on if the alarm is set, and off otherwise
        toggleAlarm.setChecked(alarmUp);

        // PendingIntent.getBroadcast() retrieve a PendingIntent that will perform a Broadcast
        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        toggleAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // deliverNotification(MainActivity.this);

                    long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                    // long repeatInterval = 5000;

                    //This returns elapsed time in milliseconds since the system was booted.
                    // This includes time spend in sleep such as CPU off, display dark, and etc.
                    long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

                    // If the Toggle is turned on, set the repeating alarm with
                    // a 15 minute interval.
                    // I am using  the AlarmManager to deliver the broadcast every 15 minutes.
                    // For this task, the appropriate type of alarm is an inexact repeating alarm that
                    // uses elapsed time and wakes the device up if it is asleep.
                    // The real-time clock is not relevant here, because you want to deliver the notification
                    // every 15 minutes.

                    if (alarmManager != null) {
                        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, repeatInterval, notifyPendingIntent);
                    }

                    toastMessage = "Alarm is On";
                } else {
                    //cancel all notifications if the alarm is turned off
                    notificationManager.cancelAll();


                    if (alarmManager != null) {
                        alarmManager.cancel(notifyPendingIntent);
                    }

                    toastMessage = "Alarm is Off";
                }
                Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });

        createNotificationChannel();
    }

    //Create notification channel for Oreo and Higher
    public void createNotificationChannel() {

        // Create a notification manager object.
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Stand up notification", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notifies every 15 minutes to stand up and walk");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

  /*  private void deliverNotification(Context context) {

        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stand_up)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text))
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        // Deliver the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }*/


}
