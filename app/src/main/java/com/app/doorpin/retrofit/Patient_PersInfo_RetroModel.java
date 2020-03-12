package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Patient_PersInfo_RetroModel {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("patient_ids")
    public String patient_ids;

    public Patient_PersInfo_RetroModel(String str_usr_type, String str_usr_id, String str_patient_id) {
        this.user_types = str_usr_type;
        this.user_ids = str_usr_id;
        this.patient_ids = str_patient_id;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public List<PatientPersonalInfo_Datum> result = null;

    public class PatientPersonalInfo_Datum {

        @SerializedName("patient_id")
        public String patient_id;

        @SerializedName("patient_name")
        public String patient_name;

        @SerializedName("patient_email_id")
        public String patient_email_id;

        @SerializedName("patient_dob")
        public String patient_dob;

        @SerializedName("patient_mobile")
        public String patient_mobile;

        @SerializedName("patient_gender")
        public String patient_gender;

        @SerializedName("Patient_marital_status")
        public String Patient_marital_status;

        @SerializedName("patient_age")
        public String patient_age;

        @SerializedName("patient_address")
        public String patient_address;

    }

}
