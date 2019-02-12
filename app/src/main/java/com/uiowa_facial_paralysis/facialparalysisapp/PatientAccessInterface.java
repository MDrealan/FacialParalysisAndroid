package com.uiowa_facial_paralysis.facialparalysisapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

//this is for defining the read/write operations to the database table. (for the Patient database table).

@Dao
public interface PatientAccessInterface
{
    @Insert
    void insertOnlySinglePatient(Patient patient);

    @Query ("SELECT * FROM Patient WHERE username = :patient_username")
    Patient getPatientViaUserName (String patient_username);

    @Update
    void updatePatient (Patient patient);

    @Delete
    void deletePatient (Patient patient);
}
