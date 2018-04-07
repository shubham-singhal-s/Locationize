package ado.fun.code.locationize;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by mahe on 25-02-2018.
 */

public class DayChangeReceiver extends BroadcastReceiver {

    DBHelper db;
    long ms;
    long hrs;
    Context context;
    Intent in;

    @Override
    public void onReceive(Context mContext, Intent intent) {
        context=mContext;
        db=new DBHelper(context);
        if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction())) {
            Log.d("Date ", "chsnge");

            Long time = new GregorianCalendar().getTimeInMillis()+7*60*60*1000+30*60*1000;

            // Create an Intent and set the class that will execute when the Alarm triggers. Here we have
            // specified AlarmReceiver in the Intent. The onReceive() method of this class will execute when the broadcast from your alarm is received.
            Intent intentAlarm = new Intent(context, DayChangeReceiver.class);

            // Get the Alarm Service.
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            // Set the alarm for a particular time.
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
            //Toast.makeText(this, "Alarm Scheduled for Tomorrow", Toast.LENGTH_LONG).show();
        }
        else{
            Log.d("Alarm time: ", "7:30");
            in=new Intent(context, GetDistance.class);
            int day=Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

            String d=null;

            switch (day){
                case 1:
                    process("Monday");
                    break;

                case 2:
                    process("Tuesday");
                    break;

                case 3:
                    process("Wednesday");
                    break;

                case 4:
                    process("Thursday");
                    break;

                case 5:
                    process("Friday");
                    break;

                case 6:
                    process("Saturday");
                    break;

                default:
                    break;
            }
        }
    }

    public long getTime(String d){
        long difference=0;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
            Date date1 = dateFormat.parse(d);
            Date date = new Date();
            String time = dateFormat.format(date);
            Date date2 = dateFormat.parse(time);
            difference = date1.getTime() - date2.getTime();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return difference;
    }

    public void process(String de){
        String d=db.returnFirstHalfTime(de);
        if(d!=null){
            ms=getTime(d);
            hrs=ms/(1000*60*60);
            String endTime=db.returnFirstHalfEndTime(de);
            long end = getTime(endTime);
            if(hrs<=0.5){
                NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = new Notification.Builder(context)
                        .setContentIntent(PendingIntent.getBroadcast(context, 0, getNotificationIntent(), PendingIntent.FLAG_UPDATE_CURRENT))
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("Class today")
                        .setContentText("Class in 30 minutes")
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true).build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;

                notificationManager.notify(1, notification);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Starting", "service");
                        context.startService(in);
                    }
                }, 1*1000);

                end+=10*1000;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent("ado.fun.code.locationize.restore");
                        context.sendBroadcast(i);
                        Log.d("Stopping","service");
                    }
                }, 1*60*1000);

            }
            else{
                Long time = new GregorianCalendar().getTimeInMillis()+ms-30*60*1000;

                // Create an Intent and set the class that will execute when the Alarm triggers. Here we have
                // specified AlarmReceiver in the Intent. The onReceive() method of this class will execute when the broadcast from your alarm is received.
                Intent intentAlarm = new Intent(context, DayChangeReceiver.class);

                // Get the Alarm Service.
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                // Set the alarm for a particular time.
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
            }
        }
//        String c=db.returnSecondHalfTime(de);
//        if(c!=null){
//            ms=getTime(c);
//            hrs=ms/(1000*60*60);
//            String endTime=db.returnSecondHalfEndTime(de);
//            long end = getTime(endTime);
//            if(hrs<=0.5){
//                NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                Notification notification = new Notification.Builder(context)
//                        .setContentIntent(PendingIntent.getBroadcast(context, 0, getNotificationIntent(), PendingIntent.FLAG_UPDATE_CURRENT))
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentTitle("Class today")
//                        .setContentText("Class in 30 minutes")
//                        .setWhen(System.currentTimeMillis())
//                        .setAutoCancel(true).build();
//                notification.flags = Notification.FLAG_AUTO_CANCEL;
//
//                notificationManager.notify(1, notification);
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("Starting", "service");
//                        context.startService(in);
//                    }
//                }, 1*1000);
//
//                end+=10*1000;
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent i = new Intent("ado.fun.code.locationize.restore");
//                        context.sendBroadcast(i);
//                        Log.d("Stopping","service");
//                    }
//                }, 1*60*1000);
//
//            }
//            else{
//                Long time = new GregorianCalendar().getTimeInMillis()+ms-30*60*1000;
//
//                // Create an Intent and set the class that will execute when the Alarm triggers. Here we have
//                // specified AlarmReceiver in the Intent. The onReceive() method of this class will execute when the broadcast from your alarm is received.
//                Intent intentAlarm = new Intent(context, DayChangeReceiver.class);
//
//                // Get the Alarm Service.
//                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//                // Set the alarm for a particular time.
//                alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
//            }
//        }

    }

    private Intent getNotificationIntent() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }
}
