package com.example.rpggame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpggame.domain.Friend;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private List<Friend> friendsList;
    private Context context;
    private boolean showAddButton = false;
    private OnAddFriendClickListener listener;

    public interface OnAddFriendClickListener {
        void onAddFriendClick(Friend friend);
    }

    public FriendsAdapter(Context context, List<Friend> friendsList) {
        this.context = context;
        this.friendsList = friendsList;
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
            holder.avatar.setImageResource(R.drawable.back_arrow); // fallback
        }

        if (showAddButton) {
            holder.addFriendBtn.setVisibility(View.VISIBLE);
            holder.addFriendBtn.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddFriendClick(friend);
                }
            });
        } else {
            holder.addFriendBtn.setVisibility(View.GONE);
        }
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

