package com.example.acs.myfyp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;

public class SignupContinue extends AppCompatActivity {

    Intent intent;
    ArrayList prefrences = new ArrayList();
    CheckBox gadgets,men,women,beauty,health,appliances,sports,food,grocery,home;
    Database db;
    Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_continue);

        db = new Database(this);
        intent = getIntent();



        signupBtn = findViewById(R.id.signupBtn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(SignupContinue.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Creating Account...");
                progressDialog.show();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // depending on success
                                getDatafromIntent();
                                progressDialog.dismiss();
                            }
                        }, 3000);
                Intent i = new Intent(SignupContinue.this,MapsActivity.class);
                startActivity(i);


            }
        });

        gadgets = (CheckBox)findViewById(R.id.gadgets);
        gadgets.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doSomething(buttonView,isChecked);
            }
        });
        appliances = (CheckBox)findViewById(R.id.electronic);
        appliances.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doSomething(buttonView,isChecked);
            }
        });
        men = (CheckBox)findViewById(R.id.men_fashion);
        men.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doSomething(buttonView,isChecked);
            }
        });
        women = (CheckBox)findViewById(R.id.women_fashion);
        women.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doSomething(buttonView,isChecked);
            }
        });
        beauty = (CheckBox)findViewById(R.id.beauty);
        beauty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doSomething(buttonView,isChecked);
            }
        });
        health= (CheckBox)findViewById(R.id.health);
        health.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doSomething(buttonView,isChecked);
            }
        });
        home = (CheckBox)findViewById(R.id.home);
        home.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doSomething(buttonView,isChecked);
            }
        });
        sports = (CheckBox)findViewById(R.id.sports);
        sports.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doSomething(buttonView,isChecked);
            }
        });
        grocery = (CheckBox)findViewById(R.id.grocery);
        grocery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doSomething(buttonView,isChecked);
            }
        });
        food = (CheckBox)findViewById(R.id.food);
        food.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doSomething(buttonView,isChecked);
            }
        });

    }

    private void doSomething(CompoundButton buttonView, boolean isChecked) {

            if(isChecked) {
                prefrences.add(buttonView.getText().toString());
            }
            else{
                prefrences.remove(buttonView.getText().toString());
                }
    }

    private void getDatafromIntent() {

        Bundle bundle = intent.getExtras();
        String userID =  bundle.getString("UID");
        String fname = bundle.getString("Fname");
        String lname = bundle.getString("Lname");
        String email = bundle.getString("Email");
        String pwd = bundle.getString("Password");

        db.doSignup(this,userID,fname,lname,email,pwd);
        db.storePreferences(prefrences,userID);

    }
}
