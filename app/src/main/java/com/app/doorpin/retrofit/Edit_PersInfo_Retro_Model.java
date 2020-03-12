package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Edit_PersInfo_Retro_Model {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("patient_ids")
    public String patient_ids;

    @SerializedName("patient_names")
    public String patient_names;

    @SerializedName("patient_email_ids")
    public String patient_email_ids;

    @SerializedName("patient_dobs")
    public String patient_dobs;

    @SerializedName("patient_mobiles")
    public String patient_mobiles;

    @SerializedName("patient_genders")
    public String patient_genders;

    @SerializedName("Patient_marital_statuss")
    public String Patient_marital_statuss;

    @SerializedName("patient_addresss")
    public String patient_addresss;

    public Edit_PersInfo_Retro_Model(String str_usr_type, String str_usr_id, String str_patient_id, String str_patient_name,
                                     String str_patient_email, String str_patient_dob, String str_patient_mobile, String str_patient_gender,
                                     String str_marital_status, String str_patient_addrss) {
        this.user_types = str_usr_type;
        this.user_ids = str_usr_id;
        this.patient_ids = str_patient_id;
        this.patient_names = str_patient_name;
        this.patient_email_ids = str_patient_email;
        this.patient_dobs = str_patient_dob;
        this.patient_mobiles = str_patient_mobile;
        this.patient_genders = str_patient_gender;
        this.Patient_marital_statuss = str_marital_status;
        this.patient_addresss = str_patient_addrss;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;


}
