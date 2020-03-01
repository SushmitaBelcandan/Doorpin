package com.app.doorpin.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.doorpin.Activity.EditPatientDetails;
import com.app.doorpin.R;

public class PatientDetails_PersoInfo_Frag extends Fragment {

    TextView tv_emailVal, tv_dobVal, tv_mobileNumberVal, tv_genderVal, tv_maritalStatusVal, tv_parentsAgeVal, tv_addressVal;
    Button btn_edit;

    public PatientDetails_PersoInfo_Frag() {
        super();
        //empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_details_personal_info, container, false);

        tv_emailVal = view.findViewById(R.id.tv_emailVal);
        tv_dobVal = view.findViewById(R.id.tv_dobVal);
        tv_mobileNumberVal = view.findViewById(R.id.tv_mobileNumberVal);
        tv_genderVal = view.findViewById(R.id.tv_genderVal);
        tv_maritalStatusVal = view.findViewById(R.id.tv_maritalStatusVal);
        tv_parentsAgeVal = view.findViewById(R.id.tv_parentsAgeVal);
        tv_addressVal = view.findViewById(R.id.tv_addressVal);
        btn_edit = view.findViewById(R.id.btn_edit);

        tv_emailVal.setText("anjali.sisodiya@gmail.com");
        tv_dobVal.setText("10/03/1997");
        tv_mobileNumberVal.setText("1234567891");
        tv_genderVal.setText("Female");
        tv_maritalStatusVal.setText("Married");
        tv_parentsAgeVal.setText("29");
        tv_addressVal.setText("1/2 Lazer Bazar Road, Balaji Layout, Cooke Town, Bengaluru , Karnataka, 560084");
        init();
        return view;
    }

    public void init() {
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEditPatientDetails = new Intent(getActivity(), EditPatientDetails.class);
                intentEditPatientDetails.putExtra("PAGE_FLAG", "2");
                startActivity(intentEditPatientDetails);
                getActivity().finish();
            }
        });
    }

}
