package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoginRequest {

    @SerializedName("user_types")
    public String userTypes;

    @SerializedName("login_ids")
    public String loginIds;

    @SerializedName("passwords")
    public String passwords;

    @SerializedName("device_ids")
    public String deviceIds;

    @SerializedName("device_tokens")
    public String deviceTokens;

    public LoginRequest(String userTypes, String loginIds, String passwords, String deviceIds, String deviceTokens) {
        this.userTypes = userTypes;
        this.loginIds = loginIds;
        this.passwords = passwords;
        this.deviceIds = deviceIds;
        this.deviceTokens = deviceTokens;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("response")
    public List<Login_Datum> response = null;

    public class Login_Datum {

        @SerializedName("user_type")
        public String user_type;

        @SerializedName("user_id")
        public String user_id;

        @SerializedName("login_id")
        public String login_id;

    }


}
