package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResendOtp {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("login_ids")
    public String login_ids;

    public ResendOtp(String user_types, String login_id) {
        this.user_types = user_types;
        this.login_ids = login_id;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("response")
    public List<ROtpModel> response = null;

    public class ROtpModel {
        @SerializedName("user_type")
        public String user_type;

        @SerializedName("login_id")
        public String login_id;

        @SerializedName("otp")
        public String otp;

    }


}
