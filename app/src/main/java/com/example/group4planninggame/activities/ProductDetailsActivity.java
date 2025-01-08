package com.example.group4planninggame.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;
import android.widget.Toast;

import com.example.group4planninggame.R;
import com.example.group4planninggame.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {
    private TextView productTitle;
    private TextView productCategory;
    private TextView productCondition;
    private TextView productUserEmail;
    private TextView productDistance;
    private TextView productDescription;
    private Double productLat;
    private Double productLon;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Button offerExchangeButton = findViewById(R.id.offerExchangeButton);
        offerExchangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOfferExchangeConfirmation();
            }
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        productTitle = findViewById(R.id.productTitle);
        productCategory = findViewById(R.id.productCategory);
        productCondition = findViewById(R.id.productCondition);
        productUserEmail = findViewById(R.id.productUserEmail2);
        productDistance = findViewById(R.id.productDistance);
        productDescription = findViewById(R.id.productDescription);

        Bundle extras = getIntent().getExtras();

        productTitle.setText(extras.get("PRODUCT_TITLE") != null ? extras.get("PRODUCT_TITLE").toString() : "");
        productCategory.setText(extras.get("PRODUCT_CATEGORY") != null ? extras.get("PRODUCT_CATEGORY").toString() : "");
        productCondition.setText(extras.get("PRODUCT_CONDITION") != null ? extras.get("PRODUCT_CONDITION").toString() : "");
        productUserEmail.setText(extras.get("PRODUCT_USER_EMAIL") != null ? extras.get("PRODUCT_USER_EMAIL").toString() : "");

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

        String description = extras.getString("PRODUCT_DESCRIPTION", "");
        Log.d("ProductDetails", "Description: " + description); // Log statement for debugging
        productDescription.setText(description.isEmpty() ? "No description available" : description);

        Button backButton = findViewById(R.id.cancelButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this, SearchPageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });

        setupViewOnMapButton();
    }

    private void setupViewOnMapButton() {
        Button viewOnMapButton = findViewById(R.id.viewOnMapButton);

        viewOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewOnMapIntent = new Intent(getApplicationContext(), SearchMapActivity.class);
                viewOnMapIntent.putExtra("CATEGORY_FILTER", productCategory.getText().toString().trim());
                viewOnMapIntent.putExtra("PRODUCT_NAME", productTitle.getText().toString().trim());
                viewOnMapIntent.putExtra("SEARCH_LAT", productLat);
                viewOnMapIntent.putExtra("SEARCH_LON", productLon);
                viewOnMapIntent.putExtra("ITEM_RADIUS", 1);

                startActivity(viewOnMapIntent);
            }
        });
    }

    private void showOfferExchangeConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Offer Exchange")
                .setMessage("Are you sure you want to offer an exchange?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openOfferExchangeActivity();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void openOfferExchangeActivity() {
        String currentUserEmail = getIntent().getStringExtra("CURRENT_USER_EMAIL");
        String itemTitle = getIntent().getStringExtra("PRODUCT_TITLE");

        DatabaseReference exchangeOffersRef = FirebaseDatabase.getInstance().getReference("EXCHANGE_OFFERS");
        exchangeOffersRef.orderByChild("itemName").equalTo(itemTitle)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean hasAcceptedOffer = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String offerStatus = snapshot.child("status").getValue(String.class);
                            String offerUserEmail = snapshot.child("userEmail").getValue(String.class);

                            if ("Accepted".equals(offerStatus) && currentUserEmail.equals(offerUserEmail)) {
                                hasAcceptedOffer = true;
                                break;
                            }
                        }

                        if (hasAcceptedOffer) {
                            Toast.makeText(ProductDetailsActivity.this, "An accepted offer already exists for this item.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProductDetailsActivity.this, "Loading Offer exchange Form", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProductDetailsActivity.this, OfferExchangeActivity.class);
                            intent.putExtra("PRODUCT_USER_EMAIL", getIntent().getStringExtra("PRODUCT_USER_EMAIL"));
                            intent.putExtra("PRODUCT_NAME", getIntent().getStringExtra("PRODUCT_TITLE"));
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ProductDetailsActivity.this, "Error checking offers: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
