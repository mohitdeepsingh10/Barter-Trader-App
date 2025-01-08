package com.example.group4planninggame.activities;

import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.group4planninggame.R;
import com.example.group4planninggame.adapters.ChatAdapter;
import com.example.group4planninggame.models.Chat;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton, leaveChatButton;
    private ChatAdapter chatAdapter;
    private DatabaseReference chatDatabase;
    private String currentUserEmail, otherUserEmail, productName, offerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        offerID = getIntent().getStringExtra("OFFER_ID");
        productName = getIntent().getStringExtra("PRODUCT_NAME");
        otherUserEmail = getIntent().getStringExtra("OTHER_USER_EMAIL");
        currentUserEmail = getIntent().getStringExtra("CURRENT_USER_EMAIL");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chat: " + productName);
            getSupportActionBar().setSubtitle("With: " + otherUserEmail);
        }

        TextView otherUser = findViewById(R.id.otherUser);
        otherUser.setText(otherUserEmail);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        leaveChatButton = findViewById(R.id.leaveChatButton);

        chatDatabase = FirebaseDatabase.getInstance().getReference("chats").child(offerID).child("messages");

        setupRecyclerView();
        setupSendButton();
        setupLeaveButton();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);

        FirebaseRecyclerOptions<Chat> options = new FirebaseRecyclerOptions.Builder<Chat>()
                .setQuery(chatDatabase, Chat.class)
                .build();
        chatAdapter = new ChatAdapter(options, currentUserEmail);
        chatRecyclerView.setAdapter(chatAdapter);

        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });
    }

    private void setupSendButton() {
        sendButton.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                long timestamp = System.currentTimeMillis();
                Chat chatMessage = new Chat(currentUserEmail, message, timestamp, "n"); // Set "n" as default
                chatDatabase.push().setValue(chatMessage).addOnSuccessListener(aVoid -> {
                    messageInput.setText("");
                }).addOnFailureListener(e -> {
                    Toast.makeText(ChatActivity.this, "Message sending failed", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatAdapter.startListening();
        markMessagesAsRead();
    }

    private void markMessagesAsRead() {
        chatDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Chat chatMessage = messageSnapshot.getValue(Chat.class);
                    if (chatMessage != null && "n".equals(chatMessage.getIsRead())
                            && !chatMessage.getEmail().equals(currentUserEmail)) {
                        messageSnapshot.getRef().child("isRead").setValue("r");
                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        manager.cancel(1001);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatActivity", "Failed to mark messages as read: " + error.getMessage());
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        chatAdapter.stopListening();
    }

    private void setupLeaveButton() {
        leaveChatButton.setOnClickListener(view -> finish());
    }
}
