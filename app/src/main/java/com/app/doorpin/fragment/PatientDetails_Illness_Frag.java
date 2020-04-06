package com.app.doorpin.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.Activity.HomePage_Doctor;
import com.app.doorpin.Activity.PatientDetails;
import com.app.doorpin.Activity.Profile_Nurse;
import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.models.Illness;
import com.app.doorpin.models.Illness_List_Model;
import com.app.doorpin.models.SetDateonCalendar;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.DocumentUpdate_RetroModel;
import com.app.doorpin.retrofit.IllnessDocUpload_RetroModel;
import com.app.doorpin.retrofit.Illness_List_Retro_Model;
import com.app.doorpin.upload_docs.Upload_Docs_Interface;
import com.app.doorpin.upload_docs.Utils_Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

import static android.app.Activity.RESULT_OK;

public class PatientDetails_Illness_Frag extends Fragment{

    ApiInterface apiInterface;
    SessionManager sessionManager;
    ProgressDialog progressDialog;

   public static PlaceHolderView phv_illness;
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
                                Object list_prsec_link = illness_data.presc_data;
                                Object list_report_link = illness_data.report_data;
                                Object list_other_docs_link = illness_data.otherdoc_data;

                                phv_illness.addView(new Illness_List_Model(getActivity(),phv_illness, str_illness_id, str_illness_name, str_followup_date,
                                        list_prsec_link, list_report_link, list_other_docs_link, str_patient_ids));
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
                    if (response.code() == 400) {
                        if (!response.isSuccessful()) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response.errorBody().string());
                                String userMessage = jsonObject.getString("status");
                                String internalMessage = jsonObject.getString("message");
                                llNoData1.setVisibility(View.VISIBLE);
                                phv_illness.setVisibility(View.GONE);
                                progressDialog.dismiss();
                                new AlertDialog.Builder(getActivity())
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
            public void onFailure(Call<Illness_List_Retro_Model> call, Throwable t) {
                call.cancel();
                llNoData1.setVisibility(View.GONE);
                phv_illness.setVisibility(View.GONE);
                progressDialog.dismiss();
            }
        });
    }
