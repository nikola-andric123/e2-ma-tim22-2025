package com.example.rpggame;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ZadatakAdapter extends RecyclerView.Adapter<ZadatakAdapter.ZadatakViewHolder> {

    private List<Zadatak> zadaci;
    private List<Kategorija> kategorije; // Treba nam lista kategorija zbog boja
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Zadatak zadatak);
    }
    // NOVO: Metoda za postavljanje listenera
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public ZadatakAdapter(List<Zadatak> zadaci, List<Kategorija> kategorije) {
        this.zadaci = zadaci;
        this.kategorije = kategorije;
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

        // Formatiramo vreme
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.textViewVreme.setText(sdf.format(zadatak.getDatumPocetka()));

        // Postavljamo boju kategorije
        for(Kategorija k : kategorije){
            if(k.getId().equals(zadatak.getKategorijaId())){
                holder.viewBoja.setBackgroundColor(Color.parseColor(k.getBoja()));
                break;
            }
        }
        holder.bind(zadatak, listener);
    }

    @Override
    public int getItemCount() {
        return zadaci.size();
    }

    // ViewHolder drži reference na view elemente jednog reda
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
            itemView.setOnClickListener(v -> listener.onItemClick(zadatak));
        }
    }
}