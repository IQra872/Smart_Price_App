package com.example.acs.myfyp;

import android.accessibilityservice.AccessibilityButtonController;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Signup extends AppCompatActivity {


    EditText f_name;
    EditText l_name;
    EditText e_mail;
    EditText pass_word;
    Button continueBtn;
    TextView link_login;
    Database db;
    String fname;
    String lname;
    String email;
    String pwd;
    String userID;
    int maxNum =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        f_name = (EditText) findViewById(R.id.Fname);
        l_name = (EditText) findViewById(R.id.Lname);
        e_mail = (EditText) findViewById(R.id.email);
        pass_word = (EditText) findViewById(R.id.password);
        continueBtn = (Button) findViewById(R.id.continueBtn);
        link_login = (TextView) findViewById(R.id.link_login);
        db = new Database(this);

        link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Signup.this,LoginActivity.class);
                startActivity(i);
            }
        });

        f_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    continueBtn.setEnabled(true);
            }
        });
        l_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                  continueBtn.setEnabled(true);
            }
        });
        e_mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                continueBtn.setEnabled(true);
            }
        });
        pass_word.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                  continueBtn.setEnabled(true);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Continue();
            }
        });

    }

        private void Continue() {

            if (!validate()) {
                onContinueFailed();
                return;
            }

            continueBtn.setEnabled(true);

            final ProgressDialog progressDialog = new ProgressDialog(Signup.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Validating Data...");
            progressDialog.show();


                new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // depending on success
                        onContinueSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

        private void onContinueSuccess() {
            continueBtn.setEnabled(true);
                Intent i = new Intent(Signup.this,SignupContinue.class);
                Bundle bundle = new Bundle();
                bundle.putString("UID",userID);
                bundle.putString("Fname",fname);
                bundle.putString("Lname",lname);
                bundle.putString("Email",email);
                bundle.putString("Password",pwd);

                i.putExtras(bundle);
                startActivity(i);

            setResult(RESULT_OK, null);
    }

        private void onContinueFailed() {
            Toast.makeText(getApplicationContext(),"Signup failed",Toast.LENGTH_SHORT).show();
            continueBtn.setEnabled(false);
    }

        private boolean validate() {

                boolean valid = true;

                 fname = f_name.getText().toString();
                 lname = l_name.getText().toString();
                 email = e_mail.getText().toString();
                 pwd = pass_word.getText().toString();
                  char first = fname.charAt(0);
                  char last = lname.charAt(0);
                userID = first + last + "_" + maxNum;



                if (fname.isEmpty() || fname.length() < 3) {
                    f_name.setError("at least 3 characters");
                    valid = false;
                } else {
                    f_name.setError(null);
                }

                if (lname.isEmpty() || lname.length() < 3) {
                    l_name.setError("at least 3 characters");
                    valid = false;
                } else {
                    f_name.setError(null);
                }

                if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    e_mail.setError("enter a valid email address");
                    valid = false;
                } else {
                    e_mail.setError(null);
                }

                if (pwd.isEmpty() || pwd.length() < 4 || pwd.length() > 10) {
                    pass_word.setError("enter between 4 and 10 alphanumeric characters");
                    valid = false;
                } else {
                    pass_word.setError(null);
                }




                return valid;
    }
}