package com.uiowa_facial_paralysis.facialparalysisapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectPage extends AppCompatActivity {

    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_page);

        username = getIntent().getStringExtra("USERNAME");

        boolean questionDone = getIntent().getBooleanExtra("QUESTIONDONE", false);
        boolean photoDone = getIntent().getBooleanExtra("PHOTODONE", false);
        String initializedBy = "HomePage";
        //Todo:: send information to NewFormActivity indicating if the photos are done or not, so it can decide if it needs to submit the form or not..

        //Todo:: dont need this ,I think.
        if(getIntent().getStringExtra("ACTIVITYINITIALIZER") != null)
        {
            initializedBy = getIntent().getStringExtra("ACTIVITYINITIALIZER");
        }


        //if user is done with the questions.
        final Button startQuestion = (Button) findViewById(R.id.question_start_button);
        if(questionDone) //well, don't let them start the question form if they've already finished it.
        {
            startQuestion.setAlpha(.5f);
            startQuestion.setClickable(false);
        }
        else
        {
            startQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    startQuestions(); //start the questions!
                }
            });

        }

        final Button startPhoto = (Button) findViewById(R.id.photo_start_button);
        if(photoDone) //default is false
        {
            startPhoto.setAlpha(.5f);
            startPhoto.setClickable(false);
        }
        else
        {
            startPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPhotos();
                }
            });
        }

        final Button saveAndExit = (Button) findViewById(R.id.save_exit_button);
        saveAndExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                saveCurrentState();
                goHome();
            }
        });

    }

    private void saveCurrentState()
    {
        //What I need:
            // form ID


    }

    private void startPhotos()
    {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }


    private void startQuestions()
    {
        //verify here (call a method).

        Intent intent = new Intent(this, NewFormActivity.class); //go to Next activity
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    private void goHome()
    {
        Intent intent = new Intent(this, HomePage.class); //go to Next activity
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }
}
