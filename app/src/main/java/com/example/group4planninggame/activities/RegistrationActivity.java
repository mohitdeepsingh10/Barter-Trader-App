package com.example.group4planninggame.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.group4planninggame.managers.CrudSingleton;
import com.example.group4planninggame.utils.Constants;
import com.example.group4planninggame.managers.Crud;
import com.example.group4planninggame.R;
import com.example.group4planninggame.utils.Validator;

import com.example.group4planninggame.utils.Constants;
import com.example.group4planninggame.managers.Crud;
import com.example.group4planninggame.R;
import com.example.group4planninggame.utils.Validator;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    Crud dbCrud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getUserDB();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Spinner profileSpinner = findViewById(R.id.RoleSpinner);
        String[] profileMenuOptions = getResources().getStringArray(R.array.RoleList);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, profileMenuOptions);
        profileSpinner.setAdapter(aa);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        setupRegistrationButton();
        setupAlreadyRegisteredButton();
    }

    protected void setupRegistrationButton() {
        Button registerButton = findViewById(R.id.SignUpPushButton);
        registerButton.setOnClickListener(this);
    }

    protected void setupAlreadyRegisteredButton() {
        Button skipRegistrationButton = findViewById(R.id.skipRegistration);
        skipRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.USER_EMAIL = null;
                Constants.USER_ROLE = null;
                moveToLogin();
            }
        });
    }

    @Override
    public void onClick(View view) {
        String email = getEmail();
        String role = getRole();
        String[] passwords = getPasswords();

        Validator validator = new Validator(dbCrud);

        if(validator.validateRegistration(email, passwords, role)) {
            writeToDB(email, passwords[0], role);
            showToast("Selection of " + role + " successful.");
            showToast("Registration successful.");
            moveToLogin();
        } else {
            showToast(validator.getError());
        }
    }

    private void showToast(String message) {
        Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public String getEmail() {
        EditText emailBox = findViewById(R.id.signUpEmailAddress);
        return emailBox.getText().toString().trim();
    }

    public String[] getPasswords() {
        String[] retArr = new String[2];
        EditText passwordBox = findViewById(R.id.signUpPassword);
        EditText password2Box = findViewById(R.id.repeatedSignUpPassword2);

        retArr[0] = passwordBox.getText().toString().trim();
        retArr[1] = password2Box.getText().toString().trim();

        return retArr;
    }

    public String getRole() {
        Spinner roleBox = findViewById(R.id.RoleSpinner);
        return roleBox.getSelectedItem().toString().trim();
    }

    private void getUserDB() {
        dbCrud = CrudSingleton.getInstance().getCrud();
    }

    private void writeToDB(String email, String password, String role) {
        dbCrud.writeUser(email, password, role);
    }

    protected void moveToLogin() {
        Intent newLoginIntent = new Intent(this, LoginActivity.class);
        startActivity(newLoginIntent);
        finish();
    }
}
