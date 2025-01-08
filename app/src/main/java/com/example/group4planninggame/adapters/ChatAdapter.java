package com.example.group4planninggame.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.group4planninggame.R;
import com.example.group4planninggame.models.Chat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatAdapter extends FirebaseRecyclerAdapter<Chat, ChatAdapter.ChatViewHolder> {

    private String currentUserEmail;

    public ChatAdapter(@NonNull FirebaseRecyclerOptions<Chat> options, String currentUserEmail) {
        super(options);
        this.currentUserEmail = currentUserEmail;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull Chat chat) {
        String formattedTimestamp = formatTimestamp(chat.getTimestamp());

        if (chat.getEmail().equals(currentUserEmail)) {
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightMessage.setText(chat.getChatMessage());
            holder.rightTimestamp.setText(formattedTimestamp);
        } else {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.leftMessage.setText(chat.getChatMessage());
            holder.leftTimestamp.setText(formattedTimestamp);
        }
    }
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftLayout, rightLayout;
        TextView leftMessage, rightMessage;
        TextView leftTimestamp, rightTimestamp;

        public ChatViewHolder(View itemView) {
            super(itemView);
            leftLayout = itemView.findViewById(R.id.leftLayout);
            rightLayout = itemView.findViewById(R.id.rightLayout);
            leftMessage = itemView.findViewById(R.id.leftMessage);
            rightMessage = itemView.findViewById(R.id.rightMessage);
            leftTimestamp = itemView.findViewById(R.id.leftTimestamp);
            rightTimestamp = itemView.findViewById(R.id.rightTimestamp);
        }
    }
}
