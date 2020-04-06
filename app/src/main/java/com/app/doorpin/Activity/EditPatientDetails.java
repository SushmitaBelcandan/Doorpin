package com.app.doorpin.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.fragment.PatientDetails_PersoInfo_Frag;
import com.app.doorpin.models.SetDateonCalendar;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.Edit_PersInfo_Retro_Model;
import com.app.doorpin.retrofit.Patient_PersInfo_RetroModel;

import org.json.JSONException;
import org.json.JSONObject;

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

public class EditPatientDetails extends AppCompatActivity implements View.OnClickListener {

    SessionManager sessionManager;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;

    Toolbar toolbar_edit_patient_details;
    Button btn_save;
    TextView tv_patientname, tv_patient_id;
    EditText et_email, et_edit_dob, et_mobile, et_address;
    RadioGroup rgp_gender;
    RadioButton rbtn_male, rbtn_female, rbtn_other;
    Spinner spnr_marital_status;
    ImageView ivCalenderDob;

    ArrayList<String> arrList_marital_status;
    String strMaritalStatus;
    String strDob;
    private String str_patient_email, str_patient_dob, str_patient_mobile, str_patient_gender, str_patient_marital_status, str_patient_address;
    private int patient_marital_status_id = 0;
    private String str_gender_name;


    public Calendar dobCalendar = Calendar.getInstance();
    //  SetDateonCalendar setDateonCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient_details);

        sessionManager = new SessionManager(EditPatientDetails.this);
        progressDialog = new ProgressDialog(EditPatientDetails.this);
        progressDialog.setMessage("Please Wait...");
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        toolbar_edit_patient_details = findViewById(R.id.toolbar_edit_patient_details);
        //toolbar
        setSupportActionBar(toolbar_edit_patient_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar_edit_patient_details.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tv_patientname = findViewById(R.id.tv_patientname);
        tv_patient_id = findViewById(R.id.tv_patient_id);
        btn_save = findViewById(R.id.btn_save);
        et_email = findViewById(R.id.et_email);
        et_edit_dob = findViewById(R.id.et_edit_dob);
        et_mobile = findViewById(R.id.et_mobile);
        et_address = findViewById(R.id.et_address);
        rgp_gender = findViewById(R.id.rgp_gender);
        rbtn_male = findViewById(R.id.rbtn_male);
        rbtn_female = findViewById(R.id.rbtn_female);
        rbtn_other = findViewById(R.id.rbtn_other);
        spnr_marital_status = findViewById(R.id.spnr_marital_status);
        ivCalenderDob = findViewById(R.id.ivCalenderDob);

        btn_save.setOnClickListener(this);
        //  setDateonCalendar = new SetDateonCalendar(et_edit_dob, this);
        getPatientIdentity();
        selectDate();
        addMaritalStatus();
        getGenderName();//call it first to get selected value otherwise it will make null value
    }

