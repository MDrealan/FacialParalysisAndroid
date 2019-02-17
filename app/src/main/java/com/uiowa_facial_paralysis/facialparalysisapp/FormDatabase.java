package com.uiowa_facial_paralysis.facialparalysisapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = { Form.class, Patient.class }, version = 1)
public abstract class FormDatabase extends RoomDatabase
{
    public abstract FormAccessInterface getFormAccessInterface();
    public abstract PatientAccessInterface getPatientAccessInterface();
}
