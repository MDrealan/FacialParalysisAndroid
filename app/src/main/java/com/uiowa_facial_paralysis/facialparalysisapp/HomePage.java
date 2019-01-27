package com.uiowa_facial_paralysis.facialparalysisapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {

    private String username;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dataRef;
    private ArrayList<Integer> ongoingFormIDS = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //ACTIVITY CREATION

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        username = getIntent().getStringExtra("USERNAME");
        dataRef = database.getReference("forms/ongoing/" + username + "/");

        String welcome = "Welcome, " + username;
        TextView user_welcome = (TextView)findViewById(R.id.hello_user_view);
        user_welcome.setText(welcome);
        //PATHING & LOGIC

        //NEW FORM
        Button new_form = (Button)findViewById(R.id.new_form_button);
        new_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewForm();
            }
        });

        //CONTINUE FORM
        Button continue_form = (Button)findViewById(R.id.continue_form_button);
        continue_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueForm();
            }
        });

        final Button logout = (Button) findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogout();
            }
        });

        //get all form ID's
        getOngoingForms();
    }


    //Todo:: VERIFY the user should start a new form (in other words, it's been about XX days since their last form input
    //Todo:: ^ don't do verification in startNewForm(), have startNewForm() call a verification method.

    private void startNewForm()
    {
        //verify here (call a method).
        Intent intent = new Intent(this, SelectPage.class); //go to Next activity
        intent.putExtra("FORMID", 10); //Todo:: actually implement this
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    private void continueForm()
    {
        Intent intent = new Intent(this, ContinueForm.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("ONGOINGFORMIDS", ongoingFormIDS);
        startActivity(intent);
    }

    private void userLogout()
    {
        Intent intent = new Intent(this, MainActivity.class); //go to Next activity
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }



    private void getOngoingForms() {
        database.getReference("forms/ongoing/" + username + "/").child("ongoing_form_ids").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists()) //make sure the questions are there.
                {
                    String temp = dataSnapshot.getValue().toString();
                    String[] parts = temp.split("[\\D]");
                    // String[] parts = temp.split(" */ *");
                    for(int i = 0; i < parts.length; i++)
                    {
                        if(IsInt_ByException(parts[i]))
                        {
                            ongoingFormIDS.add(Integer.parseInt(parts[i]));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
}
