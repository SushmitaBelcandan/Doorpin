package com.app.doorpin.models;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.doorpin.Activity.EditPatientDetails;
import com.app.doorpin.Activity.View_Docs_Surg_Patient_Act;
import com.app.doorpin.R;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

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

    Context mContext;

    public Surgery_Patient_List_Model(Context context) {
        this.mContext = context;
    }

    @Resolve
    public void onResolved() {

    }

    @Click(R.id.llViewDocs)
    public void onClickDocs() {
        Intent intentViewDocs = new Intent(mContext, View_Docs_Surg_Patient_Act.class);
        intentViewDocs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intentViewDocs);
    }
    @Click(R.id.llCaptureNotes)
    public void onClickNotes() {
        Intent intentViewDocs = new Intent(mContext, EditPatientDetails.class);
        intentViewDocs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentViewDocs.putExtra("PAGE_FLAG","1");
        mContext.startActivity(intentViewDocs);
    }
}
