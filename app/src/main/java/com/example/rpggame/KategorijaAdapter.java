package com.example.rpggame;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class KategorijaAdapter extends RecyclerView.Adapter<KategorijaAdapter.KategorijaViewHolder> {

    private List<Kategorija> kategorije = new ArrayList<>();

    @NonNull
    @Override
    public KategorijaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_kategorija, parent, false);
        return new KategorijaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KategorijaViewHolder holder, int position) {
        Kategorija kategorija = kategorije.get(position);
        holder.nazivKategorije.setText(kategorija.getNaziv());

        // Postavljamo boju na kružić
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.OVAL);
        try {
            background.setColor(Color.parseColor(kategorija.getBoja()));
            holder.bojaKategorije.setBackground(background);
        } catch (Exception e) {
            // U slučaju da je boja pogrešna, stavi default
            background.setColor(Color.LTGRAY);
            holder.bojaKategorije.setBackground(background);
        }
    }

    @Override
    public int getItemCount() {
        return kategorije.size();
    }

    public void setKategorije(List<Kategorija> noveKategorije) {
        this.kategorije.clear();
        this.kategorije.addAll(noveKategorije);
        notifyDataSetChanged();
    }

    static class KategorijaViewHolder extends RecyclerView.ViewHolder {
        View bojaKategorije;
        TextView nazivKategorije;

        public KategorijaViewHolder(@NonNull View itemView) {
            super(itemView);
            bojaKategorije = itemView.findViewById(R.id.viewBojaKategorije);
            nazivKategorije = itemView.findViewById(R.id.textViewNazivKategorije);
        }
    }
}