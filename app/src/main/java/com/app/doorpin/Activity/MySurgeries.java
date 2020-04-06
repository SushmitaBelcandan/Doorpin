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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.Adapters.FileUploader;
import com.app.doorpin.Adapters.ResizeImage_Adapter;
import com.app.doorpin.Adapters.SurgeryAdapter;
import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.models.Suregery_Header_Model;
import com.app.doorpin.models.Surgery;
import com.app.doorpin.models.Surgery_FeedItem_Model;
import com.app.doorpin.progress_bar.CallBackFilesAfterUpload_Interface;
import com.app.doorpin.progress_bar.CustomDialog;
import com.app.doorpin.progress_bar.ITrafficSpeedListener;
import com.app.doorpin.progress_bar.TrafficSpeedMeasurer;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.DocumentUpdate_RetroModel;
import com.app.doorpin.retrofit.IllnessDocUpload_RetroModel;
import com.app.doorpin.retrofit.LoginRequest;
import com.app.doorpin.retrofit.Logout_RetroModel;
import com.app.doorpin.retrofit.SurgeryList_RetroModel;
import com.app.doorpin.upload_docs.FileUtils;
import com.app.doorpin.upload_docs.Upload_Docs_Interface;
import com.app.doorpin.upload_docs.Utils_Files;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;
import com.mindorks.placeholderview.PlaceHolderView;

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

import static android.view.View.GONE;
import static com.app.doorpin.Activity.NewPatient1.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;

public class MySurgeries extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        Upload_Docs_Interface, CallBackFilesAfterUpload_Interface {

    SessionManager sessionManager;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;

    ActionBar toolBar;
    BottomNavigationView bottomNavigationView;
    ExpandablePlaceHolderView phv_surgeries;
    FloatingActionButton fab_surgeries;
    LinearLayout llNoData;
    ImageView imgBtnLogout;

