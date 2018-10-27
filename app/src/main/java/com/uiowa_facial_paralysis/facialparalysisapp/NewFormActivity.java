package com.uiowa_facial_paralysis.facialparalysisapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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

}
