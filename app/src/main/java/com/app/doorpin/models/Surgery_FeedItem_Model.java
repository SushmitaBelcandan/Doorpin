package com.app.doorpin.models;

import android.content.Context;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.app.doorpin.R;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.surgery_feeitem_model)
public class Surgery_FeedItem_Model {

    @View(R.id.phvSurgeryPatient)
    public PlaceHolderView phvSurgeryPatient;

    @View(R.id.llBtnSearch)
    public LinearLayout llBtnSearch;

    @View(R.id.etSearch)
    public EditText etSearch;

    public Context mContext;

    public Surgery_FeedItem_Model(Context context) {
        this.mContext = context;
    }

    @Resolve
    public void onResolved() {
        phvSurgeryPatient.addView(new Surgery_Patient_List_Model(mContext));
        phvSurgeryPatient.addView(new Surgery_Patient_List_Model(mContext));
        phvSurgeryPatient.addView(new Surgery_Patient_List_Model(mContext));
    }
}
