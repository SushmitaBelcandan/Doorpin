package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RP_Model {
    @SerializedName("user_types")
    public String user_types;

    @SerializedName("login_ids")
    public String login_ids;

    @SerializedName("passwords")
    public String passwords;

    public RP_Model(String user_types, String login_ids, String passwords) {
        this.user_types = user_types;
        this.login_ids = login_ids;
        this.passwords = passwords;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("response")
    public List<RPModel> response = null;

    public class RPModel {
        @SerializedName("user_type")
        public String user_type;

        @SerializedName("user_id")
        public String user_id;

        @SerializedName("login_id")
        public String login_id;
    }

}
