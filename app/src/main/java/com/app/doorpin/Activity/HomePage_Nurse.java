package com.app.doorpin.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.Adapters.PatientAdapter;
import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.models.Patient;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.HomePage_RetroModel;
import com.app.doorpin.retrofit.Logout_RetroModel;
import com.app.doorpin.retrofit.SearchPatient_RetroModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePage_Nurse extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ApiInterface apiInterface;
    SessionManager sessionManager;
    ProgressDialog progressDialog;

    ActionBar toolBar;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab_homepage;

    TextView tvUserName;
    RecyclerView rv_patient_nurse;
    EditText etSearchNurse;
    LinearLayout llBtnSearchNurse;
    LinearLayout llNoData, llNetworkError;
    ImageButton imgBtnLogoutNurse;


    private ArrayList<String> arrlist_patient_id;
    private ArrayList<String> arrlist_patient_name;
    private ArrayList<String> arrlist_display_id;

    private String str_search_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page__nurse);

        sessionManager = new SessionManager(HomePage_Nurse.this);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        progressDialog = new ProgressDialog(HomePage_Nurse.this);
        progressDialog.setMessage("Please wait.....");

        toolBar = getSupportActionBar();
        bottomNavigationView = findViewById(R.id.btm_navigation_view);
        fab_homepage = findViewById(R.id.fab_homepage_nurse);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.getMenu().findItem(R.id.navigation_nurse_patients).setChecked(true);//make bottom navigation active for current page

        tvUserName = findViewById(R.id.tvUserName);
        etSearchNurse = findViewById(R.id.etSearchNurse);
        llBtnSearchNurse = findViewById(R.id.llBtnSearchNurse);
        imgBtnLogoutNurse = findViewById(R.id.imgBtnLogoutNurse);
        rv_patient_nurse = findViewById(R.id.rv_patient_nurse);
        llNoData = findViewById(R.id.llNoData);
        llNetworkError = findViewById(R.id.llNetworkError);
        //grid view of nurse home page
        rv_patient_nurse.setHasFixedSize(true);
        rv_patient_nurse.setLayoutManager(new GridLayoutManager(this, 2));//grid view of patient list

        fab_homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage_Nurse.this, NewPatient1.class));
            }
        });

        getPatientList(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId());
        etSearchNurse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etSearchNurse.getText().toString().length() == 0) {
                    if (Utils.CheckInternetConnection(getApplicationContext())) {
                        getPatientList(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId());//when search text is empty then show home page default data
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //call search
        llBtnSearchNurse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.CheckInternetConnection(getApplicationContext())) {

                    str_search_key = etSearchNurse.getText().toString().trim();
                    if (str_search_key.equals(null) || str_search_key.isEmpty()) {
                        Toast.makeText(HomePage_Nurse.this, "Not a Valid Keyword", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean digitsOnly = TextUtils.isDigitsOnly(str_search_key);
                        if (digitsOnly == true) {
                            searchPatient(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_search_key, "NA");
                        } else {
                            searchPatient(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), "NA", str_search_key);

                        }
                    }

                } else {
                    Toast.makeText(HomePage_Nurse.this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //call logout
        imgBtnLogoutNurse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.getDeviceId().equals("NA")) {
                    new AlertDialog.Builder(HomePage_Nurse.this)
                            .setMessage("Please Try Again")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    if (Utils.CheckInternetConnection(getApplicationContext())) {
                        logoutUser(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), sessionManager.getDeviceId());
                    } else {
                        Toast.makeText(HomePage_Nurse.this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void logoutUser(String str_usr_type, String str_usr_id, String str_device_id) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logout_RetroModel logout_model = new Logout_RetroModel(str_usr_type, str_usr_id, str_device_id);
        Call<Logout_RetroModel> call_logout = apiInterface.logoutUser(logout_model);
        call_logout.enqueue(new Callback<Logout_RetroModel>() {
            @Override
            public void onResponse(Call<Logout_RetroModel> call, Response<Logout_RetroModel> response) {
                Logout_RetroModel logout_resources = response.body();
                if (response.isSuccessful()) {
                    if (logout_resources.status1.equals("success")) {
                        progressDialog.dismiss();
                        Toast.makeText(HomePage_Nurse.this, logout_resources.message1, Toast.LENGTH_SHORT).show();
                        Intent intentLogin = new Intent(HomePage_Nurse.this, MainActivity.class);
                        startActivity(intentLogin);
                        sessionManager.logoutUser();
                        finish();
                    } else {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(HomePage_Nurse.this)
                                .setMessage("Please Try Again")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                } else {
                    //response id getting failed
                    if (response.code() == 400) {
                        if (!response.isSuccessful()) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response.errorBody().string());
                                String userMessage = jsonObject.getString("status");
                                String internalMessage = jsonObject.getString("message");
                                progressDialog.dismiss();
                                new AlertDialog.Builder(HomePage_Nurse.this)
                                        .setMessage(internalMessage)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Logout_RetroModel> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                new AlertDialog.Builder(HomePage_Nurse.this)
                        .setMessage("Please Try Again")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    private void searchPatient(String usr_type, String usr_id, String patient_id, String str_search_data) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SearchPatient_RetroModel search_patient_model = new SearchPatient_RetroModel(usr_type, usr_id, patient_id, str_search_data);
        Call<SearchPatient_RetroModel> search_patient_call = apiInterface.getPatientList(search_patient_model);
        search_patient_call.enqueue(new Callback<SearchPatient_RetroModel>() {
            @Override
            public void onResponse(Call<SearchPatient_RetroModel> call, Response<SearchPatient_RetroModel> response) {
                SearchPatient_RetroModel search_resources = response.body();
                if (response.isSuccessful()) {
                    if (search_resources.status.equals("success")) {
                        List<SearchPatient_RetroModel.Search_Datum> datumList = search_resources.result;
                        if (datumList.size() <= 0) {
                            llNoData.setVisibility(View.VISIBLE);
                            rv_patient_nurse.setVisibility(View.GONE);
                            llNetworkError.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        } else {

                            rv_patient_nurse.setVisibility(View.VISIBLE);//show list
                            llNoData.setVisibility(View.GONE);
                            llNetworkError.setVisibility(View.GONE);

                            arrlist_patient_id = new ArrayList<>();
                            arrlist_patient_name = new ArrayList<>();
                            arrlist_display_id = new ArrayList<>();
                            for (SearchPatient_RetroModel.Search_Datum datum : datumList) {
                                if (datum.patient_id.equals(null) || datum.patient_id.isEmpty() || datum.patient_id.equals("NA")) {
                                    //skip row for null value
                                } else {
                                    arrlist_patient_id.add(datum.patient_id);
                                    arrlist_patient_name.add(datum.patient_name);
                                    arrlist_display_id.add(datum.display_id);
                                }

                            }
                            PatientAdapter adapter = new PatientAdapter(getApplicationContext(), arrlist_patient_id, arrlist_patient_name,
                                    arrlist_display_id);
                            rv_patient_nurse.setAdapter(adapter);
                            progressDialog.dismiss();
                            Toast.makeText(HomePage_Nurse.this, search_resources.message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        llNoData.setVisibility(View.VISIBLE);
                        rv_patient_nurse.setVisibility(View.GONE);
                        llNetworkError.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        Toast.makeText(HomePage_Nurse.this, search_resources.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 400) {
                        if (!response.isSuccessful()) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response.errorBody().string());
                                String userMessage = jsonObject.getString("status");
                                String internalMessage = jsonObject.getString("message");
                                progressDialog.dismiss();
                                llNoData.setVisibility(View.VISIBLE);
                                rv_patient_nurse.setVisibility(View.GONE);
                                llNetworkError.setVisibility(View.GONE);
                                new AlertDialog.Builder(HomePage_Nurse.this)
                                        .setMessage(internalMessage)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchPatient_RetroModel> call, Throwable t) {
                call.cancel();
                llNetworkError.setVisibility(View.VISIBLE);
                llNoData.setVisibility(View.GONE);
                rv_patient_nurse.setVisibility(View.GONE);
                progressDialog.dismiss();
            }
        });
    }

    private void getPatientList(String usr_type, String usr_id) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HomePage_RetroModel homepage_model = new HomePage_RetroModel(usr_type, usr_id);
        Call<HomePage_RetroModel> homepage_call = apiInterface.getPatientListHomePage(homepage_model);
        homepage_call.enqueue(new Callback<HomePage_RetroModel>() {
            @Override
            public void onResponse(Call<HomePage_RetroModel> call, Response<HomePage_RetroModel> response) {
                HomePage_RetroModel homepage_resources = response.body();
                if (response.isSuccessful()) {
                    if (homepage_resources.status.equals("success")) {

                        if (homepage_resources.user_name.equals("NA") || homepage_resources.user_name.equals(null) ||
                                homepage_resources.user_name.equals("null") || homepage_resources.user_name.isEmpty()) {
                            tvUserName.setText("");
                        } else {
                            tvUserName.setText(homepage_resources.user_name);
                        }
                        List<HomePage_RetroModel.HomePage_Datum> datumList = homepage_resources.response;
                        if (datumList.size() <= 0) {
                            llNoData.setVisibility(View.VISIBLE);
                            rv_patient_nurse.setVisibility(View.GONE);
                            llNetworkError.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        } else {

                            rv_patient_nurse.setVisibility(View.VISIBLE);//show list
                            llNoData.setVisibility(View.GONE);
                            llNetworkError.setVisibility(View.GONE);

                            arrlist_patient_id = new ArrayList<>();
                            arrlist_patient_name = new ArrayList<>();
                            arrlist_display_id = new ArrayList<>();
                            for (HomePage_RetroModel.HomePage_Datum datum : datumList) {
                                if (datum.patient_id.equals(null) || datum.patient_id.isEmpty() || datum.patient_id.equals("NA")) {
                                    //skip row for null value
                                } else {
                                    arrlist_patient_id.add(datum.patient_id);
                                    arrlist_patient_name.add(datum.patient_name);
                                    arrlist_display_id.add(datum.display_id);
                                }

                            }
                            PatientAdapter adapter = new PatientAdapter(getApplicationContext(), arrlist_patient_id, arrlist_patient_name,
                                    arrlist_display_id);
                            rv_patient_nurse.setAdapter(adapter);
                            progressDialog.dismiss();
                            //  Toast.makeText(HomePage_Nurse.this, homepage_resources.message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        llNoData.setVisibility(View.VISIBLE);
                        rv_patient_nurse.setVisibility(View.GONE);
                        llNetworkError.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        Toast.makeText(HomePage_Nurse.this, homepage_resources.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 400) {
                        if (!response.isSuccessful()) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response.errorBody().string());
                                String userMessage = jsonObject.getString("status");
                                String internalMessage = jsonObject.getString("message");
                                progressDialog.dismiss();
                                llNoData.setVisibility(View.VISIBLE);
                                rv_patient_nurse.setVisibility(View.GONE);
                                llNetworkError.setVisibility(View.GONE);
                                new AlertDialog.Builder(HomePage_Nurse.this)
                                        .setMessage(internalMessage)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<HomePage_RetroModel> call, Throwable t) {
                call.cancel();
                llNetworkError.setVisibility(View.VISIBLE);
                llNoData.setVisibility(View.GONE);
                rv_patient_nurse.setVisibility(View.GONE);
                progressDialog.dismiss();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_nurse_patients:
                startActivity(new Intent(HomePage_Nurse.this, HomePage_Nurse.class));
                finish();
                break;

            case R.id.navigation_nurse_profile:
                startActivity(new Intent(HomePage_Nurse.this, Profile_Nurse.class));
                finish();
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}

