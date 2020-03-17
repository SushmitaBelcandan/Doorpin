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
                                   String str_disease_type, String str_follow_up_date, String str_doc_link, String str_prsec_link) {
        this.user_types = str_usr_type;
        this.user_ids = str_usr_id;
        this.names = str_name;
        this.emails = str_email;
        this.dobs = str_dob;
        this.mobiles = str_mobile;
        this.genders = str_gender;
        this.maritals = str_marital_status;
        this.addresses = str_address;
        this.disease_types = str_disease_type;
        this.follow_up_dates = str_follow_up_date;
        this.doc_links = str_doc_link;
        this.prescription_links = str_prsec_link;
    }

    @SerializedName("genders")
    public String genders;

    @SerializedName("maritals")
    public String maritals;

    @SerializedName("addresses")
    public String addresses;

    @SerializedName("disease_types")
    public String disease_types;

    @SerializedName("follow_up_dates")
    public String follow_up_dates;

    @SerializedName("doc_links")
    public String doc_links;

    @SerializedName("prescription_links")
    public String prescription_links;

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

}
