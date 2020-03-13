package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Edit_Disease_Retro_Model {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("patient_ids")
    public String patient_ids;

    @SerializedName("disease_ids")
    public String disease_ids;

    @SerializedName("disease_names")
    public String disease_names;

    @SerializedName("follow_up_dates")
    public String follow_up_dates;

    public Edit_Disease_Retro_Model(String str_usr_types, String str_usr_ids, String str_patient_ids,
                                    String str_disease_id, String str_disease_name, String str_follow_up_date) {
        this.user_types = str_usr_types;
        this.user_ids = str_usr_ids;
        this.patient_ids = str_patient_ids;
        this.disease_ids = str_disease_id;
        this.disease_names = str_disease_name;
        this.follow_up_dates = str_follow_up_date;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("response")
    public List<Edit_Disease_Datum> response = null;

    public class Edit_Disease_Datum {

        @SerializedName("user_type")
        public String user_types_r;

        @SerializedName("user_id")
        public String user_ids_r;

        @SerializedName("patient_id")
        public String patient_ids_r;

        @SerializedName("disease_id")
        public String disease_ids_r;

        @SerializedName("disease_name")
        public String disease_names_r;

        @SerializedName("follow_up_date")
        public String follow_up_dates_r;

    }

}
