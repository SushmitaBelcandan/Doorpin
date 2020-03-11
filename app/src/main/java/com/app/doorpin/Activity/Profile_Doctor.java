package com.app.doorpin.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.LoginRequest;
import com.app.doorpin.retrofit.SurgList_DP_RetroModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile_Doctor extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    RecyclerView rv_surgeries_done;
    ActionBar toolBar;
    BottomNavigationView bottomNavigationView;
    ProgressDialog progressDialog;
    SessionManager sessionManager;
    ApiInterface apiInterface;

    TextView tvNoSurgeryDone;
    ImageView iv_edit_profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_doctor);

        sessionManager = new SessionManager(Profile_Doctor.this);
        progressDialog = new ProgressDialog(Profile_Doctor.this);
        progressDialog.setMessage("Please Wait...");

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        iv_edit_profile = (ImageView) findViewById(R.id.iv_edit_profile);
        tvNoSurgeryDone = (TextView) findViewById(R.id.tvNoSurgeryDone);
        rv_surgeries_done = (RecyclerView) findViewById(R.id.rv_surgeries_done);
        toolBar = getSupportActionBar();
        bottomNavigationView = findViewById(R.id.btm_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.getMenu().findItem(R.id.navigation_doctor_profile).setChecked(true);//make bottom navigation active for current page


        DoctorDocuments doctorDocuments[] = new DoctorDocuments[]{
                new DoctorDocuments(R.drawable.document_grey),
                new DoctorDocuments(R.drawable.document_grey),
                new DoctorDocuments(R.drawable.document_grey),
                new DoctorDocuments(R.drawable.document_grey),


        };

        RecyclerView docRv = (RecyclerView) findViewById(R.id.rv_documents);
        DoctorDocAdapter adapterdoc = new DoctorDocAdapter(doctorDocuments);
        docRv.setHasFixedSize(true);
        docRv.setLayoutManager(new GridLayoutManager(this, 4));
        docRv.setAdapter(adapterdoc);
        //****************get surgery lists**************************
        getSurgeryList(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId());
        iv_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile_Doctor.this, EditProfile.class).putExtra("PROF", "DOCT"));
                finish();
            }
        });

    }

    private void getSurgeryList(String str_user_type, String str_usr_id) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final SurgList_DP_RetroModel surg_list_model = new SurgList_DP_RetroModel(str_user_type, str_usr_id);
        Call<SurgList_DP_RetroModel> call_surg_list = apiInterface.getDocProfileSurgeryList(surg_list_model);
        call_surg_list.enqueue(new Callback<SurgList_DP_RetroModel>() {
            @Override
            public void onResponse(Call<SurgList_DP_RetroModel> call, Response<SurgList_DP_RetroModel> response) {
                SurgList_DP_RetroModel surgListRequest = response.body();
                if (response.isSuccessful()) {
                    if (surgListRequest.status.equals("success")) {

                        List<SurgList_DP_RetroModel.SurgList_DP_Datum> surg_list_datum = surgListRequest.result;
                        if (surg_list_datum.size() <= 0) {
                            //when response array is empty
                            rv_surgeries_done.setVisibility(View.GONE);
                            tvNoSurgeryDone.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                            Toast.makeText(Profile_Doctor.this, surgListRequest.message, Toast.LENGTH_SHORT).show();
                        } else {
                            ArrayList<String> arrListSurg = new ArrayList<String>();
                            rv_surgeries_done.setVisibility(View.VISIBLE);
                            tvNoSurgeryDone.setVisibility(View.GONE);
                            for (SurgList_DP_RetroModel.SurgList_DP_Datum surg_list_data : surg_list_datum) {
                                arrListSurg.add(surg_list_data.surgery_name);
                            }
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_surgeries_done);
                            SurgeryDoneAdapter adapter = new SurgeryDoneAdapter(arrListSurg);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setAdapter(adapter);
                            progressDialog.dismiss();

                        }
                    } else {
                        tvNoSurgeryDone.setVisibility(View.VISIBLE);
                        rv_surgeries_done.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    }
                } else {
                    tvNoSurgeryDone.setVisibility(View.VISIBLE);
                    rv_surgeries_done.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<SurgList_DP_RetroModel> call, Throwable t) {
                call.cancel();
                tvNoSurgeryDone.setVisibility(View.VISIBLE);
                rv_surgeries_done.setVisibility(View.GONE);
                progressDialog.dismiss();
            }
        });
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
