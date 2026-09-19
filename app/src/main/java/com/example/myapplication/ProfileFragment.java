package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.SessionManager;

import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private String userEmail;
    private User currentUser;

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextCurrentPassword;
    private EditText editTextNewPassword;
    private Button buttonSave;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,12}$");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        userEmail = SessionManager.getInstance(requireContext()).getLoggedInUser();

        editTextFirstName = view.findViewById(R.id.editText_profileFirstName);
        editTextLastName = view.findViewById(R.id.editText_profileLastName);
        editTextCurrentPassword = view.findViewById(R.id.editText_profileCurrentPassword);
        editTextNewPassword = view.findViewById(R.id.editText_profileNewPassword);
        buttonSave = view.findViewById(R.id.button_saveProfile);

        loadUser();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void loadUser() {
        currentUser = databaseHelper.getUser(userEmail);
        if (currentUser != null) {
            editTextFirstName.setText(currentUser.getFirstName());
            editTextLastName.setText(currentUser.getLastName());
        }
    }

    private void saveProfile() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String currentPassword = editTextCurrentPassword.getText().toString();
        String newPassword = editTextNewPassword.getText().toString();

        boolean valid = true;

        if (firstName.length() < 3 || firstName.length() > 10) {
            editTextFirstName.setError("First name must be 3-10 characters");
            valid = false;
        }

        if (lastName.length() < 3 || lastName.length() > 10) {
            editTextLastName.setError("Last name must be 3-10 characters");
            valid = false;
        }

        boolean isChangingPassword = !TextUtils.isEmpty(newPassword);

        if (isChangingPassword) {
            if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
                editTextNewPassword.setError("6-12 chars, with a digit, a lowercase and an uppercase letter");
                valid = false;
            }
            if (TextUtils.isEmpty(currentPassword)) {
                editTextCurrentPassword.setError("Enter your current password to change it");
                valid = false;
            } else if (!PasswordUtils.verifyPassword(currentPassword, currentUser.getSalt(), currentUser.getPassword())) {
                editTextCurrentPassword.setError("Current password is incorrect");
                valid = false;
            }
        }

        if (!valid) {
            Toast.makeText(getContext(), "Please fix the highlighted fields", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        databaseHelper.updateUserProfile(userEmail, firstName, lastName);
        if (isChangingPassword) {
            databaseHelper.updateUserPassword(userEmail, newPassword);
        }
        editTextCurrentPassword.setText("");
        editTextNewPassword.setText("");
        Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
    }
}
