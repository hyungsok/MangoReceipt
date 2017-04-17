package com.mangoreceipt;

import android.app.Activity;
import android.os.Bundle;

public class MainSplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_splash_screen);
        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(2500);
                    finish();
                } catch (Exception e) {
                }
            }
        };
        background.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}