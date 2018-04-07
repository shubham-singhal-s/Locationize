package ado.fun.code.locationize;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by mahe on 24-02-2018.
 */

public class MuteService extends Service {

    int prevMode;
    int prevBrightness;
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    NotificationManager notificationManager;
    BroadcastReceiver receiver;

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

        try{
        prevMode = notificationManager.getCurrentInterruptionFilter();
        prevBrightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);}
        catch(Exception e){
            e.printStackTrace();
        }

        storeStates(prevMode,prevBrightness);
        sendNotif();
        Intent intent = new Intent(this, DummyBrightnessActivity.class);
        intent.putExtra("bright",0);
        startActivity(intent);
        onDestroy();
    }

    @Override
    public void onDestroy(){

        super.onDestroy();

    }

    public void sendNotif(){
        Intent intent = new Intent(this, ActionReceiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setContentIntent(PendingIntent.getBroadcast(this, 0, getNotificationIntent(), PendingIntent.FLAG_UPDATE_CURRENT))
                .setSmallIcon(R.drawable.icon)
                .setTicker("Action Buttons Notification Received")
                .setContentTitle("Phone put on silent")
                .setContentText("Phone is on silent as you are in class")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .addAction(new Notification.Action(R.mipmap.ic_launcher, "Dismiss",
                        pintent)).build();
        notification.flags = Notification.FLAG_AUTO_CANCEL|Notification.FLAG_ONGOING_EVENT;

        notificationManager.notify(1, notification);
    }

    public void storeStates(int audio, int brightness){
        edit.putInt("bright",brightness);
        edit.putInt("mode",audio);
        edit.commit();
    }


    private Intent getNotificationIntent() {
        Intent intent = new Intent(this, ActionReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }


}
