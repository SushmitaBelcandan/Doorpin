package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FP_Model {
    @SerializedName("user_types")
    public String userTypes;

    @SerializedName("login_ids")
    public String loginIds;

    public FP_Model(String userTypes, String loginIds) {
        this.userTypes = userTypes;
        this.loginIds = loginIds;
    }
    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("response")
    public List<FPModelDatum> response=null;

    public class FPModelDatum{
        @SerializedName("user_type")
        public String userType;

        @SerializedName("login_id")
        public String loginId;

        @SerializedName("otp")
        public String otp;


    }
}
