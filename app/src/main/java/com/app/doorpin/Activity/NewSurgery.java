package com.app.doorpin.Activity;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.Adapters.DocsListAdapter;
import com.app.doorpin.Adapters.FileUploader;
import com.app.doorpin.Adapters.PrsecListAdapter;
import com.app.doorpin.Adapters.ResizeImage_Adapter;
import com.app.doorpin.Adapters.SearchPatientAdapter;
import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.progress_bar.CallBackFilesAfterUpload_Interface;
import com.app.doorpin.progress_bar.CustomDialog;
import com.app.doorpin.progress_bar.ITrafficSpeedListener;
import com.app.doorpin.progress_bar.TrafficSpeedMeasurer;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.NewSurgSearch_RetroModel;
import com.app.doorpin.retrofit.NewSurgery_RetroModel;
import com.app.doorpin.retrofit.SearchPatient_RetroModel;
import com.app.doorpin.retrofit.SurgList_DP_RetroModel;
import com.app.doorpin.upload_docs.FileUtils;
import com.app.doorpin.upload_docs.Utils_Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static com.app.doorpin.Activity.NewPatient1.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;

public class NewSurgery extends AppCompatActivity implements CallBackFilesAfterUpload_Interface {

    ApiInterface apiInterface;
    SessionManager sessionManager;
    ProgressDialog progressDialog;

    Toolbar toolbar_new_surgery;
    Spinner spnr_surgery_name;
    RecyclerView rv_searchlist;
    RecyclerView rv_img, rv_docs;
    EditText etSearch;
    ImageView iv_uploadImg, iv_uploadDocs;
    LinearLayout llBtnSearch;
    Button btn_add;

    ArrayList<String> arrList_surgery_name;
    ArrayList<String> arr_list_surg_id;
    ArrayList<String> arrlist_patient_id;
    ArrayList<String> arrlist_patient_name;
    ArrayList<String> arrlist_display_id;
    private String strSurgeryName;
    private int surgery_id = 0;
    private String str_surgery_id = "NA";
    private String str_search_key;
    private String str_selected_patient = "NA";

    ArrayList<String> arrList_docs;
    ArrayList<String> arrayList_Img;
    JsonArray jsn_arr_other_doc;
    JsonArray jsn_arr_prsec;
    private String str_name, str_disease_type, str_followup_date;
    private static final int GALLERY_PICTURE = 1;
    private static final int CAMERA_REQUEST = 14;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 300;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 400;
    private static final int DOC_REQUEST = 105;
    private static final String DOC_TYPE_PRSEC = "7";
    private static final String DOC_TYPE_OTHERS = "6";
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    private ArrayList<String> files_to_upload = new ArrayList<>();

    private int selected_doc;
    boolean isKitKat;

