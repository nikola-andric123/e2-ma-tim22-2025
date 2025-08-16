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

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class ListaZadatakaFragment extends Fragment {

    private enum FilterTip {
        SVI, JEDNOKRATNI, PONAVLJAJUCI
    }

    private RecyclerView recyclerView;
    private ZadatakAdapter adapter;
    private ZadatakRepository zadatakRepository;
    private ChipGroup chipGroupFilter;

    private List<Zadatak> sviZadaciIzBaze = new ArrayList<>();
    private List<Kategorija> sveKategorijeIzBaze = new ArrayList<>();
    private FilterTip trenutniFilter = FilterTip.SVI;

    private ActivityResultLauncher<Intent> detaljiLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zadatakRepository = new ZadatakRepository(getActivity().getApplication());

        detaljiLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        ucitajKategorije(); // Osveži sve podatke
                    }
                });
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
        ucitajKategorije();
    }

    private void ucitajKategorije() {
        zadatakRepository.getSveKategorije(kategorije -> {
            sveKategorijeIzBaze = kategorije;
            adapter.setKategorije(sveKategorijeIzBaze);
            osveziListuZadataka();
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ZadatakAdapter(new ArrayList<>(), new ArrayList<>());
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
            filtrirajIPrikaziZadatke();
        });
    }

    private void osveziListuZadataka() {
        zadatakRepository.getSveZadatke(zadaci -> {
            sviZadaciIzBaze = zadaci;
            filtrirajIPrikaziZadatke();
        });
    }

    private void filtrirajIPrikaziZadatke() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long pocetakDanasnjegDana = cal.getTimeInMillis();

        List<Zadatak> aktivniZadaci = sviZadaciIzBaze.stream()
                .filter(z -> (z.getStatus() == Zadatak.Status.AKTIVAN || z.getStatus() == Zadatak.Status.PAUZIRAN)
                        && z.getDatumPocetka() >= pocetakDanasnjegDana)
                .collect(Collectors.toList());

        List<Zadatak> konacnaLista;

        switch (trenutniFilter) {
            case JEDNOKRATNI:
                konacnaLista = aktivniZadaci.stream()
                        .filter(z -> !z.isPonavljajuci())
                        .collect(Collectors.toList());
                break;
            case PONAVLJAJUCI:
                konacnaLista = aktivniZadaci.stream()
                        .filter(Zadatak::isPonavljajuci)
                        .collect(Collectors.toList());
                break;
            case SVI:
            default:
                konacnaLista = aktivniZadaci;
                break;
        }

        adapter.updateZadaci(konacnaLista);
    }
}