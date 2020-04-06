package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserProfile_Retro_Model {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    public UserProfile_Retro_Model(String usrType, String usrId) {
        this.user_types = usrType;
        this.user_ids = usrId;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("user_id")
    public String user_id;

    @SerializedName("user_name")
    public String user_name;

    @SerializedName("designation")
    public String designation;

    @SerializedName("experience_years")
    public String experience_years;

    @SerializedName("mobile")
    public String mobile;

    @SerializedName("qualifications")
    public String qualifications;

    @SerializedName("address")
    public String address;

    @SerializedName("profile_pic")
    public String profile_pic;

    @SerializedName("genunity_doc")
    public List<GenunityDoc_Datum> genunity_doc = null;

    @SerializedName("experience_doc")
    public List<ExperienceDoc_Datum> experience_doc = null;

    @SerializedName("surgery_data")
    public List<UserProfile_Datum> surgery_data = null;

    public class ExperienceDoc_Datum {

        @SerializedName("doc_exp")
        public String doc_exp;
    }

    public class GenunityDoc_Datum {

        @SerializedName("doc_gen")
        public String doc_gen;
    }

    public class UserProfile_Datum {

        @SerializedName("surgery_id")
        public String surgery_id;

        @SerializedName("surgery_name")
        public String surgery_name;
    }


}
