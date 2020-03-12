package com.app.doorpin.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.app.doorpin.Adapters.PatientDetails_Adapter;
import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class PatientDetails extends AppCompatActivity {

    SessionManager session;
    Toolbar toolbar_patient_details;
    //   FloatingActionButton fab_illness;
    TabLayout tabPatientDetails;
    ViewPager viewPager;

    TextView tv_patientname, tv_patient_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_details);

        session = new SessionManager(PatientDetails.this);

        toolbar_patient_details = findViewById(R.id.toolbar_patient_details);

        tv_patientname = findViewById(R.id.tv_patientname);
        tv_patient_id = findViewById(R.id.tv_patient_id);
        //------------------------------------------------toolbar---------------------------
        setSupportActionBar(toolbar_patient_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar_patient_details.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //-----------------------------------------------tablayout---------------------------
        tabPatientDetails = findViewById(R.id.tabPatientDetails);
        viewPager = findViewById(R.id.viewPager);
        //fab_illness = findViewById(R.id.fab_illness_details);

        tabPatientDetails.addTab(tabPatientDetails.newTab().setText(getResources().getString(R.string.personal_info)));
        tabPatientDetails.addTab(tabPatientDetails.newTab().setText("Illness"));
        for (int i = 0; i < tabPatientDetails.getTabCount(); i++) {
            if (i == 0) {
                View tab = ((ViewGroup) tabPatientDetails.getChildAt(0)).getChildAt(i);
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                p.setMargins(0, 0, 1, 0);
                tab.setBackgroundResource(R.drawable.tab_selector_left);
                tab.requestLayout();
            } else {
                View tab = ((ViewGroup) tabPatientDetails.getChildAt(0)).getChildAt(i);
                tab.setBackgroundResource(R.drawable.tab_selector_right);
                tab.requestLayout();
            }
        }

        tabPatientDetails.setTabGravity(TabLayout.GRAVITY_FILL);
        PatientDetails_Adapter newsEvents_adapter = new PatientDetails_Adapter(getSupportFragmentManager(),
                PatientDetails.this, tabPatientDetails.getTabCount());
        viewPager.setAdapter(newsEvents_adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabPatientDetails));
        tabPatientDetails.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //-----------show patient info------
        getPatientIdentity();

    }

    private void getPatientIdentity() {
        if (!session.getPatientIdHome().equals("NA")) {
            tv_patient_id.setText("Patient Id" + " - " + session.getPatientIdHome());
            tv_patientname.setText(session.getPatientNameHome());
        } else {
            tv_patient_id.setText("");
            tv_patientname.setText("");
        }
    }

    /*  @Override
      public void onClick(View v) {

          switch(v.getId()){
              case R.id.tv_illness:
                  startActivity(new Intent(PatientDetails.this, IllnessDetails.class));
                  break;
              case R.id.btn_edit:
                  startActivity(new Intent(PatientDetails.this, EditPatientDetails.class));
                  break;
               default:
                   break;

          }

      }*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
