package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileDocUpload_RetroModel {

    @SerializedName("uploaded_file")
    public String uploaded_file;

    @SerializedName("document_ids")
    public String document_ids;

    public ProfileDocUpload_RetroModel(String obj_upload_file, String obj_doc_id) {
        this.uploaded_file = obj_upload_file;
        this.document_ids = obj_doc_id;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public List<ProfileDocUpload_Datum> result = null;

    public class ProfileDocUpload_Datum {

        @SerializedName("image")
        public String image;

    }
}
