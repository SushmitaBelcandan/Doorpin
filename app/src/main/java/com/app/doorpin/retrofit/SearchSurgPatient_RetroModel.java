package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchSurgPatient_RetroModel {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("patient_ids")
    public String patient_ids;

    @SerializedName("patient_names")
    public String patient_names;

    @SerializedName("surgery_ids")
    public String surgery_ids;

    public SearchSurgPatient_RetroModel(String usrType, String usrId, String patientId, String patientName, String surgeryId) {
        this.user_types = usrType;
        this.user_ids = usrId;
        this.patient_ids = patientId;
        this.patient_names = patientName;
        this.surgery_ids = surgeryId;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public List<Search_Datum> result = null;

    public class Search_Datum {

        @SerializedName("patienttable_id")
        public String patienttable_id;

        @SerializedName("patient_id")
        public String patient_id;

        @SerializedName("display_id")
        public String display_id;

        @SerializedName("patient_name")
        public String patient_name;

        @SerializedName("document_data")
        public List<SurgeryDocsDatum> document_data = null;

        @SerializedName("file_data")
        public List<SurgeryFileDatum> file_data = null;
    }

    public class SurgeryDocsDatum {
        @SerializedName("document_link")
        public String document_link = null;

    }

    public class SurgeryFileDatum {
        @SerializedName("file_link")
        public String file_link = null;

    }

}
