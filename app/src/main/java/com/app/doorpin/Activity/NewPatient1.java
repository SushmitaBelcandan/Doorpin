package com.app.doorpin.Activity;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.Adapters.DocsListAdapter;
import com.app.doorpin.Adapters.FileUploader;
import com.app.doorpin.Adapters.PrsecListAdapter;
import com.app.doorpin.Adapters.ReportListAdapter;
import com.app.doorpin.Adapters.ResizeImage_Adapter;
import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.progress_bar.CallBackFilesAfterUpload_Interface;
import com.app.doorpin.progress_bar.CustomDialog;
import com.app.doorpin.progress_bar.ITrafficSpeedListener;
import com.app.doorpin.progress_bar.TrafficSpeedMeasurer;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.DoctorList_RetroModel;
import com.app.doorpin.retrofit.New_Patient_Retro_Model;
import com.app.doorpin.upload_docs.FileUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPatient1 extends AppCompatActivity implements View.OnClickListener, CallBackFilesAfterUpload_Interface {

    private SessionManager sessionManager;
    private ApiInterface apiInterface;
    private ProgressDialog progressDialog;
    Spinner spnr_marital_status, spnr_recomnd_doct;
    EditText et_dob, et_follow_update;
    EditText et_address_newp, et_disease_type_newp, et_mobile_newp, et_email_newp, et_name_newp;
    RecyclerView rv_docs, rv_img, rv_report;
    public Calendar refCalendar = Calendar.getInstance();
    String strStoreDate;
    private int newp_marital_status_id = 0;
    private int recomnd_doct_id = 0;
    private String newp_str_gender_name = "";

    ArrayList<String> arrList_docs = new ArrayList<>();
    ArrayList<String> arrList_report = new ArrayList<>();
    ArrayList<String> arrayList_Img = new ArrayList<>();

    private static final int GALLERY_PICTURE = 1;
    private static final int CAMERA_REQUEST = 0;
    private static final int DOC_REQUEST = 105;
    private static final String DOC_TYPE_PRSEC = "1";
    private static final String DOC_TYPE_REPORT = "2";
    private static final String DOC_TYPE_OTHERS = "3";
    private int selected_doc;
    boolean isKitKat;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    CustomDialog customProgressDialog;
    private ArrayList<String> files_to_upload = new ArrayList<>();

    private static final boolean SHOW_SPEED_IN_BITS = false;
    private TrafficSpeedMeasurer mTrafficSpeedMeasurer;
    String upStreamSpeed, downStreamSpeed;
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    DatePickerDialog.OnDateSetListener dateDOB;
    DatePickerDialog.OnDateSetListener dateFollowup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient);

        mTrafficSpeedMeasurer = new TrafficSpeedMeasurer(TrafficSpeedMeasurer.TrafficType.ALL);
        mTrafficSpeedMeasurer.startMeasuring();
        customProgressDialog = CustomDialog.getInstance();

        sessionManager = new SessionManager(NewPatient1.this);
        progressDialog = new ProgressDialog(NewPatient1.this);
        progressDialog.setMessage("Please Wait...");

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Toolbar toolbar_new_patient = findViewById(R.id.toolbar_new_patient);
        et_name_newp = findViewById(R.id.et_name_newp);
        et_email_newp = findViewById(R.id.et_email_newp);
        et_dob = findViewById(R.id.et_dob);
        et_mobile_newp = findViewById(R.id.et_mobile_newp);
        RadioGroup rgp_gender_newp = findViewById(R.id.rgp_gender_newp);
        et_address_newp = findViewById(R.id.et_address_newp);
        et_disease_type_newp = findViewById(R.id.et_disease_type_newp);
        et_follow_update = findViewById(R.id.et_follow_update);
        LinearLayout ll_doctor_list = findViewById(R.id.ll_doctor_list);
        spnr_recomnd_doct = findViewById(R.id.spnr_recomnd_doct);
        rv_report = findViewById(R.id.rv_report);
        rv_docs = findViewById(R.id.rv_docs);
        rv_img = findViewById(R.id.rv_img);
        ImageView ivCalenderFollowup = findViewById(R.id.ivCalenderFollowup);
        ImageView ivCalenderDob = findViewById(R.id.ivCalenderDob);
        ivCalenderFollowup.setOnClickListener(this);
        ivCalenderDob.setOnClickListener(this);

        //toolbar
        setSupportActionBar(toolbar_new_patient);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar_new_patient.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        LinearLayout llcapturePrescription = findViewById(R.id.ll_capture_prescription);
        LinearLayout lluploadDocs = findViewById(R.id.ll_upload_doc);
        spnr_marital_status = findViewById(R.id.spnr_marital_status);

        Button btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        llcapturePrescription.setOnClickListener(this);
        lluploadDocs.setOnClickListener(this);

        //set marital status
        addMaritalStatus();
        //select Date of birth by populating calender
        selectDateDob();
        selectDateFollowUp();
        if (sessionManager.getDoctorNurseId().equals("1")) {
            ll_doctor_list.setVisibility(View.GONE);
        } else {
            ll_doctor_list.setVisibility(View.VISIBLE);
            //doctor list for nurse, if it is visible then only call api
            if (Utils.CheckInternetConnection(getApplicationContext())) {
                if (!sessionManager.getHospiatlId().equals("NA")) {
                    getDoctorList(sessionManager.getHospiatlId());
                } else {
                    //do nothing
                }
            } else {
                Toast.makeText(this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        }

        rgp_gender_newp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtn_male_newp) {
                    newp_str_gender_name = "Male";
                } else if (checkedId == R.id.rbtn_female_newp) {
                    newp_str_gender_name = "Female";
                } else if (checkedId == R.id.rbtn_other_newp) {
                    newp_str_gender_name = "Other";
                } else {
                    newp_str_gender_name = "-1";
                }
            }
        });

    }

    private boolean validateForm(String str_name, String str_email, String str_dob, String str_mobile,
                                 String str_marital_status_id, String str_address,
                                 String str_type_disease, String str_followup_date) {
        //new patient name
        if (str_name.isEmpty()) {
            et_name_newp.setError("Please enter Patient Name");
            et_name_newp.requestFocus();
            return false;
        }
        //email validations
        if (str_email.isEmpty()) {
            et_email_newp.setError("Please enter Email Id");
            et_email_newp.requestFocus();
            return false;
        }
        if (!str_email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+") &&
                !str_email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+")) {
            et_email_newp.setError("Invalid Email Id");
            et_email_newp.requestFocus();
            return false;
        }
        //date of birth validatons
        if (str_dob.isEmpty()) {
            et_dob.setError("Please enter Date of Birth");
            et_dob.requestFocus();
            return false;
        }
        //mobile validations
        if (str_mobile.isEmpty()) {
            et_mobile_newp.setError("Please enter Mobile Number");
            et_mobile_newp.requestFocus();
            return false;
        }
        if (str_mobile.matches("(\\d)(?!\\1+$)\\d{10}")) {
            et_mobile_newp.setError("Invalid Mobile Number");
            et_mobile_newp.requestFocus();
            return false;
        }
        if (str_mobile.length() != 10) {
            et_mobile_newp.setError("Invalid Mobile Number");
            et_mobile_newp.requestFocus();
            return false;
        }
        //Gender validations
        if (newp_str_gender_name.isEmpty()) {
            new AlertDialog.Builder(NewPatient1.this)
                    .setMessage("Please select Gender!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return false;
        }
        if (str_marital_status_id.equals("0")) {
            new AlertDialog.Builder(NewPatient1.this)
                    .setMessage("Please select Marital Status")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return false;
        }
        if (str_address.isEmpty()) {
            et_address_newp.setError("Please enter Address");
            et_address_newp.requestFocus();
            return false;
        }
        //new patient's type of disease
        if (str_type_disease.isEmpty()) {
            et_disease_type_newp.setError("Please enter Disease Name");
            et_disease_type_newp.requestFocus();
            return false;
        }
        //follow up date
        if (str_followup_date.isEmpty()) {
            et_follow_update.setError("Please enter Follow up Date");
            et_follow_update.requestFocus();
            return false;
        }
        return true;

    }

    private void addMaritalStatus() {
        //add spinner item
        ArrayList<String> arrList_marital_status = new ArrayList<>();
        arrList_marital_status.add("Select marital status");
        arrList_marital_status.add("Married");
        arrList_marital_status.add("Single");
        final ArrayAdapter<String> maritalStatusAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrList_marital_status);
        maritalStatusAdapter.setDropDownViewResource(R.layout.marital_status);
        spnr_marital_status.setAdapter(maritalStatusAdapter);
        spnr_marital_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.black_shade_1)); //Change selected text color
                ((TextView) view).setTextAppearance(getApplicationContext(), R.style.marital_status_dropdown);
                newp_marital_status_id = maritalStatusAdapter.getPosition(spnr_marital_status.getSelectedItem().toString());//get selected marital status id
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

    }

    private void getDoctorList(String str_hospital_id) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final DoctorList_RetroModel doc_list_model = new DoctorList_RetroModel(str_hospital_id);
        Call<DoctorList_RetroModel> call = apiInterface.getDoctorList(doc_list_model);
        call.enqueue(new Callback<DoctorList_RetroModel>() {
            @Override
            public void onResponse(Call<DoctorList_RetroModel> call, Response<DoctorList_RetroModel> response) {
                DoctorList_RetroModel doc_list_resources = response.body();
                if (response.isSuccessful()) {
                    ArrayList<String> arr_str_doctor = new ArrayList<>();
                    ArrayList<String> arr_str_doctor_id = new ArrayList<>();
                    arr_str_doctor.add("Select Doctor Name");
                    arr_str_doctor_id.add("0");
                    if (doc_list_resources.status.equals("success")) {
                        List<DoctorList_RetroModel.Doctor_List_Datum> doc_list_data = doc_list_resources.result;
                        if (doc_list_data.size() > 0) {
                            for (DoctorList_RetroModel.Doctor_List_Datum doc_list_datum : doc_list_data) {
                                arr_str_doctor.add(doc_list_datum.doctor_name);
                                arr_str_doctor_id.add(doc_list_datum.doctor_id);
                            }
                            progressDialog.dismiss();
                        } else {
                            //empty list
                            progressDialog.dismiss();
                        }
                        final ArrayAdapter<String> maritalStatusAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arr_str_doctor);
                        maritalStatusAdapter.setDropDownViewResource(R.layout.marital_status);
                        spnr_recomnd_doct.setAdapter(maritalStatusAdapter);
                        spnr_recomnd_doct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                ((TextView) view).setTextColor(getResources().getColor(R.color.black_shade_1)); //Change selected text color
                                ((TextView) view).setTextAppearance(getApplicationContext(), R.style.marital_status_dropdown);
                                String str_doct_name = spnr_recomnd_doct.getSelectedItem().toString(); //selected string
                                recomnd_doct_id = maritalStatusAdapter.getPosition(str_doct_name);//get selected doctor id
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                //do nothing
                            }
                        });
                    } else {
                        //failure response
                        progressDialog.dismiss();
                    }
                } else {
                    if (response.code() == 400) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String userMessage = jsonObject.getString("status");
                            String internalMessage = jsonObject.getString("message");
                            progressDialog.dismiss();
                            new android.app.AlertDialog.Builder(NewPatient1.this)
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

            @Override
            public void onFailure(Call<DoctorList_RetroModel> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ivCalenderFollowup:
                // TODO Auto-generated method stub
                DatePickerDialog dpd = new DatePickerDialog(NewPatient1.this, R.style.MyThemeOverlay, dateFollowup, refCalendar
                        .get(Calendar.YEAR), refCalendar.get(Calendar.MONTH), refCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMinDate(System.currentTimeMillis()); //make past date disable
                dpd.show();
                dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // do something onCancel
                        strStoreDate = "null";
                        et_follow_update.setText("");
                    }
                });

                break;
            case R.id.ivCalenderDob:
                // TODO Auto-generated method stub
                DatePickerDialog dpdDob = new DatePickerDialog(NewPatient1.this, R.style.MyThemeOverlay, dateDOB, refCalendar
                        .get(Calendar.YEAR), refCalendar.get(Calendar.MONTH), refCalendar.get(Calendar.DAY_OF_MONTH));
                dpdDob.show();
                dpdDob.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // do something onCancel
                        strStoreDate = "null";
                        et_dob.setText("");
                    }
                });
                break;
            case R.id.btn_add:

                String str_name = et_name_newp.getText().toString().trim();
                String str_patient_email = et_email_newp.getText().toString().trim();
                String str_patient_dob = et_dob.getText().toString().trim();
                String str_patient_mobile = et_mobile_newp.getText().toString().trim();
                String str_patient_gender = newp_str_gender_name;//getting selected gender associated id/value
                String str_patient_marital_status = String.valueOf(newp_marital_status_id).trim();
                String strMaritalStatus = spnr_marital_status.getSelectedItem().toString(); //selected string
                String str_recomnd_doctor = "";
                if (sessionManager.getDoctorNurseId().equals("1")) {
                    str_recomnd_doctor = sessionManager.getLoggedUsrId();
                } else {
                    str_recomnd_doctor = String.valueOf(recomnd_doct_id).trim();
                }
                String str_patient_address = et_address_newp.getText().toString().trim();
                String str_disease_type = et_disease_type_newp.getText().toString().trim();
                String str_followup_date = et_follow_update.getText().toString().trim();

                if (validateForm(str_name, str_patient_email, str_patient_dob, str_patient_mobile,
                        str_patient_marital_status, str_patient_address, str_disease_type, str_followup_date)) {
                    //remove error mark when no fields are empty
                    et_name_newp.setError(null);
                    et_email_newp.setError(null);
                    et_dob.setError(null);
                    et_mobile_newp.setError(null);
                    et_address_newp.setError(null);
                    et_disease_type_newp.setError(null);
                    et_follow_update.setError(null);

                    if (sessionManager.getDoctorNurseId().equals("2")) {
                        if (recomnd_doct_id == 0) {
                            new AlertDialog.Builder(NewPatient1.this)
                                    .setMessage("Please select Doctor Name")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            return;
                        } else {
                            JsonArray jsn_arr_prsec = new JsonArray();
                            JsonArray jsn_arr_report = new JsonArray();
                            JsonArray jsn_arr_other_doc = new JsonArray();

                            for (int i = 0; i < arrayList_Img.size(); i++) {
                                if (!arrayList_Img.get(i).equals("")) {
                                    JsonObject object = new JsonObject();
                                    object.addProperty("presc_doc", arrayList_Img.get(i));
                                    jsn_arr_prsec.add(object);
                                }
                            }
                            for (int i = 0; i < arrList_docs.size(); i++) {
                                if (!arrList_docs.get(i).equals("")) {
                                    JsonObject object = new JsonObject();
                                    object.addProperty("other_doc", arrList_docs.get(i));
                                    jsn_arr_other_doc.add(object);
                                }
                            }
                            for (int i = 0; i < arrList_report.size(); i++) {
                                if (!arrList_report.get(i).equals("")) {
                                    JsonObject object = new JsonObject();
                                    object.addProperty("report_doc", arrList_report.get(i));
                                    jsn_arr_report.add(object);
                                }
                            }
                            if (jsn_arr_report.size() <= 0 && arrList_report.size() <= 0 && jsn_arr_prsec.size() <= 0 && arrayList_Img.size() <= 0) {
                                addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                        str_patient_email,
                                        str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                        str_disease_type, str_recomnd_doctor, str_followup_date, "null", "null", jsn_arr_other_doc);

                            } else if (jsn_arr_report.size() <= 0 && arrList_report.size() <= 0 && jsn_arr_other_doc.size() <= 0 && arrList_docs.size() <= 0) {
                                addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                        str_patient_email,
                                        str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                        str_disease_type, str_recomnd_doctor, str_followup_date, jsn_arr_prsec, "null", "null");

                            } else if (jsn_arr_prsec.size() <= 0 && arrayList_Img.size() <= 0 && jsn_arr_other_doc.size() <= 0 && arrList_docs.size() <= 0) {
                                addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                        str_patient_email,
                                        str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                        str_disease_type, str_recomnd_doctor, str_followup_date, "null", jsn_arr_report, "null");

                            } else if (jsn_arr_prsec.size() <= 0 && arrayList_Img.size() <= 0) {
                                addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                        str_patient_email,
                                        str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                        str_disease_type, str_recomnd_doctor, str_followup_date, "null", jsn_arr_report, jsn_arr_other_doc);
                            } else if (jsn_arr_report.size() <= 0 && arrList_report.size() <= 0) {
                                addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                        str_patient_email,
                                        str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                        str_disease_type, str_recomnd_doctor, str_followup_date, jsn_arr_prsec, "null", jsn_arr_other_doc);

                            } else if (jsn_arr_other_doc.size() <= 0 && arrList_docs.size() <= 0) {
                                addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                        str_patient_email,
                                        str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                        str_disease_type, str_recomnd_doctor, str_followup_date, jsn_arr_prsec, jsn_arr_report, "null");

                            } else if (jsn_arr_other_doc.size() <= 0 && arrList_docs.size() <= 0 && jsn_arr_prsec.size() <= 0 && arrayList_Img.size() <= 0 &&
                                    jsn_arr_report.size() <= 0 && arrList_report.size() <= 0) {
                                addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                        str_patient_email,
                                        str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                        str_disease_type, str_recomnd_doctor, str_followup_date, "null", "null", "null");

                            } else {
                                addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                        str_patient_email,
                                        str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                        str_disease_type, str_recomnd_doctor, str_followup_date, jsn_arr_prsec, jsn_arr_report, jsn_arr_other_doc);

                            }
                            Toast.makeText(NewPatient1.this, "Data added Successfully", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        //for doctor no doctor list required
                        JsonArray jsn_arr_prsec = new JsonArray();
                        JsonArray jsn_arr_report = new JsonArray();
                        JsonArray jsn_arr_other_doc = new JsonArray();

                        for (int i = 0; i < arrayList_Img.size(); i++) {
                            if (!arrayList_Img.get(i).equals("")) {
                                JsonObject object = new JsonObject();
                                object.addProperty("presc_doc", arrayList_Img.get(i));
                                jsn_arr_prsec.add(object);
                            }
                        }
                        for (int i = 0; i < arrList_docs.size(); i++) {
                            if (!arrList_docs.get(i).equals("")) {
                                JsonObject object = new JsonObject();
                                object.addProperty("other_doc", arrList_docs.get(i));
                                jsn_arr_other_doc.add(object);
                            }
                        }
                        for (int i = 0; i < arrList_report.size(); i++) {
                            if (!arrList_report.get(i).equals("")) {
                                JsonObject object = new JsonObject();
                                object.addProperty("report_doc", arrList_report.get(i));
                                jsn_arr_report.add(object);
                            }
                        }
                        if (jsn_arr_report.size() <= 0 && arrList_report.size() <= 0 && jsn_arr_prsec.size() <= 0 && arrayList_Img.size() <= 0) {
                            addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                    str_patient_email,
                                    str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                    str_disease_type, str_recomnd_doctor, str_followup_date, "null", "null", jsn_arr_other_doc);

                        } else if (jsn_arr_report.size() <= 0 && arrList_report.size() <= 0 && jsn_arr_other_doc.size() <= 0 && arrList_docs.size() <= 0) {
                            addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                    str_patient_email,
                                    str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                    str_disease_type, str_recomnd_doctor, str_followup_date, jsn_arr_prsec, "null", "null");

                        } else if (jsn_arr_prsec.size() <= 0 && arrayList_Img.size() <= 0 && jsn_arr_other_doc.size() <= 0 && arrList_docs.size() <= 0) {
                            addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                    str_patient_email,
                                    str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                    str_disease_type, str_recomnd_doctor, str_followup_date, "null", jsn_arr_report, "null");

                        } else if (jsn_arr_prsec.size() <= 0 && arrayList_Img.size() <= 0) {
                            addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                    str_patient_email,
                                    str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                    str_disease_type, str_recomnd_doctor, str_followup_date, "null", jsn_arr_report, jsn_arr_other_doc);
                        } else if (jsn_arr_report.size() <= 0 && arrList_report.size() <= 0) {
                            addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                    str_patient_email,
                                    str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                    str_disease_type, str_recomnd_doctor, str_followup_date, jsn_arr_prsec, "null", jsn_arr_other_doc);

                        } else if (jsn_arr_other_doc.size() <= 0 && arrList_docs.size() <= 0) {
                            addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                    str_patient_email,
                                    str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                    str_disease_type, str_recomnd_doctor, str_followup_date, jsn_arr_prsec, jsn_arr_report, "null");

                        } else if (jsn_arr_other_doc.size() <= 0 && arrList_docs.size() <= 0 && jsn_arr_prsec.size() <= 0 && arrayList_Img.size() <= 0 &&
                                jsn_arr_report.size() <= 0 && arrList_report.size() <= 0) {
                            addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                    str_patient_email,
                                    str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                    str_disease_type, str_recomnd_doctor, str_followup_date, "null", "null", "null");

                        } else {
                            addNewPatient1APICall(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_name,
                                    str_patient_email,
                                    str_patient_dob, str_patient_mobile, str_patient_gender, strMaritalStatus, str_patient_address,
                                    str_disease_type, str_recomnd_doctor, str_followup_date, jsn_arr_prsec, jsn_arr_report, jsn_arr_other_doc);

                        }
                        //Toast.makeText(NewPatient1.this, "Data added Successfully", Toast.LENGTH_LONG).show();

                    }
                }
                break;

            case R.id.ll_capture_prescription:
                captureImageEvent();
                break;

            case R.id.ll_upload_doc:
                if (checkPermissionREAD_EXTERNAL_STORAGE(NewPatient1.this)) {
                    // do your stuff..
                    uploadDocs();
                } else {
                    Toast.makeText(NewPatient1.this, "Permission is Required to Open File Manager", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    //calender use for dob and follow update
    private void selectDateDob() {
        dateDOB = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                refCalendar.set(Calendar.YEAR, year);
                refCalendar.set(Calendar.MONTH, monthOfYear);
                refCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //set selcted date
                String dateFormat = "dd/MM/yyyy";//In which you need put here
                SimpleDateFormat sdfDate = new SimpleDateFormat(dateFormat, Locale.US);
                if (!sdfDate.equals("null")) {
                    strStoreDate = sdfDate.format(refCalendar.getTime());
                    if (!strStoreDate.equals("null") && !strStoreDate.equals(null)) {
                        et_dob.setText(strStoreDate);
                    } else {
                        et_dob.setText("");
                    }
                } else {
                    strStoreDate = "null";
                }
                // setUserId();
            }

        };

        //calender select--------------------------------------------------------------------
        et_dob.setFocusable(false);


    }

    private void selectDateFollowUp() {
        dateFollowup = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                refCalendar.set(Calendar.YEAR, year);
                refCalendar.set(Calendar.MONTH, monthOfYear);
                refCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //set selcted date
                String dateFormat = "dd/MM/yyyy";//In which you need put here
                SimpleDateFormat sdfDate = new SimpleDateFormat(dateFormat, Locale.US);
                if (!sdfDate.equals("null")) {
                    strStoreDate = sdfDate.format(refCalendar.getTime());
                    if (!strStoreDate.equals("null")) {
                        et_follow_update.setText(strStoreDate);
                    } else {
                        et_follow_update.setText("");
                    }
                } else {
                    strStoreDate = "null";
                }
                // setUserId();
            }

        };

        //calender select--------------------------------------------------------------------
        et_follow_update.setFocusable(false);


    }

    private void captureImageEvent() {
        LayoutInflater layoutInflater = LayoutInflater.from(NewPatient1.this);
        android.view.View promptView = layoutInflater.inflate(R.layout.choose_img_popup, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(NewPatient1.this, R.style.AlertDialogStyle);
        alertDialogBuilder.setView(promptView);

        Button btn_gallery = promptView.findViewById(R.id.btn_gallery);
        Button btn_camera = promptView.findViewById(R.id.btn_camera);

        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //mae alert bg transparent for custom rounded corner
        //------------------------------select gallery----------------------------------------------------------------
        btn_gallery.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (checkPermissionGALLERY(NewPatient1.this)) {
                    Intent pictureActionIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pictureActionIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
                    alertDialog.dismiss();
                } else {
                    //nothing
                }
                alertDialog.dismiss();
            }
        });

        //----------------------------capture image from camera--------------------------------------------------
        btn_camera.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //as for the declaration camera in manifest need to ask camera permission in activity
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    /*if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                        alertDialog.dismiss();
                    }*/
                    if (!hasPermissions(NewPatient1.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(NewPatient1.this, PERMISSIONS, MY_CAMERA_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri uri_f = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_f);
                        startActivityForResult(intent, CAMERA_REQUEST);
                        alertDialog.dismiss();
                    }
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, CAMERA_REQUEST);
                    alertDialog.dismiss();
                }
                alertDialog.dismiss();
            }
        });
    }

    private void uploadDocs() {
        LayoutInflater layoutInflater = LayoutInflater.from(NewPatient1.this);
        android.view.View promptView = layoutInflater.inflate(R.layout.docs_popup, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(NewPatient1.this, R.style.AlertDialogStyle);
        alertDialogBuilder.setView(promptView);

        TextView tv_upload_report = (TextView) promptView.findViewById(R.id.tv_upload_report);
        TextView tv_upload_other_doc = (TextView) promptView.findViewById(R.id.tv_upload_other_doc);

        final android.app.AlertDialog alertDialog_main = alertDialogBuilder.create();
        alertDialog_main.show();
        alertDialog_main.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tv_upload_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_doc = 201;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    isKitKat = true;
                    startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                    alertDialog_main.dismiss();
                } else {
                    isKitKat = false;
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                    alertDialog_main.dismiss();
                }
            }
        });
        tv_upload_other_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_doc = 202;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    isKitKat = true;
                    startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                    alertDialog_main.dismiss();
                } else {
                    isKitKat = false;
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                    alertDialog_main.dismiss();
                }
            }
        });

    }

    //for accessing the external storage permission is required
    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    //for accessing the gallery external storage permission is required
    public boolean checkPermissionGALLERY(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat.requestPermissions(
                            (Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PICTURE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    //Camera multiple permissions
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                    if (selected_doc == 201) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("*/*");
                            isKitKat = true;
                            startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                        } else {
                            isKitKat = false;
                            Intent intent = new Intent();
                            intent.setType("*/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("*/*");
                            isKitKat = true;
                            startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                        } else {
                            isKitKat = false;
                            Intent intent = new Intent();
                            intent.setType("*/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                        }
                    }
                } else {
                    Toast.makeText(NewPatient1.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case MY_CAMERA_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri uri_f = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_f);
                    startActivityForResult(intent, CAMERA_REQUEST);

                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            case GALLERY_PICTURE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pictureActionIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
                } else {
                    Toast.makeText(this, "External Storage permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {

            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }
            if (!f.exists()) {
                Toast.makeText(getBaseContext(), "Error while capturing image", Toast.LENGTH_LONG).show();
                return;
            }
            try {

                //  bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
                int rotate = 0;
                try {
                    ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                String path = ResizeImage_Adapter.compressImage(NewPatient1.this,f.getAbsolutePath());
                files_to_upload.clear();
                files_to_upload.add(path);
                uploadFile(DOC_TYPE_PRSEC);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                assert selectedImage != null;
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                assert c != null;
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String selectedImagePath = c.getString(columnIndex);
                c.close();

                if (selectedImagePath != null) {
                    files_to_upload.clear();
                    files_to_upload.add(selectedImagePath);
                    uploadFile(DOC_TYPE_PRSEC);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 105) {

            if (data != null && data.getData() != null && resultCode == RESULT_OK) {

                Uri selectedFileURI = data.getData();
                String mediaPath = FileUtils.getPath(NewPatient1.this, selectedFileURI);
                try {
                    if (selected_doc == 201) {
                        files_to_upload.clear();
                        files_to_upload.add(mediaPath);
                        uploadFile(DOC_TYPE_REPORT);
                        // uploadFile(mediaPath, DOC_TYPE_REPORT);
                    } else if (selected_doc == 202) {
                        files_to_upload.clear();
                        files_to_upload.add(mediaPath);
                        uploadFile(DOC_TYPE_OTHERS);
                        // uploadFile(mediaPath, DOC_TYPE_OTHERS);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "You haven't picked Image/Video", Toast.LENGTH_LONG).show();
        }
    }

    private void addNewPatient1APICall(String str_usr_type, String str_usr_id, String str_name, String str_email,
                                       String str_dob, String str_mobile, String str_gender, String str_marital_status, String str_address,
                                       String str_disease_type, String str_doctor_id, String str_follow_up_date, Object obj_prsec,
                                       Object obj_report, Object obj_others) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //*****************date time format conversion********************
        Date localTime_fd = null;
        try {
            localTime_fd = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(str_follow_up_date);//convert from
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf_fd = new SimpleDateFormat("yyyy-MM-dd");//convert in
        String fd_format = sdf_fd.format(new Date(localTime_fd.getTime()));
        //*******************date time format conversion***************************
        Date localTime_dob = null;
        try {
            localTime_dob = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(str_dob);//convert from
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf_dob = new SimpleDateFormat("yyyy-MM-dd");//convert in
        String dob_format = sdf_dob.format(new Date(localTime_dob.getTime()));
        //***********************************************************************
        if (Utils.CheckInternetConnection(NewPatient1.this)) {
            New_Patient_Retro_Model newpatient_model = new New_Patient_Retro_Model(str_usr_type, str_usr_id, str_name, str_email, dob_format,
                    str_mobile, str_gender, str_marital_status, str_address, str_disease_type, str_doctor_id, fd_format, obj_prsec,
                    obj_report, obj_others);
            Call<New_Patient_Retro_Model> call_new_patient = apiInterface.addPatient(newpatient_model);
            call_new_patient.enqueue(new Callback<New_Patient_Retro_Model>() {
                @Override
                public void onResponse(Call<New_Patient_Retro_Model> call, Response<New_Patient_Retro_Model> response) {
                    New_Patient_Retro_Model new_patient_resources = response.body();
                    if (response.isSuccessful()) {
                        if (new_patient_resources.status.equals("success")) {
                            if (sessionManager.getDoctorNurseId().equals("1")) {
                                Toast.makeText(NewPatient1.this, new_patient_resources.message, Toast.LENGTH_SHORT).show();
                                Intent intentDoctorHomePage = new Intent(NewPatient1.this, HomePage_Doctor.class);
                                startActivity(intentDoctorHomePage);
                                finish();
                            } else {
                                Toast.makeText(NewPatient1.this, new_patient_resources.message, Toast.LENGTH_SHORT).show();
                                Intent intentNurseHomePage = new Intent(NewPatient1.this, HomePage_Nurse.class);
                                startActivity(intentNurseHomePage);
                                finish();

                            }
                        } else {
                            Toast.makeText(NewPatient1.this, new_patient_resources.message, Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    } else {
                        if (response.code() == 400) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response.errorBody().string());
                                String userMessage = jsonObject.getString("status");
                                String internalMessage = jsonObject.getString("message");
                                progressDialog.dismiss();
                                new android.app.AlertDialog.Builder(NewPatient1.this)
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

                @Override
                public void onFailure(Call<New_Patient_Retro_Model> call, Throwable t) {
                    call.cancel();
                    progressDialog.dismiss();
                    new android.app.AlertDialog.Builder(NewPatient1.this)
                            .setMessage("Server error! Please try again later")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
        } else {
            Toast.makeText(NewPatient1.this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //=====================================progress bar=============================================

    public void uploadFile(String doc_type) {
        File[] filesToUpload = new File[files_to_upload.size()];
        for (int i = 0; i < files_to_upload.size(); i++) {
            filesToUpload[i] = new File(files_to_upload.get(i));
        }
        if (files_to_upload.size() > 0) {
            customProgressDialog.showProgress(NewPatient1.this, 0, 0, downStreamSpeed, true);
            FileUploader fileUploader = new FileUploader();
            fileUploader.uploadFiles(NewPatient1.this, sessionManager.getDoctorNurseId(), "add_page", doc_type, "file", filesToUpload, new FileUploader.FileUploaderCallback() {
                @Override
                public void onError() {
                    customProgressDialog.hideProgress();
                }

                @Override
                public void onFinish(String[] responses) {
                    customProgressDialog.hideProgress();
                    for (int i = 0; i < responses.length; i++) {
                        String str = responses[i];
                        Log.e("RESPONSE " + i, "" + responses[i]);
                    }
                }

                @Override
                public void onProgressUpdate(int currentpercent, int totalpercent, int filenumber, int file_size) {
                    customProgressDialog.updateProgress(currentpercent, file_size, downStreamSpeed);
                    //     Log.e("---------Progress Status", currentpercent + " " + totalpercent + " " + filenumber + "......" + file_size);
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTrafficSpeedMeasurer.stopMeasuring();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTrafficSpeedMeasurer.registerListener(mStreamSpeedListener);
    }

    private ITrafficSpeedListener mStreamSpeedListener = new ITrafficSpeedListener() {

        @Override
        public void onTrafficSpeedMeasured(final double upStream, final double downStream) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    upStreamSpeed = Utils.parseSpeed(upStream, SHOW_SPEED_IN_BITS);
                    downStreamSpeed = Utils.parseSpeed(downStream, SHOW_SPEED_IN_BITS);

                }
            });
        }
    };

    @Override
    public void getFile(String file, String file_type) {
        if (!file.equals("null") && !file.equals(null) && !file.equals("NA") && !file.isEmpty()) {
            if (file_type.equals("1")) {
                arrayList_Img.add(file);
            } else if (file_type.equals("2")) {
                arrList_report.add(file);
            } else {
                arrList_docs.add(file);
            }
            //---------other docs adapter--------------------
            DocsListAdapter adapter_docs = new DocsListAdapter(getApplicationContext(), arrList_docs);
            rv_docs.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//must write for display data in recycler view
            rv_docs.setAdapter(adapter_docs);
            //-----------report adapter------------------
            ReportListAdapter adapter_report = new ReportListAdapter(getApplicationContext(), arrList_report);
            rv_report.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//it call viewholder and bind the data
            rv_report.setAdapter(adapter_report);
            //----------prsecription adapter-----------------
            PrsecListAdapter adapter_prsec = new PrsecListAdapter(getApplicationContext(), arrayList_Img);
            rv_img.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//must write for display data in recycler view
            rv_img.setAdapter(adapter_prsec);

        } else {
            Toast.makeText(NewPatient1.this, "Failed to upload file, Please try again", Toast.LENGTH_SHORT).show();
        }
    }
}

