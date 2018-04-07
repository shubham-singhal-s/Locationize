package ado.fun.code.locationize;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by mahe on 24-02-2018.
 */

public class ActionReceiver extends BroadcastReceiver {

    Context mContext;
    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext=context;
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        String ns=Context.NOTIFICATION_SERVICE;
        restoreState();
        notificationManager.cancelAll();
    }

    public void restoreState(){
        int mode, brightness;
        SharedPreferences sp = mContext.getApplicationContext().getSharedPreferences("coords",Context.MODE_PRIVATE);
        brightness = sp.getInt("bright",0);
        Log.d("lulz",brightness+"");
        mode = sp.getInt("mode", NotificationManager.INTERRUPTION_FILTER_ALL);
        try{
            notificationManager.setInterruptionFilter(mode);
            Intent intent = new Intent(mContext.getApplicationContext(), DummyBrightnessActivity.class);
            intent.putExtra("bright",brightness);
            mContext.getApplicationContext().startActivity(intent);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

}
