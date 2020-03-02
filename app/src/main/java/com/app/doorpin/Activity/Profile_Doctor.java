package com.app.doorpin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.Adapters.DoctorDocAdapter;
import com.app.doorpin.Adapters.SurgeryDoneAdapter;
import com.app.doorpin.R;
import com.app.doorpin.models.DoctorDocuments;
import com.app.doorpin.models.SurgeriesDone;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Profile_Doctor extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    RecyclerView rv_surgeries_done;
    ActionBar toolBar;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_doctor);

        rv_surgeries_done=(RecyclerView)findViewById(R.id.rv_surgeries_done);
        toolBar=getSupportActionBar();
        bottomNavigationView=findViewById(R.id.btm_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.getMenu().findItem(R.id.navigation_doctor_profile).setChecked(true);//make bottom navigation active for current page


        DoctorDocuments doctorDocuments[]=new DoctorDocuments[]{
                new DoctorDocuments(R.drawable.document_grey),
                new DoctorDocuments(R.drawable.document_grey),
                new DoctorDocuments(R.drawable.document_grey),
                new DoctorDocuments(R.drawable.document_grey),


        };

        SurgeriesDone surgeriesDone[]=new SurgeriesDone[]{

                new SurgeriesDone("Cataract Surgery"),
                new SurgeriesDone("Low Back Pain Surgery "),
//                new SurgeriesDone("Corona artery bypass"),
//                new SurgeriesDone("Cholecystocomy"),
//                new SurgeriesDone("Appendectomy"),

        };

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_surgeries_done);
        SurgeryDoneAdapter adapter = new SurgeryDoneAdapter(surgeriesDone);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        RecyclerView docRv = (RecyclerView) findViewById(R.id.rv_documents);
        DoctorDocAdapter adapterdoc = new DoctorDocAdapter(doctorDocuments);
        docRv.setHasFixedSize(true);
        docRv.setLayoutManager(new GridLayoutManager(this,4));
        docRv.setAdapter(adapterdoc);




    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.navigation_doctor_patients:
                startActivity(new Intent(Profile_Doctor.this, HomePage_Doctor.class));
                finish();
                break;

            case R.id.navigation_doctor_surgeries:
                startActivity(new Intent(Profile_Doctor.this, MySurgeries.class));
                finish();
                break;

            case R.id.navigation_doctor_profile:
                startActivity(new Intent(Profile_Doctor.this, Profile_Doctor.class));
                finish();
                break;

        }


        return true;
    }
}
