package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IllnessDocUpload_RetroModel {

    @SerializedName("uploaded_file[]")
    public String uploaded_file;

    @SerializedName("document_id")
    public String document_id;

    public IllnessDocUpload_RetroModel(String obj_upload_file, String obj_doc_id) {
        this.uploaded_file = obj_upload_file;
        this.document_id = obj_doc_id;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("document_id")
    public String document_id_res;

    @SerializedName("result")
    public List<IllnessDocUpload_Datum> result = null;

    public class IllnessDocUpload_Datum {

        @SerializedName("image")
        public String image;

    }
}
