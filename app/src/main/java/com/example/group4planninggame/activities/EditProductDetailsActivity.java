package com.example.group4planninggame.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group4planninggame.R;
import com.example.group4planninggame.managers.Crud;
import com.example.group4planninggame.utils.Constants;

import java.util.Arrays;
import java.util.Locale;

public class EditProductDetailsActivity extends AppCompatActivity{ ;

    private EditText productTitleEdit;
    private Spinner productCategory;
    private Spinner productCondition;
    private TextView productUserEmail;
    private TextView productDistance;
    private EditText productDescription;
    private TextView productDatePosted;
    private Double productLat;
    private Double productLon;
    private Bundle extras;

    private final Crud dbCrud = new Crud();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_edit);

        productTitleEdit = findViewById(R.id.editName);
        productCategory = findViewById(R.id.editCategory);
        productCondition = findViewById(R.id.editCondition);
        productUserEmail = findViewById(R.id.productUserEmail2);
        productDistance = findViewById(R.id.productDistance2);
        productDescription = findViewById(R.id.editDescription);
        productDatePosted = findViewById(R.id.productDatePosted2);

        String[] categoryOptions = getResources().getStringArray(R.array.ItemCategory);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, categoryOptions);
        productCategory.setAdapter(aa);

        String[] conditionOptions = getResources().getStringArray(R.array.ItemCondition);
        aa = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, conditionOptions);
        productCondition.setAdapter(aa);

        extras = getIntent().getExtras();

        productUserEmail.setText(extras.get("PRODUCT_USER_EMAIL") != null ? extras.get("PRODUCT_USER_EMAIL").toString() : "");
        productDatePosted.setText(extras.get("PRODUCT_DATE_POSTED") != null ? extras.get("PRODUCT_DATE_POSTED").toString() : "");

        productLat = extras.getDouble("PRODUCT_LAT");
        productLon = extras.getDouble("PRODUCT_LON");

        Location itemLoc = new Location("");
        itemLoc.setLatitude(productLat);
        itemLoc.setLongitude(productLon);
        double distance = itemLoc.distanceTo(Constants.USER_LOCATION);
        String distanceText;
        if (distance >= 1000) {
            distanceText = String.format(Locale.getDefault(), "%.2f km away", distance / 1000);
        } else {
            distanceText = String.format(Locale.getDefault(), "%.0f meters away", distance);
        }
        productDistance.setText(distanceText);

        Button backButton = findViewById(R.id.backButton2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button submitButton = findViewById(R.id.submitButton2);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSubmitUpdate()) {
                    finish();
                }
            }
        });
    }

    public boolean onSubmitUpdate() {
        String productTitle = productTitleEdit.getText().toString().trim();
        String category = productCategory.getSelectedItem() != null ? productCategory.getSelectedItem().toString() : "";
        String condition = productCondition.getSelectedItem() != null ? productCondition.getSelectedItem().toString() : "";
        String description = productDescription.getText().toString().trim();


        String initialID = extras.getString("PRODUCT_INITIAL_ID");

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

        // Save information with crud (how)
        dbCrud.updateProduct(initialID, productTitle, condition, category, description);

        Toast.makeText(this, "Product edited successfully", Toast.LENGTH_SHORT).show();
        return true;
    }
}

