package com.app.doorpin.Activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.doorpin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NewPatient extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar_new_patient;

    Button btn_add;
    LinearLayout llcapturePrescription, lluploadDocs;
    Spinner spnr_marital_status;
    EditText et_dob, et_follow_update;
    RadioGroup rgp_gender_newp;
    RadioButton rbtn_male_newp, rbtn_female_newp, rbtn_other_newp;
    EditText et_address_newp, et_disease_type_newp, et_mobile_newp, et_email_newp, et_name_newp;

    public static final int CAMERA_PIC_REQUEST = 1;
    public Calendar refCalendar = Calendar.getInstance();
    String strStoreDate;
    private int newp_marital_status_id = 0;
    private String newp_str_gender_name;

    ArrayList<String> arrList_marital_status;
    String strMaritalStatus;
    private String str_name, str_patient_email, str_patient_dob, str_patient_mobile, str_patient_gender, str_patient_marital_status, str_patient_address, str_disease_type, str_followup_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient);

        toolbar_new_patient = findViewById(R.id.toolbar_new_patient);
        et_name_newp = findViewById(R.id.et_name_newp);
        et_email_newp = findViewById(R.id.et_email_newp);
        et_dob = findViewById(R.id.et_dob);
        et_mobile_newp = findViewById(R.id.et_mobile_newp);
        rgp_gender_newp = findViewById(R.id.rgp_gender_newp);
        rbtn_male_newp = findViewById(R.id.rbtn_male_newp);
        rbtn_female_newp = findViewById(R.id.rbtn_female_newp);
        rbtn_other_newp = findViewById(R.id.rbtn_other_newp);
        et_address_newp = findViewById(R.id.et_address_newp);
        et_disease_type_newp = findViewById(R.id.et_disease_type_newp);
        et_follow_update = findViewById(R.id.et_follow_update);

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

        llcapturePrescription = findViewById(R.id.ll_capture_prescription);
        lluploadDocs = findViewById(R.id.ll_upload_doc);
        spnr_marital_status = findViewById(R.id.spnr_marital_status);


        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        llcapturePrescription.setOnClickListener(this);
        lluploadDocs.setOnClickListener(this);

        //set marital status
        addMaritalStatus();
        //select Date of birth by populating calender
        et_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(et_dob);
            }
        });
        //select followupdate by populating calender
        et_follow_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(et_follow_update);
            }
        });


    }

    public String getGenderName() {
        rgp_gender_newp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtn_male_newp) {
                    newp_str_gender_name = "Male";
                    return;

                } else if (checkedId == R.id.rbtn_female_newp) {
                    newp_str_gender_name = "Female";
                    return;
                } else if (checkedId == R.id.rbtn_other_newp) {
                    newp_str_gender_name = "Other";
                    return;
                } else {
                    newp_str_gender_name = "-1";
                    return;
                }
            }
        });
        return newp_str_gender_name;
    }

    private boolean validateForm(String str_name, String str_email, String str_dob, String str_mobile,
                                 RadioGroup str_gender, String str_marital_status_id, String str_address,
                                 String str_type_disease, String str_followup_date) {
        //new patient name
        if (str_name.equals("null") || str_name.equals("NA") || str_name.equals(null) || str_name.isEmpty()) {
            et_name_newp.setError("Please enter Patient Name");
            et_name_newp.requestFocus();
            return false;
        }
        //email validations
        if (str_email.equals("null") || str_email.equals("NA") || str_email.equals(null) || str_email.isEmpty()) {
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
        if (str_dob.equals("null") || str_dob.equals("NA") || str_dob.equals(null) || str_dob.isEmpty()) {
            et_dob.setError("Please enter Date of Birth");
            et_dob.requestFocus();
            return false;
        }
        //mobile validations
        if (str_mobile.equals("null") || str_mobile.equals("NA") || str_mobile.equals(null) || str_mobile.isEmpty()) {
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
        if (str_gender.getCheckedRadioButtonId() == -1) {
            new AlertDialog.Builder(NewPatient.this)
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
            new AlertDialog.Builder(NewPatient.this)
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
            et_address_newp.setError("Please enter Address");
            et_address_newp.requestFocus();
            return false;
        }
        //new patient's type of disease
        if (str_type_disease.equals("null") || str_type_disease.equals(null) || str_type_disease.equals("NA") || str_type_disease.isEmpty()) {
            et_disease_type_newp.setError("Please enter Disease Name");
            et_disease_type_newp.requestFocus();
            return false;
        }
        //follow up date
        if (str_followup_date.equals("null") || str_followup_date.equals("NA") || str_followup_date.equals(null) || str_followup_date.isEmpty()) {
            et_follow_update.setError("Please enter Follow up Date");
            et_follow_update.requestFocus();
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
                newp_marital_status_id = maritalStatusAdapter.getPosition(strMaritalStatus);//get selected marital status id
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_add:

                str_name = et_name_newp.getText().toString().trim();
                str_patient_email = et_email_newp.getText().toString().trim();
                str_patient_dob = et_dob.getText().toString().trim();
                str_patient_mobile = et_mobile_newp.getText().toString().trim();
                str_patient_gender = getGenderName();//getting selected gender associated id/value
                str_patient_marital_status = String.valueOf(newp_marital_status_id).trim();
                str_patient_address = et_address_newp.getText().toString().trim();
                str_disease_type = et_disease_type_newp.getText().toString().trim();
                str_followup_date = et_follow_update.getText().toString().trim();

                if (validateForm(str_name, str_patient_email, str_patient_dob, str_patient_mobile,
                        rgp_gender_newp, str_patient_marital_status, str_patient_address, str_disease_type, str_followup_date)) {
                    //remove error mark when no fields are empty
                    et_name_newp.setError(null);
                    et_email_newp.setError(null);
                    et_dob.setError(null);
                    et_mobile_newp.setError(null);
                    et_address_newp.setError(null);
                    et_disease_type_newp.setError(null);
                    et_follow_update.setError(null);
                    Toast.makeText(NewPatient.this, "Data added Successfully", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.ll_capture_prescription:
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, CAMERA_PIC_REQUEST);
                break;

            case R.id.ll_upload_doc:


            default:
                break;
        }
    }

    //calender use for dob and follow update
    private void selectDate(final EditText editTextRef) {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
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
                        editTextRef.setText(strStoreDate);
                    } else {
                        editTextRef.setText("");
                    }
                } else {
                    strStoreDate = "null";
                }
                // setUserId();
            }

        };

        //calender select--------------------------------------------------------------------
        editTextRef.setFocusable(false);
        editTextRef.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                // TODO Auto-generated method stub
                DatePickerDialog dpd = new DatePickerDialog(NewPatient.this, R.style.MyThemeOverlay, date, refCalendar
                        .get(Calendar.YEAR), refCalendar.get(Calendar.MONTH), refCalendar.get(Calendar.DAY_OF_MONTH));
                //    dpd.getDatePicker().setMaxDate(System.currentTimeMillis()); //make future date disable
                dpd.show();
                dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // do something onCancel
                        strStoreDate = "null";
                        editTextRef.setText("");
                    }
                });

            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            ImageView imageview = (ImageView) findViewById(R.id.img_camera); //sets imageview as the bitmap
            imageview.setImageBitmap(image);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
