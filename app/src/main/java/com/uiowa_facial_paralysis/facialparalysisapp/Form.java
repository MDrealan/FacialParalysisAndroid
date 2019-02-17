package com.uiowa_facial_paralysis.facialparalysisapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity
public class Form
{
    private static int currentFormNumber = 0; //Todo:: this is present on class creation, but doesn't persist beyond app closing/opening again.

    @PrimaryKey public int id;
    private String name;
    private String formType;
    private String userAnswers;

    private boolean isComplete = false;
    private boolean isQuestionDone = false;
    private boolean isPhotoDone = false;
    @ColumnInfo(name = "isNewForm")
    private boolean isNewForm = true; //new forms for export. if this is false, it won't be exported.

    private int faceScore;
   // private ArrayList<String> userAnswers;
    //private String[] userAnswers;

    public final int patientID;

    public Form(String name,String formType, final int patientID)
    {
        this.id = currentFormNumber;
        currentFormNumber++;
        this.name = name;
        this.formType = formType;
        this.patientID = patientID;
    }





    //GETTERS/SETTERS


    public String getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(String userAnswers) {
        this.userAnswers = userAnswers;
    }

    public int getId() {
        return id;
    }

    public int getPatientID() {
        return patientID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isQuestionDone() {
        return isQuestionDone;
    }

    public void setQuestionDone(boolean questionDone) {
        isQuestionDone = questionDone;
    }

    public boolean isPhotoDone() {
        return isPhotoDone;
    }

    public void setPhotoDone(boolean photoDone) {
        isPhotoDone = photoDone;
    }

    public int getFaceScore() {
        return faceScore;
    }

    public void setFaceScore(int faceScore) {
        this.faceScore = faceScore;
    }

    public boolean isNewForm() {
        return isNewForm;
    }

    public void setNewForm(boolean newForm) {
        isNewForm = newForm;
    }
}
