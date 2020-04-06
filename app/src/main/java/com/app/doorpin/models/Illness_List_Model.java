package com.app.doorpin.models;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.doorpin.Activity.AddDisease;
import com.app.doorpin.Activity.Profile_Nurse;
import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.Delete_Disease_Retro_Model;
import com.app.doorpin.retrofit.Edit_Disease_Retro_Model;
import com.app.doorpin.retrofit.IllnessDocUpload_RetroModel;
import com.app.doorpin.upload_docs.Upload_Docs_Interface;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@NonReusable
@Layout(R.layout.illness_card_item)
public class Illness_List_Model {
    //-----------------rootview----------------------------
    @View(R.id.cv_illness_list)
    public CardView cv_illness_list;
    //-----------------edit view---------------------------
    @View(R.id.rl_editDisease)
    public RelativeLayout rl_editDisease;

    @View(R.id.et_type_of_illness)
    public EditText et_type_of_illness;

    @View(R.id.et_illness_follow_update)
    public EditText et_illness_follow_update;

    @View(R.id.btn_save)
    public Button btn_save;
    //-----------------------main view----------------------

    @View(R.id.rl_illness)
    public RelativeLayout rl_illness;

    @View(R.id.tv_disease_name)
    public TextView tv_disease_name;

    @View(R.id.tv_followup_date_val)
    public TextView tv_followup_date_val;

    @View(R.id.imgbtn_edit)
    public ImageButton imgbtn_edit;

    @View(R.id.imgbtn_delete)
    public ImageButton imgbtn_delete;

    @View(R.id.imgbtn_upload)
    public ImageButton imgbtn_upload;

    @View(R.id.card_view_other_doc)
    public CardView card_view_other_doc;

    @View(R.id.card_view_reports)
    public CardView card_view_reports;

    @View(R.id.card_view_prsec)
    public CardView card_view_prsec;

    @View(R.id.img_illness_presc)
    public ImageView img_illness_presc;

    @View(R.id.img_illness_reports)
    public ImageView img_illness_reports;

    @View(R.id.img_illness_other_doc)
    public ImageView img_illness_other_doc;

    @View(R.id.tv_presecription)
    public TextView tv_presecription;

    @View(R.id.tv_report)
    public TextView tv_report;

    @View(R.id.tv_other_doc)
    public TextView tv_other_doc;

    @View(R.id.phv_doc_list)
    public PlaceHolderView phv_doc_list;

    public Context mContext;
    SessionManager session;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;
    public Calendar followupCalendar = Calendar.getInstance();
    String strFollowUp;
    Boolean flag_doc_visible_prsec = true;
    Boolean flag_doc_visible_reports = true;
    Boolean flag_doc_visible_other_doc = true;

    String str1_illness_id, str1_illness_name, str1_followup_date;
    Object list_prsec_link;
    Object list_report_link;
    Object list_other_docs_link;

    ArrayList<String> arr_list_prsc_name = new ArrayList<>();
    ArrayList<String> arr_list_prsc_id = new ArrayList<>();
    ArrayList<String> arr_list_report_name = new ArrayList<>();
    ArrayList<String> arr_list_report_id = new ArrayList<>();
    ArrayList<String> arr_list_other_doc_name = new ArrayList<>();
    ArrayList<String> arr_list_other_doc_id = new ArrayList<>();

    String str1_patient_id;
    Upload_Docs_Interface upload_docs_interface;
    PlaceHolderView m_phv_list;

