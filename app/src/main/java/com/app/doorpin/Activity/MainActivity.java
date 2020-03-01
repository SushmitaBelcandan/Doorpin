package com.app.doorpin.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SessionManager sessionManager;
    private LinearLayout lldoc, llnurse;
    private ImageView doc_img, nurse_img;
    private TextView tvDoctorTitle, tvNurseTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(MainActivity.this);

        lldoc = findViewById(R.id.ll_doc);
        llnurse = findViewById(R.id.ll_nurse);

        doc_img = findViewById(R.id.doc_img);
        nurse_img = findViewById(R.id.nurse_img);
        tvDoctorTitle = findViewById(R.id.tvDoctorTitle);
        tvNurseTitle = findViewById(R.id.tvNurseTitle);

        lldoc.setOnClickListener(this);
        llnurse.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_doc:
                // if doctor is selected
                lldoc.setBackgroundResource(R.drawable.purple_bg_rounded_cor);
                Glide.with(MainActivity.this).load(R.drawable.doctor_white).into(doc_img);
                tvDoctorTitle.setTextColor(getResources().getColor(R.color.purewhite));
                //and make nurse tile unselected
                llnurse.setBackgroundResource(R.drawable.rectangle_with_border);
                Glide.with(MainActivity.this).load(R.drawable.nurse_violet).into(nurse_img);
                tvNurseTitle.setTextColor(getResources().getColor(R.color.heading_purple));
                //save id 1 if doctor is using application
                sessionManager.saveDoctorNurseId("1");
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
                break;
            case R.id.ll_nurse:
                // if nurse is selected
                llnurse.setBackgroundResource(R.drawable.purple_bg_rounded_cor);
                Glide.with(MainActivity.this).load(R.drawable.nurse_white).into(nurse_img);
                tvNurseTitle.setTextColor(getResources().getColor(R.color.purewhite));
                //and make doctor tile unselected
                lldoc.setBackgroundResource(R.drawable.rectangle_with_border);
                Glide.with(MainActivity.this).load(R.drawable.doctor_violet).into(doc_img);
                tvDoctorTitle.setTextColor(getResources().getColor(R.color.heading_purple));
                //save id 2 if nurse is using application
                sessionManager.saveDoctorNurseId("2");
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
                break;
            default:
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
