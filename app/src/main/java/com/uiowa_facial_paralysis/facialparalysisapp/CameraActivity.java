package com.uiowa_facial_paralysis.facialparalysisapp;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CameraActivity extends AppCompatActivity {

    private CameraDevice mCamera;
    private SurfaceHolder mHolder;
    private ImageView mView;
    private Uri imageUri;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private String currentPhotoPath;
    private static final int REQUEST_TAKE_PHOTO = 1;

    //db and user variables (user vars passed around activities)
    private long formID; //ID of the form.
    private boolean questionsDone;
    private String username;

    private PatientDatabase patientDB;
    private FormDatabase formDB;
    private Form newForm;
    private Patient currPatient;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //get the variables from the previous activity.
        username = getIntent().getStringExtra("USERNAME");
        formID = Long.parseLong(getIntent().getStringExtra("FORMID")); //current form ID
        questionsDone = getIntent().getBooleanExtra("QUESTIONSDONE", false); //see if the photos are done.

        patientDB = Room.databaseBuilder(getApplicationContext(), PatientDatabase.class, "patient_db").allowMainThreadQueries().build(); //allow main thread queries issue may lock UI while querying DB.
        formDB = Room.databaseBuilder(getApplicationContext(), FormDatabase.class, "form_db").allowMainThreadQueries().build(); //allow main thread queries issue may lock UI while querying DB.
        currPatient = patientDB.patientAccessInterface().getPatientViaUserName(username);

        //set up a new form to add pictures to.
        newForm = new Form("not_implemented", "FACE", currPatient.getUsername(), 0);

        Button new_Fphoto = (Button)findViewById(R.id.front_button);
        new_Fphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        Button new_Lphoto = (Button)findViewById(R.id.left_button);
        new_Lphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        Button new_Rphoto = (Button)findViewById(R.id.right_button);
        new_Rphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        Button new_Pphoto = (Button)findViewById(R.id.pucker_button);
        new_Pphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        Button new_FFphoto = (Button)findViewById(R.id.frontface_button);
        new_FFphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        /*Button new_video = (Button)findViewById(R.id.video_button);
        new_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToVideo();
            }
        });*/

        Button return_to_select = (Button)findViewById(R.id.returnToSelect);
        return_to_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                returnToSelectPage();
            }
        });
    }

    private void saveData()
    {
        //check if existing form exists from the questions
        if(questionsDone)
        {
            Form formToUpdate = formDB.getFormAccessInterface().getFormViaID(formID);
            formToUpdate.setImage(newForm.getImage());
            formToUpdate.setComplete(true);
            formToUpdate.setPhotoDone(true);
            formDB.getFormAccessInterface().update(formToUpdate);
            newForm = formToUpdate; //just for Completion (hard to make a mistake by sending the deprecated form information along).
        }
        else //we actually have a new form to insert.
        {
            formDB.getFormAccessInterface().insert(newForm);
        }
    }

    private void returnToSelectPage()
    {
        Intent intent = new Intent(this, SelectPage.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("FORMID", formID);
        intent.putExtra("QUESTIONSDONE", questionsDone);
        intent.putExtra("PHOTOSDONE", true); //done with photos.
        intent.putExtra("ACTIVITYINITIALIZER", "NewFormActivity"); //Todo:: remove?
        startActivity(intent);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
       // if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
      //      Log.e("URI",imageUri.toString());
         //   Bitmap imageBitmap = (Bitmap) extras.get("data");
          //  mView.setImageBitmap(imageBitmap);

        Bitmap bmp = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bmp.recycle();

        //add image to form (only one supported currently)
        //doesn't save it to the database. this needs to be done later.
        newForm.setImage(byteArray);

    //    }

    }




    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
          //  File photoFile = null;
          //  try {
          //      photoFile = createImageFile();
         //   } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
         //   if (photoFile != null) {
            //    Uri photoURI = FileProvider.getUriForFile(this,
             //           "com.example.android.fileprovider",
                //        photoFile);
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
         //   }
       // }
    }

    private void goToVideo()
    {
        Intent intent = new Intent(this, CameraActivity2.class); //go to Next activity
        startActivity(intent);
    }

    /*private void startNewPhoto()
    {
        //verify here

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//go to Next activity
        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"fname_" +
                String.valueOf(System.currentTimeMillis()) + ".jpg"));
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);

        if(intent.resolveActivity(getPackageManager()) != null){

            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
        }

        //getOutputMediaFile(REQUEST_IMAGE_CAPTURE);
    }*/


    /*private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "FacialParalysisApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("FacialParalysisApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }*/




}
