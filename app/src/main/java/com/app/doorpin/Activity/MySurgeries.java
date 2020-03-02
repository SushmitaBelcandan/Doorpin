package com.app.doorpin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.Adapters.SurgeryAdapter;
import com.app.doorpin.R;
import com.app.doorpin.models.Suregery_Header_Model;
import com.app.doorpin.models.Surgery;
import com.app.doorpin.models.Surgery_FeedItem_Model;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;
import com.mindorks.placeholderview.PlaceHolderView;

public class MySurgeries extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ActionBar toolBar;
    BottomNavigationView bottomNavigationView;
    ExpandablePlaceHolderView phv_surgeries;
    FloatingActionButton fab_surgeries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_surgeries);

        toolBar = getSupportActionBar();
        bottomNavigationView = findViewById(R.id.btm_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.getMenu().findItem(R.id.navigation_doctor_surgeries).setChecked(true);//make bottom navigation active for current page

        phv_surgeries = findViewById(R.id.phv_surgeries);
        fab_surgeries = findViewById(R.id.fab_surgeries);

      /*  Surgery surgery[]=new Surgery[]{

                new Surgery("Cataract Surgery"),
                new Surgery("Low Back Pain Surgery "),
                new Surgery("Corona artery bypass"),
                new Surgery("Cholecystocomy"),
                new Surgery("Appendectomy"),

        };

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_surgeries);
        SurgeryAdapter adapter = new SurgeryAdapter(surgery);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);*/
        phv_surgeries.addView(new Suregery_Header_Model(getApplicationContext()));
        phv_surgeries.addView(new Surgery_FeedItem_Model(getApplicationContext()));
        //floating button action for new surgeries
        fab_surgeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNewSurgeries = new Intent(MySurgeries.this, NewSurgery.class);
                startActivity(intentNewSurgeries);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.navigation_doctor_patients:
                startActivity(new Intent(MySurgeries.this, HomePage_Doctor.class));
                finish();
                break;

            case R.id.navigation_doctor_surgeries:
                startActivity(new Intent(MySurgeries.this, MySurgeries.class));
                finish();
                break;

            case R.id.navigation_doctor_profile:
                startActivity(new Intent(MySurgeries.this, Profile_Doctor.class));
                finish();
                break;
        }


        return true;
    }
}
