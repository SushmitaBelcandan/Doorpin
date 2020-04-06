package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

public class Delete_Disease_Retro_Model {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("patient_ids")
    public String patient_ids;

    @SerializedName("disease_ids")
    public String disease_ids;

    public Delete_Disease_Retro_Model(String userTypes, String userId, String patientId, String diseaseId) {
        this.user_types = userTypes;
        this.user_ids = userId;
        this.patient_ids = patientId;
        this.disease_ids = diseaseId;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;
}
