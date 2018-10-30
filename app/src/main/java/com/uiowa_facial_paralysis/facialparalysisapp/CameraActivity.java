package com.uiowa_facial_paralysis.facialparalysisapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class CameraActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Button new_photo1 = (Button)findViewById(R.id.front_button);
        Button new_photo2 = (Button)findViewById(R.id.left_button);
        Button new_photo3 = (Button)findViewById(R.id.right_button);
        Button new_video = (Button)findViewById(R.id.video_button);
        new_photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewPhoto();
            }
        });
        new_photo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewPhoto();
            }
        });
        new_photo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewPhoto();
            }
        });
        new_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewVideo();
            }
        });

    }

    private void startNewPhoto()
    {
        //verify here

        Intent intent = new Intent(this, CameraActivity2.class);//go to Next activity

        startActivity(intent);
    }

    private void startNewVideo()
    {
        //verify here

        Intent intent = new Intent(this, CameraActivity3.class);//go to Next activity

        startActivity(intent);
    }



}
