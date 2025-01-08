package com.example.group4planninggame.models;
public class User {
    private final String email;
    private final String pwd;
    private String role;
    private Preferences preferences;

    public User() {
        email = null;
        pwd = null;
        role = null;
        preferences = null;
    }

    public User(String email, String pwd, String role) {
        this.email = email;
        this.pwd = pwd;
        this.role = role;
        preferences = null;
    }

    public User(String email, String pwd, String role, Preferences preferences) {
        this.email = email;
        this.pwd = pwd;
        this.role = role;
        this.preferences = preferences;
    }

    public String getEmail() {
        return email;
    }

    public String getPwd() {
        return pwd;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }
}
