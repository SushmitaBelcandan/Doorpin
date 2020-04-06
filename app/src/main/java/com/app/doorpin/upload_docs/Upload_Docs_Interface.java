package com.app.doorpin.upload_docs;

import android.content.Context;
import android.widget.ImageView;

public interface Upload_Docs_Interface {
    void onHandleSelection(Context contex, int req_code, ImageView img_illness_presc, String doc_type, String illness_id, String patient_id);
    void onCaptureNotes(int req_code, String doc_type,String patient_table_id, String patient_id);
}
