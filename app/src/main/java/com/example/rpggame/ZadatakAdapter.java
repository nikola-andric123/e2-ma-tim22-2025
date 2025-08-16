package com.example.rpggame;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ZadatakAdapter extends RecyclerView.Adapter<ZadatakAdapter.ZadatakViewHolder> {

    private List<Zadatak> zadaci;
    private List<Kategorija> kategorije;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Zadatak zadatak);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ZadatakAdapter(List<Zadatak> zadaci, List<Kategorija> kategorije) {
        this.zadaci = new ArrayList<>(zadaci);
        this.kategorije = new ArrayList<>(kategorije);
    }

    @NonNull
    @Override
    public ZadatakViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_zadatak, parent, false);
        return new ZadatakViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZadatakViewHolder holder, int position) {
        Zadatak zadatak = zadaci.get(position);
        holder.textViewNaziv.setText(zadatak.getNaziv());

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.textViewVreme.setText(sdf.format(zadatak.getDatumPocetka()));

        // Pronalazimo odgovarajuću boju za kategoriju
        String bojaHex = "#808080"; // Siva boja kao default
        for(Kategorija k : kategorije){
            if(k.getId().equals(zadatak.getKategorijaId())){
                bojaHex = k.getBoja();
                break;
            }
        }
        try {
            holder.viewBoja.setBackgroundColor(Color.parseColor(bojaHex));
        } catch (IllegalArgumentException e) {
            holder.viewBoja.setBackgroundColor(Color.GRAY);
        }

        // Povezujemo klik na ceo red
        holder.bind(zadatak, listener);
    }

    @Override
    public int getItemCount() {
        return zadaci.size();
    }

    // Metoda za ažuriranje liste zadataka
    public void updateZadaci(List<Zadatak> noviZadaci) {
        this.zadaci.clear();
        this.zadaci.addAll(noviZadaci);
        notifyDataSetChanged();
    }

    // NOVA METODA KOJA JE NEDOSTAJALA
    public void setKategorije(List<Kategorija> noveKategorije) {
        this.kategorije.clear();
        this.kategorije.addAll(noveKategorije);
        // Nije potreban notifyDataSetChanged jer se prikaz neće promeniti dok se ne pozove updateZadaci
    }

    public static class ZadatakViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNaziv;
        TextView textViewVreme;
        View viewBoja;
        CheckBox checkBoxUradjeno;

        public ZadatakViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNaziv = itemView.findViewById(R.id.textViewNazivZadatka);
            textViewVreme = itemView.findViewById(R.id.textViewVremeZadatka);
            viewBoja = itemView.findViewById(R.id.viewBojaKategorije);
            checkBoxUradjeno = itemView.findViewById(R.id.checkboxUradjeno);
        }

        public void bind(final Zadatak zadatak, final OnItemClickListener listener) {
            if(listener != null) {
                itemView.setOnClickListener(v -> listener.onItemClick(zadatak));
            }
        }
    }
}