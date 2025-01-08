package com.example.group4planninggame.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.group4planninggame.managers.CrudSingleton;
import com.example.group4planninggame.utils.Constants;
import com.example.group4planninggame.managers.Crud;
import com.example.group4planninggame.R;

public class UserSettingsActivity extends AppCompatActivity {
    private Crud dbCrud;
    private String currentEmail;
    private String currentRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbCrud = CrudSingleton.getInstance().getCrud();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewProductButton), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent returnToDashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(returnToDashboard);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        currentEmail = Constants.USER_EMAIL;
        currentRole = Constants.USER_ROLE;
        updateCurrentRoleText();

        TextView currentUserTextView = findViewById(R.id.currentUserTextView);
        String userText = "Signed in as " + currentEmail;
        currentUserTextView.setText(userText);

        Button switchRole = findViewById(R.id.switchRoleButton);
        switchRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchRoles(view);
            }
        });;

        Button closeSettings = findViewById(R.id.closeSettingsButton);
        closeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnToDashboard = new Intent(UserSettingsActivity.this,
                        DashboardActivity.class);
                startActivity(returnToDashboard);
            }
        });;

        Button logoutButton = findViewById(R.id.Logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        Button deleteAccountButton = findViewById(R.id.deleteAccountButton);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDeleteConfirmation();
            }

        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to Delete Your Account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        dbCrud.removeUser(Constants.USER_EMAIL);
        Constants.USER_EMAIL = null;
        Constants.USER_ROLE = null;

        Intent registerIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(registerIntent);
        finish();
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Constants.USER_EMAIL = null;
        Constants.USER_ROLE = null;
        Toast.makeText(UserSettingsActivity.this, "Logout successful! Redirecting to the login page.", Toast.LENGTH_SHORT).show();

        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void updateCurrentRoleText() {
        TextView currentRoleText = findViewById(R.id.currentRoleText);
        String displayText = "Current role: " + currentRole;
        currentRoleText.setText(displayText);
    }

    private boolean HasError() {
        boolean errorFound = (dbCrud == null || currentEmail == null || currentRole == null);

        if (errorFound) {
            String errorMessage = "";

            if (dbCrud == null)
                errorMessage = "ERROR: Database Couldn't Load";
            else if (currentEmail == null)
                errorMessage = "ERROR: Account Email Couldn't Load";
            else if (currentRole == null)
                errorMessage ="ERROR: Account Role Couldn't Load";

            Toast.makeText(UserSettingsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        }

        return errorFound;
    }

    private void switchRoles(View view) {
        if (HasError()) {
            return;
        }
        String role;

        //I expanded this one to make it more understandable
        if (currentRole.equals(Constants.ROLE_PROVIDER)) {
            role = Constants.ROLE_RECEIVER;
        } else {
            role = Constants.ROLE_PROVIDER;
        }
        dbCrud.updateUserRole(currentEmail, role);
        currentRole = role;

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_ROLE", role);
        editor.apply();
        Constants.USER_ROLE = currentRole;
        //updated the constants and shared preferences roles, i think.
        //Update -  it works now.... role change actualy happens on dashboard level

        String toastMessage = "Switched role to " + role;
        Toast.makeText(UserSettingsActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
        updateCurrentRoleText();
    }

}