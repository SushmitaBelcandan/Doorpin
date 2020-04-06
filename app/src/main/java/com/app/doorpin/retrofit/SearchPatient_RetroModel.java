package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchPatient_RetroModel {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("patient_ids")
    public String patient_ids;

    @SerializedName("patient_names")
    public String patient_names;

    public SearchPatient_RetroModel(String usrType, String usrId, String patientId, String patientName) {
        this.user_types = usrType;
        this.user_ids = usrId;
        this.patient_ids = patientId;
        this.patient_names = patientName;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public List<Search_Datum> result = null;

    public class Search_Datum {

        @SerializedName("patient_id")
        public String patient_id;

        @SerializedName("patient_name")
        public String patient_name;

        @SerializedName("display_id")
        public String display_id;
    }

}
