package com.uiowa_facial_paralysis.facialparalysisapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //ACTIVITY CREATION

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        username = getIntent().getStringExtra("USERNAME");
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
    }


    //Todo:: VERIFY the user should start a new form (in other words, it's been about XX days since their last form input
    //Todo:: ^ don't do verification in startNewForm(), have startNewForm() call a verification method.

    private void startNewForm()
    {
        //verify here (call a method).

        Intent intent = new Intent(this, NewFormActivity.class); //go to Next activity
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }


}
