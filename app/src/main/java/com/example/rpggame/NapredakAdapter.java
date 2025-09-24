package com.example.rpggame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rpggame.domain.NapredakKorisnikaUMisiji;
import java.util.ArrayList;
import java.util.List;

public class NapredakAdapter extends RecyclerView.Adapter<NapredakAdapter.NapredakViewHolder> {

    private List<NapredakKorisnikaUMisiji> listaNapretka = new ArrayList<>();

    @NonNull
    @Override
    public NapredakViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_napredak_clana, parent, false);
        return new NapredakViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NapredakViewHolder holder, int position) {
        NapredakKorisnikaUMisiji napredak = listaNapretka.get(position);
        holder.imeClana.setText(napredak.getKorisnickoIme());
        holder.stetaClana.setText("Šteta: " + napredak.getNanetaSteta() + " HP");
    }

    @Override
    public int getItemCount() {
        return listaNapretka.size();
    }

    public void setListaNapretka(List<NapredakKorisnikaUMisiji> novaLista) {
        this.listaNapretka.clear();
        this.listaNapretka.addAll(novaLista);
        notifyDataSetChanged();
    }

    static class NapredakViewHolder extends RecyclerView.ViewHolder {
        TextView imeClana;
        TextView stetaClana;

        public NapredakViewHolder(@NonNull View itemView) {
            super(itemView);
            imeClana = itemView.findViewById(R.id.ime_clana_text);
            stetaClana = itemView.findViewById(R.id.steta_clana_text);
        }
    }
}