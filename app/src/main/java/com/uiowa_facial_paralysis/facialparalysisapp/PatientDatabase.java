package com.uiowa_facial_paralysis.facialparalysisapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Patient.class}, version = 1, exportSchema = false)
public abstract class PatientDatabase extends RoomDatabase
{
    public abstract PatientAccessInterface patientAccessInterface();
}
