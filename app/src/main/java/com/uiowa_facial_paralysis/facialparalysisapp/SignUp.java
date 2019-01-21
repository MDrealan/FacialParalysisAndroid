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

public class SignUp extends AppCompatActivity {

    private FirebaseDatabase database;


    //Todo:: don't allow for ridiculously long usernames, and try to suggest decent passwords.

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText email = (EditText)findViewById(R.id.user_name);
        final EditText password = (EditText)findViewById(R.id.password);
        final EditText pasword_verify = (EditText)findViewById(R.id.password_verify);
        final TextView incorrectInput = (TextView)findViewById(R.id.invalid_input);

        database = FirebaseDatabase.getInstance();

        Button sign_up = (Button)findViewById(R.id.sign_up);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(password.getText() != null && password.getText() != null && email.getText() != null)
                {
                    if (passwordVerificationInput(password.getText().toString(), pasword_verify.getText().toString()))
                    {
                        createNewUser(email.getText().toString(), password.getText().toString());
                        goToHomePage();
                    }
                    else
                    {
                        incorrectInput.setText("Passwords do not match.");
                    }
                }
            }
        });

    }




    private boolean passwordVerificationInput(String password, String passwordVerification)
    {
        if(password.equals(passwordVerification))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //TODO:: check for already existing email!
    public void createNewUser(String username, String password)
    {
        //Encrypt the password.
        String encryptedPassword = AESCrypt.encrypt(password);

        DatabaseReference ref = database.getReference();

        ref.child("users").child(username).child("username").setValue(username);
        ref.child("users").child(username).child("password").setValue(encryptedPassword);
        ref.child("users").child(username).child("formID").setValue("0"); //new users have zero forms
    }

    public void goToHomePage()
    {
        Intent intent = new Intent(this, MainActivity.class); //go to Next activity
        startActivity(intent);
    }

}
