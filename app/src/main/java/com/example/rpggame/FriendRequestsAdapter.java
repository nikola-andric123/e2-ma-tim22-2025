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

import java.util.List;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.RequestViewHolder> {

    public interface OnRequestActionListener {
        void onAccept(Friend friend);
        void onReject(Friend friend);
    }

    private Context context;
    private List<Friend> requestList;
    private OnRequestActionListener listener;

    public FriendRequestsAdapter(Context context, List<Friend> requestList, OnRequestActionListener listener) {
        this.context = context;
        this.requestList = requestList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Friend friend = requestList.get(position);

        holder.usernameText.setText(friend.getUsername());
        holder.levelText.setText("Level " + friend.getLevel());
        holder.avatar.setImageResource(getAvatarId(friend.getProfileImageUrl()));

        holder.acceptBtn.setOnClickListener(v -> {
            listener.onAccept(friend);

            // Change appearance
            holder.acceptBtn.setText("Accepted");
            holder.acceptBtn.setEnabled(false);
            holder.acceptBtn.setBackgroundColor(Color.GRAY); // disabled look

            // Disable reject button too (optional)
            holder.rejectBtn.setEnabled(false);
            holder.rejectBtn.setTextColor(Color.LTGRAY);
        });

        holder.rejectBtn.setOnClickListener(v -> {
            listener.onReject(friend);

            // Change appearance
            holder.rejectBtn.setText("Rejected");
            holder.rejectBtn.setEnabled(false);
            holder.rejectBtn.setBackgroundColor(Color.GRAY);

            // Disable accept button too (optional)
            holder.acceptBtn.setEnabled(false);
            holder.acceptBtn.setTextColor(Color.LTGRAY);
        });
    }
    private int getAvatarId(String avatarName) {
        switch (avatarName) {
            case "avatar_1": return R.drawable.avatar_1;
            case "avatar_2": return R.drawable.avatar_2;
            case "avatar_3": return R.drawable.avatar_3;
            case "avatar_4": return R.drawable.avatar_4;
            default: return -1;
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, levelText;
        ImageView avatar;
        Button acceptBtn, rejectBtn;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameText);
            levelText = itemView.findViewById(R.id.levelText);
            avatar = itemView.findViewById(R.id.avatarImage);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);
            rejectBtn = itemView.findViewById(R.id.rejectBtn);
        }
    }
}