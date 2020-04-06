package com.app.doorpin.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileDocUpdate_RetroModel {

    @SerializedName("user_types")
    public String user_types;

    @SerializedName("user_ids")
    public String user_ids;

    @SerializedName("document_ids")
    public String document_ids;

    @SerializedName("doc_datas")
    public Object doc_datas;

    public ProfileDocUpdate_RetroModel(String usrType, String usrId, String docId, Object docDatas1) {
        this.user_types = usrType;
        this.user_ids = usrId;
        this.document_ids = docId;
        this.doc_datas = docDatas1;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("user_type")
    public String user_type;

    @SerializedName("user_id")
    public String user_id;

    @SerializedName("document_id")
    public String document_id;

    @SerializedName("response")
    public List<Response_Datum> response = null;

    public class Response_Datum {

        @SerializedName("doc_link")
        public String doc_link;
    }

}
