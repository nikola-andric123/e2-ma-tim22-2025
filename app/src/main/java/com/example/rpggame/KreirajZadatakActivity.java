// Nalazi se u: app/java/com/example/rpgame/KreirajZadatakActivity.java
package com.example.rpggame;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class KreirajZadatakActivity extends AppCompatActivity {

    // (ovde ostaju svi prethodno deklarisani elementi)
    private EditText editTextNazivZadatka, editTextOpisZadatka, editTextInterval;
    private Spinner spinnerKategorija, spinnerTezina, spinnerBitnost, spinnerJedinicaPonavljanja;
    private Button buttonVreme, buttonDatumPocetka, buttonDatumZavrsetka, buttonKreirajZadatak;
    private Switch switchPonavljajuci;
    private LinearLayout layoutPonavljajuciDetalji;


    // NOVO: Calendar objekti za čuvanje odabranih datuma i vremena
    private Calendar odabranoVreme = Calendar.getInstance();
    private Calendar odabranDatumPocetka = Calendar.getInstance();
    private Calendar odabranDatumZavrsetka = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kreiraj_zadatak);

        // Povezujemo elemente (FindViewById) - ovaj deo ostaje isti
        editTextNazivZadatka = findViewById(R.id.editTextNazivZadatka);
        // ... svi ostali findViewById pozivi ...
        editTextOpisZadatka = findViewById(R.id.editTextOpisZadatka);
        spinnerKategorija = findViewById(R.id.spinnerKategorija);
        spinnerTezina = findViewById(R.id.spinnerTezina);
        spinnerBitnost = findViewById(R.id.spinnerBitnost);
        buttonVreme = findViewById(R.id.buttonVreme);
        switchPonavljajuci = findViewById(R.id.switchPonavljajuci);
        layoutPonavljajuciDetalji = findViewById(R.id.layoutPonavljajuciDetalji);
        editTextInterval = findViewById(R.id.editTextInterval);
        spinnerJedinicaPonavljanja = findViewById(R.id.spinnerJedinicaPonavljanja);
        buttonDatumPocetka = findViewById(R.id.buttonDatumPocetka);
        buttonDatumZavrsetka = findViewById(R.id.buttonDatumZavrsetka);
        buttonKreirajZadatak = findViewById(R.id.buttonKreirajZadatak);

        popuniSpinnere();

        switchPonavljajuci.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutPonavljajuciDetalji.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // NOVO: Postavljamo listenere za dugmad za vreme i datum
        buttonVreme.setOnClickListener(v -> prikaziTimePicker());
        buttonDatumPocetka.setOnClickListener(v -> prikaziDatePicker(buttonDatumPocetka, odabranDatumPocetka));
        buttonDatumZavrsetka.setOnClickListener(v -> prikaziDatePicker(buttonDatumZavrsetka, odabranDatumZavrsetka));

        buttonKreirajZadatak.setOnClickListener(v -> kreirajZadatak());

        // NOVO: Postavljamo početni tekst na dugmićima
        updateLabel(buttonDatumPocetka, odabranDatumPocetka);
        updateLabel(buttonDatumZavrsetka, odabranDatumZavrsetka);
    }

    private void popuniSpinnere() {
        // Ovaj kod ostaje isti
        ArrayAdapter<Zadatak.Tezina> tezinaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Zadatak.Tezina.values());
        tezinaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTezina.setAdapter(tezinaAdapter);

        ArrayAdapter<Zadatak.Bitnost> bitnostAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Zadatak.Bitnost.values());
        bitnostAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBitnost.setAdapter(bitnostAdapter);

        // NOVO: Popunjavamo spinner za jedinicu ponavljanja
        ArrayAdapter<Zadatak.TipPonavljanja> jedinicaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Zadatak.TipPonavljanja.values());
        jedinicaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJedinicaPonavljanja.setAdapter(jedinicaAdapter);
    }

    // NOVO: Metoda za prikaz dijaloga za odabir vremena
    private void prikaziTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            odabranoVreme.set(Calendar.HOUR_OF_DAY, hourOfDay);
            odabranoVreme.set(Calendar.MINUTE, minute);
            // Postavi vreme i na druga dva kalendara
            odabranDatumPocetka.set(Calendar.HOUR_OF_DAY, hourOfDay);
            odabranDatumPocetka.set(Calendar.MINUTE, minute);
            odabranDatumZavrsetka.set(Calendar.HOUR_OF_DAY, hourOfDay);
            odabranDatumZavrsetka.set(Calendar.MINUTE, minute);

            updateLabel(buttonVreme, odabranoVreme);
        };

        new TimePickerDialog(this, timeSetListener, odabranoVreme.get(Calendar.HOUR_OF_DAY), odabranoVreme.get(Calendar.MINUTE), true).show();
    }

    // NOVO: Metoda za prikaz dijaloga za odabir datuma
    private void prikaziDatePicker(Button buttonToUpdate, Calendar calendarToSet) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendarToSet.set(Calendar.YEAR, year);
            calendarToSet.set(Calendar.MONTH, month);
            calendarToSet.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(buttonToUpdate, calendarToSet);
        };

        new DatePickerDialog(this, dateSetListener, calendarToSet.get(Calendar.YEAR), calendarToSet.get(Calendar.MONTH), calendarToSet.get(Calendar.DAY_OF_MONTH)).show();
    }

    // NOVO: Pomoćna metoda za ažuriranje teksta na dugmetu
    private void updateLabel(Button button, Calendar calendar) {
        // Proveravamo da li je dugme za vreme ili datum
        if (button.getId() == R.id.buttonVreme) {
            String format = "HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            button.setText("Vreme: " + sdf.format(calendar.getTime()));
        } else {
            String format = "dd.MM.yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            button.setText(sdf.format(calendar.getTime()));
        }
    }


    // AŽURIRANO: Kompletna metoda za kreiranje zadatka
    private void kreirajZadatak() {
        String naziv = editTextNazivZadatka.getText().toString().trim();
        if (naziv.isEmpty()) {
            editTextNazivZadatka.setError("Naziv zadatka je obavezan!");
            return;
        }

        String opis = editTextOpisZadatka.getText().toString().trim();
        Zadatak.Tezina tezina = (Zadatak.Tezina) spinnerTezina.getSelectedItem();
        Zadatak.Bitnost bitnost = (Zadatak.Bitnost) spinnerBitnost.getSelectedItem();
        boolean isPonavljajuci = switchPonavljajuci.isChecked();

        int interval = 0;
        Zadatak.TipPonavljanja tipPonavljanja = null;

        if (isPonavljajuci) {
            try {
                interval = Integer.parseInt(editTextInterval.getText().toString());
            } catch (NumberFormatException e) {
                editTextInterval.setError("Unesite validan broj!");
                return;
            }
            tipPonavljanja = (Zadatak.TipPonavljanja) spinnerJedinicaPonavljanja.getSelectedItem();
        }

        // Kreiramo objekat sa svim podacima
        Zadatak noviZadatak = new Zadatak(
                UUID.randomUUID().toString(),
                naziv,
                opis,
                "privremeni_kategorija_id",
                isPonavljajuci,
                interval,
                tipPonavljanja,
                odabranDatumPocetka.getTimeInMillis(), // Datume čuvamo kao long vrednost
                odabranDatumZavrsetka.getTimeInMillis(),
                tezina,
                bitnost
        );

        // TODO: Sačuvati 'noviZadatak' u bazu podataka (SQLite/Firebase)

        Toast.makeText(this, "Zadatak '" + noviZadatak.getNaziv() + "' je kreiran!", Toast.LENGTH_LONG).show();
        finish();
    }
}