package com.app.doorpin.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("searchPatient")
    Call<SearchPatient_RetroModel> getPatientList(@Body SearchPatient_RetroModel data);

    @POST("homeScreen")
    Call<HomePage_RetroModel> getPatientListHomePage(@Body HomePage_RetroModel data);

    @POST("login")
    Call<LoginRequest> getLogin(@Body LoginRequest loginRequest);

    @POST("forgotPassword")
    Call<FP_Model> getPassword(@Body FP_Model fp_model);

    @POST("forgotPasswordVerify")
    Call<FP_Verify> verifyForgotPassword(@Body FP_Verify fp_verify);

    @POST("resetPassword")
    Call<RP_Model> resetPassword(@Body RP_Model rp_model );

    @POST("resendOTP")
    Call<ResendOtp> resendOtp(@Body ResendOtp resendOtp);

}
