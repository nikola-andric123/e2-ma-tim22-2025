package com.example.rpggame.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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

import com.example.rpggame.R;
import com.example.rpggame.ZadatakRepository;
import com.example.rpggame.domain.Kategorija;
import com.example.rpggame.domain.Zadatak;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class KreirajZadatakActivity extends AppCompatActivity {

    private EditText editTextNazivZadatka, editTextOpisZadatka, editTextInterval;
    private Spinner spinnerKategorija, spinnerTezina, spinnerBitnost, spinnerJedinicaPonavljanja;
    private Button buttonVreme, buttonDatumPocetka, buttonDatumZavrsetka, buttonKreirajZadatka;
    private Switch switchPonavljajuci;
    private LinearLayout layoutPonavljajuciDetalji;

    private Calendar odabranoVreme = Calendar.getInstance();
    private Calendar odabranDatumPocetka = Calendar.getInstance();
    private Calendar odabranDatumZavrsetka = Calendar.getInstance();
    private List<Kategorija> listaKategorija = new ArrayList<>();

    private boolean isEditMode = false;
    private Zadatak zadatakZaIzmenu;
    private ZadatakRepository zadatakRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kreiraj_zadatak);

        zadatakRepository = new ZadatakRepository(getApplication());

        // Povezivanje elemenata
        editTextNazivZadatka = findViewById(R.id.editTextNazivZadatka);
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
        buttonKreirajZadatka = findViewById(R.id.buttonKreirajZadatak);

        // Učitaj prave kategorije iz baze
        ucitajKategorije();

        popuniSpinnere();

        if (getIntent().hasExtra("ZADATAK_ZA_IZMENU")) {
            isEditMode = true;
            zadatakZaIzmenu = getIntent().getParcelableExtra("ZADATAK_ZA_IZMENU", Zadatak.class);
            setTitle("Izmena zadatka");
            buttonKreirajZadatka.setText("Sačuvaj izmene");
        } else {
            setTitle("Kreiranje novog zadatka");
            updateLabel(buttonDatumPocetka, odabranDatumPocetka);
            updateLabel(buttonDatumZavrsetka, odabranDatumZavrsetka);
        }

        switchPonavljajuci.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutPonavljajuciDetalji.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        buttonVreme.setOnClickListener(v -> prikaziTimePicker());
        buttonDatumPocetka.setOnClickListener(v -> prikaziDatePicker(buttonDatumPocetka, odabranDatumPocetka));
        buttonDatumZavrsetka.setOnClickListener(v -> prikaziDatePicker(buttonDatumZavrsetka, odabranDatumZavrsetka));
        buttonKreirajZadatka.setOnClickListener(v -> sacuvajIliKreirajZadatak());
    }

    private void ucitajKategorije() {
        zadatakRepository.getSveKategorije(kategorije -> {
            listaKategorija = kategorije;
            // Popuni spinner za kategorije sa pravim podacima
            ArrayAdapter<Kategorija> kategorijaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaKategorija);
            kategorijaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerKategorija.setAdapter(kategorijaAdapter);

            // Ako smo u "Edit" modu, sada možemo da postavimo selekciju
            if (isEditMode) {
                popuniPoljaZaIzmenu();
            }
        });
    }

    private void sacuvajIliKreirajZadatak() {
        String naziv = editTextNazivZadatka.getText().toString().trim();
        if (naziv.isEmpty()) {
            editTextNazivZadatka.setError("Naziv zadatka je obavezan!");
            return;
        }
        if (spinnerKategorija.getSelectedItem() == null) {
            Toast.makeText(this, "Molimo kreirajte kategoriju prvo.", Toast.LENGTH_SHORT).show();
            return;
        }

        String opis = editTextOpisZadatka.getText().toString().trim();
        Kategorija odabranaKategorija = (Kategorija) spinnerKategorija.getSelectedItem();
        Zadatak.Tezina tezina = (Zadatak.Tezina) spinnerTezina.getSelectedItem();
        Zadatak.Bitnost bitnost = (Zadatak.Bitnost) spinnerBitnost.getSelectedItem();
        boolean isPonavljajuci = switchPonavljajuci.isChecked();
        int interval = 0;
        Zadatak.TipPonavljanja tipPonavljanja = null;

        if (isPonavljajuci) {
            try {
                interval = Integer.parseInt(editTextInterval.getText().toString());
            } catch (NumberFormatException e) {
                editTextInterval.setError("Unesite validan broj za interval!");
                return;
            }
            tipPonavljanja = (Zadatak.TipPonavljanja) spinnerJedinicaPonavljanja.getSelectedItem();
        }

        String id = isEditMode ? zadatakZaIzmenu.getId() : UUID.randomUUID().toString();
        Zadatak zadatak = new Zadatak(id, naziv, opis, odabranaKategorija.getId(), isPonavljajuci, interval, tipPonavljanja, odabranDatumPocetka.getTimeInMillis(), odabranDatumZavrsetka.getTimeInMillis(), tezina, bitnost);
        if (isEditMode) {
            zadatak.setStatus(zadatakZaIzmenu.getStatus());
        }

        zadatakRepository.insert(zadatak);

        if (isEditMode) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("AZURIRAN_ZADATAK", zadatak);
            setResult(Activity.RESULT_OK, resultIntent);
            Toast.makeText(this, "Izmene sačuvane!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Zadatak kreiran!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void popuniPoljaZaIzmenu() {
        if (zadatakZaIzmenu == null) return;

        editTextNazivZadatka.setText(zadatakZaIzmenu.getNaziv());
        editTextOpisZadatka.setText(zadatakZaIzmenu.getOpis());

        for (int i = 0; i < listaKategorija.size(); i++) {
            if (listaKategorija.get(i).getId().equals(zadatakZaIzmenu.getKategorijaId())) {
                spinnerKategorija.setSelection(i);
                break;
            }
        }
        spinnerTezina.setSelection(zadatakZaIzmenu.getTezina().ordinal());
        spinnerBitnost.setSelection(zadatakZaIzmenu.getBitnost().ordinal());

        odabranDatumPocetka.setTimeInMillis(zadatakZaIzmenu.getDatumPocetka());
        updateLabel(buttonVreme, odabranDatumPocetka);
        updateLabel(buttonDatumPocetka, odabranDatumPocetka);

        if (zadatakZaIzmenu.isPonavljajuci()) {
            switchPonavljajuci.setChecked(true);
            layoutPonavljajuciDetalji.setVisibility(View.VISIBLE);
            editTextInterval.setText(String.valueOf(zadatakZaIzmenu.getIntervalPonavljanja()));
            spinnerJedinicaPonavljanja.setSelection(zadatakZaIzmenu.getTipPonavljanja().ordinal());
            odabranDatumZavrsetka.setTimeInMillis(zadatakZaIzmenu.getDatumZavrsetka());
            updateLabel(buttonDatumZavrsetka, odabranDatumZavrsetka);
        }
    }

    private void popuniSpinnere() {
        ArrayAdapter<Zadatak.Tezina> tezinaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Zadatak.Tezina.values());
        tezinaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTezina.setAdapter(tezinaAdapter);
        ArrayAdapter<Zadatak.Bitnost> bitnostAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Zadatak.Bitnost.values());
        bitnostAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBitnost.setAdapter(bitnostAdapter);
        ArrayAdapter<Zadatak.TipPonavljanja> jedinicaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Zadatak.TipPonavljanja.values());
        jedinicaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJedinicaPonavljanja.setAdapter(jedinicaAdapter);
    }

    private void prikaziTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            odabranDatumPocetka.set(Calendar.HOUR_OF_DAY, hourOfDay);
            odabranDatumPocetka.set(Calendar.MINUTE, minute);
            odabranDatumZavrsetka.set(Calendar.HOUR_OF_DAY, hourOfDay);
            odabranDatumZavrsetka.set(Calendar.MINUTE, minute);
            updateLabel(buttonVreme, odabranDatumPocetka);
        };
        new TimePickerDialog(this, timeSetListener, odabranDatumPocetka.get(Calendar.HOUR_OF_DAY), odabranDatumPocetka.get(Calendar.MINUTE), true).show();
    }

    private void prikaziDatePicker(Button buttonToUpdate, Calendar calendarToSet) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendarToSet.set(Calendar.YEAR, year);
            calendarToSet.set(Calendar.MONTH, month);
            calendarToSet.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(buttonToUpdate, calendarToSet);
        };
        new DatePickerDialog(this, dateSetListener, calendarToSet.get(Calendar.YEAR), calendarToSet.get(Calendar.MONTH), calendarToSet.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel(Button button, Calendar calendar) {
        String format;
        String prefix = "";
        if (button.getId() == R.id.buttonVreme) {
            format = "HH:mm";
            prefix = "Vreme: ";
        } else {
            format = "dd.MM.yyyy";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        button.setText(prefix + sdf.format(calendar.getTime()));
    }
}