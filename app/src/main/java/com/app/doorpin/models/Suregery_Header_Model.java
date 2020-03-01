package com.app.doorpin.models;

import android.content.Context;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.app.doorpin.R;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.Collapse;
import com.mindorks.placeholderview.annotations.expand.Expand;
import com.mindorks.placeholderview.annotations.expand.Parent;
import com.mindorks.placeholderview.annotations.expand.SingleTop;

@Parent
@SingleTop
@Layout(R.layout.surgery_single_item_view)
public class Suregery_Header_Model {

    @View(R.id.llHeader)
    public CardView llHeader;

    @View(R.id.tv_surgeryname)
    public TextView tv_surgeryname;

    @View(R.id.imgBtnDropdown)
    public ImageButton imgBtnDropdown;

    public Context mContext;

    public Suregery_Header_Model(Context context) {
        this.mContext = context;
    }

    @Resolve
    public void onResolved() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Expand
    public void onExpand() {
        imgBtnDropdown.setImageDrawable(mContext.getResources().getDrawable(R.drawable.dropdown_blue_mipmap));
      //  llHeader.setBackgroundResource(R.drawable.ll_shadow);
    }

    @Collapse
    public void onCollapse() {
        imgBtnDropdown.setImageDrawable(mContext.getResources().getDrawable(R.drawable.right_blue_mipmap));
       // llHeader.setBackgroundResource(R.drawable.white_bg_rounded_cor);
    }

}
