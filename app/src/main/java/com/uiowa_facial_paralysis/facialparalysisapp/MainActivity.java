package com.uiowa_facial_paralysis.facialparalysisapp;

import android.content.Intent;
import android.support.annotation.NonNull;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();


        //Todo:: find out why these need to be final (accessed from an inner class? I can't remember why this needs to be the case).
        //Email Input
        final EditText email = (EditText)findViewById(R.id.email_input);
        //Password Input
        final EditText password = (EditText)findViewById(R.id.password_input);

        final TextView incorrectInput = (TextView)findViewById(R.id.incorrect_inputs);

        //Click Listener to go to next page.
        Button login_button = (Button)findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Make sure user input was an actual input.
                if(email.getText() != null && password.getText() != null)
                {
                    verifyPassword(email.getText().toString(), password.getText().toString(), incorrectInput);
                }
            }
        });

        //Click Listener to sign up.
        Button sign_up_button = (Button)findViewById(R.id.sign_up);
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goToSignUp();
            }
        });


    }
    //Todo:: pass user info to the next page so we know where to put stuff in the database for the forms.
    //Code to go to the Users Homepage
    private void goToHomePage()
    {
        Intent intent = new Intent(this, HomePage.class); //go to Next activity
        startActivity(intent);
    }









    //////////////////////////////////////////////// VERIFICATION/SIGN UP //////////////////////////


    // Todo:: make a case for no email associated with input: Ask them to sign up?
    //verify users password.
    private void verifyPassword(String email, String password_guess, final TextView incorrect_input)
    {
        final String user_password = password_guess;

        //Todo: Verification of email/password in case of bad/malicious input
        StringBuilder userIDInput = new StringBuilder("users" + "/" + email + "/");
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
                        goToHomePage();
                    } else {
                        incorrect_input.setText("Invalid Input");
                    }
                }
                else
                {
                    incorrect_input.setText("Invalid Input");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void goToSignUp()
    {
        Intent intent = new Intent(this, sign_up.class); //go to Next activity
        startActivity(intent);
    }
}
