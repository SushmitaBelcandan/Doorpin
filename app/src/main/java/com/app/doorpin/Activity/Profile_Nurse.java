package com.app.doorpin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.app.doorpin.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Profile_Nurse extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ActionBar toolBar;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__nurse);

        toolBar = getSupportActionBar();
        bottomNavigationView = findViewById(R.id.btm_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.getMenu().findItem(R.id.navigation_nurse_profile).setChecked(true);//make bottom navigation active for current page

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.navigation_nurse_patients:
                startActivity(new Intent(Profile_Nurse.this, HomePage_Nurse.class));
                finish();
                break;

            case R.id.navigation_nurse_profile:
                startActivity(new Intent(Profile_Nurse.this, Profile_Nurse.class));
                finish();
                break;


        }

        return true;
    }
}
