package com.app.doorpin.retrofit;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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
    Call<RP_Model> resetPassword(@Body RP_Model rp_model);

    @POST("resendOTP")
    Call<ResendOtp> resendOtp(@Body ResendOtp resendOtp);

    @POST("surgeryList")
    Call<SurgList_DP_RetroModel> getDocProfileSurgeryList(@Body SurgList_DP_RetroModel data);

    @POST("logout")
    Call<Logout_RetroModel> logoutUser(@Body Logout_RetroModel data);

    @Multipart
    @POST("patientIllnessDocUpload")
    Call<IllnessDocUpload_RetroModel> uploadDocsIllness(@Part MultipartBody.Part file);

    @POST("patientPersonalDetails")
    Call<Patient_PersInfo_RetroModel> getPersonalInfo(@Body Patient_PersInfo_RetroModel data);

    @POST("patientPersonalDetailsEdit")
    Call<Edit_PersInfo_Retro_Model> updatePersonalInfo(@Body Edit_PersInfo_Retro_Model data);

}
