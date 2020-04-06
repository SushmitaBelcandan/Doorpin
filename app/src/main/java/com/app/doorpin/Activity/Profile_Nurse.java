package com.app.doorpin.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.doorpin.Adapters.FileUploader;
import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.models.Profile_Docs_Model;
import com.app.doorpin.progress_bar.CallBackFilesAfterUpload_Interface;
import com.app.doorpin.progress_bar.CustomDialog;
import com.app.doorpin.progress_bar.ITrafficSpeedListener;
import com.app.doorpin.progress_bar.TrafficSpeedMeasurer;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.Logout_RetroModel;
import com.app.doorpin.retrofit.ProfileDocUpdate_RetroModel;
import com.app.doorpin.retrofit.ProfileDocUpload_RetroModel;
import com.app.doorpin.retrofit.ProfileImageUpload_RetroModel;
import com.app.doorpin.retrofit.UpdateProfileImg_RetroModel;
import com.app.doorpin.retrofit.UserProfile_Retro_Model;
import com.app.doorpin.upload_docs.FileUtils;
import com.app.doorpin.upload_docs.Utils_Files;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mindorks.placeholderview.PlaceHolderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import static com.app.doorpin.models.Profile_Docs_Model.arrImg_exp;
import static com.app.doorpin.models.Profile_Docs_Model.arrImg_gen;

public class Profile_Nurse extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, CallBackFilesAfterUpload_Interface {

    ApiInterface apiInterface;
    SessionManager sessionManager;
    ProgressDialog progressDialog;

    ActionBar toolBar;
    BottomNavigationView bottomNavigationView;
    ImageView iv_edit_profile;
    TextView doctorname, specialiazation, tv_mobile, tv_qualifications, tv_locations;
    ImageView iv_logout, iv_upload_doc;
    ImageView img_profile;
    PlaceHolderView phv_doc_list;

    boolean isKitKat;
    private static final String DOC_TYPE_EXP = "5";
    private static final String DOC_TYPE_GEN = "4";
    private static final int DOC_REQUEST = 1;
    private String select_doc_id;
    String realPath_1;
    String mediaPath;
    String file_1;
    String file_name;
    String strProfilePic;
    public static final int PICK_IMAGE_REQUEST = 201;
    private static final int REQUEST_WRITE_PERMISSION = 786;

    private final int COMPRESS = 100;
    private File temp_path;

    private ProgressDialog pDialog;
    CustomDialog customProgressDialog;
    ArrayList<String> files_gen = new ArrayList<>();
    ArrayList<String> files_exp = new ArrayList<>();
    private static final boolean SHOW_SPEED_IN_BITS = false;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private TrafficSpeedMeasurer mTrafficSpeedMeasurer;
    String upStreamSpeed, downStreamSpeed;
    private static String PROFILE_PAGE = "profile";
    ArrayList<String> arrList = new ArrayList<>();
    ArrayList<String> arrListDocument = new ArrayList<String>();
    private ArrayList<String> files_to_upload = new ArrayList<>();

    private String str_exp = "NA", str_gen = "NA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__nurse);
        sessionManager = new SessionManager(Profile_Nurse.this);
        progressDialog = new ProgressDialog(Profile_Nurse.this);
        progressDialog.setMessage("Please Wait...");
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        toolBar = getSupportActionBar();

        phv_doc_list = findViewById(R.id.phv_doc_list);
        tv_locations = findViewById(R.id.tv_locations);
        tv_qualifications = findViewById(R.id.tv_qualifications);
        tv_mobile = findViewById(R.id.tv_mobile);
        specialiazation = findViewById(R.id.specialiazation);
        doctorname = findViewById(R.id.doctorname);
        img_profile = findViewById(R.id.img_profile);
        iv_logout = findViewById(R.id.iv_logout);
        iv_upload_doc = findViewById(R.id.iv_upload_doc);
        iv_edit_profile = findViewById(R.id.iv_edit_profile);

