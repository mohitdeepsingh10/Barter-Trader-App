package com.example.group4planninggame.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.group4planninggame.adapters.ProductAdapter;
import com.example.group4planninggame.interfaces.ProductViewInterface;
import com.example.group4planninggame.models.ExchangeOffer;
import com.example.group4planninggame.models.Product;
import com.example.group4planninggame.models.User;
import com.example.group4planninggame.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewMyProductsActivity extends AppCompatActivity implements ProductViewInterface {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private DatabaseReference productsRef;
    private TextView noProducts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_products);
        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList= new ArrayList<>();
        productAdapter = new ProductAdapter(getApplicationContext(), productList, this);
        recyclerView.setAdapter(productAdapter);

        noProducts = findViewById(R.id.productText);

        productsRef = FirebaseDatabase.getInstance().getReference("PRODUCT");

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewMyProductsActivity.this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });


        fetchProducts();

    }

    private void fetchProducts() {
        productList.clear();
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Button addButton = findViewById(R.id.addButton);
                productList.clear(); // Clear to avoid duplication on data change
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    String userEmail = Constants.USER_EMAIL;
                    if (product != null && (product.getUserEmail().equalsIgnoreCase(userEmail)))
                    {
                        productList.add(product);
                    }
                }
                if (productList.isEmpty()) {
                    noProducts.setText(R.string.no_products_found);
                    recyclerView.setVisibility(View.GONE);

                    addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ViewMyProductsActivity.this, ProductPostingActivity.class);
                            startActivity(intent);
                        }
                    });
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    addButton.setVisibility(View.GONE);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Product selectedProduct = productList.get(position);
        Intent detailsIntent = new Intent(ViewMyProductsActivity.this, MyProductDetailsActivity.class);
        detailsIntent.putExtra("PRODUCT_TITLE", selectedProduct.getTitle());
        detailsIntent.putExtra("PRODUCT_CATEGORY", selectedProduct.getCategory());
        detailsIntent.putExtra("PRODUCT_CONDITION", selectedProduct.getCondition());
        detailsIntent.putExtra("PRODUCT_USER_EMAIL", selectedProduct.getUserEmail());
        detailsIntent.putExtra("PRODUCT_DESCRIPTION", selectedProduct.getDescription());
        detailsIntent.putExtra("PRODUCT_LAT", selectedProduct.getLat());
        detailsIntent.putExtra("PRODUCT_LON", selectedProduct.getLon());
        detailsIntent.putExtra("PRODUCT_DATE_POSTED", selectedProduct.getDatePosted());
        detailsIntent.putExtra("PRODUCT_INITIAL_ID", selectedProduct.getInitialID());
        startActivity(detailsIntent);
    }
}
