package com.app.doorpin.models;

import android.content.Context;
import android.content.Intent;

import androidx.cardview.widget.CardView;

import com.app.doorpin.Activity.Document_Details_Act;
import com.app.doorpin.R;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.patient_doc_list_model)
public class Patient_Doc_List_Model {

    @View(R.id.cv_container)
    public CardView cv_container;

    public Context mContext;

    public Patient_Doc_List_Model(Context contxt) {
        this.mContext = contxt;
    }

    @Resolve
    public void onResolved() {

    }

    @Click(R.id.cv_container)
    public void viewDocDetails() {
        //navigate to doc container slider
        Intent intentDocDetails = new Intent(mContext, Document_Details_Act.class);
        intentDocDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intentDocDetails);
    }
}
