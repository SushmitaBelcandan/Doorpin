package com.app.doorpin.models;

import android.content.Context;
import android.widget.ImageView;

import com.app.doorpin.R;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.view_docs_item_model)
public class View_Docs_Item_Model {

    @View(R.id.ivDocs)
    public ImageView ivDocs;

    Context mContext;

    public View_Docs_Item_Model(Context context) {
        this.mContext = context;
    }

    @Resolve
    public void onResolved() {

    }
}
