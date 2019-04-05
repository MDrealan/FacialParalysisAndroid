package com.uiowa_facial_paralysis.facialparalysisapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

//this is the interface to create a new patient, or access an existing patients data (after a db query request has been made from the PatientAccessInterface class).

@Entity
public class Patient
{
    @NonNull
    @PrimaryKey
    private String patientID;

    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "hashed_password")
    private String hashed_password;
    @ColumnInfo(name = "current_form")
    private int current_form;

    //make a new patient
    public Patient(String username, String email, String hashed_password, int current_form)
    {
        this.username = username;
        this.email = email;
        this.hashed_password = hashed_password;
        this.current_form = current_form;
        this.patientID = username;
    }

    public int getCurrent_form() {
        return current_form;
    }

    public void setCurrent_form(int current_form) {
        this.current_form = current_form;
    }


///GETTERS AND SETTERS

    @NonNull
    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(@NonNull String patientID) {
        this.patientID = patientID;
    }

    @NonNull


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashed_password() {
        return hashed_password;
    }

    public void setHashed_password(String hashed_password) {
        this.hashed_password = hashed_password;
    }
}
