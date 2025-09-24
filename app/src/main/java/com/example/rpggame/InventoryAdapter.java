package com.example.rpggame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpggame.domain.InventoryItem;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<InventoryItem> items;
    private Context context;

    public InventoryAdapter(Context context, List<InventoryItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = items.get(position);

        holder.name.setText(item.getName());

        // show durability only for clothes
        if ("clothes".equals(item.getCategory()) && item.getDurability() != null) {
            holder.extra.setText("Durability: " + item.getDurability());
            holder.extra.setVisibility(View.VISIBLE);
        } else if("weapon".equals(item.getCategory())){
            if("Sword".equals(item.getName())) {
                holder.extra.setText("Power Increase Percent: " + item.getPowerIncreasePercent() + "%");
                holder.extra.setVisibility(View.VISIBLE);
            } else{
                holder.extra.setText("Coins Increase Percent: " + item.getCoinsIncreasePercent() + "%");
                holder.extra.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.extra.setVisibility(View.GONE);
        }

        // map names to drawable
        int resId;
        switch (item.getName()) {
            case "Sword":
                resId = R.drawable.sword;
                break;
            case "bow_and_arrow":
                resId = R.drawable.bow_and_arrow;
                break;
            case "Gloves":
                resId = R.drawable.gloves;
                break;
            case "Shield":
                resId = R.drawable.shield;
                break;
            case "Boots":
                resId = R.drawable.boots;
                break;
            case "Red Potion":
                resId = R.drawable.red_potion;
                break;
            case "Purple Potion":
                resId = R.drawable.purple_potion;
                break;
            case "Yellow Potion":
                resId = R.drawable.yellow_potion;
                break;
            case "Wine Potion":
                resId = R.drawable.wine_potion;
                break;
            default:
                resId = R.drawable.ic_launcher_foreground;
        }
        holder.icon.setImageResource(resId);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name, extra;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_item_icon);
            name = itemView.findViewById(R.id.tv_item_name);
            extra = itemView.findViewById(R.id.tv_item_extra);
        }
    }
}

