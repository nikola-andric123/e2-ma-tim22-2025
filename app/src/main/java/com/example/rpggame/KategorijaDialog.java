package com.example.rpggame;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KategorijaDialog {

    public interface OnCategorySavedListener {
        void onCategorySaved();
    }

    public static void show(Context context, ZadatakRepository repository, Kategorija kategorijaZaIzmenu, List<Kategorija> sveKategorije, OnCategorySavedListener listener) {
        boolean isEditMode = kategorijaZaIzmenu != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_kreiraj_kategoriju, null);

        final EditText inputNaziv = dialogView.findViewById(R.id.edit_text_naziv_kategorije);
        final GridLayout gridBoje = dialogView.findViewById(R.id.grid_layout_boje);
        final List<String> boje = getListaBoja(context);
        final String[] izabranaBoja = {null};
        final List<ImageView> tackiceBoja = new ArrayList<>();

        // Ako je izmena, popuni postojeća polja
        if (isEditMode) {
            builder.setTitle("Izmeni kategoriju");
            inputNaziv.setText(kategorijaZaIzmenu.getNaziv());
            izabranaBoja[0] = kategorijaZaIzmenu.getBoja();
        } else {
            builder.setTitle("Dodaj novu kategoriju");
        }

        for (String bojaHex : boje) {
            ImageView tackica = new ImageView(context);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 100; params.height = 100; params.setMargins(16, 16, 16, 16);
            tackica.setLayoutParams(params);

            GradientDrawable background = new GradientDrawable();
            background.setShape(GradientDrawable.OVAL);
            background.setColor(Color.parseColor(bojaHex));
            background.setStroke(4, Color.TRANSPARENT);
            tackica.setBackground(background);

            if (isEditMode && bojaHex.equalsIgnoreCase(kategorijaZaIzmenu.getBoja())) {
                background.setStroke(8, Color.DKGRAY);
            }

            tackica.setOnClickListener(v -> {
                for(ImageView ostala : tackiceBoja) {
                    ((GradientDrawable)ostala.getBackground()).setStroke(4, Color.TRANSPARENT);
                }
                ((GradientDrawable)tackica.getBackground()).setStroke(8, Color.DKGRAY);
                izabranaBoja[0] = bojaHex;
            });
            gridBoje.addView(tackica);
            tackiceBoja.add(tackica);
        }

        builder.setView(dialogView)
                .setPositiveButton("Sačuvaj", (dialog, id) -> {
                    String naziv = inputNaziv.getText().toString().trim();

                    if (naziv.isEmpty() || izabranaBoja[0] == null) {
                        Toast.makeText(context, "Morate uneti naziv i izabrati boju.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (Kategorija postojeca : sveKategorije) {
                        if (isEditMode && postojeca.getId().equals(kategorijaZaIzmenu.getId())) {
                            continue; // Preskoči proveru sa samim sobom u edit modu
                        }
                        if (postojeca.getBoja().equalsIgnoreCase(izabranaBoja[0])) {
                            Toast.makeText(context, "Ova boja je već iskorišćena!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    Kategorija kategorija;
                    if (isEditMode) {
                        kategorija = kategorijaZaIzmenu;
                        kategorija.setNaziv(naziv);
                        kategorija.setBoja(izabranaBoja[0]);
                    } else {
                        kategorija = new Kategorija(UUID.randomUUID().toString(), naziv, izabranaBoja[0]);
                    }
                    repository.insert(kategorija);
                    listener.onCategorySaved();
                })
                .setNegativeButton("Odustani", (dialog, id) -> dialog.cancel());

        builder.create().show();
    }

    private static List<String> getListaBoja(Context context) {
        List<String> boje = new ArrayList<>();
        int[] colorResIds = {R.color.kategorija_crvena, R.color.kategorija_plava, R.color.kategorija_zelena, R.color.kategorija_zuta, R.color.kategorija_narandzasta, R.color.kategorija_ljubicasta};
        for (int resId : colorResIds) {
            boje.add(String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(context, resId))));
        }
        return boje;
    }
}