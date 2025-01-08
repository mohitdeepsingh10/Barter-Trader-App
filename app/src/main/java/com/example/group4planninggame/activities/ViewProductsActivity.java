package com.example.group4planninggame.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import com.example.group4planninggame.adapters.ProductAdapter;
import com.example.group4planninggame.interfaces.ProductViewInterface;
import com.example.group4planninggame.models.Product;
import com.example.group4planninggame.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewProductsActivity extends AppCompatActivity implements ProductViewInterface {

    private RecyclerView recyclerView;
    private List<Product> productList;
    private ProductAdapter productAdapter;
    private DatabaseReference productsRef;
    private String selectedCategory;
    private String productName;
    private Location searchLocation;
    private int itemRadius;

    private TextView noResultsTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getApplicationContext(), productList, this);
        recyclerView.setAdapter(productAdapter);

        noResultsTextView = findViewById(R.id.noResultsTextView);

        selectedCategory = getIntent().getStringExtra("CATEGORY_FILTER");
        productName = getIntent().getStringExtra("PRODUCT_NAME");

        productsRef = FirebaseDatabase.getInstance().getReference("PRODUCT");

        getSearchRadius();
        getSearchLocation();

        fetchProducts(selectedCategory, productName);

        setupBackButton();
    }

    private void setupBackButton() {
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProductsActivity.this, SearchPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getSearchRadius() {
        itemRadius = getIntent().getIntExtra("ITEM_RADIUS", 10000);
    }

    private void getSearchLocation() {
        String locAddress = getIntent().getStringExtra("SEARCH_LOCATION");

        if(locAddress == null || locAddress.isEmpty()) {
            searchLocation = Constants.USER_LOCATION;
            Log.d("LOCATION_LOG", "Using Default Location");
            setupViewOnMapButton();
            return;
        }

        Geocoder searchCoder = new Geocoder(this);
        List<Address> addressList;
        try {
            addressList = searchCoder.getFromLocationName(locAddress, 1);

            searchLocation = new Location("");

            if(addressList.isEmpty()) {
                searchLocation = Constants.USER_LOCATION;
            } else {
                searchLocation.setLatitude(addressList.get(0).getLatitude());
                searchLocation.setLongitude(addressList.get(0).getLongitude());
            }
        } catch (IOException e) {
            Log.d("YEOWCH!", e.toString());
        }

        setupViewOnMapButton();
    }

    private void setupViewOnMapButton() {
        String categoryFilter;
        String productName;
        Double searchLat;
        Double searchLon;

        categoryFilter = getIntent().getStringExtra("CATEGORY_FILTER");
        productName = getIntent().getStringExtra("PRODUCT_NAME");

        searchLat = searchLocation.getLatitude();
        searchLon = searchLocation.getLongitude();

        Button viewOnMapButton = findViewById(R.id.mapViewButton);

        viewOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent viewOnMapIntent = new Intent(getApplicationContext(), SearchMapActivity.class);
                viewOnMapIntent.putExtra("CATEGORY_FILTER", categoryFilter);
                viewOnMapIntent.putExtra("PRODUCT_NAME", productName);
                viewOnMapIntent.putExtra("SEARCH_LAT", searchLat);
                viewOnMapIntent.putExtra("SEARCH_LON", searchLon);
                viewOnMapIntent.putExtra("ITEM_RADIUS", itemRadius);

                startActivity(viewOnMapIntent);
            }
        });
    }

    private void fetchProducts(String categoryFilter, String productName) {
        productList.clear();
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear(); // Clear to avoid duplication on data change
                boolean itemsFound = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null
                            && (categoryFilter.equals(getApplicationContext().getResources().getStringArray(R.array.ItemCategory)[0]) || product.getCategory().equalsIgnoreCase(categoryFilter))
                            && (productName == null || product.getTitle().toLowerCase().contains(productName.toLowerCase()))
                            && itemRadius >= product.getDistance(searchLocation)
                       ) {

                        productList.add(product);
                        itemsFound = true;
                    }
                }
                productAdapter.notifyDataSetChanged();
                if (!itemsFound) {
                    Toast.makeText(ViewProductsActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                    noResultsTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else{
                    noResultsTextView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewProductsActivity.this, "Error fetching products: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Product selectedProduct = productList.get(position);
        Intent detailsIntent = new Intent(ViewProductsActivity.this, ProductDetailsActivity.class);
        detailsIntent.putExtra("PRODUCT_TITLE", selectedProduct.getTitle());
        detailsIntent.putExtra("PRODUCT_CATEGORY", selectedProduct.getCategory());
        detailsIntent.putExtra("PRODUCT_CONDITION", selectedProduct.getCondition());
        detailsIntent.putExtra("PRODUCT_USER_EMAIL", selectedProduct.getUserEmail());
        detailsIntent.putExtra("PRODUCT_DESCRIPTION", selectedProduct.getDescription());
        detailsIntent.putExtra("PRODUCT_LAT", selectedProduct.getLat());
        detailsIntent.putExtra("PRODUCT_LON", selectedProduct.getLon());
        detailsIntent.putExtra("PRODUCT_DATE_POSTED", selectedProduct.getDatePosted());
        startActivity(detailsIntent);
    }
}
