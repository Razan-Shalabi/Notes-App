package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout layoutEmail;
    private TextInputLayout layoutFirstName;
    private TextInputLayout layoutLastName;
    private TextInputLayout layoutPassword;
    private TextInputLayout layoutConfirmPassword;

    private EditText editTextEmail;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button buttonRegister;

    private DatabaseHelper databaseHelper;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,12}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        databaseHelper = new DatabaseHelper(this);

        layoutEmail = findViewById(R.id.layout_email);
        layoutFirstName = findViewById(R.id.layout_firstName);
        layoutLastName = findViewById(R.id.layout_lastName);
        layoutPassword = findViewById(R.id.layout_password);
        layoutConfirmPassword = findViewById(R.id.layout_confirmPassword);

        editTextEmail = findViewById(R.id.editText_email);
        editTextFirstName = findViewById(R.id.editText_firstName);
        editTextLastName = findViewById(R.id.editText_lastName);
        editTextPassword = findViewById(R.id.editText_password);
        editTextConfirmPassword = findViewById(R.id.editText_confirmPassword);
        buttonRegister = findViewById(R.id.button_register);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister() {
        String email = editTextEmail.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        boolean valid = true;

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError("Enter a valid email address");
            valid = false;
        } else if (databaseHelper.isEmailRegistered(email)) {
            layoutEmail.setError("Email is already registered");
            valid = false;
        } else {
            layoutEmail.setError(null);
        }

        if (firstName.length() < 3 || firstName.length() > 10) {
            layoutFirstName.setError("First name must be 3-10 characters");
            valid = false;
        } else {
            layoutFirstName.setError(null);
        }

        if (lastName.length() < 3 || lastName.length() > 10) {
            layoutLastName.setError("Last name must be 3-10 characters");
            valid = false;
        } else {
            layoutLastName.setError(null);
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            layoutPassword.setError("6-12 chars, with a digit, a lowercase and an uppercase letter");
            valid = false;
        } else {
            layoutPassword.setError(null);
        }

        if (!confirmPassword.equals(password)) {
            layoutConfirmPassword.setError("Passwords do not match");
            valid = false;
        } else {
            layoutConfirmPassword.setError(null);
        }

        if (!valid) {
            Toast.makeText(this, "Please fix the highlighted fields", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(email, firstName, lastName, password);
        boolean success = databaseHelper.registerUser(user);

        if (success) {
            Toast.makeText(this, "Registration successful, please sign in", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Registration failed, please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
