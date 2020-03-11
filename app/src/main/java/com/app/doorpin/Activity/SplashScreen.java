package com.app.doorpin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseLongArray;
import android.view.WindowManager;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIME_OUT = 2000;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        session = new SessionManager(SplashScreen.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (session.isLoggedIn()) {
                    if (session.getDoctorNurseId().equals("1")) {
                        Intent intent = new Intent(SplashScreen.this, HomePage_Doctor.class);
                        startActivity(intent);
                        finish();
                    } else if (session.getDoctorNurseId().equals("2")) {
                        Intent intent = new Intent(SplashScreen.this, HomePage_Doctor.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
}
