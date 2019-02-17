package com.uiowa_facial_paralysis.facialparalysisapp;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private String username;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dataRef;
    private ArrayList<Integer> ongoingFormIDS = new ArrayList<>();

    private PatientDatabase patientDB;
    private FormDatabase formDB;

    private Patient currPatient;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //ACTIVITY CREATION

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        username = getIntent().getStringExtra("USERNAME");

        patientDB = Room.databaseBuilder(getApplicationContext(), PatientDatabase.class, "patient_db").allowMainThreadQueries().build(); //allow main thread queries issue may lock UI while querying DB.
        formDB = Room.databaseBuilder(getApplicationContext(), FormDatabase.class, "form_db").allowMainThreadQueries().build(); //allow main thread queries issue may lock UI while querying DB.
        currPatient = patientDB.patientAccessInterface().getPatientViaUserName(username); //using getIntents to maintain state information with just a couple of variables being passed around.

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

        //LOGOUT
        final Button logout = (Button) findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogout();
            }
        });


        //EXPORT
        final Button export_forms = (Button) findViewById(R.id.export_forms_button);
        export_forms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                exportNewForms(export_forms);
            }
        });

        //get all form ID's
        getOngoingForms();
    }

    private void exportNewForms(Button button)
    {
        List<Form> newPatientForms = formDB.getFormAccessInterface().getPatientNewForms(currPatient.getPatientID(), true);

        if(isExternalStorageWritable())
        {
            File fileToWriteTo = getPublicAlbumStorageDir("PatientRecordsFor" + currPatient.getUsername());
            try
            {
                FileWriter writer = new FileWriter(fileToWriteTo);

                for (int i = 0; i < newPatientForms.size(); i++)
                {
                    String formToWrite = newPatientForms.get(i).toString();
                    writer.write(formToWrite + '\n');
                }
                writer.flush();
                writer.close();
                button.setText("EXPORTED!");
            }
            catch(IOException ioException)
            {
                ioException.printStackTrace();
                button.setText("Unable To Export");
            }
        }
        else
        {
            button.setText("Device Unconnected");
        }
        //first line: patient information (so we can just update existing patient or create new in the add patient option in ruby)
        //currPatient.getUsername(), currPatient.getPatientID();
        //second line to end:
        //newPatientForms.get(0).patientID, newPatientForms.get(0).getFormType(), newPatientForms.get(0).getUserAnswers(), newPatientForms.get(0).getFaceScore(), newPatientForms.get(0).getPhotos();
    }
    //check if usb is connected and user allowed permissions (grabbed from google).
            public boolean isExternalStorageWritable()
            {
                String state = Environment.getExternalStorageState();
                if(Environment.MEDIA_MOUNTED.equals(state))
                {
                    return true;
                }
                return false;
            }

            public File getPublicAlbumStorageDir(String albumName)
            {
                Date current_date = new Date();
                String user_file = "patientData" + current_date.toString() + ".csv";
                // Get the directory for the user's public document directory.
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                File new_file = new File(path, user_file);
                return new_file;
            }

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
