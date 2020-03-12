package com.app.doorpin.reference;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    SharedPreferences sPref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "SessionData";
    private static final String IS_LOGIN = "IsLoggedIn";

    private static final String DOCTOR_NURSE_ID = "doctor_nurse_id";
    private static final String LOG_USR_ID = "user_id";
    private static final String LOGIN_ID = "login_id";
    private static final String DOC_TYPE = "doc_type";
    private static final String DEVICE_ID = "device_id";
    private static final String PATIENT_ID_HOME = "patient_id_home";
    private static final String PATIENT_NAME_HOME = "patient_name_home";


    public SessionManager(Context context) {
        this._context = context;
        sPref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sPref.edit();
    }

    public boolean isLoggedIn() {
        return sPref.getBoolean(IS_LOGIN, false);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    //------save patient id home page----------
    public void savePatientIdHome(String patient_id, String patient_name) {
        editor.putString(PATIENT_ID_HOME, patient_id);
        editor.putString(PATIENT_NAME_HOME, patient_name);
        editor.commit();
    }

    public String getPatientIdHome() {
        return sPref.getString(PATIENT_ID_HOME, null);
    }

    public String getPatientNameHome() {
        return sPref.getString(PATIENT_NAME_HOME, null);
    }

    //------save doctor and nurse id----------
    public void saveDoctorNurseId(String doc_nurse_id) {
        editor.putString(DOCTOR_NURSE_ID, doc_nurse_id);
        editor.commit();
    }

    public String getDoctorNurseId() {
        return sPref.getString(DOCTOR_NURSE_ID, null);
    }

    //------save device id----------
    public void saveDeviceId(String device_id) {
        editor.putString(DEVICE_ID, device_id);
        editor.commit();
    }

    public String getDeviceId() {
        return sPref.getString(DEVICE_ID, null);
    }

    //------save login details----------
    public void saveLoginData(String usrId, String usrLoginId) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(LOG_USR_ID, usrId);
        editor.putString(LOGIN_ID, usrLoginId);
        editor.commit();
    }

    public String getLoggedUsrId() {
        return sPref.getString(LOG_USR_ID, null);
    }

    public String getUsrLoginId() {
        return sPref.getString(LOGIN_ID, null);
    }

    //save document type
    public void saveDocType(String schl_name) {
        editor.putString(DOC_TYPE, schl_name);
        editor.commit();
    }

    public String getDocType() {
        return sPref.getString(DOC_TYPE, null);
    }

}
