package com.example.group4planninggame.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group4planninggame.R;
import com.example.group4planninggame.adapters.OffersAdapter;
import com.example.group4planninggame.interfaces.OffersViewInterface;
import com.example.group4planninggame.models.ExchangeOffer;
import com.example.group4planninggame.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyProductDetailsActivity extends AppCompatActivity implements OffersViewInterface{
    private RecyclerView recyclerView;
    private OffersAdapter offersAdapter;
    private List<ExchangeOffer> offerList;

    private TextView productTitle;
    private TextView productCategory;
    private TextView productCondition;
    private TextView productUserEmail;
    private TextView productDistance;
    private TextView productDescription;
    private TextView productDatePosted;
    private Double productLat;
    private Double productLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_product_details);

        recyclerView = findViewById(R.id.incomingOffers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        offerList= new ArrayList<>();
        offersAdapter = new OffersAdapter(offerList,this, this);
        recyclerView.setAdapter(offersAdapter);

        productTitle = findViewById(R.id.productTitle);
        productCategory = findViewById(R.id.productCategory);
        productCondition = findViewById(R.id.productCondition);
        productUserEmail = findViewById(R.id.productUserEmail2);
        productDistance = findViewById(R.id.productDistance);
        productDescription = findViewById(R.id.productDescription);
        productDatePosted = findViewById(R.id.productDatePosted2);

        Bundle extras = getIntent().getExtras();

        productTitle.setText(extras.get("PRODUCT_TITLE") != null ? extras.get("PRODUCT_TITLE").toString() : "");
        productCategory.setText(extras.get("PRODUCT_CATEGORY") != null ? extras.get("PRODUCT_CATEGORY").toString() : "");
        productCondition.setText(extras.get("PRODUCT_CONDITION") != null ? extras.get("PRODUCT_CONDITION").toString() : "");
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

        String description = extras.getString("PRODUCT_DESCRIPTION", "");
        Log.d("ProductDetails", "Description: " + description); // Log statement for debugging
        productDescription.setText(description.isEmpty() ? "No description available" : description);

        Button backButton = findViewById(R.id.backButton2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProductDetailsActivity.this, ViewMyProductsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });

        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(MyProductDetailsActivity.this, EditProductDetailsActivity.class);
                newIntent.putExtras(extras);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(newIntent);
                finish();
            }
        });

        fetchOffers();
    }


    protected void fetchOffers() {
        DatabaseReference exchangeOffersRef = FirebaseDatabase.getInstance().getReference("EXCHANGE_OFFERS");

        Bundle extras = getIntent().getExtras();
        String product = extras.get("PRODUCT_TITLE").toString();

        exchangeOffersRef.orderByChild("posterEmail").equalTo(Constants.USER_EMAIL)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        offerList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            ExchangeOffer offer = snapshot.getValue(ExchangeOffer.class);

                            if (offer != null && offer.getWantedProduct() != null && offer.getWantedProduct().equals(product)) {
                                offerList.add(offer);
                            }
                        }
                        offersAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MyProductDetailsActivity.this, "Failed to load offers: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MyProductDetailsActivity.this, DetailedExchangeOfferActivity.class);

        intent.putExtra("ITEM_NAME", offerList.get(position).getItemName());
        intent.putExtra("ITEM_CONDITION", offerList.get(position).getItemCondition());
        intent.putExtra("ITEM_CATEGORY", offerList.get(position).getItemCategory());
        intent.putExtra("ITEM_STATUS", offerList.get(position).getStatus());

        startActivity(intent);
    }
}

