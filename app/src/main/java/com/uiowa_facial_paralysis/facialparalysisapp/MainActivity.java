package com.uiowa_facial_paralysis.facialparalysisapp;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
{

private FirebaseDatabase database;
private String username = "";
private PatientDatabase patientDB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //make db object for patient
        patientDB = Room.databaseBuilder(getApplicationContext(), PatientDatabase.class, "patient_db").allowMainThreadQueries().build(); //allow main thread queries issue may lock UI while querying DB.

        //Firebase instance.
        database = FirebaseDatabase.getInstance();

        //Todo:: find out why these need to be final (accessed from an inner class? I can't remember why this needs to be the case).
        final EditText email = (EditText)findViewById(R.id.email_input);
        final EditText password = (EditText)findViewById(R.id.password_input);
        final TextView incorrectInput = (TextView)findViewById(R.id.incorrect_inputs);

        //Click Listener -> GO TO NEXT PAGE
        Button login_button = (Button)findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Make sure user input was an actual input.
                if(email.getText() != null && password.getText() != null)
                {
                   // verifyPassword(email.getText().toString(), password.getText().toString(), incorrectInput); //FIREBASE => OLD
                    verifyRoomPassword(email.getText().toString(), password.getText().toString(), incorrectInput);
                }
            }
        });

        //Click Listener -> GO TO SIGN UP.
        Button sign_up_button = (Button)findViewById(R.id.sign_up);
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goToSignUp();
            }
        });

        //in reality, this should be wrapped around the function that actually stores data (not good practice technically).
        int check = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (check == PackageManager.PERMISSION_GRANTED) {
            //Do something
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1024);
        }



    }

    //Code to go to the Users Homepage
    private void goToHomePage()
    {
        Intent intent = new Intent(this, HomePage.class); //go to Next activity
        intent.putExtra("USERNAME", username); //pass username to next activity.
        startActivity(intent);
    }



    //////////////////////////////////////////////// VERIFICATION/SIGN UP //////////////////////////

    public void verifyRoomPassword(String email, String password_guess, final TextView incorrect_input)
    {
        Patient curr_patient = patientDB.patientAccessInterface().getPatientViaUserName(email);
        if(curr_patient == null)
        {
            incorrect_input.setText("No User Exists.");
        }
        else {

            if (curr_patient.getHashed_password().equals(AESCrypt.encrypt(password_guess)))
            {
                username = curr_patient.getUsername();
                goToHomePage();
            }
            else {
                incorrect_input.setText("Invalid Input");
            }
        }
    }


    // Todo:: make a case for no email associated with input: Ask them to sign up?
    //verify users password.
    private void verifyPassword(String email, String password_guess, final TextView incorrect_input)
    {
        final String user_password = password_guess;
        final String userEmail = email;

        //Todo: Verification of email/password in case of bad/malicious input
        StringBuilder userIDInput = new StringBuilder("users" + "/" + userEmail + "/");
        DatabaseReference id_attempt = database.getReference(userIDInput.toString());

        id_attempt.child("password").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists()) //if there's a password present.
                {
                    String actual_password = dataSnapshot.getValue().toString();
                    String decrypted_actual = AESCrypt.decrypt(actual_password);

                    if (decrypted_actual.equals(user_password))
                    {
                        username = userEmail;
                        //goToHomePage();
                    } else {
                     //   incorrect_input.setText("Invalid Input");
                    }
                }
                else
                {
                    incorrect_input.setText("Invalid Input");
                }
            }
            //Todo:: what do we do here? probably need to notify the user the database isn't available, though this is unlikely.
            //Todo:: we also need to Test what happens when the database fails to return things.
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void goToSignUp()
    {
        Intent intent = new Intent(this, SignUp.class); //go to Next activity
        startActivity(intent);
    }
}