//
//    //==========================================================================================================================================
//    public Bitmap bitmap_1;
//    String selectedImagePath;
//    ImageView img_prsec;
//    String str_doc_type;
//    boolean isKitKat;
//    String realPath_1;
//    String disease_id, patient_id;
//    JsonArray json_docs;
//
//
//    //calling interface for getting upload docs
//    @Override
//    public void onHandleSelection(int req_code, ImageView img_illness_presc, String doc_type, String diseaseId, String patientId) {
//        // ... Start a new Activity here and pass the values
//        this.img_prsec = img_illness_presc;
//        this.str_doc_type = doc_type;
//        this.disease_id = diseaseId;
//        this.patient_id = patientId;
//        if (doc_type.equals("1")) {
//            if (req_code == 1) {
//                //gallery with request code 1
//                Intent pictureActionIntent = null;
//                pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(pictureActionIntent, req_code);
//            } else {
//                //camera with request code 0
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//                startActivityForResult(intent, req_code);
//            }
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("*/*");
//                isKitKat = true;
//                startActivityForResult(Intent.createChooser(intent, "Select file"), req_code);
//            } else {
//                isKitKat = false;
//                Intent intent = new Intent();
//                intent.setType("*/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select file"), req_code);
//            }
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//
//        bitmap_1 = null;
//        selectedImagePath = null;
//
//        if (resultCode == RESULT_OK && requestCode == 0) {
//
//            File f = new File(Environment.getExternalStorageDirectory().toString());
//            for (File temp : f.listFiles()) {
//                if (temp.getName().equals("temp.jpg")) {
//                    f = temp;
//                    break;
//                }
//            }
//            if (!f.exists()) {
//                Toast.makeText(getActivity(), "Error while capturing image", Toast.LENGTH_LONG).show();
//                return;
//            }
//            try {
//                bitmap_1 = BitmapFactory.decodeFile(f.getAbsolutePath());
//                bitmap_1 = Bitmap.createScaledBitmap(bitmap_1, 400, 400, true);
//                int rotate = 0;
//                try {
//                    ExifInterface exif = new ExifInterface(f.getAbsolutePath());
//                    int orientation = exif.getAttributeInt(
//                            ExifInterface.TAG_ORIENTATION,
//                            ExifInterface.ORIENTATION_NORMAL);
//
//                    switch (orientation) {
//                        case ExifInterface.ORIENTATION_ROTATE_270:
//                            rotate = 270;
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_180:
//                            rotate = 180;
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_90:
//                            rotate = 90;
//                            break;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                Matrix matrix = new Matrix();
//                matrix.postRotate(rotate);
//                bitmap_1 = Bitmap.createBitmap(bitmap_1, 0, 0, bitmap_1.getWidth(),
//                        bitmap_1.getHeight(), matrix, true);
//                Log.d("---captured image----", "" + f.getAbsolutePath());
//                img_prsec.setImageBitmap(bitmap_1);
//                if (f.getAbsolutePath() != null) {
//                    uploadFile(f.getAbsolutePath(), str_doc_type);
//                }
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//        } else if (resultCode == RESULT_OK && requestCode == 1) {
//            if (data != null) {
//
//                Uri selectedImage = data.getData();
//                String[] filePath = {MediaStore.Images.Media.DATA};
//                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath,
//                        null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePath[0]);
//                selectedImagePath = c.getString(columnIndex);
//                c.close();
//
//                if (selectedImagePath != null) {
//                    Log.d("----path----", "" + selectedImagePath);
//                    img_prsec.setImageBitmap(bitmap_1);
//                    uploadFile(selectedImagePath, str_doc_type);
//                }
//
//                bitmap_1 = BitmapFactory.decodeFile(selectedImagePath); // load
//                // preview image
//                bitmap_1 = Bitmap.createScaledBitmap(bitmap_1, 400, 400, false);
//                img_prsec.setImageBitmap(bitmap_1);
//                Log.d("----path111---", "" + selectedImagePath);
//
//            } else {
//                Toast.makeText(getActivity(), "Cancelled",
//                        Toast.LENGTH_SHORT).show();
//            }
//        }
//        //-------------select files--------------------------------
//        else if (requestCode == 105) {
//
//            if (data != null && data.getData() != null && resultCode == RESULT_OK) {
//
//                boolean isImageFromGoogleDrive = false;
//
//                Uri uri = data.getData();
//
//                if (isKitKat && DocumentsContract.isDocumentUri(getActivity(), uri)) {
//                    if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
//                        String docId = DocumentsContract.getDocumentId(uri);
//                        String[] split = docId.split(":");
//                        String type = split[0];
//
//                        if ("primary".equalsIgnoreCase(type)) {
//                            realPath_1 = Environment.getExternalStorageDirectory() + "/" + split[1];
//                        } else {
//                            Pattern DIR_SEPORATOR = Pattern.compile("/");
//                            Set<String> rv = new HashSet<>();
//                            String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
//                            String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
//                            String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
//                            if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
//                                if (TextUtils.isEmpty(rawExternalStorage)) {
//                                    rv.add("/storage/sdcard0");
//                                } else {
//                                    rv.add(rawExternalStorage);
//                                }
//                            } else {
//                                String rawUserId;
//                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                                    rawUserId = "";
//                                } else {
//                                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//                                    String[] folders = DIR_SEPORATOR.split(path);
//                                    String lastFolder = folders[folders.length - 1];
//                                    boolean isDigit = false;
//                                    try {
//                                        Integer.valueOf(lastFolder);
//                                        isDigit = true;
//                                    } catch (NumberFormatException ignored) {
//                                    }
//                                    rawUserId = isDigit ? lastFolder : "";
//                                }
//                                if (TextUtils.isEmpty(rawUserId)) {
//                                    rv.add(rawEmulatedStorageTarget);
//                                } else {
//                                    rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
//                                }
//                            }
//                            if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
//                                String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
//                                Collections.addAll(rv, rawSecondaryStorages);
//                            }
//                            String[] temp = rv.toArray(new String[rv.size()]);
//                            for (int i = 0; i < temp.length; i++) {
//                                File tempf = new File(temp[i] + "/" + split[1]);
//                                if (tempf.exists()) {
//                                    realPath_1 = temp[i] + "/" + split[1];
//                                }
//                            }
//                        }
//                    } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
//                        String id = DocumentsContract.getDocumentId(uri);
//                        Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//
//                        Cursor cursor = null;
//                        String column = "_data";
//                        String[] projection = {column};
//                        try {
//                            cursor = getActivity().getContentResolver().query(contentUri, projection, null, null,
//                                    null);
//                            if (cursor != null && cursor.moveToFirst()) {
//                                int column_index = cursor.getColumnIndexOrThrow(column);
//                                realPath_1 = cursor.getString(column_index);
//                            }
//                        } finally {
//                            if (cursor != null)
//                                cursor.close();
//                        }
//                    } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
//                        String docId = DocumentsContract.getDocumentId(uri);
//                        String[] split = docId.split(":");
//                        String type = split[0];
//
//                        Uri contentUri = null;
//                        if ("image".equals(type)) {
//                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                        } else if ("video".equals(type)) {
//                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                        } else if ("audio".equals(type)) {
//                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                        }
//
//                        String selection = "_id=?";
//                        String[] selectionArgs = new String[]{split[1]};
//
//                        Cursor cursor = null;
//                        String column = "_data";
//                        String[] projection = {column};
//
//                        try {
//                            cursor = getActivity().getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
//                            if (cursor != null && cursor.moveToFirst()) {
//                                int column_index = cursor.getColumnIndexOrThrow(column);
//                                realPath_1 = cursor.getString(column_index);
//                            }
//                        } finally {
//                            if (cursor != null)
//                                cursor.close();
//                        }
//                    } else if ("com.google.android.apps.docs.storage".equals(uri.getAuthority())) {
//                        isImageFromGoogleDrive = true;
//                        String str_path = Utils_Files.getPath(getContext(), data.getData());
//                        if (str_path != null) {
//                            realPath_1 = str_path;
//                        }
//                    }
//                } else if ("content".equalsIgnoreCase(uri.getScheme())) {
//                    Cursor cursor = null;
//                    String column = "_data";
//                    String[] projection = {column};
//
//                    try {
//                        cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
//                        if (cursor != null && cursor.moveToFirst()) {
//                            int column_index = cursor.getColumnIndexOrThrow(column);
//                            realPath_1 = cursor.getString(column_index);
//                        }
//                    } finally {
//                        if (cursor != null)
//                            cursor.close();
//                    }
//                } else if ("file".equalsIgnoreCase(uri.getScheme())) {
//                    realPath_1 = uri.getPath();
//                }
//
//                try {
//                    //   Log.d("--------real path--------", "Real Path 1 : " + realPath_1);
//
//                    uploadFile(realPath_1, str_doc_type);
//                    //  file_1 = realPath_1.substring(realPath_1.lastIndexOf('/') + 1, realPath_1.length());//fine name
//                    //  Log.d("-----------File Name 1--------- ", file_1);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            Toast.makeText(getActivity(), "You haven't picked Image/Video", Toast.LENGTH_LONG).show();
//        }
//
//    }
//
//    private void uploadFile(String path, String str_doc_type) {
//        try {
//            progressDialog.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        File file = new File(path);
//        Log.d("--------media path-----", "" + path);
//        // Parsing any Media type file
//        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
//        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("uploaded_file[]", file.getName(), requestBody);
//        RequestBody doc_type = RequestBody.create(MediaType.parse("text/plain"), str_doc_type);
//
//        Call<IllnessDocUpload_RetroModel> call = apiInterface.uploadDocsIllness(fileToUpload, doc_type);
//        call.enqueue(new Callback<IllnessDocUpload_RetroModel>() {
//            @Override
//            public void onResponse(@NonNull Call<IllnessDocUpload_RetroModel> call,
//                                   @NonNull Response<IllnessDocUpload_RetroModel> response) {
//                IllnessDocUpload_RetroModel responseModel = response.body();
//                if (response.isSuccessful()) {
//                    if (responseModel.status.equals("success")) {
//
//                        json_docs = new JsonArray();
//                        List<IllnessDocUpload_RetroModel.IllnessDocUpload_Datum> illnessDoc_Res = responseModel.result;
//
//                        for (IllnessDocUpload_RetroModel.IllnessDocUpload_Datum illnessDocData : illnessDoc_Res) {
//                            //add data to object array
//                            JsonObject object = new JsonObject();
//                            object.addProperty("doc", illnessDocData.image);
//                            json_docs.add(object);
//                        }
//                        updateDocs(sessionManager.getDoctorNurseId(), sessionManager.getLoggedUsrId(), patient_id,
//                                disease_id, responseModel.document_id_res, json_docs);
//                    } else {
//                        Toast.makeText(getActivity(), responseModel.message, Toast.LENGTH_SHORT).show();
//                    }
//                    progressDialog.dismiss();
//                } else {
//                    progressDialog.dismiss();
//                    new AlertDialog.Builder(getActivity())
//                            .setMessage("Something went wrong! Please try again later")
//                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .show();
//                }
//
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<IllnessDocUpload_RetroModel> call, @NonNull Throwable t) {
//                call.cancel();
//                progressDialog.dismiss();
//                new AlertDialog.Builder(getActivity())
//                        .setMessage("Server error! Please try again later")
//                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .show();
//            }
//        });
//    }
//
//    private void updateDocs(String usrType, String usrId, String patientId, String diseaseId, String docsId, Object doc_data) {
//        try {
//            progressDialog.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        DocumentUpdate_RetroModel doc_update_model = new DocumentUpdate_RetroModel(usrType, usrId, patientId, diseaseId, docsId, doc_data);
//        Call<DocumentUpdate_RetroModel> call_doc_update = apiInterface.updateDocs(doc_update_model);
//        call_doc_update.enqueue(new Callback<DocumentUpdate_RetroModel>() {
//            @Override
//            public void onResponse(Call<DocumentUpdate_RetroModel> call, Response<DocumentUpdate_RetroModel> response) {
//                DocumentUpdate_RetroModel responseModel = response.body();
//                if (response.isSuccessful()) {
//                    if (responseModel.status.equals("success")) {
//                        List<DocumentUpdate_RetroModel.DocUpdate_Datum> doc_update_response = responseModel.response;
//                        for (DocumentUpdate_RetroModel.DocUpdate_Datum doc_update_data : doc_update_response) {
//                        }
//                       Toast.makeText(getActivity(), responseModel.message, Toast.LENGTH_SHORT).show();
//                        phv_illness.refresh();
//                    } else {
//                        Toast.makeText(getActivity(), responseModel.message, Toast.LENGTH_SHORT).show();
//                    }
//                    progressDialog.dismiss();
//                } else {
//                    progressDialog.dismiss();
//                    new AlertDialog.Builder(getActivity())
//                            .setMessage("Something went wrong! Please try again later")
//                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DocumentUpdate_RetroModel> call, Throwable t) {
//                call.cancel();
//                progressDialog.dismiss();
//                new AlertDialog.Builder(getActivity())
//                        .setMessage("Server error! Please try again later")
//                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .show();
//            }
//        });
//    }

}
