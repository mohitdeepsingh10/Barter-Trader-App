package com.example.group4planninggame.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group4planninggame.R;
import com.example.group4planninggame.interfaces.PreferenceViewInterface;

import java.util.List;
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder>{
    private List<String> categoryList;
    private final PreferenceViewInterface preferenceViewInterface;


    public CategoriesAdapter(List<String> categoryList, PreferenceViewInterface preferenceViewInterface){
        this.categoryList = categoryList;
        this.preferenceViewInterface = preferenceViewInterface;

    }
    public CategoriesAdapter(List<String> categoryList){
        this.categoryList = categoryList;
        this.preferenceViewInterface = null;


    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_view, viewGroup, false);
        return new ViewHolder(view, preferenceViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        viewHolder.getTextView().setText(categoryList.get(position));
    }
    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View categoryView, PreferenceViewInterface preferenceViewInterface) {
            super(categoryView);
            textView = (TextView) categoryView.findViewById(R.id.CategoryName);
            categoryView.setOnClickListener(view -> {
                if(preferenceViewInterface != null) {
                    int pos = getBindingAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        preferenceViewInterface.onItemClick(pos);
                    }
                }
            });
        }
        public TextView getTextView() {
            return textView;
        }
    }

}
