package com.app.doorpin.models;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.doorpin.Activity.EditPatientDetails;
import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
    public Calendar followupCalendar = Calendar.getInstance();
    String strFollowUp;
    Boolean flag_doc_visible_prsec = true;
    Boolean flag_doc_visible_reports = true;
    Boolean flag_doc_visible_other_doc = true;

    public Illness_List_Model(Context context) {
        mContext = context;
    }

    @Resolve
    public void onResolved() {
        //initialize session
        session = new SessionManager(mContext);

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
                }
            }
        });
        String strDiseaseName = et_type_of_illness.getText().toString();
        String strFollowUpDate = et_illness_follow_update.getText().toString();
        if (!(strDiseaseName.equals(null)) && !(strDiseaseName.isEmpty())) {
            tv_disease_name.setText(strDiseaseName);
        }
        if (!(strFollowUpDate.equals(null)) && !(strFollowUpDate.isEmpty())) {
            tv_followup_date_val.setText(strFollowUpDate);
        }
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
