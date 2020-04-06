package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomePage_RetroModel {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    public HomePage_RetroModel(String usrType, String usrId) {
        this.user_types = usrType;
        this.user_ids = usrId;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("user_name")
    public String user_name;

    @SerializedName("result")
    public List<HomePage_Datum> response = null;

    public class HomePage_Datum {

        @SerializedName("patient_id")
        public String patient_id;

        @SerializedName("patient_name")
        public String patient_name;

        @SerializedName("display_id")
        public String display_id;
    }

}
