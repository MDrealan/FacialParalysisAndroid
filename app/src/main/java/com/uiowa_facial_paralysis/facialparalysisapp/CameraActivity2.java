package com.uiowa_facial_paralysis.facialparalysisapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class CameraActivity2 extends AppCompatActivity {
    private CameraDevice mCamera;
    private SurfaceHolder mHolder;
    private ImageView mView;
    private Uri imageUri;
    private static final int REQUEST_VIDEO_CAPTURE = 102;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera3);
        Button new_video = (Button) findViewById(R.id.button_capture2);
        new_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewVideo();
            }
        });
        Button back  = (Button) findViewById(R.id.back_button2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }
    private void startNewVideo()
    {
        //verify here

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);//go to Next activity
        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"fname_" +
                String.valueOf(System.currentTimeMillis()) + ".3gp"));
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);

        if(intent.resolveActivity(getPackageManager()) != null){

        }
        startActivityForResult(intent,REQUEST_VIDEO_CAPTURE);

        //getOutputMediaFile(REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Log.e("URI",imageUri.toString());
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mView.setImageBitmap(imageBitmap);
        }

    }

    private void goBack()
    {
        Intent intent = new Intent(this, CameraActivity.class); //go to Next activity
        startActivity(intent);
    }



}
