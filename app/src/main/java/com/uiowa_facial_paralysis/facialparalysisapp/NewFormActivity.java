package com.uiowa_facial_paralysis.facialparalysisapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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


    ArrayList<TextView> questionRadios= new ArrayList<>();
    private ArrayList<String> userAnswers = new ArrayList<>();


    private FirebaseDatabase database;
    private ArrayList<String> databaseQuestions;
    private ArrayList<ArrayList<String>> databaseAnswers; //nested because each question has multiple answers. index 0 of Arraylist<Arraylist<String>> contains the answers for question 1.

    private TextView question;

    private int currentQuestion; //what question we're on.
    private RadioGroup answer_group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_form);

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
        Button nextQuestion = (Button)findViewById(R.id.next_question_button);
        nextQuestion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(currentQuestion == 0)
                {
                    setQandA();
                }

                //if no option selected
                if( answer_group.getCheckedRadioButtonId() == -1)
                {
                    //dont do anything.
                }
                //if go to next question
                else
                {
                    //Get text from radio group selected button.
                    int selectedID = answer_group.getCheckedRadioButtonId();
                    View radioButton = answer_group.findViewById(selectedID);
                    int idx = answer_group.indexOfChild(radioButton);
                    RadioButton answer = (RadioButton) answer_group.getChildAt(idx);

                    userAnswers.add(answer.getText().toString());

                    //Todo:: reroute back to homepage if done with questions
                    if(currentQuestion != databaseQuestions.size())
                    {
                        setQandA();
                    }
                }
            }
        });
    }
    //Todo:: Modify database so that we have multiple questionairre forms (currently formdata path is just one questionaiire. woopsies :)
    private void getDatabaseInfo()
    {
        String questionPath = "formdata/";
        DatabaseReference basePath = database.getReference(questionPath);

        getDatabaseQuestions(basePath);
        // !!!! ANSWER call always goes AFTER question call, as its size is dependant on the amount of QUESTIONS.
        getDatabaseAnswers(basePath);
        //getDatabaseStatements();
    }

    private void getDatabaseQuestions(DatabaseReference basePath)
    {
        basePath.child("questions1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists()) //make sure the questions are there.
                {
                    for(long i = 0; i < dataSnapshot.getChildrenCount(); i++)
                    {
                        databaseQuestions.add(dataSnapshot.child(Long.toString(i)).getValue().toString());//ow, thatsalottafunctioncalls.
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
        basePath.child("answers1").addListenerForSingleValueEvent(new ValueEventListener() {
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


    private void setQandA()
    {
        question.setText(databaseQuestions.get(currentQuestion));         //Set the question.

        answer_group.removeAllViews(); //remove all previous radiobuttons.
        //Create new radio button for each answer.
        for(int i = 0; i < databaseAnswers.get(currentQuestion).size(); i++)
        {
            RadioButton radio = new RadioButton(this); //why this?
            radio.setText(databaseAnswers.get(currentQuestion).get(i)); //set the answer.
            answer_group.addView(radio);
        }

        currentQuestion++; //make sure you go to the next question~!
    }

}
