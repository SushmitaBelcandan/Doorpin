package com.app.doorpin.retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @POST("searchPatient")
    Call<SearchPatient_RetroModel> getPatientList(@Body SearchPatient_RetroModel data);

    @POST("newSurgeriesPatientSearch")
    Call<NewSurgSearch_RetroModel> getListSurgPatient(@Body NewSurgSearch_RetroModel data);

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

    @POST("patientPersonalDetails")
    Call<Patient_PersInfo_RetroModel> getPersonalInfo(@Body Patient_PersInfo_RetroModel data);

    @POST("patientPersonalDetailsEdit")
    Call<Edit_PersInfo_Retro_Model> updatePersonalInfo(@Body Edit_PersInfo_Retro_Model data);

    @POST("patientIllnessDetails")
    Call<Illness_List_Retro_Model> getIllnessList(@Body Illness_List_Retro_Model data);

    @POST("patientIllnessDiseaseEdit")
    Call<Edit_Disease_Retro_Model> updateDisease(@Body Edit_Disease_Retro_Model data);

    @POST("newPatient")
    Call<New_Patient_Retro_Model> addPatient(@Body New_Patient_Retro_Model data);

    @Multipart
    @POST("patientIllnessDocUpload")
    Call<IllnessDocUpload_RetroModel> uploadDocsIllness(@Part MultipartBody.Part file, @Part("document_id") RequestBody text);

    @Multipart
    @POST("patientIllnessDocUpload")
    Call<FileUploadModel> uploadDocuments(@Part MultipartBody.Part file, @Part("document_id") RequestBody text);


    @POST("doctor_list")
    Call<DoctorList_RetroModel> getDoctorList(@Body DoctorList_RetroModel data);

    @POST("patientIllnessDocUpdate")
    Call<DocumentUpdate_RetroModel> updateDocs(@Body DocumentUpdate_RetroModel data);

    @POST("userProfile")
    Call<UserProfile_Retro_Model> getProfileData(@Body UserProfile_Retro_Model data);

    /*@Multipart
    @POST("profileDocUpload")
    Call<ProfileDocUpload_RetroModel> uploadProfileDoc(@Part MultipartBody.Part file, @Part("document_ids") RequestBody text);
*/
    @Multipart
    @POST("profileDocUpload")
    Call<FileUploadModel> uploadProfileDocument(@Part MultipartBody.Part file, @Part("document_id") RequestBody text,@Part("user_types") RequestBody usrTypes);


    @POST("profileDocUpdate")
    Call<ProfileDocUpdate_RetroModel> updateProfileDocs(@Body ProfileDocUpdate_RetroModel data);

    @Multipart
    @POST("profileImageUpload")
    Call<ProfileImageUpload_RetroModel> uploadProfileImage(@Part MultipartBody.Part file);

    @POST("profileImageUpdate")
    Call<UpdateProfileImg_RetroModel> updateProfileImg(@Body UpdateProfileImg_RetroModel data);

    @POST("userProfileEdit")
    Call<EditProfile_RetroModel> editProfile(@Body EditProfile_RetroModel data);

    @POST("add_new_disease")
    Call<NewDisease_RetroModel> addDiease(@Body NewDisease_RetroModel data);

    @POST("patientIllnessDiseaseDelete")
    Call<Delete_Disease_Retro_Model> deleteDisease(@Body Delete_Disease_Retro_Model data);

    @POST("surgeryListPatientDetails")
    Call<SurgeryList_RetroModel> getSurgeryList(@Body SurgeryList_RetroModel data);

    @POST("surgeryPatientDelete")
    Call<DeleteSurgPatient_RetroModel> deletePatient(@Body DeleteSurgPatient_RetroModel data);

    @POST("newSurgery")
    Call<NewSurgery_RetroModel> addSurgery(@Body NewSurgery_RetroModel data);

    @POST("surgeriesPatientSearch")
    Call<SearchSurgPatient_RetroModel> searchPatientSurg(@Body SearchSurgPatient_RetroModel data);

}
