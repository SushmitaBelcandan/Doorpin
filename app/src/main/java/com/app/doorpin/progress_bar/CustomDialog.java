package com.app.doorpin.progress_bar;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.doorpin.R;

public class CustomDialog {

    private static CustomDialog customProgressDialog = null;
    private Dialog mDialog;
    String str_downstrmspeed;
    int i_val, i_file_size;
    Context c_context;
    boolean b_cancelable;
    TextView tv_title, tv_progress_perc, tv_timer;
    ProgressBar progressBar;


    public static CustomDialog getInstance() {
        if (customProgressDialog == null) {
            customProgressDialog = new CustomDialog();
        }
        return customProgressDialog;
    }

    public void showProgress(Context context, int val, int file_size, String downStreamSpeed, boolean cancelable) {

        mDialog = new Dialog(context);
        if (mDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        // no title for the dialog
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));//make background transparent of dialog
        mDialog.setContentView(R.layout.progress_dialog_layout);
        tv_title = (TextView) mDialog.findViewById(R.id.tv_title);
        tv_progress_perc = (TextView) mDialog.findViewById(R.id.tv_progress_perc);
        tv_timer = (TextView) mDialog.findViewById(R.id.tv_timer);
        progressBar = (ProgressBar) mDialog.findViewById(R.id.progressBar);

        tv_title.setText("Uploading..");
        //do not show percent value and progress value initially
        mDialog.setCancelable(cancelable);
        mDialog.setCanceledOnTouchOutside(cancelable);
        mDialog.show();
    }

    public void updateProgress(int val, int file_size, String downStreamSpeed) {
        i_val = val;
        tv_progress_perc.setText(String.valueOf(i_val) + "%");
        progressBar.setProgress(val);

        if (!downStreamSpeed.isEmpty() && !downStreamSpeed.equals("0.0") && !downStreamSpeed.equals("-0.0")) {
            double dbl_speed = Double.parseDouble(downStreamSpeed);
            int int_speed = (int) dbl_speed;
            int left_time = 0;
            if (file_size != 0) {
                double dbl_second = file_size * 8 / int_speed;
                left_time = (int) dbl_second;
            }
            tv_timer.setText(String.valueOf(left_time) + " " + "seconds left");
        }
    }

    public void hideProgress() {
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                this.mDialog.dismiss();
            }
            mDialog = null;
        }
    }
}
