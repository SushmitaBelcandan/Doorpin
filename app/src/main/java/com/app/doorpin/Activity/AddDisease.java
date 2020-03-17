package com.app.doorpin.Activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.doorpin.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddDisease extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar_add_disease;

    Button btn_add;
    LinearLayout llcapturePrescription, lluploadDocs;
    EditText et_follow_update;
    EditText et_disease_type_add_ill, et_name_newp;

    public static final int CAMERA_PIC_REQUEST = 1;
    public Calendar refCalendar = Calendar.getInstance();
    String strStoreDate;

    private String str_name, str_disease_type, str_followup_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_disease_act);

        toolbar_add_disease = findViewById(R.id.toolbar_add_disease);
        et_name_newp = findViewById(R.id.et_name_newp);
        et_disease_type_add_ill = findViewById(R.id.et_disease_type_add_ill);
        et_follow_update = findViewById(R.id.et_follow_update);
        //toolbar
        setSupportActionBar(toolbar_add_disease);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar_add_disease.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        llcapturePrescription = findViewById(R.id.ll_capture_prescription);
        lluploadDocs = findViewById(R.id.ll_upload_doc);


        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        llcapturePrescription.setOnClickListener(this);
        lluploadDocs.setOnClickListener(this);

        //select followupdate by populating calender
        et_follow_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(et_follow_update);
            }
        });


    }

    private boolean validateForm(String str_name, String str_type_disease, String str_followup_date) {
        //new patient name
        if (str_name.equals("null") || str_name.equals("NA") || str_name.equals(null) || str_name.isEmpty()) {
            et_name_newp.setError("Please enter Patient Name");
            et_name_newp.requestFocus();
            return false;
        }
        //new patient's type of disease
        if (str_type_disease.equals("null") || str_type_disease.equals(null) || str_type_disease.equals("NA") || str_type_disease.isEmpty()) {
            et_disease_type_add_ill.setError("Please enter Disease Name");
            et_disease_type_add_ill.requestFocus();
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_add:

                str_name = et_name_newp.getText().toString().trim();
                str_disease_type = et_disease_type_add_ill.getText().toString().trim();
                str_followup_date = et_follow_update.getText().toString().trim();

                if (validateForm(str_name, str_disease_type, str_followup_date)) {
                    //remove error mark when no fields are empty
                    et_name_newp.setError(null);
                    et_disease_type_add_ill.setError(null);
                    et_follow_update.setError(null);
                    Toast.makeText(AddDisease.this, "Data added Successfully", Toast.LENGTH_LONG).show();
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
        editTextRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog dpd = new DatePickerDialog(AddDisease.this, R.style.MyThemeOverlay, date, refCalendar
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
        Intent intentPatientDetails = new Intent(AddDisease.this, PatientDetails.class);
        startActivity(intentPatientDetails);
        finish();
    }
}
