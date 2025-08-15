package com.example.rpggame;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class KategorijeFragment extends Fragment {

    private RecyclerView recyclerView;
    private KategorijaAdapter adapter;
    private ZadatakRepository repository;
    private FloatingActionButton fab;

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
        fab = view.findViewById(R.id.fab_dodaj_kategoriju);

        setupRecyclerView();

        fab.setOnClickListener(v -> {
            // TODO: Otvoriti dijalog za dodavanje nove kategorije
            Toast.makeText(getContext(), "TODO: Otvori dijalog", Toast.LENGTH_SHORT).show();
        });

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
    }

    private void loadKategorije() {
        repository.getSveKategorije(kategorije -> {
            adapter.setKategorije(kategorije);
        });
    }
}