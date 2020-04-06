package com.app.doorpin.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.app.doorpin.Adapters.ReportListAdapter;
import com.app.doorpin.Adapters.ResizeImage_Adapter;
import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.fragment.PatientDetails_Illness_Frag;
import com.app.doorpin.progress_bar.CallBackFilesAfterUpload_Interface;
import com.app.doorpin.progress_bar.CustomDialog;
import com.app.doorpin.progress_bar.ITrafficSpeedListener;
import com.app.doorpin.progress_bar.TrafficSpeedMeasurer;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.IllnessDocUpload_RetroModel;
import com.app.doorpin.retrofit.NewDisease_RetroModel;
import com.app.doorpin.retrofit.New_Patient_Retro_Model;
import com.app.doorpin.upload_docs.FileUtils;
import com.app.doorpin.upload_docs.Utils_Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.doorpin.Activity.NewPatient1.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;

public class AddDisease extends AppCompatActivity implements View.OnClickListener, CallBackFilesAfterUpload_Interface {

    SessionManager sessionManager;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;

    Toolbar toolbar_add_disease;

    Button btn_add;
    LinearLayout llcapturePrescription, lluploadDocs;
    EditText et_follow_update;
    EditText et_disease_type_add_ill, et_name_newp;
    ImageView ivCalender;

    public Calendar refCalendar = Calendar.getInstance();
    String strStoreDate;

    ArrayList<String> arrList_docs;
    ArrayList<String> arrList_report;
    ArrayList<String> arrayList_Img;
    JsonArray jsn_arr_report;
    JsonArray jsn_arr_other_doc;
    JsonArray jsn_arr_prsec;
    private String str_name, str_disease_type, str_followup_date;
    private static final int GALLERY_PICTURE = 1;
    private static final int CAMERA_REQUEST = 11;
    private static final int DOC_REQUEST = 105;
    private static final String DOC_TYPE_PRSEC = "1";
    private static final String DOC_TYPE_REPORT = "2";
    private static final String DOC_TYPE_OTHERS = "3";
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    private int selected_doc;
    boolean isKitKat;
    RecyclerView rv_docs, rv_img, rv_report;
    private String str_patient_name;

