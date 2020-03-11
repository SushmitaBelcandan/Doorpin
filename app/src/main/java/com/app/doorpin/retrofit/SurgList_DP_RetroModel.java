package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SurgList_DP_RetroModel {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    public SurgList_DP_RetroModel(String usrType, String usrId) {
        this.user_types = usrType;
        this.user_ids = usrId;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public List<SurgList_DP_Datum> result = null;

    public class SurgList_DP_Datum {

        @SerializedName("surgery_id")
        public String surgery_id;

        @SerializedName("surgery_name")
        public String surgery_name;
    }


}
