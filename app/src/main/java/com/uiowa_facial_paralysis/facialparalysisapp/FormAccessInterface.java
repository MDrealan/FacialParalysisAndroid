package com.uiowa_facial_paralysis.facialparalysisapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

@Dao
public interface FormAccessInterface
{
    @Insert
    void insert(Form new_form);

    @Update
    void update(Form form_to_update);

    @Delete
    void delete(Form... form_to_delete);

    @Query("SELECT * FROM form WHERE patientID=:patientID")
    List<Form> getPatientForms(final int patientID);

    @Query("SELECT * FROM form WHERE patientID=:patientID AND isNewForm=:isNewForm")
    List<Form> getPatientNewForms(final int patientID, final boolean isNewForm);
}