    CustomDialog customProgressDialog;
    ArrayList<String> files_prsec = new ArrayList<>();
    ArrayList<String> files_other_doc = new ArrayList<>();
    private static final boolean SHOW_SPEED_IN_BITS = false;
    private static final int MY_CAMERA_REQUEST_CODE = 114;
    private TrafficSpeedMeasurer mTrafficSpeedMeasurer;
    String upStreamSpeed, downStreamSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_surgery);

        sessionManager = new SessionManager(NewSurgery.this);
        progressDialog = new ProgressDialog(NewSurgery.this);
        progressDialog.setMessage("Please Wait...");
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        //progress bar dialog-----------
        mTrafficSpeedMeasurer = new TrafficSpeedMeasurer(TrafficSpeedMeasurer.TrafficType.ALL);
        mTrafficSpeedMeasurer.startMeasuring();
        customProgressDialog = CustomDialog.getInstance();
        //------------------------------------------------------
        arrList_docs = new ArrayList<>();
        arrayList_Img = new ArrayList<>();


        toolbar_new_surgery = findViewById(R.id.toolbar_new_surgery);
        spnr_surgery_name = findViewById(R.id.spnr_surgery_name);
        etSearch = findViewById(R.id.etSearch);
        llBtnSearch = findViewById(R.id.llBtnSearch);
        rv_searchlist = findViewById(R.id.rv_searchlist);
        btn_add = findViewById(R.id.btn_add);
        iv_uploadDocs = findViewById(R.id.iv_uploadDocs);
        iv_uploadImg = findViewById(R.id.iv_uploadImg);
        rv_docs = findViewById(R.id.rv_docs);
        rv_img = findViewById(R.id.rv_img);
        //toolbar
        setSupportActionBar(toolbar_new_surgery);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar_new_surgery.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //surgery list into spinner
        if (!sessionManager.getHospiatlId().equals("NA")) {
            if (Utils.CheckInternetConnection(getApplicationContext())) {
                getSurgeryList(sessionManager.getHospiatlId());
            } else {
                Toast.makeText(this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
            }

        } else {
            //do nothing
        }
        //call search
        llBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.CheckInternetConnection(getApplicationContext())) {

                    str_search_key = etSearch.getText().toString().trim();
                    if (str_search_key.equals(null) || str_search_key.isEmpty()) {
                        Toast.makeText(NewSurgery.this, "Not a Valid Keyword", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean digitsOnly = TextUtils.isDigitsOnly(str_search_key);
                        if (digitsOnly == true) {
                            searchPatient(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_search_key, "NA");
                        } else {
                            searchPatient(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), "NA", str_search_key);

                        }
                    }

                } else {
                    Toast.makeText(NewSurgery.this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //========================================================upload documents==================================================
        iv_uploadDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionREAD_EXTERNAL_STORAGE(NewSurgery.this)) {
                    // do your stuff...
                    uploadDocs();
                } else {
                    Toast.makeText(NewSurgery.this, "Permission is Required to Open File Manager", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //======================================================================upload image========================================
        iv_uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImageEvent();
            }
        });
        //=======================================================================add action event===================================
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm(str_selected_patient, String.valueOf(surgery_id))) {
                    if (!sessionManager.getDoctorNurseId().equals(null) && !sessionManager.getDoctorNurseId().equals("NA")
                            && !sessionManager.getDoctorNurseId().equals("null") && !sessionManager.getLoggedUsrId().equals("null")
                            && !sessionManager.getLoggedUsrId().equals("NA") && !sessionManager.getLoggedUsrId().equals(null) &&
                            !sessionManager.getLoggedUsrId().isEmpty()) {

                        jsn_arr_prsec = new JsonArray();
                        jsn_arr_other_doc = new JsonArray();

                        for (int i = 0; i < arrayList_Img.size(); i++) {
                            if (!arrayList_Img.get(i).equals("")) {
                                JsonObject object = new JsonObject();
                                object.addProperty("file", arrayList_Img.get(i));
                                jsn_arr_prsec.add(object);
                            }
                        }
                        for (int i = 0; i < arrList_docs.size(); i++) {
                            if (!arrList_docs.get(i).equals("")) {
                                JsonObject object = new JsonObject();
                                object.addProperty("doc", arrList_docs.get(i));
                                jsn_arr_other_doc.add(object);
                            }
                        }


                        if (jsn_arr_prsec.size() <= 0 && arrayList_Img.size() <= 0) {
                            addNewSurgery(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_selected_patient, str_surgery_id,
                                    jsn_arr_other_doc, "null");


                        } else if (jsn_arr_other_doc.size() <= 0 && arrList_docs.size() <= 0) {
                            addNewSurgery(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_selected_patient, str_surgery_id,
                                    "null", jsn_arr_prsec);

                        } else if (jsn_arr_prsec.size() <= 0 && arrayList_Img.size() <= 0 && jsn_arr_other_doc.size() <= 0 && arrList_docs.size() <= 0) {
                            addNewSurgery(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), str_selected_patient, str_surgery_id,
                                    "null", "null");

                        } else {
                            addNewSurgery(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(),
                                    str_selected_patient, str_surgery_id,
                                    jsn_arr_other_doc, jsn_arr_prsec);

                        }
                    }
                } else {
                    //do nothing
                }
            }
        });

    }

    private boolean validateForm(String patient_id, String surgery_id) {
        if (patient_id.equals("NA")) {
            new AlertDialog.Builder(NewSurgery.this)
                    .setMessage("Please select Patient")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return false;
        }
        if (surgery_id.equals("0")) {
            new AlertDialog.Builder(NewSurgery.this)
                    .setMessage("Please select Surgery Name")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return false;
        }
        return true;
    }

    private void getSurgeryList(String str_hospital_id) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final SurgList_DP_RetroModel surg_list_model = new SurgList_DP_RetroModel(str_hospital_id);
        Call<SurgList_DP_RetroModel> call_surg_list = apiInterface.getDocProfileSurgeryList(surg_list_model);
        call_surg_list.enqueue(new Callback<SurgList_DP_RetroModel>() {
            @Override
            public void onResponse(Call<SurgList_DP_RetroModel> call, Response<SurgList_DP_RetroModel> response) {
                SurgList_DP_RetroModel surgListRequest = response.body();
                if (response.isSuccessful()) {
                    arr_list_surg_id = new ArrayList<String>();
                    arrList_surgery_name = new ArrayList<>();
                    arrList_surgery_name.add("Select Surgery Name");
                    arr_list_surg_id.add("0");
                    if (surgListRequest.status.equals("success")) {

                        List<SurgList_DP_RetroModel.SurgList_DP_Datum> surg_list_datum = surgListRequest.result;
                        if (surg_list_datum.size() <= 0) {
                            //when response array is empty
                            progressDialog.dismiss();
                            Toast.makeText(NewSurgery.this, surgListRequest.message, Toast.LENGTH_SHORT).show();
                        } else {
                            for (SurgList_DP_RetroModel.SurgList_DP_Datum surg_list_data : surg_list_datum) {
                                arrList_surgery_name.add(surg_list_data.surgery_name);
                                arr_list_surg_id.add(surg_list_data.surgery_id);
                            }
                            progressDialog.dismiss();

                        }
                        //add item whether list is empty or not
                        final ArrayAdapter<String> surgeryStatusAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrList_surgery_name);
                        surgeryStatusAdapter.setDropDownViewResource(R.layout.new_surgery_dropdown);
                        spnr_surgery_name.setAdapter(surgeryStatusAdapter);
                        spnr_surgery_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                ((TextView) view).setTextColor(getResources().getColor(R.color.heading_purple)); //Change selected text color
                                ((TextView) view).setTextAppearance(getApplicationContext(), R.style.surgery_dropdown);
                                strSurgeryName = spnr_surgery_name.getSelectedItem().toString(); //selected string
                                surgery_id = surgeryStatusAdapter.getPosition(strSurgeryName);//get selected doctor id
                                Log.d("---index id------", "" + String.valueOf(surgery_id));
                                str_surgery_id = arr_list_surg_id.get(surgery_id);
                                Log.d("---surgery id------", "" + String.valueOf(str_surgery_id));

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                //do nothing
                            }
                        });
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
                                new android.app.AlertDialog.Builder(NewSurgery.this)
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
            public void onFailure(Call<SurgList_DP_RetroModel> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
            }
        });
    }

    private void searchPatient(String usr_type, String usr_id, String patient_id, String str_search_data) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        NewSurgSearch_RetroModel search_patient_model = new NewSurgSearch_RetroModel(usr_type, usr_id, patient_id, str_search_data);
        Call<NewSurgSearch_RetroModel> search_patient_call = apiInterface.getListSurgPatient(search_patient_model);
        search_patient_call.enqueue(new Callback<NewSurgSearch_RetroModel>() {
            @Override
            public void onResponse(Call<NewSurgSearch_RetroModel> call, Response<NewSurgSearch_RetroModel> response) {
                NewSurgSearch_RetroModel search_resources = response.body();
                if (response.isSuccessful()) {
                    rv_searchlist.removeAllViews();
                    if (search_resources.status.equals("success")) {
                        List<NewSurgSearch_RetroModel.SurgSearch_Datum> datumList = search_resources.result;
                        if (datumList.size() <= 0) {
                            progressDialog.dismiss();
                        } else {
                            arrlist_patient_id = new ArrayList<>();
                            arrlist_patient_name = new ArrayList<>();
                            arrlist_display_id = new ArrayList<>();
                            for (NewSurgSearch_RetroModel.SurgSearch_Datum datum : datumList) {
                                if (datum.patient_id.equals(null) || datum.patient_id.isEmpty() || datum.patient_id.equals("NA")) {
                                    //skip row for null value
                                } else {
                                    arrlist_patient_id.add(datum.patient_id);
                                    arrlist_patient_name.add(datum.patient_name);
                                    arrlist_display_id.add(datum.display_id);
                                }

                            }
                            SearchPatientAdapter adapter_search = new SearchPatientAdapter(getApplicationContext(), rv_searchlist,
                                    arrlist_patient_name,
                                    arrlist_display_id,
                                    arrlist_patient_id, new SearchPatientAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(String str_patient_id, String str_patient_name) {
                                    etSearch.setText(str_patient_name);
                                    str_selected_patient = str_patient_id;
                                }
                            });
                            rv_searchlist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//must write for display data in recycler view
                            rv_searchlist.setAdapter(adapter_search);
                            progressDialog.dismiss();
                            Toast.makeText(NewSurgery.this, search_resources.message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(NewSurgery.this, search_resources.message, Toast.LENGTH_SHORT).show();
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
                                new android.app.AlertDialog.Builder(NewSurgery.this)
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
            public void onFailure(Call<NewSurgSearch_RetroModel> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
            }
        });
    }

    //add disease
    private void addNewSurgery(String str_usr_type, String str_usr_id, String str_patient_id, String str_surg_id,
                               Object arr_doc, Object arr_files) {

        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Utils.CheckInternetConnection(NewSurgery.this)) {

            NewSurgery_RetroModel model_surgery = new NewSurgery_RetroModel(str_usr_type, str_usr_id, str_patient_id, str_surg_id,
                    arr_doc, arr_files);
            Call<NewSurgery_RetroModel> call_surgery = apiInterface.addSurgery(model_surgery);
            call_surgery.enqueue(new Callback<NewSurgery_RetroModel>() {
                @Override
                public void onResponse(Call<NewSurgery_RetroModel> call, Response<NewSurgery_RetroModel> response) {
                    NewSurgery_RetroModel new_surg_resources = response.body();
                    if (response.isSuccessful()) {
                        if (new_surg_resources.status.equals("success")) {
                            if (sessionManager.getDoctorNurseId().equals("1")) {
                                Toast.makeText(NewSurgery.this, new_surg_resources.message, Toast.LENGTH_SHORT).show();
                                Intent intentMySurgery = new Intent(NewSurgery.this, MySurgeries.class);
                                startActivity(intentMySurgery);
                                finish();
                            } else {
                                Toast.makeText(NewSurgery.this, new_surg_resources.message, Toast.LENGTH_SHORT).show();
                                Intent intentMySurgery = new Intent(NewSurgery.this, MySurgeries.class);
                                startActivity(intentMySurgery);
                                finish();

                            }
                            progressDialog.dismiss();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(NewSurgery.this, new_surg_resources.message, Toast.LENGTH_SHORT).show();
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
                                    new android.app.AlertDialog.Builder(NewSurgery.this)
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
                public void onFailure(Call<NewSurgery_RetroModel> call, Throwable t) {
                    call.cancel();
                    progressDialog.dismiss();
                    new android.app.AlertDialog.Builder(NewSurgery.this)
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
        } else {
            Toast.makeText(NewSurgery.this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentPatientDetails = new Intent(NewSurgery.this, MySurgeries.class);
        startActivity(intentPatientDetails);
        finish();
    }

    //select images and files
    private void captureImageEvent() {
        LayoutInflater layoutInflater = LayoutInflater.from(NewSurgery.this);
        android.view.View promptView = layoutInflater.inflate(R.layout.choose_img_popup, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(NewSurgery.this, R.style.AlertDialogStyle);
        alertDialogBuilder.setView(promptView);

        Button btn_gallery = promptView.findViewById(R.id.btn_gallery);
        Button btn_camera = promptView.findViewById(R.id.btn_camera);

        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //mae alert bg transparent for custom rounded corner
        //------------------------------select gallery----------------------------------------------------------------
        btn_gallery.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (checkPermissionGALLERY(NewSurgery.this)) {
                    Intent pictureActionIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pictureActionIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
                    alertDialog.dismiss();
                } else {
                    //nothing
                }
                alertDialog.dismiss();
            }
        });

        //----------------------------capture image from camera--------------------------------------------------
        btn_camera.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //as for the declaration camera in manifest need to ask camera permission in activity
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    /*if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                        alertDialog.dismiss();
                    }*/
                    if (!hasPermissions(NewSurgery.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(NewSurgery.this, PERMISSIONS, MY_CAMERA_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri uri_f = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_f);
                        startActivityForResult(intent, CAMERA_REQUEST);
                        alertDialog.dismiss();
                    }
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, CAMERA_REQUEST);
                    alertDialog.dismiss();
                }
                alertDialog.dismiss();
            }
        });
    }


    private void uploadDocs() {
        selected_doc = 201;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            isKitKat = true;
            startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
        } else {
            isKitKat = false;
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
        }

    }

    //for accessing the external storage permission is required
    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    //for accessing the gallery external storage permission is required
    public boolean checkPermissionGALLERY(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat.requestPermissions(
                            (Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PICTURE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    //Camera multiple permissions
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                    if (selected_doc == 201) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("*/*");
                            isKitKat = true;
                            startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                        } else {
                            isKitKat = false;
                            Intent intent = new Intent();
                            intent.setType("*/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("*/*");
                            isKitKat = true;
                            startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                        } else {
                            isKitKat = false;
                            Intent intent = new Intent();
                            intent.setType("*/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                        }
                    }
                } else {
                    Toast.makeText(NewSurgery.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case MY_CAMERA_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri uri_f = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_f);
                    startActivityForResult(intent, CAMERA_REQUEST);

                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            case GALLERY_PICTURE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pictureActionIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
                } else {
                    Toast.makeText(this, "External Storage permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
    //-------------------------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {

            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }
            if (!f.exists()) {
                Toast.makeText(getBaseContext(), "Error while capturing image", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                //bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
                int rotate = 0;
                try {
                    ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                String path = ResizeImage_Adapter.compressImage(NewSurgery.this,f.getAbsolutePath());
                files_to_upload.clear();
                files_to_upload.add(path);
                uploadFile(DOC_TYPE_PRSEC);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                assert selectedImage != null;
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                assert c != null;
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String selectedImagePath = c.getString(columnIndex);
                c.close();

                if (selectedImagePath != null) {
                    files_to_upload.clear();
                    files_to_upload.add(selectedImagePath);
                    uploadFile(DOC_TYPE_PRSEC);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 105) {

            if (data != null && data.getData() != null && resultCode == this.RESULT_OK) {

                Uri selectedFileURI = data.getData();
                String mediaPath = FileUtils.getPath(NewSurgery.this, selectedFileURI);

                try {
                    if (selected_doc == 201) {
                        files_to_upload.clear();
                        files_to_upload.add(mediaPath);
                        uploadFile(DOC_TYPE_OTHERS);
                    } else {
                        //do nothing
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "You haven't picked Image/Video", Toast.LENGTH_LONG).show();
        }
    }
    //=====================================progress bar=============================================

    public void uploadFile(String doc_type) {
        File[] filesToUpload = new File[files_to_upload.size()];
        for (int i = 0; i < files_to_upload.size(); i++) {
            filesToUpload[i] = new File(files_to_upload.get(i));
        }
        if (files_to_upload.size() > 0) {
            customProgressDialog.showProgress(NewSurgery.this, 0, 0, downStreamSpeed, true);
            FileUploader fileUploader = new FileUploader();
            fileUploader.uploadFiles(NewSurgery.this,sessionManager.getDoctorNurseId(), "add_page", doc_type, "file", filesToUpload, new FileUploader.FileUploaderCallback() {
                @Override
                public void onError() {
                    customProgressDialog.hideProgress();
                }

                @Override
                public void onFinish(String[] responses) {
                    customProgressDialog.hideProgress();
                    for (int i = 0; i < responses.length; i++) {
                        String str = responses[i];
                        Log.e("RESPONSE " + i, "" + responses[i]);
                    }
                }

                @Override
                public void onProgressUpdate(int currentpercent, int totalpercent, int filenumber, int file_size) {
                    customProgressDialog.updateProgress(currentpercent, file_size, downStreamSpeed);
                    //     Log.e("---------Progress Status", currentpercent + " " + totalpercent + " " + filenumber + "......" + file_size);
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTrafficSpeedMeasurer.stopMeasuring();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTrafficSpeedMeasurer.registerListener(mStreamSpeedListener);
    }

    private ITrafficSpeedListener mStreamSpeedListener = new ITrafficSpeedListener() {

        @Override
        public void onTrafficSpeedMeasured(final double upStream, final double downStream) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    upStreamSpeed = Utils.parseSpeed(upStream, SHOW_SPEED_IN_BITS);
                    downStreamSpeed = Utils.parseSpeed(downStream, SHOW_SPEED_IN_BITS);

                }
            });
        }
    };

    @Override
    public void getFile(String file, String file_type) {
        if (!file.equals("null") && !file.equals(null) && !file.equals("NA") && !file.isEmpty()) {
            if (file_type.equals("7")) {
                arrayList_Img.add(file);
            } else {
                arrList_docs.add(file);
            }
            //---------other docs adapter--------------------
            DocsListAdapter adapter_docs = new DocsListAdapter(getApplicationContext(), arrList_docs);
            rv_docs.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//must write for display data in recycler view
            rv_docs.setAdapter(adapter_docs);
            //----------prsecription adapter-----------------
            PrsecListAdapter adapter_prsec = new PrsecListAdapter(getApplicationContext(), arrayList_Img);
            rv_img.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//must write for display data in recycler view
            rv_img.setAdapter(adapter_prsec);

        } else {
            Toast.makeText(NewSurgery.this, "Failed to upload file, Please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
