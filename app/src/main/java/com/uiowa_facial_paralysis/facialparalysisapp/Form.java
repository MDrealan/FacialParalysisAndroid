package com.uiowa_facial_paralysis.facialparalysisapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;


@Entity
public class Form
{
    private static int currentFormNumber = 0; //Todo:: this is present on class creation, but doesn't persist beyond app closing/opening again.

    @PrimaryKey public long id;
    private long formID;
    private String name;
    private String formType;
    private String userAnswers;
    private String username;
    private String formQuestions;

    private boolean isComplete = false;
    private boolean isQuestionDone = false;
    private boolean isPhotoDone = false;
    @ColumnInfo(name = "isNewForm")
    private boolean isNewForm = true; //new forms for export. if this is false, it won't be exported.

    private int faceScore;

   // @TypeConverters(ImageConverter.class)
    //private ArrayList<byte[]> images;
    //This is such poor programming it hurts.
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image1;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image2;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image3;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image4;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image5;


    public static String pattern = "yyyy-MM-dd";
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private String formDate;

   // private ArrayList<String> userAnswers;
    //private String[] userAnswers;

    public final String patientID;

    public Form(String name,String formType, String username, final String patientID)
    {
        this.formID = new Date().getTime();
        this.id = formID;
        currentFormNumber++;
        this.name = name;
        this.username = username;
        this.formType = formType;
        this.patientID = patientID;
        this.faceScore = 0;
        this.formDate = simpleDateFormat.format(new Date());
    }





    //GETTERS/SETTERS
    @Override public String toString()
    {
        String curr_name = this.username;
        String formType = this.getFormType();
        String formScore = Integer.toString(this.getFaceScore());
        String formAnswers = this.getUserAnswers();
        return curr_name + " , " + formType + " , " + formScore + " , " + formAnswers;
    }

    public byte[] getImage1() {
        return image1;
    }

    public void setImage1(byte[] image) {
        this.image1 = image;
    }

    public long getFormID() {
        return formID;
    }

    public void setFormID(long formID) {
        this.formID = formID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String patientEmail) {
        this.username = patientEmail;
    }

    public String getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(String userAnswers) {
        this.userAnswers = userAnswers;
    }

    public long getId() {
        return id;
    }

    public String getPatientID() {
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

    public String getFormDate() {
        return formDate;
    }

    public void setFormDate(String formDate) {
        this.formDate = formDate;
    }

    public void setNewForm(boolean newForm) {
        isNewForm = newForm;
    }

    public String getFormQuestions() {
        return formQuestions;
    }

    public void setFormQuestions(String formQuestions) {
        this.formQuestions = formQuestions;
    }

    public byte[] getImage2() {
        return image2;
    }

    public void setImage2(byte[] image2) {
        this.image2 = image2;
    }

    public byte[] getImage3() {
        return image3;
    }

    public void setImage3(byte[] image3) {
        this.image3 = image3;
    }

    public byte[] getImage4() {
        return image4;
    }

    public void setImage4(byte[] image4) {
        this.image4 = image4;
    }

    public byte[] getImage5() {
        return image5;
    }

    public void setImage5(byte[] image5) {
        this.image5 = image5;
    }
}
