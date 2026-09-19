package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private CheckBox checkBoxRememberMe;
    private Button buttonSignIn;
    private Button buttonSignUp;
    private TextView textViewLoginError;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = SessionManager.getInstance(this);

        editTextEmail = findViewById(R.id.editText_email);
        editTextPassword = findViewById(R.id.editText_password);
        checkBoxRememberMe = findViewById(R.id.checkBox_rememberMe);
        buttonSignIn = findViewById(R.id.button_signIn);
        buttonSignUp = findViewById(R.id.button_signUp);
        textViewLoginError = findViewById(R.id.textView_loginError);

        TextWatcher clearErrorOnEdit = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textViewLoginError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        editTextEmail.addTextChangedListener(clearErrorOnEdit);
        editTextPassword.addTextChangedListener(clearErrorOnEdit);

        String rememberedEmail = sessionManager.getRememberedEmail();
        if (!TextUtils.isEmpty(rememberedEmail)) {
            editTextEmail.setText(rememberedEmail);
            checkBoxRememberMe.setChecked(true);
        }

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    private void attemptLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!databaseHelper.isEmailRegistered(email)) {
            Toast.makeText(this, "Can't find an account for this email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!databaseHelper.validateLogin(email, password)) {
            editTextPassword.setText("");
            textViewLoginError.setVisibility(View.VISIBLE);
            return;
        }

        if (checkBoxRememberMe.isChecked()) {
            sessionManager.setRememberedEmail(email);
        } else {
            sessionManager.clearRememberedEmail();
        }

        sessionManager.setLoggedInUser(email);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
