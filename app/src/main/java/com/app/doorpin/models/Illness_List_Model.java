package com.app.doorpin.models;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.doorpin.Activity.EditPatientDetails;
import com.app.doorpin.Activity.HomePage_Doctor;
import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.Edit_Disease_Retro_Model;
import com.app.doorpin.retrofit.Logout_RetroModel;
import com.bumptech.glide.Glide;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    String str1_illness_id, str1_illness_name, str1_followup_date, str1_prsec_id, str1_prsec_link, str1_report_id, str1_report_link, str1_other_docs_id, str1_other_docs_link;
    String str1_patient_id;

    public Illness_List_Model(Context context, String str_illness_id, String str_illness_name, String str_followup_date,
                              String str_prsec_id, String str_prsec_link, String str_report_id, String str_report_link,
                              String str_other_docs_id, String str_other_docs_link, String str_patient_id) {
        this.mContext = context;
        this.str1_illness_id = str_illness_id;
        this.str1_illness_name = str_illness_name;
        this.str1_followup_date = str_followup_date;
        this.str1_prsec_id = str_prsec_id;
        this.str1_prsec_link = str_prsec_link;
        this.str1_report_id = str_report_id;
        this.str1_report_link = str_report_link;
        this.str1_other_docs_id = str_other_docs_id;
        this.str1_other_docs_link = str_other_docs_link;
        this.str1_patient_id = str_patient_id;
    }

    @Resolve
    public void onResolved() {
        //initialize session
        session = new SessionManager(mContext);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please wait.....");

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
        if (!str1_prsec_id.equals("null") && !str1_prsec_id.equals(null)
                && !str1_prsec_id.equals("NA") && !str1_prsec_id.isEmpty()) {
            if (!str1_prsec_link.equals("null") && !str1_prsec_link.equals(null)
                    && !str1_prsec_link.equals("NA") && !str1_prsec_link.isEmpty()) {

                Glide.with(mContext).load(str1_prsec_link).into(img_illness_presc);
            } else {
                Glide.with(mContext).load(R.drawable.download_sample).into(img_illness_presc);
            }
        } else {
            Glide.with(mContext).load(R.drawable.download_sample).into(img_illness_presc);
        }
        //report link
        if (!str1_report_id.equals("null") && !str1_report_id.equals(null)
                && !str1_report_id.equals("NA") && !str1_report_id.isEmpty()) {
            if (!str1_report_link.equals("null") && !str1_report_link.equals(null)
                    && !str1_report_link.equals("NA") && !str1_report_link.isEmpty()) {

                Glide.with(mContext).load(str1_report_link).into(img_illness_reports);
            } else {
                Glide.with(mContext).load(R.drawable.download_sample).into(img_illness_presc);
            }
        } else {
            Glide.with(mContext).load(R.drawable.download_sample).into(img_illness_presc);
        }
        //other docs link
        if (!str1_other_docs_id.equals("null") && !str1_other_docs_id.equals(null)
                && !str1_other_docs_id.equals("NA") && !str1_other_docs_id.isEmpty()) {
            if (!str1_other_docs_link.equals("null") && !str1_other_docs_link.equals(null)
                    && !str1_other_docs_link.equals("NA") && !str1_other_docs_link.isEmpty()) {

                Glide.with(mContext).load(str1_other_docs_link).into(img_illness_other_doc);
            } else {
                Glide.with(mContext).load(R.drawable.download_sample).into(img_illness_presc);
            }
        } else {
            Glide.with(mContext).load(R.drawable.download_sample).into(img_illness_presc);
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
                //    dpd.getDatePicker().setMaxDate(System.currentTimeMillis()); //make future date disable
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
                }
            }
        });

    }
/*
    @Click(R.id.imgbtn_delete)
    public void deleteRow() {
        phv_doc_list.removeView(phv_doc_list.getChildLayoutPosition(cv_illness_list));
    }*/

    @Click(R.id.imgbtn_upload)
    public void uploadDocs() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        android.view.View promptView = layoutInflater.inflate(R.layout.upload_document_popup, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
        alertDialogBuilder.setView(promptView);

        TextView tv_upload_presec = (TextView) promptView.findViewById(R.id.tv_upload_presec);
        TextView tv_upload_report = (TextView) promptView.findViewById(R.id.tv_upload_report);
        TextView tv_upload_other_doc = (TextView) promptView.findViewById(R.id.tv_upload_other_doc);

        //-----------------------------------upload document action event------------------------
       /* tv_upload_presec.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

            }
        });
*/

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //mae alert bg transparent for custom rounded corner
    }

    @Click(R.id.card_view_prsec)
    public void getPresecList() {

        if (flag_doc_visible_prsec == true) {
            phv_doc_list.setVisibility(android.view.View.VISIBLE);
            session.saveDocType("Prescription");
            phv_doc_list.getBuilder()
                    .setHasFixedSize(false)
                    .setItemViewCacheSize(10)
                    .setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

            phv_doc_list.addView(new Patient_Doc_List_Model(mContext));
            phv_doc_list.addView(new Patient_Doc_List_Model(mContext));
            phv_doc_list.addView(new Patient_Doc_List_Model(mContext));
            phv_doc_list.addView(new Patient_Doc_List_Model(mContext));
            flag_doc_visible_prsec = false;
        } else {
            phv_doc_list.setVisibility(android.view.View.GONE);
            flag_doc_visible_prsec = true;
        }
    }

    @Click(R.id.card_view_reports)
    public void getReportsList() {
        if (flag_doc_visible_reports == true) {
            phv_doc_list.setVisibility(android.view.View.VISIBLE);
            session.saveDocType("Reports");
            phv_doc_list.getBuilder()
                    .setHasFixedSize(false)
                    .setItemViewCacheSize(10)
                    .setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

            phv_doc_list.addView(new Patient_Doc_List_Model(mContext));
            phv_doc_list.addView(new Patient_Doc_List_Model(mContext));
            phv_doc_list.addView(new Patient_Doc_List_Model(mContext));
            phv_doc_list.addView(new Patient_Doc_List_Model(mContext));
            flag_doc_visible_reports = false;
        } else {
            phv_doc_list.setVisibility(android.view.View.GONE);
            flag_doc_visible_reports = true;
        }
    }

    @Click(R.id.card_view_other_doc)
    public void getOtherDocsList() {
        if (flag_doc_visible_other_doc == true) {
            phv_doc_list.setVisibility(android.view.View.VISIBLE);
            session.saveDocType("Other Documents");
            phv_doc_list.getBuilder()
                    .setHasFixedSize(false)
                    .setItemViewCacheSize(10)
                    .setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

            phv_doc_list.addView(new Patient_Doc_List_Model(mContext));
            phv_doc_list.addView(new Patient_Doc_List_Model(mContext));
            phv_doc_list.addView(new Patient_Doc_List_Model(mContext));
            phv_doc_list.addView(new Patient_Doc_List_Model(mContext));
            flag_doc_visible_other_doc = false;
        } else {
            phv_doc_list.setVisibility(android.view.View.GONE);
            flag_doc_visible_other_doc = true;
        }
    }
}
