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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    public static final int CAMERA_PIC_REQUEST = 1;
    public Calendar refCalendar = Calendar.getInstance();
    String strStoreDate;

    ArrayList<String> arrList_marital_status;
    String strMaritalStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient);

        toolbar_new_patient = findViewById(R.id.toolbar_new_patient);
        et_dob = findViewById(R.id.et_dob);
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

    private void addMaritalStatus() {
        //add spinner item
        arrList_marital_status = new ArrayList<>();
        arrList_marital_status.add("Married");
        arrList_marital_status.add("Unmarried");
        ArrayAdapter<String> maritalStatusAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrList_marital_status);
        maritalStatusAdapter.setDropDownViewResource(R.layout.marital_status);
        spnr_marital_status.setAdapter(maritalStatusAdapter);
        spnr_marital_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.black_shade_1)); //Change selected text color
                ((TextView) view).setTextAppearance(getApplicationContext(), R.style.marital_status_dropdown);
                strMaritalStatus = spnr_marital_status.getSelectedItem().toString(); //selected string
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
                Toast.makeText(NewPatient.this, "Patient Details added successfully", Toast.LENGTH_LONG).show();
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