        //placeholderview horizontal view-------------------------
        phv_doc_list.getBuilder().setHasFixedSize(false).setItemViewCacheSize(10)
                .setLayoutManager(new LinearLayoutManager(Profile_Nurse.this, LinearLayoutManager.HORIZONTAL, false));
        //---------------------------------------------------------


        //progress bar dialog-----------
        mTrafficSpeedMeasurer = new TrafficSpeedMeasurer(TrafficSpeedMeasurer.TrafficType.ALL);
        mTrafficSpeedMeasurer.startMeasuring();
        customProgressDialog = CustomDialog.getInstance();
        //-----------------------------

        bottomNavigationView = findViewById(R.id.btm_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.getMenu().findItem(R.id.navigation_nurse_profile).setChecked(true);//make bottom navigation active for current page
        //****************Get User Profile Information**************************
        if (Utils.CheckInternetConnection(getApplicationContext())) {
            if (!sessionManager.getDoctorNurseId().equals(null) && !sessionManager.getLoggedUsrId().equals(null)) {
                getUSerProfileInfo(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId());
            } else {
                doctorname.setText("");
                specialiazation.setText("");
                tv_mobile.setText("");
                tv_qualifications.setText("");
                tv_locations.setText("");
            }

        } else {
            doctorname.setText("");
            specialiazation.setText("");
            tv_mobile.setText("");
            tv_qualifications.setText("");
            tv_locations.setText("");
            Toast.makeText(this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        iv_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_name = doctorname.getText().toString().trim();
                String str_mobile = tv_mobile.getText().toString().trim();
                String str_education = tv_qualifications.getText().toString().trim();
                String str_specialization = specialiazation.getText().toString().trim();
                String address = tv_locations.getText().toString().trim();
                if (str_name.equals(null) || str_name.equals("null") || str_name.isEmpty()) {
                    str_name = "NA";
                }
                if (str_mobile.equals(null) || str_mobile.equals("null") || str_mobile.isEmpty()) {
                    str_mobile = "NA";
                }
                if (str_education.equals(null) || str_education.equals("null") || str_education.isEmpty()) {
                    str_education = "NA";
                }
                if (str_specialization.equals(null) || str_specialization.equals("null") || str_specialization.isEmpty()) {
                    str_specialization = "NA";
                }
                if (address.equals(null) || address.equals("null") || address.isEmpty()) {
                    address = "NA";
                }

                startActivity(new Intent(Profile_Nurse.this, EditProfile.class)
                        .putExtra("PROF", "NURSE")
                        .putExtra("NAME", str_name)
                        .putExtra("MOBILE", str_mobile)
                        .putExtra("EDUCATION", str_education)
                        .putExtra("SPECIALIZATION", str_specialization)
                        .putExtra("EXPERIENCE", "NA")
                        .putExtra("ADDRESS", address));
                finish();
            }
        });
        //***********************************Logout user************************************************************************
        //call logout
        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.getDeviceId().equals("NA")) {
                    new AlertDialog.Builder(Profile_Nurse.this)
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
                        Toast.makeText(Profile_Nurse.this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //*****************************upload docs****************************************************************************
        iv_upload_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionREAD_EXTERNAL_STORAGE(Profile_Nurse.this)) {
                    // do your stuff...
                    uploadDocs();
                } else {
                    Toast.makeText(Profile_Nurse.this, "Permission is Required to Open File Manager", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //*************************************update profile image*****************************************************
        if (sessionManager.getImageNurse().equals("null") || sessionManager.getImageNurse().equals(null)
                || sessionManager.getImageNurse().isEmpty()) {

            Glide.with(Profile_Nurse.this).load(R.drawable.person).fitCenter().dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(img_profile);
        } else {
            Glide.with(Profile_Nurse.this).load(sessionManager.getImageNurse()).fitCenter().dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(img_profile);
        }
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFile();
            }
        });


    }

    public void onClickImageOrDoc(String str_docs_link) {
        if (!str_docs_link.equals("NA") && !str_docs_link.equals("null") && !str_docs_link.equals(null) && !str_docs_link.isEmpty()) {

            String extension = str_docs_link.substring(0, str_docs_link.lastIndexOf(".") + 1);
            String file_extension = str_docs_link.replace(extension, "");
            if (file_extension.toLowerCase().equals("jpg") || file_extension.toLowerCase().equals("png")
                    || file_extension.toLowerCase().equals("bmp") || file_extension.toLowerCase().equals("svg")
                    || file_extension.toLowerCase().equals("jpeg")) {
                Intent intentDocDetails = new Intent(Profile_Nurse.this, Document_Details_Act.class);

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Profile_Nurse.this);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(arrList);
                editor.putString("DOC", json);
                editor.commit();
                intentDocDetails.putExtra("FLAG_FILE", "PROFILE_D");
                intentDocDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentDocDetails);
                finish();
                arrList.clear();
            } else if (file_extension.toLowerCase().equals("pdf")) {
                Intent intentDocDetails = new Intent(Profile_Nurse.this, Sample_Pdf_View_Act.class);
                intentDocDetails.putExtra("FILE", str_docs_link);
                intentDocDetails.putExtra("FLAG", "PROFILE_D");
                intentDocDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentDocDetails);
                finish();
            } else if (file_extension.toLowerCase().equals("doc") || file_extension.toLowerCase().equals("docx")) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(str_docs_link);
                intent.setDataAndType(uri, "*/*");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                MimeTypeMap myMime = MimeTypeMap.getSingleton();
                Intent newIntent = new Intent(Intent.ACTION_VIEW);
                String mimeType = myMime.getMimeTypeFromExtension(fileExt(str_docs_link));
                File file = new File(str_docs_link);
                newIntent.setDataAndType(Uri.fromFile(file), mimeType);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(newIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(Profile_Nurse.this, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(Profile_Nurse.this, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if (data != null && data.getData() != null && resultCode == this.RESULT_OK) {

                Uri selectedFileURI = data.getData();
                String mediaPath = FileUtils.getPath(Profile_Nurse.this, selectedFileURI);

                try {

                    if (select_doc_id == DOC_TYPE_GEN) {
                        files_to_upload.clear();
                        files_to_upload.add(mediaPath);
                        uploadFile(DOC_TYPE_GEN);
                    } else if (select_doc_id == DOC_TYPE_EXP) {
                        files_to_upload.clear();
                        files_to_upload.add(mediaPath);
                        uploadFile(DOC_TYPE_EXP);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "You haven't picked document", Toast.LENGTH_LONG).show();
        }
        //upload profile image
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            if (Build.VERSION.SDK_INT < 19) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();


                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(picturePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);


                InputStream imInputStream = null;
                try {
                    imInputStream = getContentResolver().openInputStream(data.getData());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Bitmap bitmap = BitmapFactory.decodeStream(imInputStream);

                Bitmap rot_bitmap = rotateImage(bitmap, orientation);

                Bitmap bp_resized = resize(rot_bitmap, 200, 200);

                String smallImagePath = saveGalaryImageOnLitkat(bp_resized);


                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bp_resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //Glide.with(getActivity()).load(stream.toByteArray()).asBitmap().into(ivProfilePic);
                if (Utils.CheckInternetConnection(Profile_Nurse.this)) {
                    imagePath(smallImagePath);
                } else {
                    Toast.makeText(Profile_Nurse.this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                }


            } else {

                InputStream in = null;
                ExifInterface exifInterface = null;
                try {
                    in = getContentResolver().openInputStream(data.getData());
                    exifInterface = new ExifInterface(in);
                    // Now you can extract any Exif tag you want
                    // Assuming the image is a JPEG or supported raw format
                } catch (IOException e) {
                    // Handle any errors
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ignored) {
                        }
                    }
                }

                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);


                InputStream imInputStream = null;
                try {
                    imInputStream = getContentResolver().openInputStream(data.getData());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Bitmap bitmap = BitmapFactory.decodeStream(imInputStream);

                Bitmap rot_bitmap = rotateImage(bitmap, orientation);

                Bitmap bp_resized = resize(rot_bitmap, 200, 200);

                String smallImagePath = saveGalaryImageOnLitkat(bp_resized);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bp_resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //Glide.with(getActivity()).load(stream.toByteArray()).asBitmap().into(ivProfilePic);
                if (Utils.CheckInternetConnection(Profile_Nurse.this)) {
                    imagePath(smallImagePath);
                } else {
                    Toast.makeText(Profile_Nurse.this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void updateDocs(String usrType, String usrId, String docId, Object fileName) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ProfileDocUpdate_RetroModel doc_update_model = new ProfileDocUpdate_RetroModel(usrType, usrId, docId, fileName);
        Call<ProfileDocUpdate_RetroModel> call_doc_update = apiInterface.updateProfileDocs(doc_update_model);
        call_doc_update.enqueue(new Callback<ProfileDocUpdate_RetroModel>() {
            @Override
            public void onResponse(Call<ProfileDocUpdate_RetroModel> call, Response<ProfileDocUpdate_RetroModel> response) {
                ProfileDocUpdate_RetroModel responseModel = response.body();
                if (response.isSuccessful()) {
                    if (responseModel.status.equals("success")) {
                        int count = 0;
                        List<ProfileDocUpdate_RetroModel.Response_Datum> response_list = responseModel.response;
                        for (ProfileDocUpdate_RetroModel.Response_Datum response_data : response_list) {
                            if (count == 0) {
                                if (!response_data.doc_link.equals("null") && !response_data.doc_link.equals("NA")
                                        && !response_data.doc_link.equals(null) && !response_data.doc_link.isEmpty()) {
                                    phv_doc_list.addView(new Profile_Docs_Model(Profile_Nurse.this,
                                            responseModel.document_id, response_data.doc_link));
                                }
                            }
                            count++;
                        }
                        Toast.makeText(Profile_Nurse.this, responseModel.message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Profile_Nurse.this, responseModel.message, Toast.LENGTH_SHORT).show();
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
                                new android.app.AlertDialog.Builder(Profile_Nurse.this)
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
            public void onFailure(Call<ProfileDocUpdate_RetroModel> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                new AlertDialog.Builder(Profile_Nurse.this)
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


    private void getUSerProfileInfo(String str_user_type, String str_usr_id) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final UserProfile_Retro_Model user_profile_info_model = new UserProfile_Retro_Model(str_user_type, str_usr_id);
        Call<UserProfile_Retro_Model> call_user_profile_info = apiInterface.getProfileData(user_profile_info_model);
        call_user_profile_info.enqueue(new Callback<UserProfile_Retro_Model>() {
            @Override
            public void onResponse(Call<UserProfile_Retro_Model> call, Response<UserProfile_Retro_Model> response) {
                UserProfile_Retro_Model profileInfoRequest = response.body();
                if (response.isSuccessful()) {
                    if (profileInfoRequest.status.equals("success")) {

                        String userID = profileInfoRequest.user_id;
                        String userName = profileInfoRequest.user_name;
                        String designation = profileInfoRequest.designation;
                        String expYears = profileInfoRequest.experience_years;
                        String mobile = profileInfoRequest.mobile;
                        String qualifications = profileInfoRequest.qualifications;
                        String address = profileInfoRequest.address;
                        String profile_pic = profileInfoRequest.profile_pic;
                        //-----------------------------------------------------------------PROFILE PICTURE------------------------------
                        if (!profile_pic.equals("null") && !profile_pic.equals(null) && !profile_pic.equals("NA") && !profile_pic.isEmpty()) {
                            Glide.with(getApplicationContext()).load(profile_pic).into(img_profile);
                        } else {
                            Glide.with(getApplicationContext()).load(R.drawable.person).into(img_profile);
                        }
                        //-----------------------------------------------------------------USER NAME------------------------------
                        if (!userName.equals("null") && !userName.equals(null) && !userName.equals("NA") && !userName.isEmpty()) {
                            doctorname.setText(userName);
                        } else {
                            doctorname.setText("");
                        }
                        //-----------------------------------------------------------------DESIGNATION------------------------------
                        if (!designation.equals("null") && !designation.equals(null) && !designation.equals("NA") && !designation.isEmpty()) {
                            specialiazation.setText(designation);
                        } else {
                            specialiazation.setText("");
                        }
                        //-----------------------------------------------------------------MOBILE------------------------------
                        if (!mobile.equals("null") && !mobile.equals(null) && !mobile.equals("NA") && !mobile.isEmpty()) {
                            tv_mobile.setText(mobile);
                        } else {
                            tv_mobile.setText("");
                        }
                        //-----------------------------------------------------------------QUALIFICATIONS------------------------------
                        if (!qualifications.equals("null") && !qualifications.equals(null) && !qualifications.equals("NA") && !qualifications.isEmpty()) {
                            tv_qualifications.setText(qualifications);
                        } else {
                            tv_qualifications.setText("");
                        }
                        //-----------------------------------------------------------------ADDRESS------------------------------
                        if (!address.equals("null") && !address.equals(null) && !address.equals("NA") && !address.isEmpty()) {
                            tv_locations.setText(address);
                        } else {
                            tv_locations.setText("");
                        }
                        arrImg_gen.clear();
                        arrImg_exp.clear();
                        //------------------------------------------------GENUNITY DOCUMENT------------------------------------------------
                        List<UserProfile_Retro_Model.GenunityDoc_Datum> gen_list_datum = profileInfoRequest.genunity_doc;
                        if (gen_list_datum.size() <= 0) {
                            //when genunity array is empty
                            progressDialog.dismiss();
                        } else {
                            for (UserProfile_Retro_Model.GenunityDoc_Datum gen_list_data : gen_list_datum) {
                                if (!gen_list_data.doc_gen.equals(null) && !gen_list_data.doc_gen.equals("null") &&
                                        !gen_list_data.doc_gen.isEmpty()) {
                                    arrListDocument.add(gen_list_data.doc_gen);
                                    phv_doc_list.addView(new Profile_Docs_Model(Profile_Nurse.this, "4", gen_list_data.doc_gen));
                                } else {
                                    //skip rows
                                }
                            }
                            progressDialog.dismiss();

                        }
                        //------------------------------------------------EXPERIENCE DOCUMENT------------------------------------------------
                        List<UserProfile_Retro_Model.ExperienceDoc_Datum> exp_list_datum = profileInfoRequest.experience_doc;
                        if (exp_list_datum.size() <= 0) {
                            //when experience array is empty
                            progressDialog.dismiss();
                        } else {
                            for (UserProfile_Retro_Model.ExperienceDoc_Datum exp_list_data : exp_list_datum) {
                                if (!exp_list_data.doc_exp.equals(null) && !exp_list_data.doc_exp.equals("null") &&
                                        !exp_list_data.doc_exp.isEmpty()) {
                                    arrListDocument.add(exp_list_data.doc_exp);
                                    phv_doc_list.addView(new Profile_Docs_Model(Profile_Nurse.this, "5", exp_list_data.doc_exp));
                                } else {
                                    //skip rows
                                }
                            }
                            progressDialog.dismiss();

                        }
                        progressDialog.dismiss();
                    } else {
                        //do not show data
                        doctorname.setText("");
                        specialiazation.setText("");
                        tv_mobile.setText("");
                        tv_qualifications.setText("");
                        tv_locations.setText("");
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
                                doctorname.setText("");
                                specialiazation.setText("");
                                tv_mobile.setText("");
                                tv_qualifications.setText("");
                                tv_locations.setText("");
                                progressDialog.dismiss();
                                new android.app.AlertDialog.Builder(Profile_Nurse.this)
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
            public void onFailure(Call<UserProfile_Retro_Model> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                new AlertDialog.Builder(Profile_Nurse.this)
                        .setMessage("Server Error! Please try again later")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                doctorname.setText("");
                                specialiazation.setText("");
                                tv_mobile.setText("");
                                tv_qualifications.setText("");
                                tv_locations.setText("");
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
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
                        Toast.makeText(Profile_Nurse.this, logout_resources.message1, Toast.LENGTH_SHORT).show();
                        Intent intentLogin = new Intent(Profile_Nurse.this, MainActivity.class);
                        startActivity(intentLogin);
                        sessionManager.logoutUser();
                        finish();
                    } else {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(Profile_Nurse.this)
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
                    if (response.code() == 400) {
                        if (!response.isSuccessful()) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response.errorBody().string());
                                String userMessage = jsonObject.getString("status");
                                String internalMessage = jsonObject.getString("message");
                                progressDialog.dismiss();
                                new AlertDialog.Builder(Profile_Nurse.this)
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
                new AlertDialog.Builder(Profile_Nurse.this)
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.navigation_nurse_patients:
                startActivity(new Intent(Profile_Nurse.this, HomePage_Nurse.class));
                finish();
                break;

            case R.id.navigation_nurse_profile:
                startActivity(new Intent(Profile_Nurse.this, Profile_Nurse.class));
                finish();
                break;
        }

        return true;
    }

    //user profile image upload
    //profile image upload methods
    public void pickFile() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_PERMISSION);

            return;

        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    private String saveGalaryImageOnLitkat(Bitmap bitmap) {
        try {
            File cacheDir;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                cacheDir = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.app_name));
            else
                cacheDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (!cacheDir.exists())
                cacheDir.mkdirs();
            String filename = System.currentTimeMillis() + ".jpg";
            File file = new File(cacheDir, filename);
            temp_path = file.getAbsoluteFile();
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS, out);


            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private void imagePath(String imagepath) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File file0 = new File(imagepath);
        RequestBody requestFile0 = RequestBody.create(MediaType.parse("image"), file0);
        MultipartBody.Part img1 = MultipartBody.Part.createFormData("uploaded_file", file0.getName(),
                requestFile0);

        Call<ProfileImageUpload_RetroModel> call = apiInterface.uploadProfileImage(img1);
        call.enqueue(new Callback<ProfileImageUpload_RetroModel>() {
            @Override
            public void onResponse(@NonNull Call<ProfileImageUpload_RetroModel> call, @NonNull Response<ProfileImageUpload_RetroModel> response) {
                ProfileImageUpload_RetroModel responseModel = response.body();
                if (response.isSuccessful()) {
                    if (responseModel.status.equals("success")) {
                        List<ProfileImageUpload_RetroModel.ImgUploadDatum> imgUploadRes = responseModel.result;
                        for (ProfileImageUpload_RetroModel.ImgUploadDatum imgData : imgUploadRes) {
                            strProfilePic = imgData.image;
                        }
                        if (!strProfilePic.equals(null) && !strProfilePic.equals("null") && !strProfilePic.isEmpty() && !strProfilePic.equals("NA")) {
                            if (Utils.CheckInternetConnection(getApplicationContext())) {
                                profileImageUpdate(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), strProfilePic);
                            } else {
                                Toast.makeText(getApplicationContext(), "No Internet. Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), responseModel.message, Toast.LENGTH_SHORT).show();
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
                                new android.app.AlertDialog.Builder(Profile_Nurse.this)
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
            public void onFailure(@NonNull Call<ProfileImageUpload_RetroModel> call, @NonNull Throwable t) {
                call.cancel();
                progressDialog.dismiss();
            }
        });
    }

    private void profileImageUpdate(String usrType, String usrId, String fileName) {
        final UpdateProfileImg_RetroModel navEditImage = new UpdateProfileImg_RetroModel(usrType, usrId, fileName);

        Call<UpdateProfileImg_RetroModel> call = apiInterface.updateProfileImg(navEditImage);
        call.enqueue(new Callback<UpdateProfileImg_RetroModel>() {
            @Override
            public void onResponse(Call<UpdateProfileImg_RetroModel> call, Response<UpdateProfileImg_RetroModel> response) {
                UpdateProfileImg_RetroModel responsedata = response.body();
                if (response.isSuccessful()) {
                    if (responsedata.status.equals("success")) {
                        Toast.makeText(getApplicationContext(), responsedata.message, Toast.LENGTH_SHORT).show();
                        if (!responsedata.file.equals(null) && !responsedata.file.equals("null") && !responsedata.file.equals("NA") && !responsedata.file.isEmpty()) {
                            Glide.with(Profile_Nurse.this).load(responsedata.file).fitCenter().dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(img_profile);

                            sessionManager.saveImageNurse(responsedata.file);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), responsedata.message, Toast.LENGTH_SHORT).show();
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
                                new android.app.AlertDialog.Builder(Profile_Nurse.this)
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
            public void onFailure(Call<UpdateProfileImg_RetroModel> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
            }
        });
    }

    //=================progress bar==========================

    private void uploadDocs() {
        LayoutInflater layoutInflater = LayoutInflater.from(Profile_Nurse.this);
        android.view.View promptView = layoutInflater.inflate(R.layout.userprofile_popup_dcos, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile_Nurse.this, R.style.AlertDialogStyle);
        alertDialogBuilder.setView(promptView);

        TextView tv_upload_genunity = (TextView) promptView.findViewById(R.id.tv_upload_genunity);
        TextView tv_upload_exp = (TextView) promptView.findViewById(R.id.tv_upload_exp);

        final AlertDialog alertDialog_main = alertDialogBuilder.create();
        alertDialog_main.show();
        alertDialog_main.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tv_upload_genunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_doc_id = DOC_TYPE_GEN;
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
        tv_upload_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_doc_id = DOC_TYPE_EXP;
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
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

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
            case REQUEST_WRITE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();//for profile image upload
                }
                break;
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                    if (select_doc_id == DOC_TYPE_GEN) {
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
                    Toast.makeText(Profile_Nurse.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    public void uploadFile(String doc_type) {
        File[] filesToUpload = new File[files_to_upload.size()];
        for (int i = 0; i < files_to_upload.size(); i++) {
            filesToUpload[i] = new File(files_to_upload.get(i));
        }
        if (files_to_upload.size() > 0) {
            customProgressDialog.showProgress(Profile_Nurse.this, 0, 0, downStreamSpeed, true);
            FileUploader fileUploader = new FileUploader();
            fileUploader.uploadFiles(Profile_Nurse.this, sessionManager.getDoctorNurseId(), "profile", doc_type, "file", filesToUpload, new FileUploader.FileUploaderCallback() {
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
        //for profie image
        if (sessionManager.getImageNurse().equals("null") || sessionManager.getImageNurse().equals(null)
                || sessionManager.getImageNurse().isEmpty()) {

            Glide.with(Profile_Nurse.this).load(R.drawable.person).fitCenter().dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(img_profile);
        } else {
            Glide.with(Profile_Nurse.this).load(sessionManager.getImageNurse()).fitCenter().dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(img_profile);
        }
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

    JsonArray jsnArrDatas;

    @Override
    public void getFile(String file, String file_type) {
        if (!file.equals("null") && !file.equals(null) && !file.equals("NA") && !file.isEmpty()) {
            if (Utils.CheckInternetConnection(Profile_Nurse.this)) {
                jsnArrDatas = new JsonArray();
                JsonObject object = new JsonObject();
                object.addProperty("doc", file);
                jsnArrDatas.add(object);
                updateDocs(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), file_type, jsnArrDatas);
            } else {
                Toast.makeText(Profile_Nurse.this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Profile_Nurse.this, "Failed to upload file, Please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
