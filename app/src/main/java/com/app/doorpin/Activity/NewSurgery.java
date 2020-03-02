package com.app.doorpin.Activity;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.doorpin.R;

import java.util.ArrayList;

public class NewSurgery extends AppCompatActivity {

    Toolbar toolbar_new_surgery;
    Spinner spnr_surgery_name;

    ArrayList<String> arrList_surgery_name;
    String strSurgeryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_surgery);


        toolbar_new_surgery = findViewById(R.id.toolbar_new_surgery);
        spnr_surgery_name = findViewById(R.id.spnr_surgery_name);
        //toolbar
        setSupportActionBar(toolbar_new_surgery);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar_new_surgery.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //surgery list into spinner
        addSurgeryName();

    }

    private void addSurgeryName() {
        //add spinner item
        arrList_surgery_name = new ArrayList<>();
        arrList_surgery_name.add("Cataract Surgery");
        arrList_surgery_name.add("Low Back Pain Surgery");
        ArrayAdapter<String> surgeryStatusAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrList_surgery_name);
        surgeryStatusAdapter.setDropDownViewResource(R.layout.new_surgery_dropdown);
        spnr_surgery_name.setAdapter(surgeryStatusAdapter);
        spnr_surgery_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.heading_purple)); //Change selected text color
                ((TextView) view).setTextAppearance(getApplicationContext(), R.style.surgery_dropdown);
                strSurgeryName = spnr_surgery_name.getSelectedItem().toString(); //selected string
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
