package com.app.doorpin.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.app.doorpin.Activity.EditPatientDetails;
import com.app.doorpin.Activity.Login;
import com.app.doorpin.Activity.Profile_Nurse;
import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.Patient_PersInfo_RetroModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientDetails_PersoInfo_Frag extends Fragment {

    SessionManager sessionManager;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;

    TextView tv_emailVal, tv_dobVal, tv_mobileNumberVal, tv_genderVal, tv_maritalStatusVal, tv_patientsAgeVal, tv_addressVal;
    LinearLayout ll_edit;

    public PatientDetails_PersoInfo_Frag() {
        super();
        //empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_details_personal_info, container, false);

        sessionManager = new SessionManager(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        tv_emailVal = view.findViewById(R.id.tv_emailVal);
        tv_dobVal = view.findViewById(R.id.tv_dobVal);
        tv_mobileNumberVal = view.findViewById(R.id.tv_mobileNumberVal);
        tv_genderVal = view.findViewById(R.id.tv_genderVal);
        tv_maritalStatusVal = view.findViewById(R.id.tv_maritalStatusVal);
        tv_patientsAgeVal = view.findViewById(R.id.tv_patientsAgeVal);
        tv_addressVal = view.findViewById(R.id.tv_addressVal);
        ll_edit = view.findViewById(R.id.ll_edit);

        if (Utils.CheckInternetConnection(getActivity())) {
            if (!sessionManager.getPatientIdHome().equals("NA")) {
                getPersonalInformation(sessionManager.getDoctorNurseId(),
                        sessionManager.getLoggedUsrId(), sessionManager.getPatientIdHome());
            } else {
                //do nothing
                Toast.makeText(getActivity(), "Something went wrong! Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
        }


        init();
        return view;
    }

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
                            Toast.makeText(getContext(), patientPersonalInfoRequest.message, Toast.LENGTH_SHORT).show();
                        } else {
                            for (Patient_PersInfo_RetroModel.PatientPersonalInfo_Datum personal_info_data : personal_info_datum) {
                                String p_email = personal_info_data.patient_email_id;
                                String p_dob = personal_info_data.patient_dob;
                                String p_mobile = personal_info_data.patient_mobile;
                                String p_gender = personal_info_data.patient_gender;
                                String p_marital_status = personal_info_data.Patient_marital_status;
                                String p_age = personal_info_data.patient_age;
                                String p_address = personal_info_data.patient_address;

                                //patient email id
                                if (!p_email.equals("null") && !p_email.equals(null) && !p_email.equals("NA") && !p_email.isEmpty()) {

                                    tv_emailVal.setText(p_email);
                                } else {
                                    tv_emailVal.setText("");
                                }
                                //patient date of birth
                                if (!p_dob.equals("null") && !p_dob.equals(null) && !p_dob.equals("NA") && !p_dob.isEmpty()) {

                                    tv_dobVal.setText(p_dob);
                                } else {
                                    tv_dobVal.setText("");
                                }
                                //patient mobile number
                                if (!p_mobile.equals("null") && !p_mobile.equals(null) && !p_mobile.equals("NA") && !p_mobile.isEmpty()) {

                                    tv_mobileNumberVal.setText(p_mobile);
                                } else {
                                    tv_mobileNumberVal.setText("");
                                }
                                //patient gender
                                if (!p_gender.equals("null") && !p_gender.equals(null) && !p_gender.equals("NA") && !p_gender.isEmpty()) {

                                    tv_genderVal.setText(p_gender);
                                } else {
                                    tv_genderVal.setText("");
                                }
                                //patient marital status
                                if (!p_marital_status.equals("null") && !p_marital_status.equals(null) && !p_marital_status.equals("NA") && !p_marital_status.isEmpty()) {

                                    tv_maritalStatusVal.setText(p_marital_status);
                                } else {
                                    tv_maritalStatusVal.setText("");
                                }
                                //patient age
                                if (!p_age.equals("null") && !p_age.equals(null) && !p_age.equals("NA") && !p_age.isEmpty()) {

                                    tv_patientsAgeVal.setText(p_age);
                                } else {
                                    tv_patientsAgeVal.setText("");
                                }
                                //patient address
                                if (!p_address.equals("null") && !p_address.equals(null) && !p_address.equals("NA") && !p_address.isEmpty()) {

                                    tv_addressVal.setText(p_address);
                                } else {
                                    tv_addressVal.setText("");
                                }

                            }
                            progressDialog.dismiss();

                        }
                    } else {
                        //failure response
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), patientPersonalInfoRequest.message, Toast.LENGTH_SHORT).show();
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
                                new android.app.AlertDialog.Builder(getActivity())
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
                new AlertDialog.Builder(getActivity())
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


    private void init() {
        ll_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check patient id if it is null stay on same page, do not proceed further
                if (!sessionManager.getPatientIdHome().equals("NA")) {
                    Intent intentEditPatientDetails = new Intent(getActivity(), EditPatientDetails.class);
                    intentEditPatientDetails.putExtra("PAGE_FLAG", "2");
                    intentEditPatientDetails.putExtra("PATIENT_ID", sessionManager.getPatientIdHome());
                    startActivity(intentEditPatientDetails);
                    getActivity().finish();
                } else {
                    //do nothing
                    Toast.makeText(getActivity(), "Invalid Patient Information!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
