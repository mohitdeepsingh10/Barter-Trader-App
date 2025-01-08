package com.example.group4planninggame.managers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.group4planninggame.models.Product;
import com.example.group4planninggame.utils.Constants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProductManager {
    private final DatabaseReference productsRef;
    private final HashMap<String, Product> products;

    public ProductManager(FirebaseDatabase database) {
        productsRef = database.getReference("PRODUCT");
        products = new HashMap<>();
        addProductRefListener();
    }

    private void addProductRefListener(){
        productsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Product product = snapshot.getValue(Product.class);
                if (product != null && product.getInitialID() != null) {
                    products.put(product.getInitialID(), product);
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                Product product = snapshot.getValue(Product.class);
                if (product != null && product.getInitialID() != null) {
                    products.put(product.getInitialID(), product);
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                if (product != null && product.getInitialID() != null) {
                    products.remove(product.getInitialID());
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ProductManager", error.getMessage());
            }
        });
    }

    // Adding a product to firebase and local
    public void addProductToDatabase(String userEmail, String title, String condition,
                                     String category, String description, Double lat, Double lon , String datePosted) {
        if (!isProductInDB(userEmail + " " + title)) {
            DatabaseReference newRef = productsRef.push();
            Product product = new Product(userEmail, title, condition, category, description, lat, lon, datePosted);
            newRef.setValue(product);
        } else {
            Log.e("DatabaseError", "Tried to add item that already exists");
        }
    }

    // Updating a product in firebase and local
    public void updateProduct(String initialID, String title, String condition,
                              String category, String description) {
        productsRef.orderByChild("initialID").equalTo(initialID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    DatabaseReference thisRef = childSnapshot.getRef();
                    thisRef.child("title").setValue(title);
                    thisRef.child("condition").setValue(condition);
                    thisRef.child("category").setValue(category);
                    thisRef.child("description").setValue(description);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ProductManager", error.getMessage());
            }
        });
    }

    // removing a product from firebase and local
    public void removeProduct(String initialID) {
        productsRef.orderByChild("initialID").equalTo(initialID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    childSnapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("UserManager", error.getMessage());
            }
        });
    }

    // get a product from the local by initialID
    public Product getProduct(String initialID) {
        return products.get(initialID);
    }

    // checking if a product exists in local
    public boolean isProductInDB(String initialID) {
        return products.containsKey(initialID);
    }
}
