package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SurgList_DP_RetroModel {

    @SerializedName("hospital_ids")
    public String hospital_ids;

    public SurgList_DP_RetroModel(String hospitalId) {
        this.hospital_ids = hospitalId;
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
