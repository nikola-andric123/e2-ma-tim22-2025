// Nalazi se u: app/java/com/example/rpgame/ListaZadatakaFragment.java
package com.example.rpggame;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListaZadatakaFragment extends Fragment {

    private RecyclerView recyclerView;
    private ZadatakAdapter adapter;
    private List<Zadatak> privremenaListaZadataka = new ArrayList<>();
    private List<Kategorija> privremenaListaKategorija = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_zadataka, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewZadaci);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Kreiramo privremene podatke da bismo testirali prikaz
        kreirajPrivremenePodatke();

        adapter = new ZadatakAdapter(privremenaListaZadataka, privremenaListaKategorija);
        // NOVO: Postavi listener na adapter
        adapter.setOnItemClickListener(zadatak -> {
            // Kreiraj Intent (nameru) da se otvori DetaljiZadatkaActivity
            Intent intent = new Intent(getActivity(), DetaljiZadatkaActivity.class);

            // "Spakuj" naš zadatak u intent. "KLJUC_ZADATAK" je ključ pomoću koga ćemo ga kasnije pronaći.
            intent.putExtra("KLJUC_ZADATAK", zadatak);

            // Pokreni novu aktivnost
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void kreirajPrivremenePodatke() {
        // Privremene kategorije
        Kategorija katZdravlje = new Kategorija("1", "Zdravlje", "#FF5733");
        Kategorija katUcenje = new Kategorija("2", "Učenje", "#337BFF");
        privremenaListaKategorija.add(katZdravlje);
        privremenaListaKategorija.add(katUcenje);

        // Privremeni zadaci
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        privremenaListaZadataka.add(new Zadatak("z1", "Jutarnje trčanje", "5km", katZdravlje.getId(), false, 0, null, cal.getTimeInMillis(), 0, Zadatak.Tezina.LAK, Zadatak.Bitnost.NORMALAN));

        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 30);
        privremenaListaZadataka.add(new Zadatak("z2", "Učenje za ispit", "Poglavlje 5", katUcenje.getId(), false, 0, null, cal.getTimeInMillis(), 0, Zadatak.Tezina.TEZAK, Zadatak.Bitnost.VAZAN));
    }
}