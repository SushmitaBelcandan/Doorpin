package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

public class New_Patient_Retro_Model {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("names")
    public String names;

    @SerializedName("emails")
    public String emails;

    @SerializedName("dobs")
    public String dobs;

    @SerializedName("mobiles")
    public String mobiles;

    public New_Patient_Retro_Model(String str_usr_type, String str_usr_id, String str_name, String str_email,
                                   String str_dob, String str_mobile, String str_gender, String str_marital_status, String str_address,
                                   String str_disease_type, String str_doctor_id, String str_follow_up_date, Object obj_prsec,
                                   Object obj_report, Object obj_others) {
        this.user_types = str_usr_type;
        this.user_ids = str_usr_id;
        this.names = str_name;
        this.emails = str_email;
        this.dobs = str_dob;
        this.mobiles = str_mobile;
        this.genders = str_gender;
        this.maritals = str_marital_status;
        this.addresses = str_address;
        this.disease_names = str_disease_type;
        this.doctor_ids = str_doctor_id;
        this.follow_up_dates = str_follow_up_date;
        this.prescriptions = obj_prsec;
        this.reports = obj_report;
        this.others = obj_others;
    }

    @SerializedName("genders")
    public String genders;

    @SerializedName("maritals")
    public String maritals;

    @SerializedName("addresses")
    public String addresses;

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

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

}
