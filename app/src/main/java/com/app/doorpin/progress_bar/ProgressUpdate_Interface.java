package com.app.doorpin.progress_bar;

import android.content.Context;

public interface ProgressUpdate_Interface {
    void onProgressData(Context context, int val, int file_size, String downStreamSpeed, boolean cancelable);
}
