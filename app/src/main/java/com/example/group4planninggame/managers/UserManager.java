package com.example.group4planninggame.managers;

import android.util.Log;
import androidx.annotation.NonNull;

import com.example.group4planninggame.models.Preferences;
import com.example.group4planninggame.models.User;
import com.example.group4planninggame.utils.Constants;
import com.google.firebase.database.*;
import java.util.HashMap;

public class UserManager {
    private final DatabaseReference usersRef;
    private final HashMap<String, User> users;

    public UserManager(FirebaseDatabase database) {
        usersRef = database.getReference("USER");
        users = new HashMap<>();
        addUserRefListener();
    }

    private void addUserRefListener() {
        usersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user != null && user.getEmail() != null) {
                    users.put(user.getEmail(), user);
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user != null && user.getEmail() != null) {
                    users.put(user.getEmail(), user);
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null && user.getEmail() != null) {
                    users.remove(user.getEmail());
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("UserManager", error.getMessage());
            }
        });
    }

    public String getExtractedPassword(String email) {
        User user = users.get(email);
        return user != null ? user.getPwd() : null;
    }

    public String getExtractedRole(String email) {
        User user = users.get(email);
        return user != null ? user.getRole() : null;
    }

    public void writeUser(String email, String pwd, String role) {
        DatabaseReference newUserRef = usersRef.push();
        newUserRef.child("email").setValue(email);
        newUserRef.child("pwd").setValue(pwd);
        newUserRef.child("role").setValue(role);
    }

    public void removeUser(String email) {
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    childSnapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("UserManager", error.getMessage());
            }
        });
    }

    public void updateUserRole(String email, String role) {
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    childSnapshot.getRef().child("role").setValue(role);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("UserManager", error.getMessage());
            }
        });
    }

    public void updateUserPreferences(Preferences preferences) {
        usersRef.orderByChild("email").equalTo(Constants.USER_EMAIL)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if (childSnapshot.hasChild("preferences")){
                        childSnapshot.getRef().child("preferences").setValue(preferences);
                    } else {
                        childSnapshot.getRef().child("preferences").setValue(preferences);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("UserManager", error.getMessage());
            }
        });
    }

    public Preferences getUserPreferences() {
        User user = users.get(Constants.USER_EMAIL);
        return user != null ? user.getPreferences() : null;
    }

    public boolean checkUserInDB(String email) {
        return users.containsKey(email);
    }
}
