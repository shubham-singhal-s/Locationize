package ado.fun.code.locationize;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {

    TextView locationText;
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 99;
    String lat_main, long_main;
    AlertDialog.Builder builder;
    NotificationManager notificationManager;
    BroadcastReceiver updateUIReciver;
    BroadcastReceiver updateUIReciver2;
    BroadcastReceiver updateUIReciver3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHelper db=new DBHelper(getApplicationContext());
        //db.dropTab();
        Log.d("Val db: ", String.valueOf(db.isTableExist()));

//        if(!db.isTableExist()){
//            Intent in=new Intent(this, InputDays.class);
//            startActivityForResult(in, 1);
//        }

        sp = getSharedPreferences("coords", MODE_PRIVATE);
        edit = sp.edit();
        if (sp.contains("Lat") && sp.contains("Long")) {
            sp.getString("Lat", lat_main);
            sp.getString("Long", long_main);
        } else {
            Intent in=new Intent(this, InputDays.class);
            startActivityForResult(in, 1);
            getMap();
        }



        //Toast.makeText(this, "Lat is "+long_main, Toast.LENGTH_SHORT).show();

        locationText = (TextView) findViewById(R.id.locText);

        builder = new AlertDialog.Builder(this);

        IntentFilter filter = new IntentFilter();

        filter.addAction("ado.fun.code.locationize.off");

        updateUIReciver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                locationText.setText("");

            }
        };
        registerReceiver(updateUIReciver,filter);

        IntentFilter filter2 = new IntentFilter();

        filter2.addAction("ado.fun.code.locationize.on");

        updateUIReciver2 = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                locationText.setText("Warming up the GPS...");

            }
        };
        registerReceiver(updateUIReciver2,filter2);

        IntentFilter filter3 = new IntentFilter();

        filter3.addAction("ado.fun.code.locationize.stop");

        updateUIReciver3 = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                findViewById(R.id.start).setVisibility(View.VISIBLE);
                findViewById(R.id.stop).setVisibility(View.GONE);

            }
        };
        registerReceiver(updateUIReciver3,filter3);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {

                builder.setTitle("Permission Request")
                        .setMessage("Permission required to access Settings. Enable Locationize to access settings.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, 200);
                            }
                        })
                        .setIcon(R.drawable.icon)
                        .show();

            }
        }

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !notificationManager.isNotificationPolicyAccessGranted()) {
                builder.setTitle("Permission Request")
                        .setMessage("Permission required to access Do Not Disturb. Enable Locationize to access settings.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(
                                        android.provider.Settings
                                                .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                startActivity(intent);


                            }
                        })
                        .setIcon(R.drawable.icon)
                        .show();
            }
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        findViewById(R.id.places).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Edit")
                        .setMessage("TimeTable or Place?")
                        .setPositiveButton("Timetable", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent in=new Intent(MainActivity.this, InputDays.class);
                                startActivityForResult(in, 1);
                            }
                        })
                        .setNegativeButton("Place", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getMap();
                    }
                })
                        .setIcon(R.drawable.icon)
                        .show();
            }
        });

        final Intent in = new Intent(this, GetDistance.class);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(in);
                findViewById(R.id.start).setVisibility(View.GONE);
                findViewById(R.id.stop).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.locText)).setText("Warming up the GPS...");
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(in);
                findViewById(R.id.start).setVisibility(View.VISIBLE);
                findViewById(R.id.stop).setVisibility(View.GONE);
                locationText.setText("");
            }
        });
    }

    public void getMap() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(MainActivity.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                LatLng latlong = place.getLatLng();
                updatePreference(latlong.latitude, latlong.longitude);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("main ", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    public void updatePreference(double c1, double c2) {
        edit.putString("Lat", String.valueOf(c1));
        edit.putString("Long", String.valueOf(c2));
        String rand;
        edit.commit();
        rand = sp.getString("Lat", "");
        //Toast.makeText(this, "Lat is "+rand, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(updateUIReciver);
        unregisterReceiver(updateUIReciver2);
        unregisterReceiver(updateUIReciver3);
    }


}

