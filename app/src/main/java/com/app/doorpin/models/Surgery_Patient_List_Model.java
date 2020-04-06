package com.app.doorpin.models;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.doorpin.Activity.EditPatientDetails;
import com.app.doorpin.Activity.MySurgeries;
import com.app.doorpin.Activity.View_Docs_Surg_Patient_Act;
import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.DeleteSurgPatient_RetroModel;
import com.app.doorpin.retrofit.Delete_Disease_Retro_Model;
import com.app.doorpin.upload_docs.Upload_Docs_Interface;
import com.google.gson.Gson;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

@Layout(R.layout.surgery_patient_list_model)
public class Surgery_Patient_List_Model {

    @View(R.id.llContainer)
    public LinearLayout llContainer;

    @View(R.id.tvPatientName)
    public TextView tvPatientName;

    @View(R.id.tvPatientId)
    public TextView tvPatientId;

    @View(R.id.llDelete)
    public LinearLayout llDelete;

    @View(R.id.llViewDocs)
    public LinearLayout llViewDocs;

    @View(R.id.imgBtnViewDocs)
    public ImageButton imgBtnViewDocs;

    @View(R.id.llCaptureNotes)
    public LinearLayout llCaptureNotes;

    @View(R.id.imgBtnCaptureNotes)
    public ImageButton imgBtnCaptureNotes;

    SessionManager session;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;
    Context mContext;
    PlaceHolderView phv_list;
    String str_patient_table_id, str_patient_id, str_display_id, str_patient_name;
    ArrayList<String> arr_list_doc_name;
    ArrayList<String> arr_list_file_name;
    ArrayList<String> arr_list_docs;

    Upload_Docs_Interface upload_docs_interface;

    public Surgery_Patient_List_Model(Context context, PlaceHolderView phv_list, String patient_table_id, String patient_id,
                                      String display_id, String patient_name, ArrayList<String> arr_list_docs, ArrayList<String> arr_list_files) {
        this.mContext = context;
        this.phv_list = phv_list;
        this.str_patient_table_id = patient_table_id;
        this.str_patient_id = patient_id;
        this.str_display_id = display_id;
        this.str_patient_name = patient_name;
        this.arr_list_doc_name = arr_list_docs;
        this.arr_list_file_name = arr_list_files;
        try {
            upload_docs_interface = (Upload_Docs_Interface) context;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
            Log.e("MyAdapter", "Must implement the CallbackInterface in the Activity", ex);
        }
    }

    @Resolve
    public void onResolved() {
        //initialize session
        session = new SessionManager(mContext);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please wait.....");
        //----------------------------------------------------------
        if (!str_patient_table_id.equals(null) && !str_patient_table_id.equals("null") && !str_patient_table_id.equals("NA") && !str_patient_table_id.isEmpty()) {
            if (!str_display_id.equals(null) && !str_display_id.equals("null") && !str_display_id.equals("NA") && !str_display_id.isEmpty()) {
                tvPatientId.setText("Patient Id -" + " " + str_display_id);
            } else {
                tvPatientId.setText("Patient Id -" + " ");
            }
            if (!str_patient_name.equals(null) && !str_patient_name.equals("null") && !str_patient_name.equals("NA") && !str_patient_name.isEmpty()) {
                tvPatientName.setText(str_patient_name);
            } else {
                tvPatientName.setText("");
            }
        }
        arr_list_docs = new ArrayList<>();
        if (arr_list_doc_name.size() > 0) {
            for (int i = 0; i < arr_list_doc_name.size(); i++) {
                Log.d("doc array------", "" + arr_list_doc_name.get(i));
                arr_list_docs.add(arr_list_doc_name.get(i));
            }
        }
        if (arr_list_file_name.size() > 0) {
            for (int i = 0; i < arr_list_file_name.size(); i++) {
                Log.d("files array------", "" + arr_list_file_name.get(i));
                arr_list_docs.add(arr_list_file_name.get(i));
            }
        }

    }

