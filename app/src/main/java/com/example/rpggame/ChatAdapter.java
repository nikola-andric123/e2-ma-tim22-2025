package com.example.rpggame;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpggame.domain.ChatMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LEFT = 0;
    private static final int VIEW_TYPE_RIGHT = 1;

    private final List<ChatMessage> messages = new ArrayList<>();
    private final String currentUid;

    public ChatAdapter(String currentUid) {
        this.currentUid = currentUid;

    }

    public void setItems(List<ChatMessage> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    public void addItem(ChatMessage m) {
        messages.add(m);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage msg = messages.get(position);
        return currentUid.equals(msg.getSenderId()) ? VIEW_TYPE_RIGHT : VIEW_TYPE_LEFT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_RIGHT) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_right, parent, false);
            return new RightVH(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_left, parent, false);
            return new LeftVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        if (holder instanceof RightVH) {
            RightVH vh = (RightVH) holder;
            vh.avatar.setImageResource(getAvatarId(msg.getAvatar()));
            vh.messageText.setText(msg.getText());
            vh.time.setText(formatTimestamp(msg.getTimestamp()));
            vh.messageText.setTextColor(Color.WHITE);
        } else {
            LeftVH vh = (LeftVH) holder;
            vh.avatar.setImageResource(getAvatarId(msg.getAvatar()));
            vh.senderName.setText(msg.getSenderName());
            vh.messageText.setText(msg.getText());
            vh.time.setText(formatTimestamp(msg.getTimestamp()));
        }
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

    private String formatTimestamp(long ts) {
        if (ts == 0) return "";
        Date date = new Date(ts);
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class LeftVH extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView senderName, messageText, time;
        LeftVH(View v) {
            super(v);
            avatar = v.findViewById(R.id.avatarLeft);
            senderName = v.findViewById(R.id.senderNameLeft);
            messageText = v.findViewById(R.id.messageTextLeft);
            time = v.findViewById(R.id.timeLeft);
        }
    }

    static class RightVH extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView messageText, time;
        RightVH(View v) {
            super(v);
            avatar = v.findViewById(R.id.avatarRight);
            messageText = v.findViewById(R.id.messageTextRight);
            time = v.findViewById(R.id.timeRight);
        }
    }
}
