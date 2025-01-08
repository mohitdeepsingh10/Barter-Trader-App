package com.example.group4planninggame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.group4planninggame.interfaces.OffersViewInterface;
import com.example.group4planninggame.utils.Constants;
import com.example.group4planninggame.models.ExchangeOffer;
import com.example.group4planninggame.adapters.OffersAdapter;
import com.example.group4planninggame.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ViewSubmittedOffersActivity extends AppCompatActivity implements OffersViewInterface {

    private RecyclerView recyclerView;
    private OffersAdapter offersAdapter;
    private List<ExchangeOffer> offerList;
    private TextView noOffersTextView;
    private ImageButton chatButton;
    private ImageButton submitBackButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_submitted_offers);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        offerList = new ArrayList<>();
        offersAdapter = new OffersAdapter(offerList, this, this);
        recyclerView.setAdapter(offersAdapter);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        noOffersTextView = findViewById(R.id.noOffersTextView);

        fetchOffers();
    }

    private void fetchOffers() {
        DatabaseReference exchangeOffersRef = FirebaseDatabase.getInstance().getReference("EXCHANGE_OFFERS");

        exchangeOffersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                offerList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ExchangeOffer offer = snapshot.getValue(ExchangeOffer.class);
                    if (offer != null && offer.getUserEmail().equals(Constants.USER_EMAIL)) {
                        offerList.add(offer);
                    }
                }
                offersAdapter.notifyDataSetChanged();

                if (offerList.isEmpty()) {
                    noOffersTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    noOffersTextView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
        intent.putExtra("OTHER_USER_EMAIL", selectedOffer.getPosterEmail());
        intent.putExtra("CURRENT_USER_EMAIL", Constants.USER_EMAIL);
        startActivity(intent);
    }

}