    CustomDialog customProgressDialog;
    private static final boolean SHOW_SPEED_IN_BITS = false;
    private static final int MY_CAMERA_REQUEST_CODE = 111;
    private TrafficSpeedMeasurer mTrafficSpeedMeasurer;
    String upStreamSpeed, downStreamSpeed;
    private ArrayList<String> files_to_upload = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_disease_act);

        sessionManager = new SessionManager(AddDisease.this);
        progressDialog = new ProgressDialog(AddDisease.this);
        progressDialog.setMessage("Please Wait...");

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        //progress bar dialog-----------
        mTrafficSpeedMeasurer = new TrafficSpeedMeasurer(TrafficSpeedMeasurer.TrafficType.ALL);
        mTrafficSpeedMeasurer.startMeasuring();
        customProgressDialog = CustomDialog.getInstance();
        //-----------------------------
        arrList_docs = new ArrayList<>();
        arrList_report = new ArrayList<>();
        arrayList_Img = new ArrayList<>();

        Intent oIntent = getIntent();//calling from patient details
        str_patient_name = oIntent.getExtras().getString("PATIENT_NAME");

        toolbar_add_disease = findViewById(R.id.toolbar_add_disease);
        et_name_newp = findViewById(R.id.et_name_addp_ill);
        et_disease_type_add_ill = findViewById(R.id.et_disease_type_add_ill);
        et_follow_update = findViewById(R.id.et_follow_update);
        rv_report = findViewById(R.id.rv_report);
        rv_docs = findViewById(R.id.rv_docs);
        rv_img = findViewById(R.id.rv_img);
        ivCalender = findViewById(R.id.ivCalender);

        //toolbar
        setSupportActionBar(toolbar_add_disease);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar_add_disease.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        llcapturePrescription = findViewById(R.id.ll_capture_prescription);
        lluploadDocs = findViewById(R.id.ll_upload_doc);

        if (!str_patient_name.equals(null) && !str_patient_name.isEmpty()) {
            et_name_newp.setText(str_patient_name);
        }
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        llcapturePrescription.setOnClickListener(this);
        lluploadDocs.setOnClickListener(this);

        //select followupdate by populating calender
        selectDate();


    }

    private boolean validateForm(String str_name, String str_type_disease, String str_followup_date) {
        //new patient name
        if (str_name.equals("null") || str_name.equals("NA") || str_name.equals(null) || str_name.isEmpty()) {
            et_name_newp.setError("Please enter Patient Name");
            et_name_newp.requestFocus();
            return false;
        }
        //new patient's type of disease
        if (str_type_disease.equals("null") || str_type_disease.equals(null) || str_type_disease.equals("NA") || str_type_disease.isEmpty()) {
            et_disease_type_add_ill.setError("Please enter Disease Name");
            et_disease_type_add_ill.requestFocus();
            return false;
        }
        //follow up date
        if (str_followup_date.equals("null") || str_followup_date.equals("NA") || str_followup_date.equals(null) || str_followup_date.isEmpty()) {
            et_follow_update.setError("Please enter Follow up Date");
            et_follow_update.requestFocus();
            return false;
        }
        return true;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_add:

                str_name = et_name_newp.getText().toString().trim();
                str_disease_type = et_disease_type_add_ill.getText().toString().trim();
                str_followup_date = et_follow_update.getText().toString().trim();

                if (validateForm(str_name, str_disease_type, str_followup_date)) {
                    //remove error mark when no fields are empty
                    et_name_newp.setError(null);
                    et_disease_type_add_ill.setError(null);
                    et_follow_update.setError(null);
                    jsn_arr_prsec = new JsonArray();
                    jsn_arr_report = new JsonArray();
                    jsn_arr_other_doc = new JsonArray();

                    for (int i = 0; i < arrayList_Img.size(); i++) {
                        if (!arrayList_Img.get(i).equals("")) {
                            JsonObject object = new JsonObject();
                            object.addProperty("presc_doc", arrayList_Img.get(i));
                            jsn_arr_prsec.add(object);
                        }
                    }
                    for (int i = 0; i < arrList_docs.size(); i++) {
                        if (!arrList_docs.get(i).equals("")) {
                            JsonObject object = new JsonObject();
                            object.addProperty("other_doc", arrList_docs.get(i));
                            jsn_arr_other_doc.add(object);
                        }
                    }
                    for (int i = 0; i < arrList_report.size(); i++) {
                        if (!arrList_report.get(i).equals("")) {
                            JsonObject object = new JsonObject();
                            object.addProperty("report_doc", arrList_report.get(i));
                            jsn_arr_report.add(object);
                        }
                    }
                    if (jsn_arr_report.size() <= 0 && arrList_report.size() <= 0 && jsn_arr_prsec.size() <= 0 && arrayList_Img.size() <= 0) {
                        addDisease(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), sessionManager.getPatientIdHome(),
                                str_disease_type, sessionManager.getLoggedUsrId(), str_followup_date, "null",
                                "null", jsn_arr_other_doc);

                    } else if (jsn_arr_report.size() <= 0 && arrList_report.size() <= 0 && jsn_arr_other_doc.size() <= 0 && arrList_docs.size() <= 0) {
                        addDisease(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), sessionManager.getPatientIdHome(),
                                str_disease_type, sessionManager.getLoggedUsrId(), str_followup_date, jsn_arr_prsec, "null", "null");

                    } else if (jsn_arr_prsec.size() <= 0 && arrayList_Img.size() <= 0 && jsn_arr_other_doc.size() <= 0 && arrList_docs.size() <= 0) {
                        addDisease(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), sessionManager.getPatientIdHome(),
                                str_disease_type, sessionManager.getLoggedUsrId(), str_followup_date, "null", jsn_arr_report, "null");

                    } else if (jsn_arr_prsec.size() <= 0 && arrayList_Img.size() <= 0) {
                        addDisease(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), sessionManager.getPatientIdHome(),
                                str_disease_type, sessionManager.getLoggedUsrId(), str_followup_date, "null", jsn_arr_report, jsn_arr_other_doc);
                    } else if (jsn_arr_report.size() <= 0 && arrList_report.size() <= 0) {
                        addDisease(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), sessionManager.getPatientIdHome(),
                                str_disease_type, sessionManager.getLoggedUsrId(), str_followup_date, jsn_arr_prsec, "null", jsn_arr_other_doc);

                    } else if (jsn_arr_other_doc.size() <= 0 && arrList_docs.size() <= 0) {
                        addDisease(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), sessionManager.getPatientIdHome(),
                                str_disease_type, sessionManager.getLoggedUsrId(), str_followup_date, jsn_arr_prsec, jsn_arr_report, "null");

                    } else if (jsn_arr_other_doc.size() <= 0 && arrList_docs.size() <= 0 && jsn_arr_prsec.size() <= 0 && arrayList_Img.size() <= 0 &&
                            jsn_arr_report.size() <= 0 && arrList_report.size() <= 0) {
                        addDisease(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), sessionManager.getPatientIdHome(),
                                str_disease_type, sessionManager.getLoggedUsrId(), str_followup_date, "null", "null", "null");

                    } else {
                        addDisease(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), sessionManager.getPatientIdHome(),
                                str_disease_type, sessionManager.getLoggedUsrId(), str_followup_date, jsn_arr_prsec, jsn_arr_report, jsn_arr_other_doc);

                    }
                    Toast.makeText(AddDisease.this, "Data added Successfully", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.ll_capture_prescription:
                captureImageEvent();
                break;

            case R.id.ll_upload_doc:
                if (checkPermissionREAD_EXTERNAL_STORAGE(AddDisease.this)) {
                    // do your stuff...
                    uploadDocs();
                } else {
                    Toast.makeText(AddDisease.this, "Permission is Required to Open File Manager", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    //calender use for dob and follow update
    private void selectDate() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                refCalendar.set(Calendar.YEAR, year);
                refCalendar.set(Calendar.MONTH, monthOfYear);
                refCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //set selcted date
                String dateFormat = "dd/MM/yyyy";//In which you need put here
                SimpleDateFormat sdfDate = new SimpleDateFormat(dateFormat, Locale.US);
                if (!sdfDate.equals("null")) {
                    strStoreDate = sdfDate.format(refCalendar.getTime());
                    if (!strStoreDate.equals("null") && !strStoreDate.equals(null)) {
                        et_follow_update.setText(strStoreDate);
                    } else {
                        et_follow_update.setText("");
                    }
                } else {
                    strStoreDate = "null";
                }
                // setUserId();
            }

        };

        //calender select--------------------------------------------------------------------
        et_follow_update.setFocusable(false);
        ivCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog dpd = new DatePickerDialog(AddDisease.this, R.style.MyThemeOverlay, date, refCalendar
                        .get(Calendar.YEAR), refCalendar.get(Calendar.MONTH), refCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMinDate(System.currentTimeMillis()); //make future date disable
                dpd.show();
                dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // do something onCancel
                        strStoreDate = "null";
                        et_follow_update.setText("");
                    }
                });

            }
        });

    }

    private void captureImageEvent() {
        LayoutInflater layoutInflater = LayoutInflater.from(AddDisease.this);
        android.view.View promptView = layoutInflater.inflate(R.layout.choose_img_popup, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(AddDisease.this, R.style.AlertDialogStyle);
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
                if (checkPermissionGALLERY(AddDisease.this)) {
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
                    if (!hasPermissions(AddDisease.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(AddDisease.this, PERMISSIONS, MY_CAMERA_REQUEST_CODE);
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
        LayoutInflater layoutInflater = LayoutInflater.from(AddDisease.this);
        android.view.View promptView = layoutInflater.inflate(R.layout.docs_popup, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(AddDisease.this, R.style.AlertDialogStyle);
        alertDialogBuilder.setView(promptView);

        TextView tv_upload_report = (TextView) promptView.findViewById(R.id.tv_upload_report);
        TextView tv_upload_other_doc = (TextView) promptView.findViewById(R.id.tv_upload_other_doc);

        final android.app.AlertDialog alertDialog_main = alertDialogBuilder.create();
        alertDialog_main.show();
        alertDialog_main.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tv_upload_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_doc = 201;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    isKitKat = true;
                    startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                    alertDialog_main.dismiss();
                } else {
                    isKitKat = false;
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                    alertDialog_main.dismiss();
                }
            }
        });
        tv_upload_other_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_doc = 202;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    isKitKat = true;
                    startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                    alertDialog_main.dismiss();
                } else {
                    isKitKat = false;
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select file"), DOC_REQUEST);
                    alertDialog_main.dismiss();
                }
            }
        });

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
                    Toast.makeText(AddDisease.this, "GET_ACCOUNTS Denied",
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
                String path = ResizeImage_Adapter.compressImage(AddDisease.this, f.getAbsolutePath());
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

            if (data != null && data.getData() != null && resultCode == RESULT_OK) {

                Uri selectedFileURI = data.getData();
                String mediaPath = FileUtils.getPath(AddDisease.this, selectedFileURI);
                try {
                    if (selected_doc == 201) {
                        files_to_upload.clear();
                        files_to_upload.add(mediaPath);
                        uploadFile(DOC_TYPE_REPORT);
                        // uploadFile(mediaPath, DOC_TYPE_REPORT);
                    } else if (selected_doc == 202) {
                        files_to_upload.clear();
                        files_to_upload.add(mediaPath);
                        uploadFile(DOC_TYPE_OTHERS);
                        // uploadFile(mediaPath, DOC_TYPE_OTHERS);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "You haven't picked Image/Video", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentPatientDetails = new Intent(AddDisease.this, PatientDetails.class);
        startActivity(intentPatientDetails);
        finish();
    }

    //add disease
    private void addDisease(String str_usr_type, String str_usr_id, String patient_id, String str_disease_names,
                            String str_doctor_id, String str_follow_up_date, Object obj_prsec,
                            Object obj_report, Object obj_others) {

        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //*****************date time format conversion********************
        Date localTime_fd = null;
        try {
            localTime_fd = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(str_follow_up_date);//convert from
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        if (Utils.CheckInternetConnection(AddDisease.this)) {
            SimpleDateFormat sdf_fd = new SimpleDateFormat("yyyy-MM-dd");//convert in
            String fd_format = sdf_fd.format(new Date(localTime_fd.getTime()));
            //***********************************************************************
            NewDisease_RetroModel model_disease = new NewDisease_RetroModel(str_usr_type, str_usr_id, patient_id, str_disease_names, str_doctor_id,
                    fd_format, obj_prsec, obj_report, obj_others);
            Call<NewDisease_RetroModel> call_disease = apiInterface.addDiease(model_disease);
            call_disease.enqueue(new Callback<NewDisease_RetroModel>() {
                @Override
                public void onResponse(Call<NewDisease_RetroModel> call, Response<NewDisease_RetroModel> response) {
                    NewDisease_RetroModel new_patient_resources = response.body();
                    if (response.isSuccessful()) {
                        if (new_patient_resources.status.equals("success")) {
                            if (sessionManager.getDoctorNurseId().equals("1")) {
                                Toast.makeText(AddDisease.this, new_patient_resources.message, Toast.LENGTH_SHORT).show();
                                Intent intentDoctorHomePage = new Intent(AddDisease.this, PatientDetails.class);
                                startActivity(intentDoctorHomePage);
                                sessionManager.saveDefaultTab("illness_tab");
                                finish();
                            } else {
                                Toast.makeText(AddDisease.this, new_patient_resources.message, Toast.LENGTH_SHORT).show();
                                Intent intentNurseHomePage = new Intent(AddDisease.this, PatientDetails.class);
                                startActivity(intentNurseHomePage);
                                sessionManager.saveDefaultTab("illness_tab");
                                finish();

                            }
                            progressDialog.dismiss();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(AddDisease.this, new_patient_resources.message, Toast.LENGTH_SHORT).show();
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
                                    new android.app.AlertDialog.Builder(AddDisease.this)
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
                public void onFailure(Call<NewDisease_RetroModel> call, Throwable t) {
                    call.cancel();
                    progressDialog.dismiss();
                    new android.app.AlertDialog.Builder(AddDisease.this)
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
            Toast.makeText(AddDisease.this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    //=====================================progress bar=============================================

    public void uploadFile(String doc_type) {
        File[] filesToUpload = new File[files_to_upload.size()];
        for (int i = 0; i < files_to_upload.size(); i++) {
            filesToUpload[i] = new File(files_to_upload.get(i));
        }
        if (files_to_upload.size() > 0) {
            customProgressDialog.showProgress(AddDisease.this, 0, 0, downStreamSpeed, true);
            FileUploader fileUploader = new FileUploader();
            fileUploader.uploadFiles(AddDisease.this, sessionManager.getDoctorNurseId(), "add_page", doc_type, "file", filesToUpload, new FileUploader.FileUploaderCallback() {
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
            if (file_type.equals("1")) {
                arrayList_Img.add(file);
            } else if (file_type.equals("2")) {
                arrList_report.add(file);
            } else {
                arrList_docs.add(file);
            }
            //---------other docs adapter--------------------
            DocsListAdapter adapter_docs = new DocsListAdapter(getApplicationContext(), arrList_docs);
            rv_docs.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//must write for display data in recycler view
            rv_docs.setAdapter(adapter_docs);
            //-----------report adapter------------------
            ReportListAdapter adapter_report = new ReportListAdapter(getApplicationContext(), arrList_report);
            rv_report.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//it call viewholder and bind the data
            rv_report.setAdapter(adapter_report);
            //----------prsecription adapter-----------------
            PrsecListAdapter adapter_prsec = new PrsecListAdapter(getApplicationContext(), arrayList_Img);
            rv_img.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//must write for display data in recycler view
            rv_img.setAdapter(adapter_prsec);

        } else {
            Toast.makeText(AddDisease.this, "Failed to upload file, Please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
