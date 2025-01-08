package com.example.group4planninggame.utils;

import android.content.Context;

import com.example.group4planninggame.R;
import com.example.group4planninggame.managers.Crud;

public class ItemValidator {
    private final Crud dbCrud;
    private String error;
    private Context context;

    public ItemValidator() {
        this.dbCrud = null;
        this.error = "";
        context = null;
    }

    public ItemValidator(Crud dbCrud, Context context) {
        this.dbCrud = dbCrud;
        this.error = "";
        this.context = context;
    }

    public ItemValidator(Crud dbCrud) {
        this.dbCrud = dbCrud;
    }

    /**
     * Validates all parameters, and if not valid sends errors
     *
     * @param userEmail
     * @param title
     * @param condition
     * @param category
     * @param description
     * @return true if all match the required, if not false with set error message
     */
    public boolean validateItemPosting(String userEmail, String title, String condition,
                                       String category, String description) {
        if (!isEmailValid(userEmail)) {
            error = "Email fetching error.";
            return false;
        }
        if (isEmptyTitle(title)) {
            error = "Title cannot be empty.";
            return false;
        }
        if (!isTitleValid(title)) {
            error = "Title must be at least 3 characters long.";
            return false;
        }
        if (!isConditionValid(condition)) {
            error = "Invalid condition. Please select a valid condition.";
            return false;
        }
        if (!isCategoryValid(category)) {
            error = "Invalid category. Please select a valid category.";
            return false;
        }
        if (isEmptyDescription(description)) {
            error = "Description cannot be empty.";
            return false;
        }

        error = "";
        return true;
    }

    private boolean isEmailValid(String email) {
        Validator validator = new Validator();
        return !validator.isEmptyEmail(email) && validator.isEmailValid(email);
    }

    private boolean isEmptyTitle(String title) {
        return title.isEmpty();
    }

    private boolean isTitleValid(String title) {
        return title.length() >= 3;
    }

    private boolean isConditionValid(String condition) {
        String[] validConditions = context.getResources().getStringArray(R.array.ItemCondition);
        for (int i = 1; i < validConditions.length; i++) {
            if (validConditions[i].equals(condition)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCategoryValid(String category) {
        String[] validCategories = context.getResources().getStringArray(R.array.ItemCategory);
        for (int i =1; i < validCategories.length; i++) {
            if (validCategories[i].equals(category)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmptyDescription(String description) {
        return description.isEmpty();
    }

    public String getError() {
        return error;
    }
}
