package com.uiowa_facial_paralysis.facialparalysisapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import java.util.Calendar;

public class NewFormActivity extends AppCompatActivity {


    ArrayList<TextView> questionRadios= new ArrayList<>();
    private ArrayList<String> userAnswers = new ArrayList<>();


    private FirebaseDatabase database;
    private ArrayList<String> databaseQuestions;
    private ArrayList<ArrayList<String>> databaseAnswers; //nested because each question has multiple answers. index 0 of Arraylist<Arraylist<String>> contains the answers for question 1.

    private TextView question;

    private int currentQuestion; //what question we're on.
    private RadioGroup answer_group;

    private String formStartDate;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_form);

        username = getIntent().getStringExtra("USERNAME");
        Calendar temp =Calendar.getInstance();
        formStartDate = temp.getTime().toString();

        //Questionview, radiogroup for answers. currentQuestion is the current question, duh.
        question = (TextView)findViewById(R.id.question_view);
        currentQuestion = 0;
        answer_group = (RadioGroup)findViewById(R.id.answer_group);

        databaseQuestions = new ArrayList<String>();
        databaseAnswers = new ArrayList<ArrayList<String>>();

        database = FirebaseDatabase.getInstance();
        getDatabaseInfo(); //ALSO CALLS METHOD TO INITIALIZE FIRST QUESTION AND ANSWER!



        //Set the initial text to the first questions.

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

                //if no option selected
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
                        sendFinalForm();
                        // Todo:: If photo portion is done, return to home, else, return to select page.
                        returnToSelectPage();
                    }
                }
            }
        });
    }
    //Todo:: Modify database so that we have multiple questionairre forms (currently formdata path is just one questionaiire. woopsies :)
    private void getDatabaseInfo()
    {
        String questionPath = "formdata/synkinesis/";
        DatabaseReference basePath = database.getReference(questionPath);


        //Todo:: do these need to be static so that they can run before the constructor runs? That way we won't get threading issues.
        getDatabaseQuestions(basePath);
        // !!!! ANSWER call always goes AFTER question call, as its size is dependant on the amount of QUESTIONS.
        getDatabaseAnswers(basePath);
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

                        databaseAnswers.get((int)i).add(dataSnapshot.child(currQuestionAnswer).child(Long.toString(j)).getValue().toString());//ow, evenmorefunctioncalls.
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Todo:: only send final form if images are done (to be done later).
    //Todo:: package the array for the DB in a nice format please.
    private void sendFinalForm()
    {
        DatabaseReference ref = database.getReference();

        String answers = userAnswers.toString();
        ref.child("forms").child("finalized").child(username).child(formStartDate).child("answers").setValue(answers);
        ref.child("forms").child("finalized").child(username).child(formStartDate).child("q_type").setValue("FACE");
        //Todo:: images!!!!!!!!
        ref.child("forms").child("finalized").child(username).child(formStartDate).child("image_references").setValue("not implemented!");
        //Todo:: if face questionairre, then also give the face score.
        ref.child("forms").child("finalized").child(username).child(formStartDate).child("face_score").setValue("not implemented!");
    }

    private void setQandA()
    {
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

    //Todo::  modified to go back to the choose page if not everything has been completed for the form
    private void returnToHome()
    {
        Intent intent = new Intent(this, HomePage.class); //go to Next activity
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }
    private void returnToSelectPage()
    {
        Intent intent = new Intent(this, SelectPage.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("QUESTIONDONE", true); //Todo:: if not done (saved and exited), return false.
        intent.putExtra("ACTIVITYINITIALIZER", "NewFormActivity"); //to tell
        startActivity(intent);
    }

}
