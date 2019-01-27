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
        photosDone = getIntent().getBooleanExtra("PHOTOSDONE", false);

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
        if(questionsDone && photosDone) // && photosDone
        {
            String completedStringPath = "forms/finalized/" + username + "/" + formID + "/";
            DatabaseReference completedPath = database.getReference(completedStringPath);
            moveFormToCompleted(basePath, completedPath);
            removeIDFromOngoing();
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
    }

    private void removeIDFromOngoing()
    {
        database.getReference("forms/ongoing/" + username + "/").child("ongoing_form_ids").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                ArrayList <Integer> tempArr = new ArrayList<Integer>();
                if(dataSnapshot.exists()) //make sure the questions are there.
                {
                    String temp = dataSnapshot.getValue().toString();
                    String[] parts = temp.split("[\\D]");
                    // String[] parts = temp.split(" */ *");
                    for(int i = 0; i < parts.length; i++)
                    {
                        if(IsInt_ByException(parts[i]))
                        {
                            if( formID == Integer.parseInt((parts[i])))
                            { }
                            else {
                                tempArr.add(Integer.parseInt(parts[i]));
                            }
                        }
                    }
                    //send updated form ID list to DB
                    database.getReference().child("forms").child("ongoing").child(username).child("ongoing_form_ids").setValue(tempArr.toString());
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

    //https://stackoverflow.com/questions/237159/whats-the-best-way-to-check-if-a-string-represents-an-integer-in-java
    private boolean IsInt_ByException(String str)
    {
        try
        {
            Integer.parseInt(str);
            return true;
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
    }

    private void startPhotos()
    {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("QUESTIONSDONE", questionsDone);
        startActivity(intent);
    }


    private void startQuestions()
    {
        Intent intent = new Intent(this, NewFormActivity.class); //go to Next activity
        intent.putExtra("FORMID", Integer.toString(formID)); //so the question activity knows where to send the formID to.
        intent.putExtra("USERNAME", username);
        intent.putExtra("PHOTOSDONE", photosDone);
        startActivity(intent);
    }

    private void goHome()
    {
        Intent intent = new Intent(this, HomePage.class); //go to Next activity
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }
}
