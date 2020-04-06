package com.app.doorpin.Adapters;

import android.content.Context;
import android.net.ConnectivityManager;

import com.app.doorpin.retrofit.ApiInterface;

import java.util.Locale;

public class Utils {

    private static final String TAG = "Utils";
    static ApiInterface apiInterface;

    private static final long B = 1;
    private static final long KB = B * 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;


    public static final boolean CheckInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    //for getting network speed
    public static String parseSpeed(double bytes, boolean inBits) {
        double value = inBits ? bytes * 8 : bytes;
        if (value < KB) {
            // return String.format(Locale.getDefault(), "%.1f" + (inBits ? "b" : "B"), value);
            return String.format(Locale.getDefault(), "%.1f", value);
        } else if (value < MB) {
            //  return String.format(Locale.getDefault(), "%.1f K" + (inBits ? "b" : "B"), value / KB);
            return String.format(Locale.getDefault(), "%.1f", value / KB);
        } else if (value < GB) {
            //  return String.format(Locale.getDefault(), "%.1f M" + (inBits ? "b" : "B"), value / MB);
            return String.format(Locale.getDefault(), "%.1f", value / MB);
        } else {
            //return String.format(Locale.getDefault(), "%.2f G" + (inBits ? "b" : "B") + "/s", value / GB);
            return String.format(Locale.getDefault(), "%.2f", value / GB);
        }
    }
}

