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
import android.widget.Spinner;
import android.widget.TextView;

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
    Button btn_save;
    EditText et_edit_dob;
    Spinner spnr_marital_status;
    Toolbar toolbar_edit_patient_details;

    ArrayList<String> arrList_marital_status;
    String strMaritalStatus;
    String strDob;

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
        et_edit_dob = findViewById(R.id.et_edit_dob);
        spnr_marital_status = findViewById(R.id.spnr_marital_status);

        btn_save.setOnClickListener(this);
        //  setDateonCalendar = new SetDateonCalendar(et_edit_dob, this);
        selectDate();
        addMaritalStatus();

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
                startActivity(new Intent(EditPatientDetails.this, PatientDetails.class));
                finish();
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
