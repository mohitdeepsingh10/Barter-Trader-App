package com.example.group4planninggame.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group4planninggame.R;
import com.example.group4planninggame.managers.Crud;
import com.example.group4planninggame.managers.CrudSingleton;
import com.example.group4planninggame.utils.Constants;
import com.example.group4planninggame.utils.Validator;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Crud dbCrud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If user is already logged in, navigate to Dashboard
        if (Constants.USER_EMAIL != null && Constants.USER_ROLE != null) {
            moveToDashboard();
            return;
        }

        // Check saved session
        getSession();
        if (Constants.USER_EMAIL != null && Constants.USER_ROLE != null) {
            moveToDashboard();
            return;
        }

        // Set layout and initialize components
        setContentView(R.layout.activity_login);
        setUpLoginButton();
        setupNotRegisteredButton();
        getUserDB();
    }

    protected void setUpLoginButton() {
        Button loginButton = findViewById(R.id.signInPushButton);
        loginButton.setOnClickListener(this);
    }

    protected void setupNotRegisteredButton() {
        Button notRegisteredButton = findViewById(R.id.register);
        notRegisteredButton.setOnClickListener(view -> {
            Constants.USER_EMAIL = null;
            Constants.USER_ROLE = null;
            moveToRegistration();
        });
    }

    @Override
    public void onClick(View view) {
        String email = getEmail();
        String password = getPassword();

        Validator validator = new Validator(dbCrud);

        if (validator.validateLogin(email, password)) {
            Constants.USER_EMAIL = email;
            Constants.USER_ROLE = dbCrud.getExtractedRole(email);

            saveUserSession(email, Constants.USER_ROLE);
            moveToDashboard();
        } else {
            showToast("Invalid credentials.");
        }
    }

    private void getSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = sharedPreferences.getString("USER_EMAIL", null);
        String role = sharedPreferences.getString("USER_ROLE", null);

        if (email != null && role != null) {
            Constants.USER_EMAIL = email;
            Constants.USER_ROLE = role;
        }
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public String getEmail() {
        EditText emailBox = findViewById(R.id.LoginEmailAddress);
        return emailBox.getText().toString().trim();
    }

    public String getPassword() {
        EditText passwordBox = findViewById(R.id.loginPassword);
        return passwordBox.getText().toString().trim();
    }

    private void getUserDB() {
        dbCrud = CrudSingleton.getInstance().getCrud();
    }

    protected void saveUserSession(String email, String role) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_EMAIL", email);
        editor.putString("USER_ROLE", role);
        editor.apply();
    }

    protected void moveToDashboard() {
        saveUserSession(Constants.USER_EMAIL, Constants.USER_ROLE);
        Intent dashboardIntent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(dashboardIntent);
        finish();
    }

    protected void moveToRegistration() {
        Intent registrationIntent = new Intent(this, RegistrationActivity.class);
        startActivity(registrationIntent);
        finish();
    }
}
