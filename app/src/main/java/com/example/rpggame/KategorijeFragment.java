package com.example.rpggame;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KategorijeFragment extends Fragment {

    private RecyclerView recyclerView;
    private KategorijaAdapter adapter;
    private ZadatakRepository repository;
    private FloatingActionButton fab;
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
        fab = view.findViewById(R.id.fab_dodaj_kategoriju);

        setupRecyclerView();

        fab.setOnClickListener(v -> {
            prikaziDijalogZaDodavanjeKategorije();
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
            sveKategorije = kategorije; // Čuvamo listu svih kategorija
            adapter.setKategorije(kategorije);
        });
    }

    private void prikaziDijalogZaDodavanjeKategorije() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_kreiraj_kategoriju, null);

        final EditText inputNaziv = dialogView.findViewById(R.id.edit_text_naziv_kategorije);
        final GridLayout gridBoje = dialogView.findViewById(R.id.grid_layout_boje);
        final List<String> boje = getListaBoja();
        final String[] izabranaBoja = {null}; // Koristimo niz da bi bio 'final' a promenljiv
        final List<ImageView> tackiceBoja = new ArrayList<>();

        for (String bojaHex : boje) {
            ImageView tackica = new ImageView(getContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 100;
            params.height = 100;
            params.setMargins(16, 16, 16, 16);
            tackica.setLayoutParams(params);

            GradientDrawable background = new GradientDrawable();
            background.setShape(GradientDrawable.OVAL);
            background.setColor(Color.parseColor(bojaHex));
            background.setStroke(4, Color.TRANSPARENT);
            tackica.setBackground(background);

            tackica.setOnClickListener(v -> {
                // Resetuj border za sve ostale
                for(ImageView ostala : tackiceBoja) {
                    ((GradientDrawable)ostala.getBackground()).setStroke(4, Color.TRANSPARENT);
                }
                // Postavi border za izabranu
                ((GradientDrawable)tackica.getBackground()).setStroke(8, Color.DKGRAY);
                izabranaBoja[0] = bojaHex;
            });

            gridBoje.addView(tackica);
            tackiceBoja.add(tackica);
        }

        builder.setView(dialogView)
                .setTitle("Dodaj novu kategoriju")
                .setPositiveButton("Sačuvaj", (dialog, id) -> {
                    String naziv = inputNaziv.getText().toString().trim();

                    // Validacija
                    if (naziv.isEmpty()) {
                        Toast.makeText(getContext(), "Naziv kategorije ne može biti prazan.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (izabranaBoja[0] == null) {
                        Toast.makeText(getContext(), "Morate izabrati boju.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Provera da li boja već postoji (prema specifikaciji)
                    for (Kategorija postojeca : sveKategorije) {
                        if (postojeca.getBoja().equalsIgnoreCase(izabranaBoja[0])) {
                            Toast.makeText(getContext(), "Ova boja je već iskorišćena!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    // Kreiranje i čuvanje nove kategorije
                    Kategorija novaKategorija = new Kategorija(UUID.randomUUID().toString(), naziv, izabranaBoja[0]);
                    repository.insert(novaKategorija);
                    loadKategorije(); // Osveži listu
                })
                .setNegativeButton("Odustani", (dialog, id) -> dialog.cancel());

        builder.create().show();
    }

    private List<String> getListaBoja() {
        List<String> boje = new ArrayList<>();
        // Heksadecimalni kodovi za boje iz colors.xml
        boje.add(String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(getContext(), R.color.kategorija_crvena))));
        boje.add(String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(getContext(), R.color.kategorija_plava))));
        boje.add(String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(getContext(), R.color.kategorija_zelena))));
        boje.add(String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(getContext(), R.color.kategorija_zuta))));
        boje.add(String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(getContext(), R.color.kategorija_narandzasta))));
        boje.add(String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(getContext(), R.color.kategorija_ljubicasta))));
        return boje;
    }
}