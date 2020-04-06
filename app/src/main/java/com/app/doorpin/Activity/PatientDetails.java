package com.app.doorpin.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.app.doorpin.Adapters.DocsListAdapter;
import com.app.doorpin.Adapters.FileUploader;
import com.app.doorpin.Adapters.PatientDetails_Adapter;
import com.app.doorpin.Adapters.PrsecListAdapter;
import com.app.doorpin.Adapters.ReportListAdapter;
import com.app.doorpin.Adapters.ResizeImage_Adapter;
import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.models.Patient;
import com.app.doorpin.progress_bar.CallBackFilesAfterUpload_Interface;
import com.app.doorpin.progress_bar.CustomDialog;
import com.app.doorpin.progress_bar.ITrafficSpeedListener;
import com.app.doorpin.progress_bar.TrafficSpeedMeasurer;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.DocumentUpdate_RetroModel;
import com.app.doorpin.retrofit.IllnessDocUpload_RetroModel;
import com.app.doorpin.upload_docs.CallBack_Docs_Interface;
import com.app.doorpin.upload_docs.FileUtils;
import com.app.doorpin.upload_docs.Upload_Docs_Interface;
import com.app.doorpin.upload_docs.Utils_Files;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.doorpin.Activity.NewPatient1.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static com.app.doorpin.fragment.PatientDetails_Illness_Frag.phv_illness;

public class PatientDetails extends AppCompatActivity implements Upload_Docs_Interface, CallBackFilesAfterUpload_Interface {

    SessionManager session;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;

