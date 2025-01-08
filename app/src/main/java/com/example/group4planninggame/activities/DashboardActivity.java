package com.example.group4planninggame.activities;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationCompat;

import com.example.group4planninggame.R;
import com.example.group4planninggame.managers.Crud;
import com.example.group4planninggame.managers.CrudSingleton;
import com.example.group4planninggame.models.Preferences;
import com.example.group4planninggame.models.Product;
import com.example.group4planninggame.utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicReference;

public class DashboardActivity extends AppCompatActivity {
    private DatabaseReference productsRef;
    private Preferences prefs;
    private Crud dbCrud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUserSession();
        productsRef = FirebaseDatabase.getInstance().getReference("PRODUCT");
        dbCrud = CrudSingleton.getInstance().getCrud();

        if (Constants.USER_ROLE.equals(Constants.ROLE_RECEIVER)) {
            setContentView(R.layout.activity_dashboard_receiver);
            setupReceiverDashboard();
        } else {
            setContentView(R.layout.activity_dashboard_provider);
            setupProviderDashboard();
        }

        EdgeToEdge.enable(this);

        TextView roleText = findViewById(R.id.roleTextView);
        roleText.setText(Constants.USER_ROLE);

        getUserLocation(roleText);
        listenForPreferenceUpdates(this::listenForPreferredProducts);

