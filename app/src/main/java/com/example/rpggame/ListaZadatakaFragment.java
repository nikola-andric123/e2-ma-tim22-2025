package com.example.rpggame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaZadatakaFragment extends Fragment {

    // Definišemo tipove filtera radi lakšeg snalaženja
    private enum FilterTip {
        SVI, JEDNOKRATNI, PONAVLJAJUCI
    }

    private RecyclerView recyclerView;
    private ZadatakAdapter adapter;
    private ZadatakRepository zadatakRepository;
    private ChipGroup chipGroupFilter;

    private List<Zadatak> sviZadaciIzBaze = new ArrayList<>();
    private List<Kategorija> privremenaListaKategorija = new ArrayList<>();
    private FilterTip trenutniFilter = FilterTip.SVI; // Početni filter

    private ActivityResultLauncher<Intent> detaljiLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        zadatakRepository = new ZadatakRepository(getActivity().getApplication());

        detaljiLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        osveziListuZadataka();
                    }
                });

        kreirajPrivremeneKategorije();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_zadataka, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewZadaci);
        chipGroupFilter = view.findViewById(R.id.chip_group_filter);

        setupRecyclerView();
        setupFilterListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        osveziListuZadataka();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ZadatakAdapter(new ArrayList<>(), privremenaListaKategorija);
        adapter.setOnItemClickListener(zadatak -> {
            Intent intent = new Intent(getActivity(), DetaljiZadatkaActivity.class);
            intent.putExtra("KLJUC_ZADATAK", zadatak);
            detaljiLauncher.launch(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupFilterListener() {
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_svi) {
                trenutniFilter = FilterTip.SVI;
            } else if (checkedId == R.id.chip_jednokratni) {
                trenutniFilter = FilterTip.JEDNOKRATNI;
            } else if (checkedId == R.id.chip_ponavljajuci) {
                trenutniFilter = FilterTip.PONAVLJAJUCI;
            }
            // Nakon promene filtera, ponovo filtriraj i prikaži listu
            filtrirajIPrikaziZadatke();
        });
    }

    private void osveziListuZadataka() {
        zadatakRepository.getSveZadatke(zadaci -> {
            // Kada su podaci pročitani iz baze, sačuvamo ih
            sviZadaciIzBaze = zadaci;
            // I onda primenimo trenutni filter
            filtrirajIPrikaziZadatke();
        });
    }

    private void filtrirajIPrikaziZadatke() {
        List<Zadatak> filtriranaLista = new ArrayList<>();

        // Prvo filtriramo po statusu (samo aktivni i pauzirani)
        List<Zadatak> aktivniZadaci = sviZadaciIzBaze.stream()
                .filter(z -> z.getStatus() == Zadatak.Status.AKTIVAN || z.getStatus() == Zadatak.Status.PAUZIRAN)
                .collect(Collectors.toList());

        // Zatim, na osnovu izabranog čipa, primenjujemo dodatni filter
        switch (trenutniFilter) {
            case JEDNOKRATNI:
                filtriranaLista = aktivniZadaci.stream()
                        .filter(z -> !z.isPonavljajuci())
                        .collect(Collectors.toList());
                break;
            case PONAVLJAJUCI:
                filtriranaLista = aktivniZadaci.stream()
                        .filter(Zadatak::isPonavljajuci)
                        .collect(Collectors.toList());
                break;
            case SVI:
            default:
                filtriranaLista = aktivniZadaci;
                break;
        }

        // Na kraju, ažuriramo adapter
        adapter.updateZadaci(filtriranaLista);
    }

    private void kreirajPrivremeneKategorije() {
        if (!privremenaListaKategorija.isEmpty()) return;
        Kategorija katZdravlje = new Kategorija("1", "Zdravlje", "#FF5733");
        Kategorija katUcenje = new Kategorija("2", "Učenje", "#337BFF");
        privremenaListaKategorija.add(katZdravlje);
        privremenaListaKategorija.add(katUcenje);
    }
}