package com.example.group4planninggame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group4planninggame.R;
import com.example.group4planninggame.interfaces.ProductViewInterface;
import com.example.group4planninggame.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final ProductViewInterface productViewInterface;
    private List<Product> productList;
    private Context context;

    public ProductAdapter(Context context, List<Product> productList, ProductViewInterface productViewInterface) {
        this.productList = productList;
        this.context = context;
        this.productViewInterface = productViewInterface;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_view, parent, false);
        return new ProductViewHolder(view, productViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getTitle());
        holder.productCategory.setText(product.getCategory());
        holder.productCondition.setText(product.getCondition());
        holder.productPostedDate.setText(product.getDatePosted());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productCategory, productCondition, productPostedDate;

        public ProductViewHolder(@NonNull View itemView, ProductViewInterface productViewInterface) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productCategory = itemView.findViewById(R.id.productCategory);
            productCondition = itemView.findViewById(R.id.productCondition);
            productPostedDate = itemView.findViewById(R.id.productDatePosted2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(productViewInterface != null) {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION) {
                            productViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