        String toastMsg = "Loading Success!";
        Toast successToast = Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT);
        successToast.show();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        });
    }

    private void setupReceiverDashboard() {
        Button viewProductButton = findViewById(R.id.viewProductButton);
        Button submittedOffersButton = findViewById(R.id.submittedOffers);
        Button settingsButton = findViewById(R.id.settingsButton);
        Button updatePreferenceButton = findViewById(R.id.updatePreferences);

        submittedOffersButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ViewSubmittedOffersActivity.class);
            startActivity(intent);
        });

        viewProductButton.setOnClickListener(v -> openProductDetails());
        settingsButton.setOnClickListener(v -> openUserSettings());
        updatePreferenceButton.setOnClickListener(v -> openUpdatePreferenceMenu());
    }

    private void setupProviderDashboard() {
        Button viewExchangeButton = findViewById(R.id.viewIncomingExchangeButton);
        Button postProductButton = findViewById(R.id.postProductButton);
        Button viewPostedButton = findViewById(R.id.viewPostedButton);
        Button settingsButton = findViewById(R.id.settingsButton);

        postProductButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProductPostingActivity.class);
            startActivity(intent);
        });

        viewPostedButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ViewMyProductsActivity.class);
            startActivity(intent);
        });

        viewExchangeButton.setOnClickListener(v -> viewIncomingExchanges());
        settingsButton.setOnClickListener(v -> openUserSettings());
    }

    private void openProductDetails() {
        Intent intent = new Intent(DashboardActivity.this, SearchPageActivity.class);
        startActivity(intent);
    }

    private void openUpdatePreferenceMenu() {
        Intent intent = new Intent(DashboardActivity.this, UpdatePreferenceActivity.class);
        startActivity(intent);
    }

    private void openUserSettings() {
        Intent intent = new Intent(DashboardActivity.this, UserSettingsActivity.class);
        startActivity(intent);
    }

    private void viewIncomingExchanges() {
        Intent intent = new Intent(DashboardActivity.this, ViewIncomingOffersActivity.class);
        startActivity(intent);
    }

    private void listenForPreferredProducts() {
        productsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                processProducts(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                processProducts(snapshot);
                Log.d("TEST", "found a prod");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MainActivity", "Failed to listen for products: " + error.getMessage());
            }
        });
    }

    private void processProducts(DataSnapshot snapshot) {
        Product prod = snapshot.getValue(Product.class);

        if (prod != null) {
            List<String> preferredCategories = prefs.getCategories();

            Location preferredLocation = new Location("");
            preferredLocation.setLatitude(prefs.getLat());
            preferredLocation.setLongitude(prefs.getLon());

            Location productLocation = new Location("");
            productLocation.setLatitude(prod.getLat());
            productLocation.setLongitude(prod.getLon());

            // Use the product's initialID as the unique identifier
            String productId = prod.getInitialID();

            // Check if the product has already been notified
            if (isProductNotified(productId)) {
                Log.d("DashboardActivity", "Notification already sent for this product: " + productId);
                return; // Skip further processing
            }

            // Calculate distance between product and preferred location
            float distanceInMeters = preferredLocation.distanceTo(productLocation);

            // Check if the product matches the user's preferences
            if (distanceInMeters <= prefs.getDistance() * 1000  // Convert distance to meters
                    && preferredCategories.contains(prod.getCategory())) {

                // Convert distance to a user-friendly format
                String distanceText;
                if (distanceInMeters >= 1000) {
                    distanceText = String.format(Locale.getDefault(), "%.2f km away", distanceInMeters / 1000);
                } else {
                    distanceText = String.format(Locale.getDefault(), "%.0f meters away", distanceInMeters);
                }

                // Generate notification content with specific category and distance details
                String notificationContent = "New " + prod.getCategory() +
                        " available " + distanceText + "!";

                // Display the notification
                showNotification("Product Alert!", notificationContent, prod);

                // Mark the product as notified locally
                markProductAsNotified(productId);
            }
        }
    }

    private void showNotification(String title, String content, Product product) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Create an intent to open ProductDetailsActivity
        Intent intent = new Intent(this, ProductDetailsActivity.class);

        // Pass product details to the intent
        intent.putExtra("PRODUCT_TITLE", product.getTitle());
        intent.putExtra("PRODUCT_CATEGORY", product.getCategory());
        intent.putExtra("PRODUCT_CONDITION", product.getCondition());
        intent.putExtra("PRODUCT_USER_EMAIL", product.getUserEmail());
        intent.putExtra("PRODUCT_LAT", product.getLat());
        intent.putExtra("PRODUCT_LON", product.getLon());
        intent.putExtra("PRODUCT_DESCRIPTION", product.getDescription());
        intent.putExtra("PRODUCT_DATE_POSTED", product.getDatePosted());

        // Add FLAG_IMMUTABLE to the PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "product_notifications";
            String channelName = "Product Notifications";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);

            Notification notification = new Notification.Builder(this, channelId)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();

            manager.notify((int) System.currentTimeMillis(), notification);
        } else {
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();

            manager.notify((int) System.currentTimeMillis(), notification);
        }
    }

    // Updated to accept a callback to ensure location is fetched before product processing
    protected void getUserLocation(TextView roleText) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        fusedLocationClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, null).addOnSuccessListener(location -> {
            if (location != null) {
                Constants.USER_LOCATION = location;

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addressList == null || addressList.isEmpty()) {
                        roleText.setText(roleText.getText() + " Waiting for location...");
                    } else {
                        roleText.setText(roleText.getText() + " in " + addressList.get(0).getLocality());
                    }
                } catch (IOException e) {
                    Log.e("DashboardActivity", "Geocoder failed: " + e.getMessage());
                }
            } else {
                Log.e("DashboardActivity", "Failed to retrieve user location.");
            }
        }).addOnFailureListener(e -> {
            Log.d("LOCATION", "Failed to get location in Dashboard: " + e.getMessage());
        });
    }

    private void listenForPreferenceUpdates(Runnable onPreferencesFetched) {
        FirebaseDatabase.getInstance().getReference("USER").orderByChild("email").equalTo(Constants.USER_EMAIL).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(dataSnapshot.hasChild("preferences")) {
                        prefs = dataSnapshot.child("preferences").getValue(Preferences.class);
                        Log.d("DEBUG", "User preferences fetched successfully.");
                        onPreferencesFetched.run();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DashboardActivity", "Failed to listen for preferences: " + error.getMessage());
            }
        });
    }

    private boolean isProductNotified(String productId) {
        SharedPreferences prefs = getSharedPreferences("NotifiedProducts", MODE_PRIVATE);
        return prefs.contains(productId);
    }

    private void markProductAsNotified(String productId) {
        SharedPreferences prefs = getSharedPreferences("NotifiedProducts", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(productId, true);
        editor.apply();
    }

    protected void checkUserSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = sharedPreferences.getString("USER_EMAIL", null);
        String role = sharedPreferences.getString("USER_ROLE", null);

        if (email == null || role == null) {
            Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            Constants.USER_EMAIL = email;
            Constants.USER_ROLE = role;
        }
    }
}
