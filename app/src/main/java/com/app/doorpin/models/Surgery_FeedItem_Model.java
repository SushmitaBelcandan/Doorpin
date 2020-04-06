package com.app.doorpin.models;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.doorpin.Activity.HomePage_Doctor;
import com.app.doorpin.Activity.MySurgeries;
import com.app.doorpin.Activity.NewSurgery;
import com.app.doorpin.Adapters.SearchPatientAdapter;
import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.SearchPatient_RetroModel;
import com.app.doorpin.retrofit.SearchSurgPatient_RetroModel;
import com.app.doorpin.retrofit.SurgeryList_RetroModel;
import com.google.gson.Gson;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

@Layout(R.layout.surgery_feeitem_model)
public class Surgery_FeedItem_Model {

    @View(R.id.phvSurgeryPatient)
    public PlaceHolderView phvSurgeryPatient;

    @View(R.id.llBtnSearch)
    public LinearLayout llBtnSearch;

    @View(R.id.etSearch)
    public EditText etSearch;

    @View(R.id.tv_noData)
    public TextView tv_noData;

    SessionManager sessionManager;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;

    public Context mContext;
    ArrayList<String> arr_list_patient_table_id;
    ArrayList<String> arr_list_patient_id;
    ArrayList<String> arr_list_patient_name;
    ArrayList<String> arr_list_doc_name = new ArrayList<>();
    ArrayList<String> arr_list_files = new ArrayList<>();
    ArrayList<String> arrlist_patient_id;
    ArrayList<String> arrlist_patient_name;

    String patient_id = "null";
    String patient_name = "null";
    List<SurgeryList_RetroModel.PatientDatum> mLists;
    String str_surgery_id;

    public Surgery_FeedItem_Model(Context context, List<SurgeryList_RetroModel.PatientDatum> list_data, String surgery_id) {
        this.mContext = context;
        this.mLists = list_data;
        this.str_surgery_id = surgery_id;
    }

    @Resolve
    public void onResolved() {

        sessionManager = new SessionManager(mContext);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait...");

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        getPatientList();//default list
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etSearch.getText().toString().length() == 0) {
                    getPatientList();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    String str_search_key;

    @Click(R.id.llBtnSearch)
    public void searchPatient() {

        if (Utils.CheckInternetConnection(mContext)) {

            str_search_key = etSearch.getText().toString().trim();
            if (str_search_key.equals(null) || str_search_key.isEmpty()) {
                Toast.makeText(mContext, "Not a Valid Keyword", Toast.LENGTH_SHORT).show();
            } else {
                boolean digitsOnly = TextUtils.isDigitsOnly(str_search_key);
                if (digitsOnly == true) {
                    searchPatient(sessionManager.getDoctorNurseId(),
                            sessionManager.getLoggedUsrId(), str_search_key, "NA", str_surgery_id);
                } else {
                    searchPatient(sessionManager.getDoctorNurseId(),
                            sessionManager.getLoggedUsrId(), "NA", str_search_key, str_surgery_id);

                }
            }

        } else {
            Toast.makeText(mContext, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchPatient(String usrType, String usrId, String patientId, String patientName, String surgeryId) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        phvSurgeryPatient.removeAllViews();
        if (!str_search_key.equals("null")) {
            final SearchSurgPatient_RetroModel searchModel = new SearchSurgPatient_RetroModel(usrType, usrId, patientId, patientName, surgeryId);

            Call<SearchSurgPatient_RetroModel> callSearchList = apiInterface.searchPatientSurg(searchModel);
            callSearchList.enqueue(new Callback<SearchSurgPatient_RetroModel>() {
                @Override
                public void onResponse(Call<SearchSurgPatient_RetroModel> call, Response<SearchSurgPatient_RetroModel> response) {
                    SearchSurgPatient_RetroModel surgery_list_resources = response.body();
                    if (response.isSuccessful()) {
                        tv_noData.setVisibility(GONE);
                        phvSurgeryPatient.setVisibility(android.view.View.VISIBLE);
                        if (surgery_list_resources.status.equals("success")) {
                            List<SearchSurgPatient_RetroModel.Search_Datum> patient_list = surgery_list_resources.result;
                            if (patient_list.size() > 0) {
                                for (SearchSurgPatient_RetroModel.Search_Datum data : patient_list) {
                                    //---------documents
                                    List<SearchSurgPatient_RetroModel.SurgeryDocsDatum> datum_1 = data.document_data;
                                    ArrayList<String> docs_datum = new ArrayList<>();
                                    for (SearchSurgPatient_RetroModel.SurgeryDocsDatum docs_data : datum_1) {
                                        docs_datum.add(docs_data.document_link);
                                    }
                                    //--------------files
                                    List<SearchSurgPatient_RetroModel.SurgeryFileDatum> datum_2 = data.file_data;
                                    ArrayList<String> files_datum = new ArrayList<>();
                                    for (SearchSurgPatient_RetroModel.SurgeryFileDatum files_data : datum_2) {
                                        files_datum.add(files_data.file_link);
                                    }
                                    phvSurgeryPatient.addView(new Surgery_Patient_List_Model(mContext, phvSurgeryPatient,
                                            data.patienttable_id, data.patient_id,data.display_id, data.patient_name, docs_datum, files_datum));

                                }
                                progressDialog.dismiss();

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(mContext, "No patient added in surgery list", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
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
                                    tv_noData.setVisibility(android.view.View.VISIBLE);
                                    phvSurgeryPatient.setVisibility(GONE);
                                    new android.app.AlertDialog.Builder(mContext)
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
                public void onFailure(Call<SearchSurgPatient_RetroModel> call, Throwable t) {
                    call.cancel();
                    progressDialog.dismiss();
                    new AlertDialog.Builder(mContext)
                            .setMessage("Server error! Please try again later")
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
    }

    private void getPatientList() {
        phvSurgeryPatient.removeAllViews();
        tv_noData.setVisibility(GONE);
        phvSurgeryPatient.setVisibility(android.view.View.VISIBLE);
        for (SurgeryList_RetroModel.PatientDatum datum : mLists) {
            List<SurgeryList_RetroModel.DocsDatum> datum_1 = datum.document_data;
            ArrayList<String> docs_datum = new ArrayList<>();
            for (SurgeryList_RetroModel.DocsDatum docs_data : datum_1) {
                docs_datum.add(docs_data.document_link);
            }
            //--------------files
            List<SurgeryList_RetroModel.FileDatum> datum_2 = datum.file_data;
            ArrayList<String> files_datum = new ArrayList<>();
            for (SurgeryList_RetroModel.FileDatum files_data : datum_2) {
                files_datum.add(files_data.document_link1);
            }
            phvSurgeryPatient.addView(new Surgery_Patient_List_Model(mContext, phvSurgeryPatient, datum.patienttable_id, datum.patient_id,
                   datum.display_id, datum.patient_name, docs_datum, files_datum));
        }
    }
}
