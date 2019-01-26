package com.uiowa_facial_paralysis.facialparalysisapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectPage extends AppCompatActivity {

    private String username;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private int formID;
    private boolean photosDone = false;
    private boolean questionsDone = false;
    private DatabaseReference basePath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_page);

        username = getIntent().getStringExtra("USERNAME");
        formID = getIntent().getIntExtra("FORMID", 0);
        questionsDone = getIntent().getBooleanExtra("QUESTIONSDONE", false);
        String initializedBy = "HomePage";
        //Todo:: send information to NewFormActivity indicating if the photos are done or not, so it can decide if it needs to submit the form or not..

        String questionPath = "forms/ongoing/" + username + "/" + formID + "/";
        basePath = database.getReference(questionPath);

        //if user is done with the questions.
        final Button startQuestion = (Button) findViewById(R.id.question_start_button);
        if(questionsDone) //well, don't let them start the question form if they've already finished it.
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
        if(photosDone) //default is false
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
                saveCurrentState(); //check whether form is done (move to finalized forms) or if
                goHome();
            }
        });
        if(questionsDone) {
            String completedStringPath = "forms/finalized/" + username + "/" + formID + "/";
            DatabaseReference completedPath = database.getReference(completedStringPath);
            moveFormToCompleted(basePath, completedPath);
        }

    }

    private void saveCurrentState()
    {
        //if done with form, move it to finalized in the database.
        //Todo:: implement photosDone.
        if(questionsDone)
        {
           // moveFormToCompleted(basePath, completedPath);
        }
        else //not done with form, just exit. the questionairre and photo activities handle sending info to DB.
        {

        }
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
        intent.putExtra("FORMID", Integer.toString(formID));
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    private void goHome()
    {
        Intent intent = new Intent(this, HomePage.class); //go to Next activity
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    //find out if questions are done.
    private void isQuestionDone(DatabaseReference basePath, final Button startQuestion)
    {
        basePath.child("question_done").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists()) //make sure the question variable is there
                {
                    boolean questionsDone = Boolean.getBoolean(dataSnapshot.getValue().toString());
                    if (questionsDone) {
                        startQuestion.setAlpha(.5f);
                        startQuestion.setClickable(false);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    //base code from : https://stackoverflow.com/questions/40456443/how-to-move-firebase-child-from-one-node-to-another-in-android
    private void moveFormToCompleted(final DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener()
                {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                    }
                });
                fromPath.setValue(null);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
