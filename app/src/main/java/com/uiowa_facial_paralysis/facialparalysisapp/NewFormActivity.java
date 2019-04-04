package com.uiowa_facial_paralysis.facialparalysisapp;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewFormActivity extends AppCompatActivity {
    private ArrayList<String> userAnswers = new ArrayList<>();

    private long formID; //ID of the form.
    private String photosDone = "FALSE";
    private String username;

    private FirebaseDatabase database;
    private ArrayList<String> databaseQuestions;
    private ArrayList<ArrayList<String>> databaseAnswers; //nested because each question has multiple answers. index 0 of Arraylist<Arraylist<String>> contains the answers for question 1.
    private ArrayList<Integer> ongoingFormIDS = new ArrayList<>();

    private TextView question;

    private int currentQuestion; //what question we're on.
    private RadioGroup answer_group;

    private PatientDatabase patientDB;
    private FormDatabase formDB;
    private Form newForm;
    private Patient currPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_form);

        username = getIntent().getStringExtra("USERNAME");
        formID = Integer.parseInt(getIntent().getStringExtra("FORMID")); //current form ID
        photosDone = getIntent().getStringExtra("PHOTOSDONE");

        //Questionview, radiogroup for answers. currentQuestion is the current question, duh.
        question = (TextView)findViewById(R.id.question_view);
        currentQuestion = 0;
        answer_group = (RadioGroup)findViewById(R.id.answer_group);

        databaseQuestions = new ArrayList<String>();
        databaseAnswers = new ArrayList<ArrayList<String>>();

        patientDB = Room.databaseBuilder(getApplicationContext(), PatientDatabase.class, "patient_db").allowMainThreadQueries().build(); //allow main thread queries issue may lock UI while querying DB.
        formDB = Room.databaseBuilder(getApplicationContext(), FormDatabase.class, "form_db").allowMainThreadQueries().build(); //allow main thread queries issue may lock UI while querying DB.

        currPatient = patientDB.patientAccessInterface().getPatientViaUserName(username); //using getIntents to maintain state information with just a couple of variables being passed around.

        database = FirebaseDatabase.getInstance();
        getDatabaseInfo(); //ALSO CALLS METHOD TO INITIALIZE FIRST QUESTION AND ANSWER!

        //Button Listener to go to the next question.
        final Button nextQuestion = (Button)findViewById(R.id.next_question_button);
        nextQuestion.setText("Start Form"); //at the beginning, for now have this be it. Todo:: Get Rid of this once active call is done.
        nextQuestion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(currentQuestion == 0)
                {
                    setQandA();
                    nextQuestion.setText("Next Question");
                }

                //if no answer selected by user
                if( answer_group.getCheckedRadioButtonId() == -1)
                {
                    //dont do anything.
                }
                else
                {
                    //Get text from radio group selected button.
                    int selectedID = answer_group.getCheckedRadioButtonId();
                    View radioButton = answer_group.findViewById(selectedID);
                    int idx = answer_group.indexOfChild(radioButton);
                    RadioButton answer = (RadioButton) answer_group.getChildAt(idx);

                    userAnswers.add(answer.getText().toString());

                    if(currentQuestion != databaseQuestions.size())
                    {
                        setQandA();
                    }
                    else
                    {
                        sendQuestionForm(); //send the form to the DB
                        returnToSelectPage(); //return to the selection page, indicate that the question form is done.
                    }
                }
            }
        });
    }
    //Todo:: Modify database so that we have multiple questionairre forms (currently formdata path is just one questionaiire. woopsies :)
    private void getDatabaseInfo()
    {
        //always create a new form - only check if photos are done at the end.
        newForm = new Form("not_implemented", "FACE", currPatient.getUsername(), 0);


        //OLD code below - firebase.


     //   formDB.getFormAccessInterface().insert(newForm);
        String questionPath = "formdata/synkinesis/";
        DatabaseReference basePath = database.getReference(questionPath);
        getDatabaseQuestions(basePath);
        // !!!! ANSWER call always goes AFTER question call, as its size is dependent on the amount of QUESTIONS.
        getDatabaseAnswers(basePath);
        getOngoingForms();
        //getDatabaseStatements();
    }

    private void getDatabaseQuestions(DatabaseReference basePath)
    {
        basePath.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists()) //make sure the questions are there.
                {
                    for(long i = 0; i < dataSnapshot.getChildrenCount(); i++)
                    {
                        String currQuestion = "q" + Long.toString(i + 1);
                        databaseQuestions.add(dataSnapshot.child(currQuestion).getValue().toString());
                    }
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getDatabaseAnswers(DatabaseReference basePath)
    {
        basePath.child("answers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists()) //make sure the questions are there.
                {
                    for(int i = 0; i <databaseQuestions.size(); i++)
                    {
                        databaseAnswers.add(new ArrayList<String>());
                    }

                    for(long i = 0; i < dataSnapshot.getChildrenCount(); i++)
                    {
                        String currQuestionAnswer = "q" + Long.toString(i + 1);

                        for(long j = 0; j < dataSnapshot.child(currQuestionAnswer).getChildrenCount(); j++)

                        databaseAnswers.get((int)i).add(dataSnapshot.child(currQuestionAnswer).child(Long.toString(j)).getValue().toString());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getOngoingForms()
    {
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

    private void sendQuestionForm()
    {
        String[] userAnswerArray = new String[userAnswers.size()];
        userAnswerArray = userAnswers.toArray(userAnswerArray);
        //newForm.setUserAnswers(userAnswerArray);
        newForm.setQuestionDone(true);
        if(newForm.isPhotoDone()) //if photo is done as well, then form is complete. Todo:: can route to home page if so.
        {
            newForm.setComplete(true);
        }

        StringBuilder user_answer_sb = new StringBuilder();
        for (int i = 0; i < userAnswers.size(); i++)
        {
            user_answer_sb.append(userAnswers.get(i).toString());
            user_answer_sb.append(" , ");
        }
        newForm.setUserAnswers(user_answer_sb.toString());

        //check if photos are done, if so then just transfer form info.
        if(photosDone == null) {
            formDB.getFormAccessInterface().insert(newForm); //update form information.
        }
        else //the photos were completed and already exist in a form, so just update that form.
        {
            Form formToUpdate = formDB.getFormAccessInterface().getFormViaID(formID);
            formToUpdate.setUserAnswers(user_answer_sb.toString());
            formToUpdate.setComplete(true);
            formToUpdate.setQuestionDone(true);
            formDB.getFormAccessInterface().update(formToUpdate);
            newForm = formToUpdate; //just for Completion (hard to make a mistake by sending the deprecated form information along).
        }
        ///////////////////////////////////// OLD FIREBASE BELOW:

        DatabaseReference ref = database.getReference();
        String answers = userAnswers.toString();
       // ongoingFormIDS.add(formID);
       // ref.child("forms").child("ongoing").child(username).child("ongoing_form_ids").setValue(ongoingFormIDS.toString());
        //Always have the questionairre and photos save things to the ongoing page. Let selectPage do the deciding on whether a form is done (to move it to finalized).
       // ref.child("forms").child("ongoing").child(username).child(Integer.toString(formID)).child("answers").setValue(answers);
       // ref.child("forms").child("ongoing").child(username).child(Integer.toString(formID)).child("q_type").setValue("FACE");
       // ref.child("forms").child("ongoing").child(username).child(Integer.toString(formID)).child("question_done").setValue(true); //done with questions. Let the DB know (for continuing forms)

       // ref.child("forms").child("ongoing").child(username).child(Integer.toString(formID)).child("image_references").setValue("not implemented!");
       // ref.child("forms").child("ongoing").child(username).child(Integer.toString(formID)).child("face_score").setValue("not implemented!");
    }

    private void setQandA()
    {
        //changing the color of each radio button (not implemented)
        int color1 = getResources().getColor(R.color.colorPrimaryDark);
        int color2 = getResources().getColor(R.color.colorPrimary);

        question.setText(databaseQuestions.get(currentQuestion));         //Set the question.


        answer_group.clearCheck(); //get rid of checked one from before
        answer_group.removeAllViews(); //remove all previous radiobuttons.
        //Create new radio button for each answer.

        ViewGroup.LayoutParams layout = answer_group.getLayoutParams();
        int groupheight = layout.height;
        int groupwidth = layout.width;

        for(int i = 0; i < databaseAnswers.get(currentQuestion).size(); i++)
        {
            int weightParam = databaseAnswers.get(currentQuestion).size(); //for setting how large each radiobutton should be.

            RadioButton radio = new RadioButton(this); //why this?
            radio.setText(databaseAnswers.get(currentQuestion).get(i)); //set the answer.

           // radio.setWidth((int) (groupwidth));
            radio.setHeight((int) (groupheight/weightParam));

            //stripey!
            /*
            if ( (i % 2) == 0) {
                radio.setBackgroundColor(color1);
                radio.setTextColor(color2);
            }
            else
            {
                radio.setBackgroundColor(color2);
                radio.setTextColor(color1);
            }

            */
            answer_group.addView(radio);
        }

        currentQuestion++; //make sure you go to the next question~!
    }
    private void returnToSelectPage()
    {
        Intent intent = new Intent(this, SelectPage.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("FORMID", formID);
        intent.putExtra("QUESTIONSDONE", true); //also send it locally bc of firebase asynch tasks not being nice.
        intent.putExtra("PHOTOSDONE", photosDone);
        intent.putExtra("ACTIVITYINITIALIZER", "NewFormActivity"); //Todo:: remove?
        startActivity(intent);
    }

}
