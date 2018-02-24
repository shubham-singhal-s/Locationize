package ado.fun.code.locationize;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * Created by mahe on 24-02-2018.
 */

public class DummyBrightnessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Settings.System.putInt(this.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 0);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 0;
        getWindow().setAttributes(lp);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                finish();
            }
        }, 500);


    }


}
