package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileImageUpload_RetroModel {

    @SerializedName("uploaded_file")
    public String uploaded_file;

    public ProfileImageUpload_RetroModel(String img) {
        this.uploaded_file = img;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public List<ImgUploadDatum> result = null;

    public class ImgUploadDatum{

        @SerializedName("image")
        public String image;

    }
}
