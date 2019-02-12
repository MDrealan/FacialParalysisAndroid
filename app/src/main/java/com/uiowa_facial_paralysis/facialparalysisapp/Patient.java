package com.uiowa_facial_paralysis.facialparalysisapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

//this is the interface to create a new patient, or access an existing patients data.

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

    //make a new patient
    public Patient()
    {

    }








    ///GETTERS AND SETTERS

    @NonNull
    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(@NonNull String patientID) {
        this.patientID = patientID;
    }

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
