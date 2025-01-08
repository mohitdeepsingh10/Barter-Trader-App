package com.example.group4planninggame.utils;

import androidx.core.util.PatternsCompat;

import com.example.group4planninggame.managers.Crud;

import java.util.ArrayList;

public class Validator {
    private final Crud dbCrud;
    private String error;

    public Validator() {
        dbCrud = null;
        this.error = "";
    }

    public Validator(Crud dbCrud) {
        this.dbCrud = dbCrud;
        this.error = "";
    }

    public boolean validateLogin(String email, String password) {
        if (isEmptyEmail(email)) {
            error = "Empty email.";
            return false;
        }
        if (isEmptyPassword(password)) {
            error = "Empty password.";
            return false;
        }
        if (!isEmailValid(email)) {
            error = "Invalid email.";
            return false;
        }
        if (!dbCrud.checkUserInDB(email)) {
            error = "No user matching email entered.";
            return false;
        }
        if (!password.equals(dbCrud.getExtractedPassword(email))) {
            error = "Incorrect password.";
            return false;
        }

        error = "";
        return true;
    }

    public boolean validateRegistration(String email, String[] passwords, String role) {
        if (isEmptyEmail(email)) {
            error = "Empty email.";
            return false;
        }
        if (!isMatchingPassword(passwords[0],passwords[1])) {
            error = "Passwords do not match.";
            return false;
        }
        if (!isValidPassword(passwords[0])){
            error = "Password needs to be over 8 characters.";
            return false;
        }
        if (isEmptyPassword(passwords[0])) {
            error = "Empty password.";
            return false;
        }
        if (!isEmailValid(email)) {
            error = "Invalid email.";
            return false;
        }
        if (dbCrud.checkUserInDB(email)) {
            error = "User already exists with that email.";
            return false;
        }
        if (!isValidRole(role)) {
            error = "Invalid role.";
            return false;
        }
        if (!passwords[0].equals(dbCrud.getExtractedPassword(email))) {
            error = "Incorrect password.";
        }

        return true;
    }

    public boolean isEmptyEmail(String email) {
        return email.isEmpty();
    }

    public boolean isEmailValid(String email) {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isEmptyPassword(String password) {
        return password.isEmpty();
    }

    public boolean isValidPassword(String password) {
        return !password.isEmpty();       // replace with actual logic on release
    }

    public boolean isMatchingPassword(String password1, String password2){
        return password1.equals(password2);
    }

    public boolean isValidRole(String role) {
        ArrayList<String> roles = new ArrayList<>();
        roles.add(Constants.ROLE_PROVIDER);
        roles.add(Constants.ROLE_RECEIVER);

        return roles.contains(role);
    }

    public String getError() {
        return this.error;
    }

}
