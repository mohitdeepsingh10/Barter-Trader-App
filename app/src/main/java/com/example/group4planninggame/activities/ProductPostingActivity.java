package com.example.group4planninggame.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group4planninggame.R;
import com.example.group4planninggame.managers.Crud;
import com.example.group4planninggame.managers.CrudSingleton;
import com.example.group4planninggame.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProductPostingActivity extends AppCompatActivity {
    private EditText titleTextBox;
    private Spinner itemConditionSpinner;
    private Spinner itemCategorySpinner;
    private MultiAutoCompleteTextView descriptionTextBox;
    private Button submitButton;
    private Button cancelButton;
    private Crud dbCrud;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbCrud = CrudSingleton.getInstance().getCrud();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_posting);
        titleTextBox = findViewById(R.id.titleTextBox);
        itemConditionSpinner = findViewById(R.id.itemConditionSpinner);
        itemCategorySpinner = findViewById(R.id.itemCategorySpinner);
        descriptionTextBox = findViewById(R.id.multiAutoCompleteTextView);
        submitButton = findViewById(R.id.submitButton);
        cancelButton = findViewById(R.id.cancelButton);

        String[] categoryOptions = getResources().getStringArray(R.array.ItemCategory);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, categoryOptions);
        itemCategorySpinner.setAdapter(aa);

        String[] conditionOptions = getResources().getStringArray(R.array.ItemCondition);
        aa = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, conditionOptions);
        itemConditionSpinner.setAdapter(aa);

        // Listener for submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSubmitProductForm()) {
                    finish();

                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public boolean onSubmitProductForm() {
        String productTitle = titleTextBox.getText().toString().trim();
        String category = itemCategorySpinner.getSelectedItem() != null ? itemCategorySpinner.getSelectedItem().toString() : "";
        String condition = itemConditionSpinner.getSelectedItem() != null ? itemConditionSpinner.getSelectedItem().toString() : "";
        String description = descriptionTextBox.getText().toString().trim();

        String[] categoryOptions = getResources().getStringArray(R.array.ItemCategory);
        String[] conditionOptions = getResources().getStringArray(R.array.ItemCondition);


        // Validate fields
        if (productTitle.isEmpty()) {
            Toast.makeText(this, "Please provide product name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (category.equals(categoryOptions[0])) {
            Toast.makeText(this, "Please select a valid product category", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (condition.equals(conditionOptions[0])) {
            Toast.makeText(this, "Please select a valid product condition", Toast.LENGTH_SHORT).show();
            return false;
        }

        Double lat = Constants.USER_LOCATION.getLatitude();
        Double lon = Constants.USER_LOCATION.getLongitude();

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        // Save information with crud (how)
        dbCrud.addProductToDatabase(Constants.USER_EMAIL, productTitle, condition, category, description, lat, lon, date);

        Toast.makeText(this, "Product posted successfully", Toast.LENGTH_SHORT).show();
        return true;
    }
}
