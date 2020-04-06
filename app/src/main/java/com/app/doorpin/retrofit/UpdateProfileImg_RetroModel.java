package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileImg_RetroModel {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("files")
    public String files;

    public UpdateProfileImg_RetroModel(String usrType, String usrId, String fileName) {
        this.user_types = usrType;
        this.user_ids = usrId;
        this.files = fileName;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("user_type")
    public String user_type;

    @SerializedName("user_id")
    public String user_id;

    @SerializedName("file")
    public String file;

}
