package com.uiowa_facial_paralysis.facialparalysisapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

public class ContinueForm extends AppCompatActivity
{
    private int numForms = 0;
    private ArrayList<Integer> ongoingFormIDS = null;
    private String username;
    private int buttonI;
    private boolean[] photosDone;
    private boolean[] questionsDone;

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
        getStatusbooleans();

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

            currLink.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    goToForm(ongoingFormIDS.get(buttonI));
                }
            });
            //currLink.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            ///currLink.setTextColor(getResources().getColor(R.color.colorPrimary));
            table.addView(currLink,buttonI);
            buttonI++;
        }
    }

    public void goToForm(int formID)
    {
        Intent intent = new Intent(this, SelectPage.class); //go to Next activity
        intent.putExtra("FORMID", Integer.toString(formID)); //so the question activity knows where to send the formID to.
        intent.putExtra("USERNAME", username);
        intent.putExtra("PHOTOSDONE", photosDone);
        intent.putExtra("QUESTIONSDONE", questionsDone);
        startActivity(intent);

    }

    public void getStatusbooleans()
    {
        // Todo:: set this up.
    }
}
