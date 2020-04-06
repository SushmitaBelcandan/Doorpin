package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DoctorList_RetroModel {

    @SerializedName("hospital_ids")
    public String hospital_ids;

    public DoctorList_RetroModel(String hospital_id) {
        hospital_ids = hospital_id;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public List<Doctor_List_Datum> result = null;

    public class Doctor_List_Datum {

        @SerializedName("doctor_id")
        public String doctor_id;

        @SerializedName("doctor_name")
        public String doctor_name;
    }

}