    @Click(R.id.imgBtnDelete)
    public void deleteRow() {
        if (Utils.CheckInternetConnection(mContext)) {
            if (!str_patient_table_id.equals(null) && !str_patient_table_id.equals("null") && !str_patient_table_id.equals("NA") && !str_patient_table_id.isEmpty()) {
                DeleteSurgPatient_RetroModel delete_model = new DeleteSurgPatient_RetroModel(session.getDoctorNurseId(),
                        session.getLoggedUsrId(), str_patient_table_id);
                Call<DeleteSurgPatient_RetroModel> deletel_call = apiInterface.deletePatient(delete_model);
                deletel_call.enqueue(new Callback<DeleteSurgPatient_RetroModel>() {
                    @Override
                    public void onResponse(Call<DeleteSurgPatient_RetroModel> call, Response<DeleteSurgPatient_RetroModel> response) {
                        DeleteSurgPatient_RetroModel delete_illness_request = response.body();
                        if (response.isSuccessful()) {
                            if (delete_illness_request.status.equals("success")) {
                                Toast.makeText(mContext, delete_illness_request.message, Toast.LENGTH_SHORT).show();
                                phv_list.removeView(phv_list.getChildAdapterPosition(llContainer));

                            } else {
                                Toast.makeText(mContext, delete_illness_request.message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (response.code() == 400) {
                                if (!response.isSuccessful()) {
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(response.errorBody().string());
                                        String userMessage = jsonObject.getString("status");
                                        String internalMessage = jsonObject.getString("message");
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
                    public void onFailure(Call<DeleteSurgPatient_RetroModel> call, Throwable t) {
                        call.cancel();
                    }
                });
            }
        } else {
            Toast.makeText(mContext, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
        }

    }

    @Click(R.id.imgBtnViewDocs)
    public void onClickDocs() {

        if (!session.getDocsDataId().equals("null")) {
            arr_list_docs.add(session.getDocsDataName());
            session.saveDocsData("null", "null", "null");
        }
        Intent intentViewDocs = new Intent(mContext, View_Docs_Surg_Patient_Act.class);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arr_list_docs);
        editor.putString("doc_id", json);
        editor.commit();//save all the list into shared pref
        intentViewDocs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intentViewDocs);
    }

    private static final int GALLERY_PICTURE = 1;
    private static final int CAMERA_REQUEST = 12;
    private static final int DOC_REQUEST = 105;
    private static final String DOC_TYPE_PRSEC = "7";
    private static final String DOC_TYPE_REPORT = "6";

    @Click(R.id.imgBtnCaptureNotes)
    public void onClickNotes() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        android.view.View promptView = layoutInflater.inflate(R.layout.upload_document_popup, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
        alertDialogBuilder.setView(promptView);

        TextView tv_upload_presec = (TextView) promptView.findViewById(R.id.tv_upload_presec);
        tv_upload_presec.setText("Upload Image");
        TextView tv_upload_report = (TextView) promptView.findViewById(R.id.tv_upload_report);
        tv_upload_report.setText("Upload Documents");
        TextView tv_upload_other_doc = (TextView) promptView.findViewById(R.id.tv_upload_other_doc);
        tv_upload_other_doc.setVisibility(android.view.View.GONE);

        final AlertDialog alertDialog_main = alertDialogBuilder.create();
        alertDialog_main.show();
        alertDialog_main.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //-----------------------------------upload document action event------------------------
        tv_upload_presec.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                android.view.View promptView = layoutInflater.inflate(R.layout.choose_img_popup, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
                alertDialogBuilder.setView(promptView);

                TextView tv_line = promptView.findViewById(R.id.tv_line);
                tv_line.setText("How do you want to upload Image?");
                Button btn_gallery = promptView.findViewById(R.id.btn_gallery);
                Button btn_camera = promptView.findViewById(R.id.btn_camera);

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //mae alert bg transparent for custom rounded corner
                //------------------------------select gallery----------------------------------------------------------------
                btn_gallery.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        if (upload_docs_interface != null) {
                            if (str_patient_table_id.equals(null) || str_patient_id.equals(null)) {
                                //do nothing
                            } else {
                                upload_docs_interface.onCaptureNotes(GALLERY_PICTURE, DOC_TYPE_PRSEC, str_patient_table_id, str_patient_id);
                                alertDialog.dismiss();
                                alertDialog_main.dismiss();
                            }
                        }
                    }
                });
                //----------------------------capture image from camera--------------------------------------------------
                btn_camera.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        if (upload_docs_interface != null) {
                            if (str_patient_table_id.equals(null) || str_patient_id.equals(null)) {
                                //do nothing
                            } else {
                                upload_docs_interface.onCaptureNotes(CAMERA_REQUEST, DOC_TYPE_PRSEC, str_patient_table_id, str_patient_id);
                                alertDialog.dismiss();
                                alertDialog_main.dismiss();
                            }
                        }
                    }
                });
            }
        });
        //----------------------------------------upload report---------------------------------------------------------
        tv_upload_report.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (upload_docs_interface != null) {
                    if (str_patient_table_id.equals(null) || str_patient_id.equals(null)) {
                        //do nothing
                    } else {
                        upload_docs_interface.onCaptureNotes(DOC_REQUEST, DOC_TYPE_REPORT, str_patient_table_id, str_patient_id);
                        alertDialog_main.dismiss();
                    }
                }
            }
        });
    }
}
