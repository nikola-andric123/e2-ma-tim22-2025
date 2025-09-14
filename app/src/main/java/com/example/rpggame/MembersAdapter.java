package com.example.rpggame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpggame.domain.Member;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {

    private List<Member> members;

    public MembersAdapter(List<Member> members) {
        this.members = members;
    }

    public void updateList(List<Member> newMembers) {
        this.members = newMembers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = members.get(position);
        holder.name.setText(member.getUsername());
        holder.role.setText(member.getRole());
        holder.avatar.setImageResource(getAvatarResId(member.getAvatar()));
    }

    @Override
    public int getItemCount() {
        return members.size();
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
    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView name, role;
        ImageView avatar;

        MemberViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.memberUsername);
            role = itemView.findViewById(R.id.memberRole);
            avatar = itemView.findViewById(R.id.memberAvatar);
        }
    }
}

