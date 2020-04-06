package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DocumentUpdate_RetroModel {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("patient_ids")
    public String patient_ids;

    @SerializedName("doctor_newsurgeries_patients_ids")
    public String doctor_newsurgeries_patients_ids;

    @SerializedName("disease_ids")
    public String disease_ids;

    @SerializedName("document_ids")
    public String document_ids;

    @SerializedName("doc_datas")
    public Object doc_datas;

    public DocumentUpdate_RetroModel(String usrType, String usrId, String patientId, String patient_tbl_id, String diseaseId, String docsId, Object doc_data) {
        this.user_types = usrType;
        this.user_ids = usrId;
        this.patient_ids = patientId;
        this.doctor_newsurgeries_patients_ids = patient_tbl_id;
        this.disease_ids = diseaseId;
        this.document_ids = docsId;
        this.doc_datas = doc_data;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("user_type")
    public String user_type;

    @SerializedName("user_id")
    public String user_id;

    @SerializedName("patient_id")
    public String patient_id;

    @SerializedName("disease_id")
    public String disease_id;

    @SerializedName("document_id")
    public String document_id;

    @SerializedName("response")
    public List<DocUpdate_Datum> response = null;

    public class DocUpdate_Datum {

        @SerializedName("doc_id")
        public String doc_id;

        @SerializedName("doc_link")
        public String doc_link;
    }

}
