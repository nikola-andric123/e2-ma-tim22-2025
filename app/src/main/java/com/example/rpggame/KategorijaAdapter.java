package com.example.rpggame;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpggame.domain.Kategorija;

import java.util.ArrayList;
import java.util.List;

public class KategorijaAdapter extends RecyclerView.Adapter<KategorijaAdapter.KategorijaViewHolder> {

    private List<Kategorija> kategorije = new ArrayList<>();
    private OnItemClickListener listener;

    // Interfejs za obradu klika
    public interface OnItemClickListener {
        void onItemClick(Kategorija kategorija);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public KategorijaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_kategorija, parent, false);
        return new KategorijaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KategorijaViewHolder holder, int position) {
        Kategorija kategorija = kategorije.get(position);
        holder.bind(kategorija, listener); // Povezujemo podatke i listener
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

        // Metoda koja postavlja podatke i listener za klik
        public void bind(final Kategorija kategorija, final OnItemClickListener listener) {
            nazivKategorije.setText(kategorija.getNaziv());

            GradientDrawable background = new GradientDrawable();
            background.setShape(GradientDrawable.OVAL);
            try {
                background.setColor(Color.parseColor(kategorija.getBoja()));
            } catch (Exception e) {
                background.setColor(Color.LTGRAY);
            }
            bojaKategorije.setBackground(background);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(kategorija);
                }
            });
        }
    }
}