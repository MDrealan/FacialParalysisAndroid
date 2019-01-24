package com.uiowa_facial_paralysis.facialparalysisapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class CameraActivity extends AppCompatActivity {

    private CameraDevice mCamera;
    private SurfaceHolder mHolder;
    private ImageView mView;
    private Uri imageUri;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Button new_Fphoto = (Button)findViewById(R.id.front_button);
        new_Fphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewPhoto();
            }
        });
        Button new_Lphoto = (Button)findViewById(R.id.left_button);
        new_Lphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewPhoto();
            }
        });
        Button new_Rphoto = (Button)findViewById(R.id.right_button);
        new_Rphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewPhoto();
            }
        });
        Button new_Pphoto = (Button)findViewById(R.id.pucker_button);
        new_Pphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewPhoto();
            }
        });
        Button new_FFphoto = (Button)findViewById(R.id.frontface_button);
        new_FFphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewPhoto();
            }
        });
        Button new_video = (Button)findViewById(R.id.video_button);
        new_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToVideo();
            }
        });
    }

    private void startNewPhoto()
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
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Log.e("URI",imageUri.toString());
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mView.setImageBitmap(imageBitmap);
        }

    }

    private void goToVideo()
    {
        Intent intent = new Intent(this, CameraActivity2.class); //go to Next activity
        startActivity(intent);
    }


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