    ArrayList<String> arr_list_patient_name;
    ArrayList<String> arr_list_patient_id;
    ArrayList<String> arr_list_patient_del_id;
    Object list_docs = null;
    Object list_files = null;
    private TrafficSpeedMeasurer mTrafficSpeedMeasurer;
    CustomDialog customProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_surgeries);
        sessionManager = new SessionManager(MySurgeries.this);
        progressDialog = new ProgressDialog(MySurgeries.this);
        progressDialog.setMessage("Please Wait...");

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        //progress bar dialog-----------
        mTrafficSpeedMeasurer = new TrafficSpeedMeasurer(TrafficSpeedMeasurer.TrafficType.ALL);
        mTrafficSpeedMeasurer.startMeasuring();
        customProgressDialog = CustomDialog.getInstance();
        //-----------------------------
        toolBar = getSupportActionBar();
        bottomNavigationView = findViewById(R.id.btm_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.getMenu().findItem(R.id.navigation_doctor_surgeries).setChecked(true);//make bottom navigation active for current page

        phv_surgeries = findViewById(R.id.phv_surgeries);
        fab_surgeries = findViewById(R.id.fab_surgeries);
        llNoData = findViewById(R.id.llNoData);
        imgBtnLogout = findViewById(R.id.imgBtnLogout);
        if (Utils.CheckInternetConnection(MySurgeries.this)) {
            if (!sessionManager.getDoctorNurseId().equals(null)) {
                if (!sessionManager.getLoggedUsrId().equals(null) && !sessionManager.getLoggedUsrId().equals("null")
                        && !sessionManager.getLoggedUsrId().isEmpty() && !sessionManager.getLoggedUsrId().equals("NA")) {

                    if (!sessionManager.getHospiatlId().equals(null) && !sessionManager.getHospiatlId().equals("null")
                            && !sessionManager.getHospiatlId().isEmpty() && !sessionManager.getHospiatlId().equals("NA")) {

                        getMySurgeryList(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), sessionManager.getHospiatlId());
                    }
                }
            }
        } else {
            Toast.makeText(MySurgeries.this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        //floating button action for new surgeries
        fab_surgeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNewSurgeries = new Intent(MySurgeries.this, NewSurgery.class);
                startActivity(intentNewSurgeries);
            }
        });
        //call logout
        imgBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.getDeviceId().equals("NA")) {
                    new AlertDialog.Builder(MySurgeries.this)
                            .setMessage("Please Try Again")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    if (Utils.CheckInternetConnection(getApplicationContext())) {
                        logoutUser(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), sessionManager.getDeviceId());
                    } else {
                        Toast.makeText(MySurgeries.this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private void logoutUser(String str_usr_type, String str_usr_id, String str_device_id) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logout_RetroModel logout_model = new Logout_RetroModel(str_usr_type, str_usr_id, str_device_id);
        Call<Logout_RetroModel> call_logout = apiInterface.logoutUser(logout_model);
        call_logout.enqueue(new Callback<Logout_RetroModel>() {
            @Override
            public void onResponse(Call<Logout_RetroModel> call, Response<Logout_RetroModel> response) {
                Logout_RetroModel logout_resources = response.body();
                if (response.isSuccessful()) {
                    if (logout_resources.status1.equals("success")) {
                        progressDialog.dismiss();
                        Toast.makeText(MySurgeries.this, logout_resources.message1, Toast.LENGTH_SHORT).show();
                        Intent intentLogin = new Intent(MySurgeries.this, MainActivity.class);
                        startActivity(intentLogin);
                        sessionManager.logoutUser();
                        finish();
                    } else {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(MySurgeries.this)
                                .setMessage("Please Try Again")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                } else {
                    //response id getting failed
                    if (response.code() == 400) {
                        if (!response.isSuccessful()) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response.errorBody().string());
                                String userMessage = jsonObject.getString("status");
                                String internalMessage = jsonObject.getString("message");
                                progressDialog.dismiss();
                                new AlertDialog.Builder(MySurgeries.this)
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
            public void onFailure(Call<Logout_RetroModel> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                new AlertDialog.Builder(MySurgeries.this)
                        .setMessage("Please Try Again")
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

    private void getMySurgeryList(String user_type, String user_id, String hospital_id) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final SurgeryList_RetroModel surgery_list_model = new SurgeryList_RetroModel(user_type, user_id, hospital_id);
        Call<SurgeryList_RetroModel> call_surgery_list = apiInterface.getSurgeryList(surgery_list_model);
        call_surgery_list.enqueue(new Callback<SurgeryList_RetroModel>() {
            @Override
            public void onResponse(Call<SurgeryList_RetroModel> call, Response<SurgeryList_RetroModel> response) {
                SurgeryList_RetroModel surgery_list_resources = response.body();
                if (response.isSuccessful()) {
                    if (surgery_list_resources.status.equals("success")) {
                        List<SurgeryList_RetroModel.ResultDatum> result_list = surgery_list_resources.result;
                        if (result_list.size() > 0) {
                            llNoData.setVisibility(GONE);
                            phv_surgeries.setVisibility(View.VISIBLE);
                            for (SurgeryList_RetroModel.ResultDatum result_data : result_list) {

                                arr_list_patient_name = new ArrayList<>();
                                arr_list_patient_id = new ArrayList<>();
                                arr_list_patient_del_id = new ArrayList<>();
                                phv_surgeries.addView(new Suregery_Header_Model(MySurgeries.this, result_data.surgery_id, result_data.surgery_name));
                                List<SurgeryList_RetroModel.PatientDatum> patient_list = result_data.patient_data;
                                List<SurgeryList_RetroModel.PatientDatum> patient_data = new ArrayList<>();
                                for (int i = 0; i < patient_list.size(); i++) {
                                    patient_data.add(patient_list.get(i));
                                }
                                phv_surgeries.addView(new Surgery_FeedItem_Model(MySurgeries.this, patient_data, result_data.surgery_id));
                                progressDialog.dismiss();

                            }


                        } else {
                            progressDialog.dismiss();
                            llNoData.setVisibility(View.VISIBLE);
                            phv_surgeries.setVisibility(GONE);

                        }

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MySurgeries.this, surgery_list_resources.message, Toast.LENGTH_SHORT).show();
                        llNoData.setVisibility(View.VISIBLE);
                        phv_surgeries.setVisibility(GONE);

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
                                llNoData.setVisibility(View.VISIBLE);
                                phv_surgeries.setVisibility(GONE);
                                new android.app.AlertDialog.Builder(MySurgeries.this)
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
            public void onFailure(Call<SurgeryList_RetroModel> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                new AlertDialog.Builder(MySurgeries.this)
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.navigation_doctor_patients:
                startActivity(new Intent(MySurgeries.this, HomePage_Doctor.class));
                finish();
                break;

            case R.id.navigation_doctor_surgeries:
                startActivity(new Intent(MySurgeries.this, MySurgeries.class));
                finish();
                break;

            case R.id.navigation_doctor_profile:
                startActivity(new Intent(MySurgeries.this, Profile_Doctor.class));
                finish();
                break;
        }


        return true;
    }

    //--------------callback capture image----------------------------------------------------------
    public Bitmap bitmap_1;
    String selectedImagePath;
    String str_doc_type;
    boolean isKitKat;
    String realPath_1;
    String patient_tbl_id, patient_id;
    JsonArray json_docs;

    private static int REQ_CODE = 12;
    private static final String DOC_TYPE_PRSEC = "7";
    private static final String DOC_TYPE_REPORT = "6";
    private static final int MY_CAMERA_REQUEST_CODE = 112;
    private static final int GALLERY_PICTURE = 200;
    private ArrayList<String> files_to_upload = new ArrayList<>();
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    int selected_doc;
    ArrayList<String> files_prsec = new ArrayList<>();
    ArrayList<String> files_report = new ArrayList<>();
    String upStreamSpeed, downStreamSpeed;
    private static String ADD_PAGE = "add_page";
    private static final boolean SHOW_SPEED_IN_BITS = false;

    @Override
    public void onHandleSelection(Context context, int req_code, ImageView img_illness_presc, String doc_type, String diseaseId, String patientId) {
        // ... Start a new Activity here and pass the values
    }

    @Override
    public void onCaptureNotes(int req_code, String doc_type, String patient_table_id, String patient_id) {
        this.str_doc_type = doc_type;
        this.patient_tbl_id = patient_table_id;
        this.patient_id = patient_id;
        json_docs = new JsonArray(); //initialize json for storing response

        if (doc_type.equals("7")) {
            if (req_code == 1) {
                REQ_CODE = req_code;
                //gallery with request code 1
                if (checkPermissionGALLERY(MySurgeries.this)) {
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
                    if (!hasPermissions(MySurgeries.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(MySurgeries.this, PERMISSIONS, MY_CAMERA_REQUEST_CODE);
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
            if (checkPermissionREAD_EXTERNAL_STORAGE(MySurgeries.this)) {
                selected_doc = 201;
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
                Toast.makeText(MySurgeries.this, "Permission is Required to Open File Manager", Toast.LENGTH_SHORT).show();
            }
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
                    Toast.makeText(MySurgeries.this, "GET_ACCOUNTS Denied",
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

        if (resultCode == RESULT_OK && requestCode == 12) {

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
               // bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
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
                String path = ResizeImage_Adapter.compressImage(MySurgeries.this,f.getAbsolutePath());
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
                String mediaPath = FileUtils.getPath(MySurgeries.this, selectedFileURI);

                try {
                    if (selected_doc == 201) {
                        files_to_upload.clear();
                        files_to_upload.add(mediaPath);
                        uploadFile(DOC_TYPE_REPORT);
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
            customProgressDialog.showProgress(MySurgeries.this, 0, 0, downStreamSpeed, true);
            FileUploader fileUploader = new FileUploader();
            fileUploader.uploadFiles(MySurgeries.this, sessionManager.getDoctorNurseId(),"add_page", doc_type, "file", filesToUpload, new FileUploader.FileUploaderCallback() {
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
        if (Utils.CheckInternetConnection(MySurgeries.this)) {
            if (!file.equals("null") && !file.equals(null) && !file.equals("NA") && !file.isEmpty()) {
                //add data to object array
                JsonObject object = new JsonObject();
                object.addProperty("doc", file);
                json_docs.add(object);
                updateDocs(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), patient_id, patient_tbl_id,
                        "null", str_doc_type, json_docs);
            } else {
                Toast.makeText(MySurgeries.this, "Failed to upload file, Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MySurgeries.this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDocs(String usrType, String usrId, String patientId, String patientTblId, String
            diseaseId, final String docsId, Object doc_data) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //patient table id is null for disease section
        DocumentUpdate_RetroModel doc_update_model = new DocumentUpdate_RetroModel(usrType, usrId, patientId, patientTblId, diseaseId, docsId, doc_data);
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
                                sessionManager.saveDocsData(docsId, docs_link, docs_id);
                            } else {
                                sessionManager.saveDocsData(docsId, "null", "null");
                            }
                        }
                        Toast.makeText(MySurgeries.this, responseModel.message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MySurgeries.this, responseModel.message, Toast.LENGTH_SHORT).show();
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
                                new android.app.AlertDialog.Builder(MySurgeries.this)
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
                new AlertDialog.Builder(MySurgeries.this)
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
