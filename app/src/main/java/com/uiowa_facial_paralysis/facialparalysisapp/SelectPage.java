package com.uiowa_facial_paralysis.facialparalysisapp;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class SelectPage extends AppCompatActivity {

    private String username;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private long formID;
    private boolean photosDone = false;
    private boolean questionsDone = false;
    private DatabaseReference basePath;

    private PatientDatabase patientDB;
    private FormDatabase formDB;

    private Form formToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_page);

        username = getIntent().getStringExtra("USERNAME");
        formID = getIntent().getLongExtra("FORMID", 0);
        questionsDone = getIntent().getBooleanExtra("QUESTIONSDONE", false);
        photosDone = getIntent().getBooleanExtra("PHOTOSDONE", false);

        patientDB = Room.databaseBuilder(getApplicationContext(), PatientDatabase.class, "patient_db").allowMainThreadQueries().build(); //allow main thread queries issue may lock UI while querying DB.
        formDB = Room.databaseBuilder(getApplicationContext(), FormDatabase.class, "form_db").allowMainThreadQueries().build(); //allow main thread queries issue may lock UI while querying DB.

        //ready to save & exit (the only command they can now do)
        if(photosDone && questionsDone)
        {
            formToSend = formDB.getFormAccessInterface().getFormViaID(formID);
        }

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
                //note that all saving is done within the questionaiire activity and the photo activity.
                //no form has to be moved to completed here.
                //however, we will automatically send the completed form to the rails app here:
                exportNewFormsViaPOSTRequest();
                goHome();
            }
        });

        //OLD: FIREBASE
        if(questionsDone && photosDone)
        {
            String completedStringPath = "forms/finalized/" + username + "/" + formID + "/";
            DatabaseReference completedPath = database.getReference(completedStringPath);
            moveFormToCompleted(basePath, completedPath);
            removeIDFromOngoing();
        }

    }

    private void exportNewFormsViaPOSTRequest()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try {
            String URL = "https://paralysisapp.herokuapp.com/recordapi/add/createviaweb";
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("username", formToSend.getPatientID());
            jsonBody.put("form_date", formToSend.getFormDate());
            jsonBody.put("form_questions", formToSend.getFormQuestions());
            jsonBody.put("form_answers", formToSend.getUserAnswers());
            jsonBody.put("total_score", 100);
            byte[] imageBase64Encoded = android.util.Base64.encode(formToSend.getImage(), android.util.Base64.DEFAULT);
            String imageAsString = " ";
            //convert to string
            try
            {
                imageAsString = new String(imageBase64Encoded, "UTF-8");
            }
            catch(UnsupportedEncodingException s)
            {

            }

            jsonBody.put("imagetest", imageAsString); //testing for sending the images.
            jsonBody.put("form_type", "FACE");


            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Toast.makeText(getApplicationContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    // onBackPressed();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Basic " + "c2FnYXJAa2FydHBheS5jb206cnMwM2UxQUp5RnQzNkQ5NDBxbjNmUDgzNVE3STAyNzI=");//put your token here
                    return headers;
                }
            };
            //   VolleyApplication.getInstance().addToRequestQueue(jsonOblect);
            requestQueue.add(jsonOblect);
        } catch (JSONException e) {
            e.printStackTrace();
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
        intent.putExtra("FORMID", formID); //so the question activity knows where to send the formID to.
        intent.putExtra("USERNAME", username);
        intent.putExtra("QUESTIONSDONE", questionsDone);
        startActivity(intent);
    }


    private void startQuestions()
    {
        Intent intent = new Intent(this, NewFormActivity.class); //go to Next activity
        intent.putExtra("FORMID", formID); //so the question activity knows where to send the formID to.
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
