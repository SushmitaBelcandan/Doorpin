package com.app.doorpin.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.doorpin.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar_edit_profile;

    Button btn_save;
    Spinner spnr_edit_exp;
    EditText et_edit_name, et_edit_mobile, et_edit_education, et_edit_specialization, et_edit_address;

    ArrayList<String> arrList_Exp;
    String str_exp;
    private int edit_exp_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //toolbar
        toolbar_edit_profile = findViewById(R.id.toolbar_edit_profile);
        setSupportActionBar(toolbar_edit_profile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar_edit_profile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //-------------------
        et_edit_name = findViewById(R.id.et_edit_name);
        et_edit_mobile = findViewById(R.id.et_edit_mobile);
        et_edit_education = findViewById(R.id.et_edit_education);
        et_edit_specialization = findViewById(R.id.et_edit_specialization);
        et_edit_address = findViewById(R.id.et_edit_address);
        spnr_edit_exp = findViewById(R.id.spnr_edit_exp);
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);

        //----------------experience spinner
        addExperience();

    }

    private String str_name, str_mobile, str_education, str_specialization, str_experience, str_address;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_save:
                str_name = et_edit_name.getText().toString().trim();
                str_mobile = et_edit_mobile.getText().toString().trim();
                str_education = et_edit_education.getText().toString().trim();
                str_specialization = et_edit_specialization.getText().toString().trim();
                str_experience = String.valueOf(edit_exp_num).trim();
                str_address = et_edit_address.getText().toString().trim();

                if (validateForm(str_name, str_mobile, str_education, str_specialization,
                        str_experience, str_address)) {
                    //remove error mark when no fields are empty
                    et_edit_name.setError(null);
                    et_edit_mobile.setError(null);
                    et_edit_education.setError(null);
                    et_edit_specialization.setError(null);
                    et_edit_address.setError(null);

                    Intent oIntent = getIntent();//for page flag
                    String str_page_flag = oIntent.getExtras().getString("PROF");
                    if (str_page_flag.equals("DOCT")) {
                        Intent intentProfileDoctor = new Intent(EditProfile.this, Profile_Doctor.class);
                        startActivity(intentProfileDoctor);
                        finish();
                    } else {
                        Intent intentProfileNurse = new Intent(EditProfile.this, Profile_Nurse.class);
                        startActivity(intentProfileNurse);
                        finish();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void addExperience() {
        //add spinner item
        arrList_Exp = new ArrayList<>();
        arrList_Exp.add("Select experience");
        arrList_Exp.add("1 year");
        arrList_Exp.add("2 years");
        arrList_Exp.add("3 years");
        arrList_Exp.add("4 years");
        arrList_Exp.add("5 years");
        arrList_Exp.add("6 years");
        arrList_Exp.add("8 years");
        arrList_Exp.add("9 years");
        arrList_Exp.add("10 years");
        arrList_Exp.add("11 years");
        arrList_Exp.add("12 years");
        arrList_Exp.add("13 years");
        arrList_Exp.add("14 years");
        arrList_Exp.add("15 years");
        arrList_Exp.add("More than 15 Years");
        final ArrayAdapter<String> expAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrList_Exp);
        expAdapter.setDropDownViewResource(R.layout.marital_status);
        //-----------custom dropdown view fixed size and scrollable--
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spnr_edit_exp);

            // Set popupWindow height to 500px
            popupWindow.setHeight(100);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
        //----------------------------------------------------
        spnr_edit_exp.setAdapter(expAdapter);
        spnr_edit_exp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.black_shade_1)); //Change selected text color
                ((TextView) view).setTextAppearance(getApplicationContext(), R.style.marital_status_dropdown);
                str_exp = spnr_edit_exp.getSelectedItem().toString(); //selected string
                edit_exp_num = expAdapter.getPosition(str_exp);//get selected marital status id
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });


    }

    private boolean validateForm(String str_name, String str_mobile, String str_education,
                                 String str_specialization, String str_address, String str_exp) {
        //new patient name
        if (str_name.equals("null") || str_name.equals("NA") || str_name.equals(null) || str_name.isEmpty()) {
            et_edit_name.setError("Please enter Your Name");
            et_edit_name.requestFocus();
            return false;
        }
        //mobile validations
        if (str_mobile.equals("null") || str_mobile.equals("NA") || str_mobile.equals(null) || str_mobile.isEmpty()) {
            et_edit_mobile.setError("Please enter Mobile Number");
            et_edit_mobile.requestFocus();
            return false;
        }
        if (str_mobile.matches("(\\d)(?!\\1+$)\\d{10}")) {
            et_edit_mobile.setError("Invalid Mobile Number");
            et_edit_mobile.requestFocus();
            return false;
        }
        if (str_mobile.length() != 10) {
            et_edit_mobile.setError("Invalid Mobile Number");
            et_edit_mobile.requestFocus();
            return false;
        }
        //education
        if (str_education.equals("null") || str_education.equals("NA") || str_education.equals(null) || str_education.isEmpty()) {
            et_edit_education.setError("Please enter Your Education");
            et_edit_education.requestFocus();
            return false;
        }
        //specialization
        if (str_specialization.equals("null") || str_specialization.equals(null) || str_specialization.equals("NA") || str_specialization.isEmpty()) {
            et_edit_specialization.setError("Please enter Your Specialization");
            et_edit_specialization.requestFocus();
            return false;
        }
        //experience
        if (str_exp.equals("0")) {
            new AlertDialog.Builder(EditProfile.this)
                    .setMessage("Please select Experience")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return false;
        }
        //address
        if (str_address.equals("null") || str_address.equals(null) || str_address.equals("NA") || str_address.isEmpty()) {
            et_edit_address.setError("Please enter Address");
            et_edit_address.requestFocus();
            return false;
        }
        return true;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent oIntent = getIntent();//for page flag
        String str_page_flag = oIntent.getExtras().getString("PROF");
        if (str_page_flag.equals("DOCT")) {
            Intent intentProfileDoctor = new Intent(EditProfile.this, Profile_Doctor.class);
            startActivity(intentProfileDoctor);
            finish();
        } else {
            Intent intentProfileNurse = new Intent(EditProfile.this, Profile_Nurse.class);
            startActivity(intentProfileNurse);
            finish();
        }
    }
}
