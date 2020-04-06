package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SurgeryList_RetroModel {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("hospital_ids")
    public String hospital_ids;

    public SurgeryList_RetroModel(String userType, String userId, String hospitalId) {
        this.user_types = userType;
        this.user_ids = userId;
        this.hospital_ids = hospitalId;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public List<ResultDatum> result = null;

    public class ResultDatum {
        @SerializedName("surgery_id")
        public String surgery_id;

        @SerializedName("surgery_name")
        public String surgery_name;

        @SerializedName("patient_data")
        public List<PatientDatum> patient_data = null;
    }

    public class PatientDatum {

        @SerializedName("patienttable_id")
        public String patienttable_id;

        @SerializedName("patient_id")
        public String patient_id;

        @SerializedName("patient_name")
        public String patient_name;

        @SerializedName("display_id")
        public String display_id;

        @SerializedName("document_data")
        public List<DocsDatum> document_data = null;

        @SerializedName("file_data")
        public List<FileDatum> file_data = null;

    }

    public class DocsDatum {
        @SerializedName("document_link")
        public String document_link = null;

    }

    public class FileDatum {
        @SerializedName("document_link")
        public String document_link1 = null;

    }
}
