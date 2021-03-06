package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Illness_List_Retro_Model {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("patient_ids")
    public String patient_ids;

    public Illness_List_Retro_Model(String str_usr_types, String str_usr_ids, String str_patient_ids) {
        this.user_types = str_usr_types;
        this.user_ids = str_usr_ids;
        this.patient_ids = str_patient_ids;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("user_type")
    public String user_type;

    @SerializedName("user_id")
    public String user_id;

    @SerializedName("patient_id")
    public String patient_id;

    @SerializedName("result")
    public List<Illness_List_Datum> result = null;

    public class Illness_List_Datum {

        @SerializedName("disease_id")
        public String disease_id;

        @SerializedName("disease_name")
        public String disease_name;

        @SerializedName("follow_up_date")
        public String follow_up_date;

        @SerializedName("presc_data")
        public Object presc_data;

        @SerializedName("report_data")
        public Object report_data;

        @SerializedName("otherdoc_data")
        public Object otherdoc_data;

    }


}