    public String getGenderName() {
        rgp_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtn_male) {
                    str_gender_name = "Male";
                    return;

                } else if (checkedId == R.id.rbtn_female) {
                    str_gender_name = "Female";
                    return;
                } else if (checkedId == R.id.rbtn_other) {
                    str_gender_name = "Other";
                    return;
                } else {
                    str_gender_name = "-1";
                    return;
                }
            }
        });
        return str_gender_name;
    }

    private boolean validateForm(String str_email, String str_dob, String str_mobile,
                                 RadioGroup str_gender, String str_marital_status_id, String str_address) {
        //email validations
        if (str_email.equals("null") || str_email.equals("NA") || str_email.equals(null) || str_email.isEmpty()) {
            et_email.setError("Please enter Email Id");
            et_email.requestFocus();
            return false;
        }
        if (!str_email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+") &&
                !str_email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+")) {
            et_email.setError("Invalid Email Id");
            et_email.requestFocus();
            return false;
        }
        //date of birth validatons
        if (str_dob.equals("null") || str_dob.equals("NA") || str_dob.equals(null) || str_dob.isEmpty()) {
            et_edit_dob.setError("Please enter Date of Birth");
            et_edit_dob.requestFocus();
            return false;
        }
        //mobile validations
        if (str_mobile.equals("null") || str_mobile.equals("NA") || str_mobile.equals(null) || str_mobile.isEmpty()) {
            et_mobile.setError("Please enter Mobile Number");
            et_mobile.requestFocus();
            return false;
        }
        if (str_mobile.matches("(\\d)(?!\\1+$)\\d{10}")) {
            et_mobile.setError("Invalid Mobile Number");
            et_mobile.requestFocus();
            return false;
        }
        if (str_mobile.length() != 10) {
            et_mobile.setError("Invalid Mobile Number");
            et_mobile.requestFocus();
            return false;
        }
        //Gender validations
        if (str_gender.getCheckedRadioButtonId() == -1) {
            new AlertDialog.Builder(EditPatientDetails.this)
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
            new AlertDialog.Builder(EditPatientDetails.this)
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
        if (str_address.equals("null") || str_address.equals(null) || str_address.equals("NA") || str_address.isEmpty()) {
            et_address.setError("Please enter Address");
            et_address.requestFocus();
            return false;
        }
        return true;

    }


    private void addMaritalStatus() {
        //add spinner item
        arrList_marital_status = new ArrayList<>();
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
                strMaritalStatus = spnr_marital_status.getSelectedItem().toString(); //selected string
                patient_marital_status_id = maritalStatusAdapter.getPosition(strMaritalStatus);//get selected marital status id
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

    }

    private void selectDate() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                dobCalendar.set(Calendar.YEAR, year);
                dobCalendar.set(Calendar.MONTH, monthOfYear);
                dobCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //set selcted date
                String dateFormat = "dd/MM/yyyy";//In which you need put here
                SimpleDateFormat sdfDate = new SimpleDateFormat(dateFormat, Locale.US);
                if (!sdfDate.equals("null")) {
                    strDob = sdfDate.format(dobCalendar.getTime());
                    if (!strDob.equals("null") && !strDob.equals(null)) {
                        et_edit_dob.setText(strDob);
                    } else {
                        et_edit_dob.setText("");
                    }
                } else {
                    strDob = "null";
                }
                // setUserId();
            }

        };

        //calender select--------------------------------------------------------------------
        et_edit_dob.setFocusable(false);
        ivCalenderDob.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                // TODO Auto-generated method stub
                DatePickerDialog dpd = new DatePickerDialog(EditPatientDetails.this, R.style.MyThemeOverlay, date, dobCalendar
                        .get(Calendar.YEAR), dobCalendar.get(Calendar.MONTH), dobCalendar.get(Calendar.DAY_OF_MONTH));
                //    dpd.getDatePicker().setMaxDate(System.currentTimeMillis()); //make future date disable
                dpd.show();
                dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // do something onCancel
                        strDob = "null";
                        et_edit_dob.setText("");
                    }
                });

            }
        });

    }

    private void getPatientIdentity() {
        if (!sessionManager.getDisplayIdHome().equals("NA")) {
            tv_patient_id.setText("Patient Id" + " - " + sessionManager.getDisplayIdHome());
            tv_patientname.setText(sessionManager.getPatientNameHome());

            Intent oIntent = getIntent();//for page flag
            String str_intent_p_id = oIntent.getExtras().getString("PATIENT_ID");

            if (Utils.CheckInternetConnection(getApplicationContext())) {
                if (!str_intent_p_id.equals("null") && !str_intent_p_id.equals(null) && !str_intent_p_id.isEmpty() && !str_intent_p_id.equals("NA")) {
                    getPersonalInformation(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_intent_p_id);
                } else {
                    //do not display data
                }
            } else {
                Toast.makeText(EditPatientDetails.this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        } else {
            tv_patient_id.setText("");
            tv_patientname.setText("");
        }
    }

    //get patient information
    private void getPersonalInformation(String str_usr_type, String str_usr_id, String str_patient_id) {

        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Patient_PersInfo_RetroModel personal_info_model = new Patient_PersInfo_RetroModel(str_usr_type, str_usr_id, str_patient_id);
        Call<Patient_PersInfo_RetroModel> call = apiInterface.getPersonalInfo(personal_info_model);
        call.enqueue(new Callback<Patient_PersInfo_RetroModel>() {
            @Override
            public void onResponse(Call<Patient_PersInfo_RetroModel> call, Response<Patient_PersInfo_RetroModel> response) {
                Patient_PersInfo_RetroModel patientPersonalInfoRequest = response.body();
                if (response.isSuccessful()) {
                    if (patientPersonalInfoRequest.status.equals("success")) {

                        List<Patient_PersInfo_RetroModel.PatientPersonalInfo_Datum> personal_info_datum = patientPersonalInfoRequest.result;
                        if (personal_info_datum.size() <= 0) {
                            //when response array is empty
                            progressDialog.dismiss();
                            Toast.makeText(EditPatientDetails.this, patientPersonalInfoRequest.message, Toast.LENGTH_SHORT).show();
                        } else {
                            for (Patient_PersInfo_RetroModel.PatientPersonalInfo_Datum personal_info_data : personal_info_datum) {
                                String p_email = personal_info_data.patient_email_id;
                                String p_dob = personal_info_data.patient_dob;
                                String p_mobile = personal_info_data.patient_mobile;
                                String p_gender = personal_info_data.patient_gender;
                                String p_marital_status = personal_info_data.Patient_marital_status;
                                String p_address = personal_info_data.patient_address;

                                //patient email id
                                if (!p_email.equals("null") && !p_email.equals(null) && !p_email.equals("NA") && !p_email.isEmpty()) {

                                    et_email.setText(p_email);
                                } else {
                                    et_email.setText("");
                                }
                                //patient date of birth
                                if (!p_dob.equals("null") && !p_dob.equals(null) && !p_dob.equals("NA") && !p_dob.isEmpty()) {

                                    Date localTime = null;
                                    try {
                                        localTime = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(p_dob);//convert from
                                    } catch (java.text.ParseException e) {
                                        e.printStackTrace();
                                    }
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");//convert in
                                    String dob_format = sdf.format(new Date(localTime.getTime()));

                                    et_edit_dob.setText(dob_format);
                                } else {
                                    et_edit_dob.setText("");
                                }
                                //patient mobile number
                                if (!p_mobile.equals("null") && !p_mobile.equals(null) && !p_mobile.equals("NA") && !p_mobile.isEmpty()) {

                                    et_mobile.setText(p_mobile);
                                } else {
                                    et_mobile.setText("");
                                }
                                //patient gender
                                if (!p_gender.equals("null") && !p_gender.equals(null) && !p_gender.equals("NA") && !p_gender.isEmpty()) {
                                    if (p_gender.trim().toLowerCase().equals("female")) {
                                        rbtn_female.setChecked(true);
                                    } else if (p_gender.trim().toLowerCase().equals("male")) {
                                        rbtn_male.setChecked(true);
                                    } else {
                                        rbtn_other.setChecked(true);
                                    }
                                } else {
                                    rbtn_female.setChecked(false);
                                    rbtn_male.setChecked(false);
                                    rbtn_other.setChecked(false);
                                }
                                //patient marital status
                                if (!p_marital_status.equals("null") && !p_marital_status.equals(null) && !p_marital_status.equals("NA") && !p_marital_status.isEmpty()) {

                                    if (p_marital_status.trim().toLowerCase().equals("single")) {
                                        spnr_marital_status.setSelection(2);
                                    } else if (p_marital_status.trim().toLowerCase().equals("married")) {
                                        spnr_marital_status.setSelection(1);
                                    } else {
                                        spnr_marital_status.setSelection(0);
                                    }
                                } else {
                                    spnr_marital_status.setSelection(0);
                                }
                                //patient address
                                if (!p_address.equals("null") && !p_address.equals(null) && !p_address.equals("NA") && !p_address.isEmpty()) {

                                    et_address.setText(p_address);
                                } else {
                                    et_address.setText("");
                                }

                            }
                            progressDialog.dismiss();

                        }
                    } else {
                        //failure response
                        progressDialog.dismiss();
                        Toast.makeText(EditPatientDetails.this, patientPersonalInfoRequest.message, Toast.LENGTH_SHORT).show();
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
                                new android.app.AlertDialog.Builder(EditPatientDetails.this)
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
            public void onFailure(Call<Patient_PersInfo_RetroModel> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                new AlertDialog.Builder(EditPatientDetails.this)
                        .setMessage("Network Connection error! Please try again later")
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


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_save:
                str_patient_email = et_email.getText().toString().trim();
                str_patient_dob = et_edit_dob.getText().toString().trim();
                str_patient_mobile = et_mobile.getText().toString().trim();
                str_patient_gender = getGenderName();//getting selected gender associated id/value
                str_patient_marital_status = String.valueOf(patient_marital_status_id).trim();
                str_patient_address = et_address.getText().toString().trim();
                if (Utils.CheckInternetConnection(getApplicationContext())) {

                    if (validateForm(str_patient_email, str_patient_dob, str_patient_mobile, rgp_gender, str_patient_marital_status,
                            str_patient_address)) {
                        //remove error mark when no fields are empty
                        et_email.setError(null);
                        et_edit_dob.setError(null);
                        et_mobile.setError(null);
                        et_address.setError(null);
                        if (!sessionManager.getPatientIdHome().equals("NA")) {
                            if (!sessionManager.getPatientNameHome().equals("NA")) {
                                //**********************change date format********************
                                Date localTime = null;
                                try {
                                    localTime = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(str_patient_dob);//convert from
                                } catch (java.text.ParseException e) {
                                    e.printStackTrace();
                                }
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//convert in
                                String dob_format = sdf.format(new Date(localTime.getTime()));

                                updatePatientDetail(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(),
                                        sessionManager.getPatientIdHome(), sessionManager.getPatientNameHome(),
                                        str_patient_email, dob_format, str_patient_mobile,
                                        str_patient_gender, strMaritalStatus, str_patient_address);
                            } else {
                                Toast.makeText(EditPatientDetails.this, "Invalid Patient Name!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //do nothing
                            Toast.makeText(EditPatientDetails.this, "Invalid Patient Information!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                }


                break;
            default:
                break;
        }

    }

    private void updatePatientDetail(String str_usr_type, String str_usr_id, String str_patient_id, String str_patient_name,
                                     String str_patient_email, String str_patient_dob, String str_patient_mobile,
                                     String str_patient_gender, String str_marital_status, String str_patient_addrss) {

        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Edit_PersInfo_Retro_Model edit_info_model = new Edit_PersInfo_Retro_Model(str_usr_type, str_usr_id,
                str_patient_id, str_patient_name, str_patient_email, str_patient_dob, str_patient_mobile, str_patient_gender,
                str_marital_status, str_patient_addrss);
        Call<Edit_PersInfo_Retro_Model> edit_info_call = apiInterface.updatePersonalInfo(edit_info_model);
        edit_info_call.enqueue(new Callback<Edit_PersInfo_Retro_Model>() {
            @Override
            public void onResponse(Call<Edit_PersInfo_Retro_Model> call, Response<Edit_PersInfo_Retro_Model> response) {
                Edit_PersInfo_Retro_Model edit_info_request = response.body();
                if (response.isSuccessful()) {
                    if (edit_info_request.status.equals("success")) {
                        progressDialog.dismiss();
                        Toast.makeText(EditPatientDetails.this, edit_info_request.message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditPatientDetails.this, PatientDetails.class));
                        finish();
                    } else {
                        //failure response
                        progressDialog.dismiss();
                        Toast.makeText(EditPatientDetails.this, edit_info_request.message, Toast.LENGTH_SHORT).show();
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
                                new android.app.AlertDialog.Builder(EditPatientDetails.this)
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
            public void onFailure(Call<Edit_PersInfo_Retro_Model> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                new AlertDialog.Builder(EditPatientDetails.this)
                        .setMessage("Network Connection error! Please try again later")
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent oIntent = getIntent();//for page flag
        String str_page_flag = oIntent.getExtras().getString("PAGE_FLAG");
        if (str_page_flag.equals("2")) {
            Intent intentPatientPersonalInfo = new Intent(EditPatientDetails.this, PatientDetails.class);
            startActivity(intentPatientPersonalInfo);
            finish();
        } else {
            Intent intentSurgPatient = new Intent(EditPatientDetails.this, MySurgeries.class);
            startActivity(intentSurgPatient);
            finish();
        }
    }
}
