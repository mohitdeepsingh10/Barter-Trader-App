package com.example.group4planninggame.managers;

import android.util.Log;
import androidx.annotation.NonNull;

import com.example.group4planninggame.models.ExchangeOffer;
import com.google.firebase.database.*;
import java.util.HashMap;

public class ExchangeOfferManager {
    private final DatabaseReference exchangeOffersRef;
    private final HashMap<String, ExchangeOffer> exchangeOffers;

    public ExchangeOfferManager(FirebaseDatabase database) {
        exchangeOffersRef = database.getReference("EXCHANGE_OFFERS");
        exchangeOffers = new HashMap<>();
        addExchangeOfferRefListener();
    }

    private void addExchangeOfferRefListener() {
        exchangeOffersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                ExchangeOffer offer = snapshot.getValue(ExchangeOffer.class);
                if (offer != null && offer.getItemName() != null && offer.getUserEmail() != null) {
                    exchangeOffers.put(offer.getItemName() + "-" + offer.getUserEmail(), offer);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                ExchangeOffer offer = snapshot.getValue(ExchangeOffer.class);
                if (offer != null && offer.getItemName() != null && offer.getUserEmail() != null) {
                    exchangeOffers.put(offer.getItemName() + "-" + offer.getUserEmail(), offer);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                ExchangeOffer offer = snapshot.getValue(ExchangeOffer.class);
                if (offer != null && offer.getItemName() != null && offer.getUserEmail() != null) {
                    exchangeOffers.remove(offer.getItemName() + "-" + offer.getUserEmail());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ExchangeOfferManager", error.getMessage());
            }
        });
    }

    public void saveExchangeOffer(String itemName, String userEmail, String itemCondition, String itemCategory, String posterEmail, String wantedProduct) {
        String key = exchangeOffersRef.push().getKey();
        ExchangeOffer newOffer = new ExchangeOffer(itemName, userEmail, itemCondition, itemCategory, "Pending", posterEmail, key, wantedProduct, null);
        if (key != null) {
            exchangeOffersRef.child(key).setValue(newOffer);
        }
    }

    public void fetchIncomingOffers(String posterEmail, ValueEventListener valueEventListener) {
        exchangeOffersRef.orderByChild("posterEmail").equalTo(posterEmail).addListenerForSingleValueEvent(valueEventListener);
    }

    public void isItemAlreadyListed(String itemName, String userEmail, Crud.ItemAlreadyListedCallback callback) {
        exchangeOffersRef.orderByChild("itemName").equalTo(itemName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isListed = false;
                        for (DataSnapshot data : snapshot.getChildren()) {
                            ExchangeOffer offer = data.getValue(ExchangeOffer.class);
                            if (offer != null && offer.getUserEmail().equals(userEmail)) {
                                isListed = true;
                                break;
                            }
                        }
                        callback.onCheckComplete(isListed);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("ExchangeOfferManager", "Database error: " + error.getMessage());
                        callback.onCheckComplete(false);
                    }
                });
    }

}
