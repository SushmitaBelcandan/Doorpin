package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

public class NewSurgery_RetroModel {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("patient_ids")
    public String patient_ids;

    @SerializedName("surgery_ids")
    public String surgery_ids;

    @SerializedName("Documents")
    public Object Documents;

    @SerializedName("files")
    public Object files;

    public NewSurgery_RetroModel(String str_usr_type, String str_usr_id, String str_patient_id, String str_surg_id,
                                 Object str_doc, Object str_files) {
        this.user_types = str_usr_type;
        this.user_ids = str_usr_id;
        this.patient_ids = str_patient_id;
        this.surgery_ids = str_surg_id;
        this.Documents = str_doc;
        this.files = str_files;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("user_type")
    public String user_type;

    @SerializedName("user_id")
    public String user_id;

    @SerializedName("surgery_id")
    public String surgery_id;

    @SerializedName("patient_id")
    public String patient_id;

}
