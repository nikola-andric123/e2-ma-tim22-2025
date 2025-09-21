package com.example.rpggame;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rpggame.domain.Kategorija;

import java.util.ArrayList;
import java.util.List;

public class KategorijeFragment extends Fragment {

    private RecyclerView recyclerView;
    private KategorijaAdapter adapter;
    private ZadatakRepository repository;
    private List<Kategorija> sveKategorije = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new ZadatakRepository(getActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kategorije, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewKategorije);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadKategorije();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new KategorijaAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(kategorija -> {
            KategorijaDialog.show(getContext(), repository, kategorija, sveKategorije, this::loadKategorije);
        });
    }

    private void loadKategorije() {
        repository.getSveKategorije(kategorije -> {
            sveKategorije = kategorije;
            adapter.setKategorije(kategorije);
        });
    }

    // AŽURIRANO: Metoda je sada PUBLIC
    public void prikaziDijalogZaDodavanjeKategorije() {
        // Pozivamo dijalog za dodavanje nove kategorije (null znači da nije edit mode)
        KategorijaDialog.show(getContext(), repository, null, sveKategorije, this::loadKategorije);
    }
}