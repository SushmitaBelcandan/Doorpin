package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

public class EditProfile_RetroModel {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("names")
    public String names;

    @SerializedName("mobilenos")
    public String mobilenos;

    @SerializedName("educations")
    public String educations;

    @SerializedName("specializations")
    public String specializations;

    @SerializedName("experiences")
    public String experiences;

    @SerializedName("addresses")
    public String addresses;


    public EditProfile_RetroModel(String str_usr_types, String str_usr_ids, String str_names,
                                  String str_mobilenos, String str_educations, String str_specializations,
                                  String str_experiences, String str_addresses) {
        this.user_types = str_usr_types;
        this.user_ids = str_usr_ids;
        this.names = str_names;
        this.mobilenos = str_mobilenos;
        this.educations = str_educations;
        this.specializations = str_specializations;
        this.experiences = str_experiences;
        this.addresses = str_addresses;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

}
