package com.app.doorpin.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.Activity.HomePage_Doctor;
import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.models.Illness;
import com.app.doorpin.models.Illness_List_Model;
import com.app.doorpin.models.SetDateonCalendar;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.Illness_List_Retro_Model;
import com.mindorks.placeholderview.PlaceHolderView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientDetails_Illness_Frag extends Fragment {

    ApiInterface apiInterface;
    SessionManager sessionManager;
    ProgressDialog progressDialog;

    PlaceHolderView phv_illness;
    SetDateonCalendar setDateonCalendar;

    LinearLayout llNoData1;


    public PatientDetails_Illness_Frag() {
        super();
        //empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.illness_details, container, false);

      /*  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        sessionManager = new SessionManager(getActivity());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait.....");

        phv_illness = view.findViewById(R.id.phv_illness);
        llNoData1 = view.findViewById(R.id.llNoData1);

        if (Utils.CheckInternetConnection(getActivity())) {
            if (!sessionManager.getPatientIdHome().equals("NA")) {
                getIllnessList(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), sessionManager.getPatientIdHome());
            } else {
                //do nothing
                Toast.makeText(getActivity(), "Something went wrong! Please try again", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getContext(), "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void getIllnessList(String str_usr_types, String str_usr_ids, final String str_patient_ids) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Illness_List_Retro_Model illness_model = new Illness_List_Retro_Model(str_usr_types, str_usr_ids, str_patient_ids);
        Call<Illness_List_Retro_Model> illness_call = apiInterface.getIllnessList(illness_model);
        illness_call.enqueue(new Callback<Illness_List_Retro_Model>() {
            @Override
            public void onResponse(Call<Illness_List_Retro_Model> call, Response<Illness_List_Retro_Model> response) {
                Illness_List_Retro_Model illness_resources = response.body();
                if (response.isSuccessful()) {
                    if (illness_resources.status.equals("success")) {
                        List<Illness_List_Retro_Model.Illness_List_Datum> datumList = illness_resources.result;
                        if (datumList.size() <= 0) {
                            llNoData1.setVisibility(View.VISIBLE);
                            phv_illness.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        } else {

                            phv_illness.setVisibility(View.VISIBLE);//show list
                            llNoData1.setVisibility(View.GONE);
                            for (Illness_List_Retro_Model.Illness_List_Datum illness_data : datumList) {
                                String str_illness_id = illness_data.disease_id;
                                String str_illness_name = illness_data.disease_name;
                                String str_followup_date = illness_data.follow_up_date;
                                String str_prsec_id = illness_data.prescription_id;
                                String str_prsec_link = illness_data.prescription_link;
                                String str_report_id = illness_data.report_id;
                                String str_report_link = illness_data.report_link;
                                String str_other_id = illness_data.document_id;
                                String str_other_docs_link = illness_data.document_link;

                                phv_illness.addView(new Illness_List_Model(getActivity(), str_illness_id, str_illness_name, str_followup_date,
                                        str_prsec_id, str_prsec_link, str_report_id, str_report_link, str_other_id, str_other_docs_link, str_patient_ids));
                            }
                            progressDialog.dismiss();
                            //   Toast.makeText(getActivity(), illness_resources.message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        llNoData1.setVisibility(View.VISIBLE);
                        phv_illness.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), illness_resources.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    llNoData1.setVisibility(View.VISIBLE);
                    phv_illness.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Illness_List_Retro_Model> call, Throwable t) {
                call.cancel();
                llNoData1.setVisibility(View.GONE);
                phv_illness.setVisibility(View.GONE);
                progressDialog.dismiss();
            }
        });
    }


}
