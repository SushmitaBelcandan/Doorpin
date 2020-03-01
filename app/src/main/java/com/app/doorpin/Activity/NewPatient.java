package com.app.doorpin.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.doorpin.R;
import com.app.doorpin.models.SetDateonCalendar;

import java.util.ArrayList;

public class NewPatient extends AppCompatActivity implements View.OnClickListener {

    Button btn_add;
    LinearLayout llcapturePrescription, lluploadDocs;
    Spinner spnr_marital_status;
    EditText et_dob, et_follow_update;

    public static final int CAMERA_PIC_REQUEST = 1;
    SetDateonCalendar setDateonCalendar, setFollowUpdate;

    ArrayList<String> arrList_marital_status;
    String strMaritalStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        et_dob = findViewById(R.id.et_dob);
        et_follow_update = findViewById(R.id.et_follow_update);

        llcapturePrescription = findViewById(R.id.ll_capture_prescription);
        lluploadDocs = findViewById(R.id.ll_upload_doc);
        spnr_marital_status = findViewById(R.id.spnr_marital_status);


        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        llcapturePrescription.setOnClickListener(this);
        lluploadDocs.setOnClickListener(this);

        setDateonCalendar = new SetDateonCalendar(et_dob, this);
        setFollowUpdate = new SetDateonCalendar(et_follow_update, this);

        //set marital status
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            ImageView imageview = (ImageView) findViewById(R.id.img_camera); //sets imageview as the bitmap
            imageview.setImageBitmap(image);
        }

    }
}
