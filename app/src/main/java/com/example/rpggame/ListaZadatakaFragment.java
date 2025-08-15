package com.example.rpggame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaZadatakaFragment extends Fragment {

    private RecyclerView recyclerView;
    private ZadatakAdapter adapter;
    private ZadatakRepository zadatakRepository;
    private List<Kategorija> privremenaListaKategorija = new ArrayList<>();

    private ActivityResultLauncher<Intent> detaljiLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicijalizacija Repository-ja
        zadatakRepository = new ZadatakRepository(getActivity().getApplication());

        detaljiLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Kada se vratimo sa ekrana za detalje, resultCode je OK,
                    // onResume() će svakako osvežiti listu, ali možemo i ovde pozvati za brži odziv.
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        osveziListuZadataka();
                    }
                });

        // Privremene kategorije nam i dalje trebaju za prikaz boja i imena
        kreirajPrivremeneKategorije();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_zadataka, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewZadaci);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicijalizujemo adapter sa praznom listom
        adapter = new ZadatakAdapter(new ArrayList<>(), privremenaListaKategorija);
        adapter.setOnItemClickListener(zadatak -> {
            Intent intent = new Intent(getActivity(), DetaljiZadatkaActivity.class);
            intent.putExtra("KLJUC_ZADATAK", zadatak);
            detaljiLauncher.launch(intent);
        });

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Osvežavamo listu svaki put kada se fragment prikaže korisniku
        osveziListuZadataka();
    }

    private void osveziListuZadataka() {
        zadatakRepository.getSveZadatke(zadaci -> {
            // Kada su podaci pročitani iz baze, ova metoda se poziva
            // Filtriramo listu da prikažemo samo aktivne i pauzirane zadatke
            List<Zadatak> filtriranaLista = zadaci.stream()
                    .filter(z -> z.getStatus() == Zadatak.Status.AKTIVAN || z.getStatus() == Zadatak.Status.PAUZIRAN)
                    .collect(Collectors.toList());
            adapter.updateZadaci(filtriranaLista);
        });
    }

    private void kreirajPrivremeneKategorije() {
        if (!privremenaListaKategorija.isEmpty()) return;
        Kategorija katZdravlje = new Kategorija("1", "Zdravlje", "#FF5733");
        Kategorija katUcenje = new Kategorija("2", "Učenje", "#337BFF");
        privremenaListaKategorija.add(katZdravlje);
        privremenaListaKategorija.add(katUcenje);
    }
}