package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

public class DeleteSurgPatient_RetroModel {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("patienttable_ids")
    public String patienttable_ids;


    public DeleteSurgPatient_RetroModel(String str_usr_types, String str_usr_ids, String str_patienttable_ids) {
        this.user_types = str_usr_types;
        this.user_ids = str_usr_ids;
        this.patienttable_ids = str_patienttable_ids;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;
}
