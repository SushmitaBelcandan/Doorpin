package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FP_Verify {

    @SerializedName("user_types")
    public String userTypes;
    @SerializedName("login_ids")
    public String loginIds;
    @SerializedName("otps")
    public String otps;

    public FP_Verify(String userTypes, String loginIds, String otps) {
        this.userTypes = userTypes;
        this.loginIds = loginIds;
        this.otps = otps;
    }
    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
    @SerializedName("response")
    public List<FPVerify> response=null;

    public class FPVerify{
        @SerializedName("phone_id")
        public String phone_id;



    }




}