    Toolbar toolbar_patient_details;
    FloatingActionButton fab_illness;
    TabLayout tabPatientDetails;
    ViewPager viewPager;
    TextView tv_patientname, tv_patient_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_details);

        session = new SessionManager(PatientDetails.this);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        progressDialog = new ProgressDialog(PatientDetails.this);
        progressDialog.setMessage("Please wait.....");
        toolbar_patient_details = findViewById(R.id.toolbar_patient_details);
        //progress bar dialog-----------
        mTrafficSpeedMeasurer = new TrafficSpeedMeasurer(TrafficSpeedMeasurer.TrafficType.ALL);
        mTrafficSpeedMeasurer.startMeasuring();
        customProgressDialog = CustomDialog.getInstance();
        //-----------------------------
        tv_patientname = findViewById(R.id.tv_patientname);
        tv_patient_id = findViewById(R.id.tv_patient_id);
        //------------------------------------------------toolbar---------------------------
        setSupportActionBar(toolbar_patient_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar_patient_details.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //-----------------------------------------------tablayout---------------------------
        tabPatientDetails = findViewById(R.id.tabPatientDetails);
        viewPager = findViewById(R.id.viewPager);
        fab_illness = findViewById(R.id.fab_patient_details);

        tabPatientDetails.addTab(tabPatientDetails.newTab().setText(getResources().getString(R.string.personal_info)));
        tabPatientDetails.addTab(tabPatientDetails.newTab().setText("Illness"));
        for (int i = 0; i < tabPatientDetails.getTabCount(); i++) {
            if (i == 0) {
                View tab = ((ViewGroup) tabPatientDetails.getChildAt(0)).getChildAt(i);
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                p.setMargins(0, 0, 1, 0);
                tab.setBackgroundResource(R.drawable.tab_selector_left);
                tab.requestLayout();
            } else {
                View tab = ((ViewGroup) tabPatientDetails.getChildAt(0)).getChildAt(i);
                tab.setBackgroundResource(R.drawable.tab_selector_right);
                tab.requestLayout();
            }
        }

        tabPatientDetails.setTabGravity(TabLayout.GRAVITY_FILL);
        PatientDetails_Adapter newsEvents_adapter = new PatientDetails_Adapter(getSupportFragmentManager(),
                PatientDetails.this, tabPatientDetails.getTabCount());
        viewPager.setAdapter(newsEvents_adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabPatientDetails));
        tabPatientDetails.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (session.openDefaultTab().equals("illness_tab")) {
            tabPatientDetails.getTabAt(1).select();
            session.saveDefaultTab("personal_info");
        }
        //-----------show patient info------
        getPatientIdentity();
        //add disease
        fab_illness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNewDiease = new Intent(PatientDetails.this, AddDisease.class);
                intentNewDiease.putExtra("PATIENT_NAME", session.getPatientNameHome());
                startActivity(intentNewDiease);
                finish();

            }
        });

    }

    private void getPatientIdentity() {
        if (!session.getDisplayIdHome().equals("NA")) {
            tv_patient_id.setText("Patient Id" + " - " + session.getDisplayIdHome());
            tv_patientname.setText(session.getPatientNameHome());
        } else {
            tv_patient_id.setText("");
            tv_patientname.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public Bitmap bitmap_1;
    String selectedImagePath;
    ImageView img_prsec;
    String str_doc_type;
    boolean isKitKat;
    String realPath_1;
    String disease_id, patient_id;
    JsonArray json_docs;

    private static final int MY_CAMERA_REQUEST_CODE = 113;
    private static final int GALLERY_PICTURE = 200;
    private static int REQ_CODE = 13;
    private static final String DOC_TYPE_PRSEC = "1";
    private static final String DOC_TYPE_REPORT = "2";
    private static final String DOC_TYPE_OTHER = "3";
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    int selected_doc;
    CustomDialog customProgressDialog;
    private ArrayList<String> files_to_upload = new ArrayList<>();
    private TrafficSpeedMeasurer mTrafficSpeedMeasurer;
    String upStreamSpeed, downStreamSpeed;
    private static String ADD_PAGE = "add_page";
    private static final boolean SHOW_SPEED_IN_BITS = false;
    Context c_context;

    //calling interface for getting upload docs
    @Override
    public void onHandleSelection(Context context, int req_code, ImageView img_illness_presc, String doc_type, String diseaseId, String patientId) {
        // ... Start a new Activity here and pass the values
        this.c_context = context;
        this.img_prsec = img_illness_presc;
        this.str_doc_type = doc_type;
        this.disease_id = diseaseId;
        this.patient_id = patientId;
        json_docs = new JsonArray(); //initialize json for storing response

        if (doc_type.equals("1")) {
            if (req_code == 1) {
                REQ_CODE = req_code;
                //gallery with request code 1
                if (checkPermissionGALLERY(PatientDetails.this)) {
                    Intent pictureActionIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pictureActionIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(pictureActionIntent, req_code);
                } else {
                    //nothing
                }
            } else {
                //camera with request code 0
                REQ_CODE = req_code;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!hasPermissions(PatientDetails.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(PatientDetails.this, PERMISSIONS, MY_CAMERA_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri uri_f = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_f);
                        startActivityForResult(intent, req_code);
                    }
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, req_code);
                }
            }
        } else {
            if (checkPermissionREAD_EXTERNAL_STORAGE(PatientDetails.this)) {
                if (doc_type.equals("2")) {
                    selected_doc = 201;
                } else {
                    selected_doc = 202;
                }
                REQ_CODE = req_code;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    isKitKat = true;
                    startActivityForResult(Intent.createChooser(intent, "Select file"), req_code);
                } else {
                    isKitKat = false;
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select file"), req_code);
                }
            } else {
                Toast.makeText(PatientDetails.this, "Permission is Required to Open File Manager", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCaptureNotes(int req_code, String doc_type, String patient_table_id, String patient_id) {
        //do nothing
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
        androidx.appcompat.app.AlertDialog.Builder alertBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
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
        androidx.appcompat.app.AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        isKitKat = true;
                        startActivityForResult(Intent.createChooser(intent, "Select file"), REQ_CODE);
                    } else {
                        isKitKat = false;
                        Intent intent = new Intent();
                        intent.setType("*/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select file"), REQ_CODE);
                    }
                } else {
                    Toast.makeText(PatientDetails.this, "File Manager Permission Denied",
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
                    startActivityForResult(intent, REQ_CODE);

                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            case GALLERY_PICTURE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pictureActionIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(pictureActionIntent, REQ_CODE);
                } else {
                    Toast.makeText(this, "External Storage permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;

        if (resultCode == RESULT_OK && requestCode == 13) {

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
              //  bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
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
                String path = ResizeImage_Adapter.compressImage(PatientDetails.this,f.getAbsolutePath());
                files_to_upload.clear();
                files_to_upload.add(path);
                uploadFile(str_doc_type);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == 1) {
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
                    uploadFile(str_doc_type);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }
        //-------------select files--------------------------------
        else if (requestCode == 105) {
            if (data != null && data.getData() != null && resultCode == this.RESULT_OK) {

                Uri selectedFileURI = data.getData();
                String mediaPath = FileUtils.getPath(PatientDetails.this, selectedFileURI);
                try {
                    if (selected_doc == 201) {
                        files_to_upload.clear();
                        files_to_upload.add(mediaPath);
                        uploadFile(DOC_TYPE_REPORT);
                    } else if (selected_doc == 202) {
                        files_to_upload.clear();
                        files_to_upload.add(mediaPath);
                        uploadFile(DOC_TYPE_OTHER);
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
            customProgressDialog.showProgress(PatientDetails.this, 0, 0, downStreamSpeed, true);
            FileUploader fileUploader = new FileUploader();
            fileUploader.uploadFiles(PatientDetails.this,session.getDoctorNurseId(), "add_page", doc_type, "file", filesToUpload, new FileUploader.FileUploaderCallback() {
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
        if (Utils.CheckInternetConnection(PatientDetails.this)) {
            if (!file.equals("null") && !file.equals(null) && !file.equals("NA") && !file.isEmpty()) {
                //add data to object array
                JsonObject object = new JsonObject();
                object.addProperty("doc", file);
                json_docs.add(object);
                updateDocs(session.getDoctorNurseId(), session.getLoggedUsrId(), patient_id,
                        disease_id, str_doc_type, json_docs);
            } else {
                Toast.makeText(PatientDetails.this, "Failed to upload file, Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(PatientDetails.this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDocs(String usrType, String usrId, String patientId, String
            diseaseId, final String docsId, Object doc_data) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //patient table id is null for disease section
        DocumentUpdate_RetroModel doc_update_model = new DocumentUpdate_RetroModel(usrType, usrId, patientId, "null", diseaseId, docsId, doc_data);
        Call<DocumentUpdate_RetroModel> call_doc_update = apiInterface.updateDocs(doc_update_model);
        call_doc_update.enqueue(new Callback<DocumentUpdate_RetroModel>() {
            @Override
            public void onResponse(Call<DocumentUpdate_RetroModel> call, Response<DocumentUpdate_RetroModel> response) {
                DocumentUpdate_RetroModel responseModel = response.body();
                if (response.isSuccessful()) {
                    if (responseModel.status.equals("success")) {
                        List<DocumentUpdate_RetroModel.DocUpdate_Datum> doc_update_response = responseModel.response;
                        int count = 0;
                        String docs_id = "null", docs_link = "null";
                        for (DocumentUpdate_RetroModel.DocUpdate_Datum doc_update_data : doc_update_response) {
                            if (count == 0) {
                                docs_id = doc_update_data.doc_id;
                                docs_link = doc_update_data.doc_link;
                            }
                            count++;
                        }
                        if (!docs_id.equals("null") && !docs_id.equals(null)
                                && !docs_id.equals("NA") && !docs_id.isEmpty()) {
                            if (!docs_link.equals("null") && !docs_link.equals(null)
                                    && !docs_link.equals("NA") && !docs_link.isEmpty()) {
                                session.saveDocsData(docsId, docs_link, docs_id);
                            } else {
                                session.saveDocsData(docsId, "null", "null");
                            }
                        }
                        phv_illness.refresh();
                        Toast.makeText(PatientDetails.this, responseModel.message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PatientDetails.this, responseModel.message, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                } else {
                    if (response.code() == 400) {
                        if (!response.isSuccessful()) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response.errorBody().string());
                                String userMessage = jsonObject.getString("status");
                                String internalMessage = jsonObject.getString("message");
                                progressDialog.dismiss();
                                new android.app.AlertDialog.Builder(PatientDetails.this)
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
            public void onFailure(Call<DocumentUpdate_RetroModel> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                new AlertDialog.Builder(PatientDetails.this)
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
