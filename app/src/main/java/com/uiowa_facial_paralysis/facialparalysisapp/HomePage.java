package com.uiowa_facial_paralysis.facialparalysisapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //ACTIVITY CREATION

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //PATHING & LOGIC

        //NEW FORM
        Button new_form = (Button)findViewById(R.id.new_form_button);
        new_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewForm();
            }
        });
    }


    //Todo:: VERIFY the user should start a new form (in other words, it's been about XX days since their last form input
    //Todo:: ^ don't do verification in startNewForm(), have startNewForm() call a verification method.

    private void startNewForm()
    {
        //verify here

        Intent intent = new Intent(this, NewFormActivity.class); //go to Next activity
        startActivity(intent);
    }


}
