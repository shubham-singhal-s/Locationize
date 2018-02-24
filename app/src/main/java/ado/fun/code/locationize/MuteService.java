package ado.fun.code.locationize;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

/**
 * Created by mahe on 24-02-2018.
 */

public class MuteService extends Service {

    int prevMode;
    int prevBrightness;
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onStart(Intent in, int startid) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        sp = getSharedPreferences("coords", MODE_PRIVATE);
        edit = sp.edit();
        prevMode = notificationManager.getCurrentInterruptionFilter();
        prevBrightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
        storeStates(prevMode,prevBrightness);
        sendNotif();
        startActivity(new Intent(getBaseContext(), DummyBrightnessActivity.class));
    }

    @Override
    public void onDestroy(){

    }

    public void sendNotif(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentIntent(PendingIntent.getActivity(this, 0, getNotificationIntent(), PendingIntent.FLAG_UPDATE_CURRENT))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Action Buttons Notification Received")
                .setContentTitle("Phone put on silent")
                .setContentText("Phone is on silent as you are in class")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .addAction(new NotificationCompat.Action(R.mipmap.ic_launcher, "Dismiss",
                        PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))).build();
        notificationManager.notify(1, notification);
    }

    public void storeStates(int audio, int brightness){
        edit.putInt("bright",brightness);
        edit.putInt("mode",audio);
        edit.commit();
    }

    private Intent getNotificationIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }


}
