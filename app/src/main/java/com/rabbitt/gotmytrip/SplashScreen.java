package com.rabbitt.gotmytrip;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.rabbitt.gotmytrip.PrefsManager.PrefsManager;

public class SplashScreen extends AppCompatActivity {

    public static final String LOG_TAG = "splash";

    private Handler splash = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splash.postDelayed(new Runnable() {
            @Override
            public void run() {
                try
                {
                    PrefsManager prefsManager = new PrefsManager(getApplicationContext());
                    if (!prefsManager.isFirstTimeLaunch()) {
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
                catch (Exception ex)
                {
                    Log.i(LOG_TAG, "Error in handler");
                    ex.printStackTrace();
                }
            }
        }, 1000);
    }
}
