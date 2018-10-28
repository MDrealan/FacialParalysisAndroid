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

    private ArrayList testQuestions;
    private ArrayList testAnswer1;
    private ArrayList testAnswer2;

    private int currentQuestion = 0;

    private RadioButton q_1;
    private RadioButton q_2;
    private RadioButton q_3;
    private RadioButton q_4;

    ArrayList<TextView> questionRadios= new ArrayList<>();

    private ArrayList<String> userAnswers = new ArrayList<>();


    private FirebaseDatabase database;
    private ArrayList<String> databaseQuestions;
    private ArrayList<ArrayList<String>> databaseAnswers; //nested because each question has multiple answers. index 0 of Arraylist<Arraylist<String>> contains the answers for question 1.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_form);

        //Todo:: database input: these will be replaced with integers for the current question/answer for the questionaiire in the database.
        testQuestions = new ArrayList();
        testQuestions.add("Question 1");
        testQuestions.add("Question 2");
        testAnswer1 = new ArrayList();
        testAnswer1.add("Answer 1");
        testAnswer1.add("Answer 2");
        testAnswer1.add("Answer 3");
        testAnswer1.add("Answer 4");
        testAnswer2 = new ArrayList();
        testAnswer2.add("Answer 5");
        testAnswer2.add("Answer 6");
        testAnswer2.add("Answer 7");
        testAnswer2.add("Answer 8");


        databaseQuestions = new ArrayList<String>();
        databaseAnswers = new ArrayList<ArrayList<String>>();
        database = FirebaseDatabase.getInstance();
        getDatabaseInfo();

        q_1 = (RadioButton)findViewById(R.id.q_1);
        q_2 = (RadioButton)findViewById(R.id.q_2);
        q_3 = (RadioButton)findViewById(R.id.q_3);
        q_4 = (RadioButton)findViewById(R.id.q_4);

        questionRadios.add(q_1);
        questionRadios.add(q_2);
        questionRadios.add(q_3);
        questionRadios.add(q_4);

        setRadioButtonsText(testAnswer1); //HARDCODED

        final TextView question = (TextView)findViewById(R.id.question_text);
        question.setText(testQuestions.get(currentQuestion).toString());
        currentQuestion = currentQuestion+1;

        final RadioGroup answer_group = (RadioGroup)findViewById(R.id.answer_group);

        //Set the initial text to the first questions.

        //Button Listener to go to the next question.
        Button nextQuestion = (Button)findViewById(R.id.next_question_button);
        nextQuestion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //if no option selected



                //if go to next question
                if(currentQuestion != testQuestions.size())
                {
                    question.setText(testQuestions.get(currentQuestion).toString());
                    currentQuestion++;

                    //Get text from radio group selected button.
                    int selectedID = answer_group.getCheckedRadioButtonId();
                    View radioButton = answer_group.findViewById(selectedID);
                    int idx = answer_group.indexOfChild(radioButton);
                    RadioButton answer = (RadioButton) answer_group.getChildAt(idx);

                    userAnswers.add(answer.getText().toString());

                    setRadioButtonsText(testAnswer2); //HARDCODED: database needs to be generalized.

                }
                else
                    {
                    //if last question, go to next page.
                }
            }
        });
    }

    private void setRadioButtonsText(ArrayList answers)
    {
        for(int i = 0; i < answers.size(); i++)
        {
            TextView currTextView = questionRadios.get(i);
            currTextView.setText(answers.get(i).toString());
        }
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

                    String questionArray = dataSnapshot.getValue().toString();
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
}
