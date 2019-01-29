package com.uiowa_facial_paralysis.facialparalysisapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContinueForm extends AppCompatActivity
{
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private int numForms = 0;
    private ArrayList<Integer> ongoingFormIDS = null;
    private String username;
    private int buttonI;
    private int booleanIterator;
    private ArrayList<Boolean> photosDone = new ArrayList<>();
    private ArrayList<Boolean> questionsDone = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_form);
        username = getIntent().getStringExtra("USERNAME");


        ongoingFormIDS = getIntent().getIntegerArrayListExtra("ONGOINGFORMIDS");
        if ( !(ongoingFormIDS == null) )
        {
            numForms = ongoingFormIDS.size();
        }

        setContinueButtons();
        //setContinueFormButtons();
        getStatusbooleans();

    }
/*
    @Override
    public void onClick(View v)
    {
        for (int i = 0; i < numForms; i++) {
            if (ongoingFormIDS.get(i) == v.getId()) {
                goToForm(ongoingFormIDS.get(i));
            }
        }
    } */

    public void setContinueFormButtons()
    {
        TableLayout table = (TableLayout) findViewById(R.id.button_table);
        for(int i = 0; i < numForms; i++)
        {
            table.addView(new TableRow(this));
        }
        for(buttonI = 0; buttonI<numForms; buttonI++)
        {
            Button currLink = new Button(this); //why this?
            currLink.setText(ongoingFormIDS.get(buttonI).toString()); //set the answer.
            currLink.setId(ongoingFormIDS.get(buttonI));
            table.addView(currLink,buttonI);
        }
    }


    public void setContinueButtons()
    {
        TableLayout table = (TableLayout) findViewById(R.id.button_table);
        for(int i = 0; i < numForms; i++)
        {
            table.addView(new TableRow(this));
        }

        for(buttonI = 0; buttonI < numForms; buttonI++)
        {
            Button currLink = new Button(this); //why this?
            currLink.setText(ongoingFormIDS.get(buttonI).toString()); //set the answer.

            final int currIndex = buttonI;
          //  final int currID = ongoingFormIDS.get(buttonI);
           //final boolean currQuestionDone = questionsDone.get(buttonI);
            currLink.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    goToForm(currIndex);
                }
            });
            //currLink.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            ///currLink.setTextColor(getResources().getColor(R.color.colorPrimary));
            table.addView(currLink,buttonI);
            buttonI++;
        }
    }

    public void goToForm(int formToGet)
    {
        int formID = ongoingFormIDS.get(formToGet);
        boolean questionIsDone = questionsDone.get(formToGet);
        Intent intent = new Intent(this, SelectPage.class); //go to Next activity
        intent.putExtra("FORMID", Integer.toString(formID)); //so the question activity knows where to send the formID to.
        intent.putExtra("USERNAME", username);
        intent.putExtra("PHOTOSDONE", photosDone);
        intent.putExtra("QUESTIONSDONE", questionIsDone);
        startActivity(intent);

    }

    public void getStatusbooleans()
    {
        for(booleanIterator = 0; booleanIterator < numForms; booleanIterator++)
        {
            database.getReference("forms/ongoing/" + username + "/" + ongoingFormIDS.get(booleanIterator) + "/").child("question_done").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists()) //make sure the questions are there (won't be if not true).
                    {
                        boolean temp = Boolean.parseBoolean(dataSnapshot.getValue().toString());
                        questionsDone.add(temp);
                    }
                    else
                    {
                        questionsDone.add(false);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}
