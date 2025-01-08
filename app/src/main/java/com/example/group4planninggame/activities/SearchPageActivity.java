package com.example.group4planninggame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group4planninggame.R;
import com.example.group4planninggame.utils.Constants;

public class SearchPageActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner categoryFilter;
    private EditText searchBar;
    private EditText addressBar;
    private EditText itemRadiusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        categoryFilter = findViewById(R.id.categoryFilter);
        searchBar = findViewById(R.id.searchBar);
        addressBar = findViewById(R.id.addressBar);
        itemRadiusBar = findViewById(R.id.itemRadiusBar);
        String[] category = getResources().getStringArray(R.array.ItemCategory);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, category);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryFilter.setAdapter(aa);

        setupSearchButton();
        setupCancelButton();
    }

    private void setupCancelButton() {
        Button backButton = findViewById(R.id.cancelButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchPageActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    protected void setupSearchButton() {
        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String selectedCategory = categoryFilter.getSelectedItem().toString();
        String productName = searchBar.getText().toString().trim();
        String locationAddress = addressBar.getText().toString().trim();
        int itemRadius;
        if(itemRadiusBar.getText().toString().isEmpty()) {
            itemRadius = Constants.ITEM_RADIUS;
        } else {
            itemRadius = Integer.parseInt(itemRadiusBar.getText().toString()) * 1000;
        }

        Intent newProductIntent = new Intent(this, ViewProductsActivity.class);
        newProductIntent.putExtra("CATEGORY_FILTER", selectedCategory);
        newProductIntent.putExtra("PRODUCT_NAME", productName);
        newProductIntent.putExtra("SEARCH_LOCATION", locationAddress);
        newProductIntent.putExtra("ITEM_RADIUS", itemRadius);

        startActivity(newProductIntent);
    }
}
