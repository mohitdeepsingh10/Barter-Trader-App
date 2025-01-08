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

public class ViewIncomingOffersActivity extends AppCompatActivity implements OffersViewInterface {
    private RecyclerView recyclerView;
    private OffersAdapter offersAdapter;
    private List<ExchangeOffer> offerList;
    private TextView noIncomingOffersTextView;
    private Button leaveIncomingOffers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_incoming_exchange_offers);
        recyclerView = findViewById(R.id.incomingOffersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        offerList = new ArrayList<>();
        offersAdapter = new OffersAdapter(offerList, this, this);
        recyclerView.setAdapter(offersAdapter);
        noIncomingOffersTextView = findViewById(R.id.noIncomingOffersTextView);
        leaveIncomingOffers = findViewById(R.id.leaveIncomingOffers);
        fetchOffers();
        setupLeaveButton();
    }

    protected void fetchOffers() {
        DatabaseReference exchangeOffersRef = FirebaseDatabase.getInstance().getReference("EXCHANGE_OFFERS");

        exchangeOffersRef.orderByChild("posterEmail").equalTo(Constants.USER_EMAIL)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        offerList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ExchangeOffer offer = snapshot.getValue(ExchangeOffer.class);
                            if (offer != null) {
                                offerList.add(offer);
                            }
                        }
                        offersAdapter.notifyDataSetChanged();
                        if (offerList.isEmpty()) {
                            noIncomingOffersTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noIncomingOffersTextView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ViewIncomingOffersActivity.this, "Failed to load offers: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        ExchangeOffer selectedOffer = offerList.get(position);
        Intent intent = new Intent(this, DetailedExchangeOfferActivity.class);
        intent.putExtra("ITEM_NAME", selectedOffer.getItemName());
        intent.putExtra("ITEM_CATEGORY", selectedOffer.getItemCategory());
        intent.putExtra("ITEM_CONDITION", selectedOffer.getItemCondition());
        intent.putExtra("ITEM_STATUS", selectedOffer.getStatus());
        intent.putExtra("OFFER_ID", selectedOffer.getOfferID());
        intent.putExtra("POSTER_EMAIL", selectedOffer.getPosterEmail());
        intent.putExtra("OTHER_USER_EMAIL", selectedOffer.getUserEmail());
        intent.putExtra("CURRENT_USER_EMAIL", Constants.USER_EMAIL);
        startActivity(intent);
    }

    private void setupLeaveButton() {
        leaveIncomingOffers.setOnClickListener(view -> {
            this.finish();
        });
    }
}
