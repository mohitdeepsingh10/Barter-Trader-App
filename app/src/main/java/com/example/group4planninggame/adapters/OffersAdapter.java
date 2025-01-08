package com.example.group4planninggame.adapters;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group4planninggame.interfaces.OffersViewInterface;
import com.example.group4planninggame.R;
import com.example.group4planninggame.models.ExchangeOffer;

import java.util.List;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OfferViewHolder> {
    private final OffersViewInterface offersViewInterface;
    private final Context context;

    private List<ExchangeOffer> offerList;

    public OffersAdapter(List<ExchangeOffer> offerList, Context context) {
        this.offerList = offerList;
        this.offersViewInterface = null;
        this.context = context;

    }

    public OffersAdapter(List<ExchangeOffer> offerList, OffersViewInterface offersViewInterface, Context context) {
        this.offerList = offerList;
        this.offersViewInterface = offersViewInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view, offersViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        int nightModeFlags =
                context.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;

        ExchangeOffer offer = offerList.get(position);
        holder.itemName.setText(offer.getItemName());
        holder.itemCategory.setText(offer.getItemCategory());
        holder.itemCondition.setText(offer.getItemCondition());
        holder.itemStatus.setText(offer.getStatus());


        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                if (offer.getStatus().equals("Declined")) {
                    holder.itemStatus.setTextColor(context.getResources().getColor(R.color.onErrorDark, context.getTheme()));
                } else if (offer.getStatus().equals("Accepted")) {
                    holder.itemStatus.setTextColor(context.getResources().getColor(R.color.onSuccessDark, context.getTheme()));
                } else {
                    holder.itemStatus.setTextColor(context.getResources().getColor(R.color.onPendingDark, context.getTheme()));
                }
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                if (offer.getStatus().equals("Declined")) {
                    holder.itemStatus.setTextColor(context.getResources().getColor(R.color.onErrorLight, context.getTheme()));
                } else if (offer.getStatus().equals("Accepted")) {
                    holder.itemStatus.setTextColor(context.getResources().getColor(R.color.onSuccessLight, context.getTheme()));
                } else {
                    holder.itemStatus.setTextColor(context.getResources().getColor(R.color.onPendingLight, context.getTheme()));
                }
                break;

            default:
                if (offer.getStatus().equals("Declined")) {
                    holder.itemStatus.setTextColor(context.getResources().getColor(R.color.onErrorDark, context.getTheme()));
                } else if (offer.getStatus().equals("Accepted")) {
                    holder.itemStatus.setTextColor(context.getResources().getColor(R.color.onSuccessDark, context.getTheme()));
                } else {
                    holder.itemStatus.setTextColor(context.getResources().getColor(R.color.onPendingDark, context.getTheme()));
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemCategory, itemCondition, itemStatus;

        public OfferViewHolder(@NonNull View itemView, OffersViewInterface offersViewInterface) {
            super(itemView);
            itemName = itemView.findViewById(R.id.offerItemName);
            itemCategory = itemView.findViewById(R.id.offerItemCategory);
            itemCondition = itemView.findViewById(R.id.offerItemCondition);
            itemStatus = itemView.findViewById(R.id.offerItemStatus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (offersViewInterface != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            offersViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
