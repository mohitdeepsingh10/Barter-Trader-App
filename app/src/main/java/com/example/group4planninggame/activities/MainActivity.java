package com.example.group4planninggame.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.group4planninggame.R;
import com.example.group4planninggame.models.Chat;
import com.example.group4planninggame.utils.Constants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference chatDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        chatDatabase = FirebaseDatabase.getInstance().getReference("chats");

        listenForUnreadMessages();

        Intent switchToLogin = new Intent(this, LoginActivity.class);
        startActivity(switchToLogin);
        finish();
    }

    private void listenForUnreadMessages() {
        chatDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                processMessages(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                processMessages(snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MainActivity", "Failed to listen for messages: " + error.getMessage());
            }
        });
    }

    private void processMessages(DataSnapshot snapshot) {
        for (DataSnapshot messageSnapshot : snapshot.child("messages").getChildren()) {
            Chat chatMessage = messageSnapshot.getValue(Chat.class);
            if (chatMessage != null && "n".equals(chatMessage.getIsRead())
                    && !"y".equals(messageSnapshot.child("isNotified").getValue())
                    && !chatMessage.getEmail().equals(Constants.USER_EMAIL)) {
                showNotification("New Message", chatMessage.getEmail() + ": " + chatMessage.getChatMessage());
                messageSnapshot.getRef().child("isNotified").setValue("y");
            }
        }
    }

    private void showNotification(String title, String content) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "chat_notifications";
            String channelName = "Chat Notifications";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);

            Notification notification = new Notification.Builder(this, channelId)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true)
                    .build();

            manager.notify(1001, notification);
        } else {
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true)
                    .build();

            manager.notify(1001, notification);
        }
    }
}
