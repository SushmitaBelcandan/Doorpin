package com.app.doorpin.Activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.doorpin.R;
import com.app.doorpin.fragment.PatientDetails_PersoInfo_Frag;
import com.app.doorpin.models.SetDateonCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EditPatientDetails extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar_edit_patient_details;
    Button btn_save;
    EditText et_email, et_edit_dob, et_mobile, et_address;
    RadioGroup rgp_gender;
    RadioButton rbtn_male, rbtn_female, rbtn_other;
    Spinner spnr_marital_status;

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

        btn_save.setOnClickListener(this);
        //  setDateonCalendar = new SetDateonCalendar(et_edit_dob, this);
        selectDate();
        addMaritalStatus();
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
        et_edit_dob.setOnClickListener(new android.view.View.OnClickListener() {
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
                if (validateForm(str_patient_email, str_patient_dob, str_patient_mobile,
                        rgp_gender, str_patient_marital_status, str_patient_address)) {
                    //remove error mark when no fields are empty
                    et_email.setError(null);
                    et_edit_dob.setError(null);
                    et_mobile.setError(null);
                    et_address.setError(null);

                    startActivity(new Intent(EditPatientDetails.this, PatientDetails.class));
                    finish();
                }


                break;
            default:
                break;
        }


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