    public Illness_List_Model(Context context, PlaceHolderView phv_list, String str_illness_id, String str_illness_name, String str_followup_date,
                              Object obj_arr_prsec_link, Object obj_arr_report_link, Object obj_arr_other_docs_link, String str_patient_id) {
        this.mContext = context;
        this.m_phv_list = phv_list;
        this.str1_illness_id = str_illness_id;
        this.str1_illness_name = str_illness_name;
        this.str1_followup_date = str_followup_date;
        this.list_prsec_link = obj_arr_prsec_link;
        this.list_report_link = obj_arr_report_link;
        this.list_other_docs_link = obj_arr_other_docs_link;
        this.str1_patient_id = str_patient_id;
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
        arr_list_prsc_id.clear();
        arr_list_prsc_name.clear();
        arr_list_report_id.clear();
        arr_list_report_name.clear();
        arr_list_other_doc_id.clear();
        arr_list_other_doc_name.clear();
//-------------------------------------------prescription data--------------------------
        Gson gson_prsec = new Gson();
        String json_prsec = gson_prsec.toJson(list_prsec_link);
        JSONArray jarr_Prsec = null;
        try {
            jarr_Prsec = new JSONArray(json_prsec);
            for (int count = 0; count < jarr_Prsec.length(); count++) {

                JSONObject obj = jarr_Prsec.getJSONObject(count);
                String prscName = obj.getString("prescription_link");
                String prscId = obj.getString("prescription_id");
                if (!prscName.equals(null) && !prscName.equals("NA") && !prscName.isEmpty()) {
                    arr_list_prsc_name.add(prscName);
                    arr_list_prsc_id.add(prscId);
                }
                //so on
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
//---------------------------------------------report data-------------------------------
        Gson gson_report = new Gson();
        String json_report = gson_report.toJson(list_report_link);
        JSONArray jarr_report = null;
        try {
            jarr_report = new JSONArray(json_report);
            for (int count = 0; count < jarr_report.length(); count++) {

                JSONObject obj = jarr_report.getJSONObject(count);
                String reportName = obj.getString("reportdoc_link");
                String reportId = obj.getString("reportdoc_id");
                if (!reportName.equals(null) && !reportName.equals("NA") && !reportName.isEmpty()) {
                    arr_list_report_name.add(reportName);
                    arr_list_report_id.add(reportId);
                }
                //so on
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
//----------------------------------------------other documents---------------------------
        Gson gson_other_docs = new Gson();
        String json_other_docs = gson_other_docs.toJson(list_other_docs_link);
        JSONArray jarr_other_docs = null;
        try {
            jarr_other_docs = new JSONArray(json_other_docs);
            for (int count = 0; count < jarr_other_docs.length(); count++) {

                JSONObject obj = jarr_other_docs.getJSONObject(count);
                String odocsName = obj.getString("otherdoc_name");
                String odocsId = obj.getString("otherdoc_id");
                if (!odocsName.equals(null) && !odocsName.equals("NA") && !odocsName.isEmpty()) {
                    arr_list_other_doc_name.add(odocsName);
                    arr_list_other_doc_id.add(odocsId);
                }
                //so on
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
//----------------------------------------------------------------------------------------------
        //disease name
        if (!str1_illness_name.equals("null") && !str1_illness_name.equals(null)
                && !str1_illness_name.equals("NA") && !str1_illness_name.isEmpty()) {
            tv_disease_name.setText(str1_illness_name);
        } else {
            tv_disease_name.setText("");
        }
        //followup date
        if (!str1_followup_date.equals("null") && !str1_followup_date.equals(null)
                && !str1_followup_date.equals("NA") && !str1_followup_date.isEmpty()) {
            Date localTime = null;
            try {
                localTime = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(str1_followup_date);//convert from
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");//convert in
            String dob_format = sdf.format(new Date(localTime.getTime()));
            tv_followup_date_val.setText(dob_format);
        } else {
            tv_followup_date_val.setText("");
        }
        //prsec Link
        if (arr_list_prsc_name.size() > 0) {
            Glide.with(mContext).load(arr_list_prsc_name.get(0)).into(img_illness_presc);
        } else {
            Glide.with(mContext).load(R.drawable.download_sample).into(img_illness_presc);
        }
        //report link
        if (arr_list_report_name.size() > 0) {
            Glide.with(mContext).load(arr_list_report_name.get(0)).into(img_illness_reports);
        } else {
            Glide.with(mContext).load(R.drawable.download_sample).into(img_illness_reports);
        }
        //other docs link
        if (arr_list_other_doc_name.size() > 0) {
            Glide.with(mContext).load(arr_list_other_doc_name.get(0)).into(img_illness_other_doc);
        } else {
            Glide.with(mContext).load(R.drawable.download_sample).into(img_illness_other_doc);
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Click(R.id.imgbtn_edit)
    public void editDisease() {
        rl_illness.setVisibility(android.view.View.GONE);
        rl_editDisease.setVisibility(android.view.View.VISIBLE);//edit layout visible
        //select followup date-------------------
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                followupCalendar.set(Calendar.YEAR, year);
                followupCalendar.set(Calendar.MONTH, monthOfYear);
                followupCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //set selcted date
                String dateFormat = "dd/MM/yyyy";//In which you need put here
                SimpleDateFormat sdfDate = new SimpleDateFormat(dateFormat, Locale.US);
                if (!sdfDate.equals("null")) {
                    strFollowUp = sdfDate.format(followupCalendar.getTime());
                    if (!strFollowUp.equals("null") && !strFollowUp.equals(null)) {
                        et_illness_follow_update.setText(strFollowUp);
                    } else {
                        et_illness_follow_update.setText("");
                    }
                } else {
                    strFollowUp = "null";
                }

            }

        };
        //calender select--------------------------------------------------------------------
        et_illness_follow_update.setFocusable(false);
        et_illness_follow_update.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                // TODO Auto-generated method stub
                DatePickerDialog dpd = new DatePickerDialog(mContext, R.style.MyThemeOverlay, date, followupCalendar
                        .get(Calendar.YEAR), followupCalendar.get(Calendar.MONTH), followupCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMinDate(System.currentTimeMillis()); //make past date disable
                dpd.show();
                dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // do something onCancel
                        strFollowUp = "null";
                        et_illness_follow_update.setText("");
                    }
                });

            }
        });
        //----------------------------------------------------------------------------------

        btn_save.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                //api call for edit disease

            }
        });
        if (!tv_disease_name.getText().toString().isEmpty() && !tv_disease_name.getText().toString().equals(null)) {
            et_type_of_illness.setText(tv_disease_name.getText().toString());
        } else {
            et_type_of_illness.setText("");
        }
        if (!tv_followup_date_val.getText().toString().isEmpty() && !tv_followup_date_val.getText().toString().equals(null)) {
            et_illness_follow_update.setText(tv_followup_date_val.getText().toString());
        } else {
            et_illness_follow_update.setText("");
        }
       /* String strDiseaseName = et_type_of_illness.getText().toString();
        String strFollowUpDate = et_illness_follow_update.getText().toString();
        if (!(strDiseaseName.equals(null)) && !(strDiseaseName.isEmpty())) {
            tv_disease_name.setText(strDiseaseName);
        }
        if (!(strFollowUpDate.equals(null)) && !(strFollowUpDate.isEmpty())) {
            tv_followup_date_val.setText(strFollowUpDate);
        }*/
        btn_save.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if ((et_type_of_illness.getText().toString().isEmpty() || et_type_of_illness == null)) {
                    et_type_of_illness.setError("Enter the Illness Type");
                } else if (et_illness_follow_update.getText().toString().isEmpty() || et_illness_follow_update.getText().toString() == null) {
                    et_illness_follow_update.setError("Enter the Date");
                } else if (((et_type_of_illness.getText().toString().isEmpty()) && (et_illness_follow_update.getText().toString().isEmpty()))) {
                    et_type_of_illness.setError("Enter the Illness Type");
                    et_illness_follow_update.setError("Enter the Date");
                } else {
                    et_illness_follow_update.setError(null);
                    et_type_of_illness.setError(null);//remove error icon once the text has filled
                    rl_editDisease.setVisibility(android.view.View.GONE);
                    rl_illness.setVisibility(android.view.View.VISIBLE); //hide edit view

                    String str_update_illness = et_type_of_illness.getText().toString().trim();
                    String str_update_date = et_illness_follow_update.getText().toString().trim();
                    //format date from dd/MM/yyyy to yyyy-MM-dd
                    Date localTime = null;
                    try {
                        localTime = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(str_update_date);//convert from
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//convert in
                    String dob_format = sdf.format(new Date(localTime.getTime()));
                    if (Utils.CheckInternetConnection(mContext)) {
                        Edit_Disease_Retro_Model edit_disease_model = new Edit_Disease_Retro_Model(session.getDoctorNurseId(), session.getLoggedUsrId(),
                                str1_patient_id, str1_illness_id, str_update_illness, dob_format);
                        Call<Edit_Disease_Retro_Model> edit_disease_call = apiInterface.updateDisease(edit_disease_model);
                        edit_disease_call.enqueue(new Callback<Edit_Disease_Retro_Model>() {
                            @Override
                            public void onResponse(Call<Edit_Disease_Retro_Model> call, Response<Edit_Disease_Retro_Model> response) {
                                Edit_Disease_Retro_Model edit_illness_resources = response.body();
                                if (response.isSuccessful()) {
                                    if (edit_illness_resources.status.equals("success")) {
                                        List<Edit_Disease_Retro_Model.Edit_Disease_Datum> edit_illness_list = edit_illness_resources.response;
                                        if (edit_illness_list.size() <= 0) {
                                            //empty
                                            progressDialog.dismiss();
                                            rl_illness.setVisibility(android.view.View.VISIBLE);
                                            rl_editDisease.setVisibility(android.view.View.GONE);//edit layout visible
                                        } else {
                                            //hide edit layout and display changes
                                            for (Edit_Disease_Retro_Model.Edit_Disease_Datum edit_illness_data : edit_illness_list) {

                                                //update disease name
                                                if (!edit_illness_data.disease_names_r.equals("null") && !edit_illness_data.disease_names_r.equals("NA")
                                                        && !edit_illness_data.disease_names_r.equals(null) && !edit_illness_data.disease_names_r.isEmpty()) {
                                                    tv_disease_name.setText(edit_illness_data.disease_names_r);
                                                } else {
                                                    tv_disease_name.setText("");
                                                }
                                                //update follow up date
                                                if (!edit_illness_data.follow_up_dates_r.equals("null") && !edit_illness_data.follow_up_dates_r.equals("NA")
                                                        && !edit_illness_data.follow_up_dates_r.equals(null) && !edit_illness_data.follow_up_dates_r.isEmpty()) {
                                                    Date localTime = null;
                                                    try {
                                                        localTime = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(edit_illness_data.follow_up_dates_r);//convert from
                                                    } catch (java.text.ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");//convert in
                                                    String dob_format = sdf.format(new Date(localTime.getTime()));
                                                    tv_followup_date_val.setText(dob_format);
                                                } else {
                                                    tv_followup_date_val.setText("");
                                                }
                                            }
                                            rl_illness.setVisibility(android.view.View.VISIBLE);
                                            rl_editDisease.setVisibility(android.view.View.GONE);//edit layout visible
                                            progressDialog.dismiss();
                                        }
                                    } else {
                                        //failure status
                                        progressDialog.dismiss();
                                        new AlertDialog.Builder(mContext)
                                                .setMessage("Please Try Again")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        rl_illness.setVisibility(android.view.View.VISIBLE);
                                                        rl_editDisease.setVisibility(android.view.View.GONE);//edit layout visible
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
                                                rl_illness.setVisibility(android.view.View.VISIBLE);
                                                rl_editDisease.setVisibility(android.view.View.GONE);
                                                progressDialog.dismiss();
                                                new AlertDialog.Builder(mContext)
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
                            public void onFailure(Call<Edit_Disease_Retro_Model> call, Throwable t) {
                                call.cancel();
                                progressDialog.dismiss();
                                new AlertDialog.Builder(mContext)
                                        .setMessage("Please Try Again")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                rl_illness.setVisibility(android.view.View.VISIBLE);
                                                rl_editDisease.setVisibility(android.view.View.GONE);//edit layout visible
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        });

                    } else {
                        Toast.makeText(mContext, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Click(R.id.imgbtn_delete)
    public void deleteRow() {
        if (Utils.CheckInternetConnection(mContext)) {
            if (!session.getDoctorNurseId().equals("null") || !session.getLoggedUsrId().equals("null")) {
                Delete_Disease_Retro_Model illness_delete_model = new Delete_Disease_Retro_Model(session.getDoctorNurseId(),
                        session.getLoggedUsrId(), str1_patient_id, str1_illness_id);
                Call<Delete_Disease_Retro_Model> illness_deletel_call = apiInterface.deleteDisease(illness_delete_model);
                illness_deletel_call.enqueue(new Callback<Delete_Disease_Retro_Model>() {
                    @Override
                    public void onResponse(Call<Delete_Disease_Retro_Model> call, Response<Delete_Disease_Retro_Model> response) {
                        Delete_Disease_Retro_Model delete_illness_request = response.body();
                        if (response.isSuccessful()) {
                            if (delete_illness_request.status.equals("success")) {
                                Toast.makeText(mContext, delete_illness_request.message, Toast.LENGTH_SHORT).show();
                                m_phv_list.removeView(m_phv_list.getChildAdapterPosition(cv_illness_list));

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
                    public void onFailure(Call<Delete_Disease_Retro_Model> call, Throwable t) {
                        call.cancel();
                    }
                });
            } else {
                Toast.makeText(mContext, "Unable to delete", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int GALLERY_PICTURE = 1;
    private static final int CAMERA_REQUEST = 13;
    private static final int DOC_REQUEST = 105;
    private static final String DOC_TYPE_PRSEC = "1";
    private static final String DOC_TYPE_REPORT = "2";
    private static final String DOC_TYPE_OTHER = "3";
    Bitmap bitmap;
    String selectedImagePath;
    public String mediaPath;

    @Click(R.id.imgbtn_upload)
    public void uploadDocs() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        android.view.View promptView = layoutInflater.inflate(R.layout.upload_document_popup, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
        alertDialogBuilder.setView(promptView);

        TextView tv_upload_presec = (TextView) promptView.findViewById(R.id.tv_upload_presec);
        TextView tv_upload_report = (TextView) promptView.findViewById(R.id.tv_upload_report);
        TextView tv_upload_other_doc = (TextView) promptView.findViewById(R.id.tv_upload_other_doc);

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
                            if (str1_illness_id.equals(null) || str1_patient_id.equals(null)) {
                                //do nothing
                            } else {
                                upload_docs_interface.onHandleSelection(mContext, GALLERY_PICTURE, img_illness_presc, DOC_TYPE_PRSEC, str1_illness_id, str1_patient_id);
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
                            if (str1_illness_id.equals(null) || str1_patient_id.equals(null)) {
                                //do nothing
                            } else {
                                upload_docs_interface.onHandleSelection(mContext, CAMERA_REQUEST, img_illness_presc, DOC_TYPE_PRSEC, str1_illness_id, str1_patient_id);
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
                    if (str1_illness_id.equals(null) || str1_patient_id.equals(null)) {
                        //do nothing
                    } else {
                        upload_docs_interface.onHandleSelection(mContext, DOC_REQUEST, img_illness_reports, DOC_TYPE_REPORT, str1_illness_id, str1_patient_id);
                        alertDialog_main.dismiss();
                    }
                }
            }
        });
        //----------------------------------------upload other documents---------------------------------------------------------
        tv_upload_other_doc.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (upload_docs_interface != null) {
                    if (str1_illness_id.equals(null) || str1_patient_id.equals(null)) {
                        //do nothing
                    } else {
                        upload_docs_interface.onHandleSelection(mContext, DOC_REQUEST, img_illness_other_doc, DOC_TYPE_OTHER, str1_illness_id, str1_patient_id);
                        alertDialog_main.dismiss();
                    }
                }
            }
        });
    }

    @Click(R.id.card_view_prsec)
    public void getPresecList() {
        if (session.getDocsDataType().equals("1")) {
            if (!session.getDocsDataId().equals("null")) {
                arr_list_prsc_id.add(session.getDocsDataId());
                arr_list_prsc_name.add(session.getDocsDataName());
                session.saveDocsData("null", "null", "null");
            }
        }
        if (flag_doc_visible_prsec == true) {
            phv_doc_list.setVisibility(android.view.View.VISIBLE);
            session.saveDocType("Prescription");
            phv_doc_list.getBuilder()
                    .setHasFixedSize(false)
                    .setItemViewCacheSize(10)
                    .setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

            if (arr_list_prsc_id.size() > 0) {
                for (int i = 0; i < arr_list_prsc_id.size(); i++) {
                    phv_doc_list.addView(new Patient_Doc_List_Model(mContext, arr_list_prsc_id.get(i), arr_list_prsc_name.get(i), arr_list_prsc_name));
                }
            } else {
                //do not display anything
            }
            flag_doc_visible_prsec = false;
        } else {
            phv_doc_list.setVisibility(android.view.View.GONE);
            flag_doc_visible_prsec = true;
        }
    }

    @Click(R.id.card_view_reports)
    public void getReportsList() {
        if (session.getDocsDataType().equals("2")) {
            if (!session.getDocsDataId().equals("null")) {
                arr_list_report_id.add(session.getDocsDataId());
                arr_list_report_name.add(session.getDocsDataName());
                session.saveDocsData("null", "null", "null");
            }
        }
        if (flag_doc_visible_reports == true) {
            phv_doc_list.setVisibility(android.view.View.VISIBLE);
            session.saveDocType("Reports");
            phv_doc_list.getBuilder()
                    .setHasFixedSize(false)
                    .setItemViewCacheSize(10)
                    .setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

            if (arr_list_report_id.size() > 0) {
                for (int i = 0; i < arr_list_report_id.size(); i++) {
                    phv_doc_list.addView(new Patient_Doc_List_Model(mContext, arr_list_report_id.get(i), arr_list_report_name.get(i), arr_list_report_name));
                }
            } else {
                //do not display anything
            }
            flag_doc_visible_reports = false;
        } else {
            phv_doc_list.setVisibility(android.view.View.GONE);
            flag_doc_visible_reports = true;
        }
    }

    @Click(R.id.card_view_other_doc)
    public void getOtherDocsList() {
        if (session.getDocsDataType().equals("3")) {
            if (!session.getDocsDataId().equals("null")) {
                arr_list_other_doc_id.add(session.getDocsDataId());
                arr_list_other_doc_name.add(session.getDocsDataName());
                session.saveDocsData("null", "null", "null");
            }
        }
        if (flag_doc_visible_other_doc == true) {
            phv_doc_list.setVisibility(android.view.View.VISIBLE);
            session.saveDocType("Other Documents");
            phv_doc_list.getBuilder()
                    .setHasFixedSize(false)
                    .setItemViewCacheSize(10)
                    .setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

            if (arr_list_other_doc_id.size() > 0) {
                for (int i = 0; i < arr_list_other_doc_id.size(); i++) {
                    phv_doc_list.addView(new Patient_Doc_List_Model(mContext, arr_list_other_doc_id.get(i),
                            arr_list_other_doc_name.get(i), arr_list_other_doc_name));
                }
            } else {
                //do not display anything
            }
            flag_doc_visible_other_doc = false;
        } else {
            phv_doc_list.setVisibility(android.view.View.GONE);
            flag_doc_visible_other_doc = true;
        }
    }

}
