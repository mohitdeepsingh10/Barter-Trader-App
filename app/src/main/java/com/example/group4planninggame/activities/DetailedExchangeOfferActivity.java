package com.example.group4planninggame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.group4planninggame.R;
import com.example.group4planninggame.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailedExchangeOfferActivity extends AppCompatActivity {

    private String status;
    private String itemName;
    private Button chatButton;
    private String offerID;
    private String productName;
    private String otherUserEmail;
    private String currentUserEmail;
    private String posterEmail;
    private String itemCategory;
    private String itemCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_detailed_exchange_offer);

        TextView nameTextView = findViewById(R.id.detailExchangeName);
        TextView categoryTextView = findViewById(R.id.detailCategory);
        TextView conditionTextView = findViewById(R.id.detailCondition);
        TextView statusTextView = findViewById(R.id.detailStatus);

        itemName = getIntent().getStringExtra("ITEM_NAME");
        itemCategory = getIntent().getStringExtra("ITEM_CATEGORY");
        itemCondition = getIntent().getStringExtra("ITEM_CONDITION");
        status = getIntent().getStringExtra("ITEM_STATUS");
        posterEmail = getIntent().getStringExtra("POSTER_EMAIL");
        offerID = getIntent().getStringExtra("OFFER_ID");

        nameTextView.setText(itemName != null ? itemName : "N/A");
        categoryTextView.setText(itemCategory != null ? itemCategory : "N/A");
        conditionTextView.setText(itemCondition != null ? itemCondition : "N/A");
        statusTextView.setText(getString(R.string.status_text, status != null ? status : "N/A"));

        chatButton = findViewById(R.id.chatButton);

        if ("Accepted".equalsIgnoreCase(status)) {
            offerID = getIntent().getStringExtra("OFFER_ID");
            otherUserEmail = getIntent().getStringExtra("OTHER_USER_EMAIL");
            currentUserEmail = getIntent().getStringExtra("CURRENT_USER_EMAIL");

            if (offerID != null && otherUserEmail != null && currentUserEmail != null) {
                chatButton.setVisibility(View.VISIBLE);
                chatButton.setOnClickListener(v -> {
                    Intent intent = new Intent(DetailedExchangeOfferActivity.this, ChatActivity.class);
                    intent.putExtra("OFFER_ID", offerID);
                    intent.putExtra("PRODUCT_NAME", itemName);
                    intent.putExtra("OTHER_USER_EMAIL", otherUserEmail);
                    intent.putExtra("CURRENT_USER_EMAIL", currentUserEmail);
                    startActivity(intent);
                });
            } else {
                chatButton.setVisibility(View.GONE);
                Toast.makeText(this, "Missing chat details.", Toast.LENGTH_SHORT).show();
            }
        } else {
            chatButton.setVisibility(View.GONE);
        }

        setupButtons(status);
    }

    private void setupButtons(String status) {
        Button acceptExchangeButton = findViewById(R.id.acceptBtn);
        Button declineExchangeButton = findViewById(R.id.declineBtn);

        if (Constants.USER_EMAIL.equals(posterEmail)) {
            acceptExchangeButton.setOnClickListener(v -> acceptExchangeButton());
            declineExchangeButton.setOnClickListener(v -> declineExchangeButton());

            if ("Pending".equalsIgnoreCase(status)) {
                acceptExchangeButton.setVisibility(View.VISIBLE);
                declineExchangeButton.setVisibility(View.VISIBLE);
            } else {
                acceptExchangeButton.setVisibility(View.INVISIBLE);
                declineExchangeButton.setVisibility(View.INVISIBLE);
            }
        } else {

            acceptExchangeButton.setVisibility(View.GONE);
            declineExchangeButton.setVisibility(View.GONE);
        }

        Button returnButton = findViewById(R.id.returnToOffersBtn);
        returnButton.setOnClickListener(v -> returnToExchangeOffers());
    }

    private void acceptExchangeButton() {
        status = "Accepted";
        setOfferStatus(status);
    }

    private void declineExchangeButton() {
        status = "Declined";
        setOfferStatus(status);
    }

    private void returnToExchangeOffers() {
        finish();
    }

    private void setupStatusListener() {
        try {
            DatabaseReference offerRef = FirebaseDatabase.getInstance().getReference("EXCHANGE_OFFERS").child(offerID);

            offerRef.child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String currentStatus = snapshot.getValue(String.class);
                        if ("Accepted".equalsIgnoreCase(currentStatus)) {
                            chatButton.setVisibility(View.VISIBLE);
                        } else {
                            chatButton.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(DetailedExchangeOfferActivity.this, "Failed to load status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up status listener: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setOfferStatus(String newStatus) {
        DatabaseReference exchangeOffersRef = FirebaseDatabase.getInstance().getReference("EXCHANGE_OFFERS");

        exchangeOffersRef.orderByChild("itemName").equalTo(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String id = snapshot.getKey();
                        DatabaseReference specificOfferRef = exchangeOffersRef.child(id);
                        specificOfferRef.child("status").setValue(newStatus).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(DetailedExchangeOfferActivity.this, "Exchange " + newStatus, Toast.LENGTH_SHORT).show();
                                returnToExchangeOffers();
                            } else {
                                Toast.makeText(DetailedExchangeOfferActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(DetailedExchangeOfferActivity.this, "Offer not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailedExchangeOfferActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
