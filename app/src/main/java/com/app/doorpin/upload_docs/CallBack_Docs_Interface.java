package com.app.doorpin.upload_docs;

import android.graphics.Bitmap;

public interface CallBack_Docs_Interface {

    String selectedPath(String str_path);

    Bitmap getBitmap(Bitmap bitmap_ref);
}
