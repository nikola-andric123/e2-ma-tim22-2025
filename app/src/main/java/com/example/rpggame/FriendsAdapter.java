package com.example.rpggame;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpggame.domain.Friend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private List<Friend> friendsList;
    private Context context;
    private boolean showAddButton = false;
    private OnAddFriendClickListener listener;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserUid;

    public interface OnAddFriendClickListener {
        void onAddFriendClick(Friend friend);
    }

    public FriendsAdapter(Context context, List<Friend> friendsList) {
        this.context = context;
        this.friendsList = friendsList;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserUid = mAuth.getCurrentUser().getUid();
    }

    public void setShowAddButton(boolean show, OnAddFriendClickListener listener) {
        this.showAddButton = show;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendsList.get(position);
        holder.username.setText(friend.getUsername());
        holder.level.setText("Level " + friend.getLevel());

        int avatarId = getAvatarResId(friend.getProfileImageUrl());
        if (avatarId != -1) {
            holder.avatar.setImageResource(avatarId);
        } else {
            holder.avatar.setImageResource(R.drawable.back_arrow);
        }

        if (showAddButton) {
            holder.addFriendBtn.setVisibility(View.VISIBLE);

            // First check if they are already friends
            db.collection("users")
                    .document(currentUserUid)
                    .collection("friends")
                    .document(friend.getUid())
                    .get()
                    .addOnSuccessListener(friendDoc -> {
                        if (friendDoc.exists()) {
                            // Already friends
                            setButtonState(holder, "Friends", false, android.R.color.darker_gray, Color.BLACK);
                        } else {
                            // Not friends → check if request already sent
                            db.collection("users")
                                    .document(currentUserUid)
                                    .collection("sentRequests")
                                    .document(friend.getUid())
                                    .get()
                                    .addOnSuccessListener(requestDoc -> {
                                        if (requestDoc.exists()) {
                                            // Request already sent
                                            setButtonState(holder, "Sent", false, android.R.color.darker_gray, Color.BLACK);
                                        } else {
                                            // Not friends and no request → allow add
                                            setButtonState(holder, "Add", true, R.color.my_primary, Color.WHITE);

                                            holder.addFriendBtn.setOnClickListener(v -> {
                                                if (listener != null) {
                                                    listener.onAddFriendClick(friend);
                                                }
                                                // Optimistic UI update
                                                setButtonState(holder, "Sent", false, android.R.color.darker_gray, Color.BLACK);
                                            });
                                        }
                                    });
                        }
                    });

        } else {
            holder.addFriendBtn.setVisibility(View.GONE);
        }
    }

    private void setButtonState(FriendViewHolder holder, String text, boolean enabled, int bgColorRes, int textColor) {
        holder.addFriendBtn.setText(text);
        holder.addFriendBtn.setEnabled(enabled);
        holder.addFriendBtn.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.getContext(), bgColorRes)
        );
        holder.addFriendBtn.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    private int getAvatarResId(String avatarName) {
        if (avatarName == null) return -1;
        switch (avatarName) {
            case "avatar_1": return R.drawable.avatar_1;
            case "avatar_2": return R.drawable.avatar_2;
            case "avatar_3": return R.drawable.avatar_3;
            case "avatar_4": return R.drawable.avatar_4;
            default: return -1;
        }
    }
    static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView username, level;
        Button addFriendBtn;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.friendAvatar);
            username = itemView.findViewById(R.id.friendUsername);
            level = itemView.findViewById(R.id.friendLevel);
            addFriendBtn = itemView.findViewById(R.id.btnAddFriend);
        }
    }
}

