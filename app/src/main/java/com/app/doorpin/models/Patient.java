package com.app.doorpin.models;

public class Patient {

    public String patientname;
    public String patientid;

    public Patient(String patientname, String patientid) {
        this.patientname = patientname;
        this.patientid = patientid;
    }


    public String getPatientname() {
        return patientname;
    }

    public void setPatientname(String patientname) {
        this.patientname = patientname;
    }

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

}
