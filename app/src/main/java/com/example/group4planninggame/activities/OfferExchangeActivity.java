package com.example.group4planninggame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group4planninggame.managers.Crud;
import com.example.group4planninggame.R;
import com.example.group4planninggame.managers.CrudSingleton;
import com.example.group4planninggame.utils.Constants;
import com.example.group4planninggame.utils.Validator;

public class OfferExchangeActivity extends AppCompatActivity {

    private Spinner itemConditionSpinner;
    private Spinner itemCategorySpinner;
    private Crud dbCrud;
    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_exchange);

        dbCrud = CrudSingleton.getInstance().getCrud();
        validator = new Validator();

        itemConditionSpinner = findViewById(R.id.itemCondition);
        itemCategorySpinner = findViewById(R.id.itemCategory);
        String productOwnerEmail = getIntent().getStringExtra("PRODUCT_USER_EMAIL");
        if (productOwnerEmail != null && productOwnerEmail.equals(Constants.USER_EMAIL)) {
            Toast.makeText(this, "You cannot offer an exchange on your own product.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String[] conditions = getResources().getStringArray(R.array.ItemCondition);
        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, conditions);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemConditionSpinner.setAdapter(conditionAdapter);

        String[] categories = getResources().getStringArray(R.array.ItemCategory);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemCategorySpinner.setAdapter(categoryAdapter);

        EditText userEmailInput = findViewById(R.id.userEmailInput);
        userEmailInput.setText(Constants.USER_EMAIL);
        userEmailInput.setEnabled(false);

        Button submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(v -> offerExchange());

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> finish());
    }

    private void offerExchange() {

        EditText itemNameInput = findViewById(R.id.itemNameInput);
        String itemName = itemNameInput.getText().toString().trim();
        String userEmail = Constants.USER_EMAIL;
        String itemCondition = itemConditionSpinner.getSelectedItem().toString();
        String itemCategory = itemCategorySpinner.getSelectedItem().toString();
        String posterEmail = getIntent().getStringExtra("PRODUCT_USER_EMAIL"); // Poster email passed as intent extra
        String wantedProduct =  getIntent().getStringExtra("PRODUCT_NAME");

        String[] categoryOptions = getResources().getStringArray(R.array.ItemCategory);
        String[] conditionOptions = getResources().getStringArray(R.array.ItemCondition);

        if (itemName.isEmpty()) {
            Toast.makeText(this, "Item Name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (itemCategory.equals(categoryOptions[0])) {
            Toast.makeText(this, "Please select a valid product category", Toast.LENGTH_SHORT).show();
            return;
        }
        if (itemCondition.equals(conditionOptions[0])) {
            Toast.makeText(this, "Please select a valid product condition", Toast.LENGTH_SHORT).show();
            return;
        }

        dbCrud.isItemAlreadyListed(itemName, userEmail, isAlreadyListed -> {
            if (isAlreadyListed) {
                Toast.makeText(this, "You have already listed this item for exchange.", Toast.LENGTH_LONG).show();
            } else {
                dbCrud.saveExchangeOffer(itemName, userEmail, itemCondition, itemCategory, posterEmail, wantedProduct);
                Toast.makeText(this, "Exchange offer for " + itemName + " sent.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
