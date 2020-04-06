package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

public class NewDisease_RetroModel {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("patient_ids")
    public String patient_ids;

    @SerializedName("disease_names")
    public String disease_names;

    @SerializedName("doctor_ids")
    public String doctor_ids;

    @SerializedName("follow_up_dates")
    public String follow_up_dates;

    @SerializedName("prescriptions")
    public Object prescriptions;

    @SerializedName("reports")
    public Object reports;

    @SerializedName("others")
    public Object others;

    public NewDisease_RetroModel(String str_usr_type, String str_usr_id, String patient_id, String str_disease_names,
                                 String str_doctor_id, String str_follow_up_date, Object obj_prsec,
                                 Object obj_report, Object obj_others) {
        this.user_types = str_usr_type;
        this.user_ids = str_usr_id;
        this.patient_ids = patient_id;
        this.disease_names = str_disease_names;
        this.doctor_ids = str_doctor_id;
        this.follow_up_dates = str_follow_up_date;
        this.prescriptions = obj_prsec;
        this.reports = obj_report;
        this.others = obj_others;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;
}
