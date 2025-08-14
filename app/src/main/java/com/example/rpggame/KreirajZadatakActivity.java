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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class KreirajZadatakActivity extends AppCompatActivity {

    // Deklaracija svih elemenata sa ekrana
    private EditText editTextNazivZadatka, editTextOpisZadatka, editTextInterval;
    private Spinner spinnerKategorija, spinnerTezina, spinnerBitnost, spinnerJedinicaPonavljanja;
    private Button buttonVreme, buttonDatumPocetka, buttonDatumZavrsetka, buttonKreirajZadatak;
    private Switch switchPonavljajuci;
    private LinearLayout layoutPonavljajuciDetalji;

    // Calendar objekti za čuvanje odabranih datuma i vremena
    private Calendar odabranoVreme = Calendar.getInstance();
    private Calendar odabranDatumPocetka = Calendar.getInstance();
    private Calendar odabranDatumZavrsetka = Calendar.getInstance();

    // Lista za privremene kategorije
    private List<Kategorija> listaKategorija = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kreiraj_zadatak);

        // Povezivanje elemenata sa njihovim ID-jevima iz XML-a
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
        buttonKreirajZadatak = findViewById(R.id.buttonKreirajZadatak);

        // Kreiramo privremene podatke za kategorije
        kreirajPrivremeneKategorije();

        // Popunjavamo sve padajuće liste (Spinners)
        popuniSpinnere();

        // Listener za switch koji prikazuje/sakriva detalje za ponavljajuće zadatke
        switchPonavljajuci.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutPonavljajuciDetalji.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Postavljamo listenere za dugmad za vreme i datum
        buttonVreme.setOnClickListener(v -> prikaziTimePicker());
        buttonDatumPocetka.setOnClickListener(v -> prikaziDatePicker(buttonDatumPocetka, odabranDatumPocetka));
        buttonDatumZavrsetka.setOnClickListener(v -> prikaziDatePicker(buttonDatumZavrsetka, odabranDatumZavrsetka));

        // Listener za glavno dugme za kreiranje zadatka
        buttonKreirajZadatak.setOnClickListener(v -> kreirajZadatak());

        // Postavljamo početni tekst na dugmićima za datum
        updateLabel(buttonDatumPocetka, odabranDatumPocetka);
        updateLabel(buttonDatumZavrsetka, odabranDatumZavrsetka);
    }

    // Metoda koja simulira učitavanje kategorija iz baze
    private void kreirajPrivremeneKategorije() {
        // Ovo su probni podaci. Kasnije će ovo dolaziti iz baze koju puni Student 1.
        listaKategorija.add(new Kategorija("1", "Zdravlje", "#FF5733"));
        listaKategorija.add(new Kategorija("2", "Učenje", "#337BFF"));
        listaKategorija.add(new Kategorija("3", "Zabava", "#33FF57"));
        listaKategorija.add(new Kategorija("4", "Sređivanje", "#F0A0F0"));
    }

    private void popuniSpinnere() {
        // Popunjavanje spinnera za Težinu
        ArrayAdapter<Zadatak.Tezina> tezinaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Zadatak.Tezina.values());
        tezinaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTezina.setAdapter(tezinaAdapter);

        // Popunjavanje spinnera za Bitnost
        ArrayAdapter<Zadatak.Bitnost> bitnostAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Zadatak.Bitnost.values());
        bitnostAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBitnost.setAdapter(bitnostAdapter);

        // Popunjavanje spinnera za jedinicu ponavljanja
        ArrayAdapter<Zadatak.TipPonavljanja> jedinicaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Zadatak.TipPonavljanja.values());
        jedinicaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJedinicaPonavljanja.setAdapter(jedinicaAdapter);

        // Popunjavanje spinnera za kategorije koristeći privremenu listu
        ArrayAdapter<Kategorija> kategorijaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaKategorija);
        kategorijaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKategorija.setAdapter(kategorijaAdapter);
    }

    // Metoda za prikaz dijaloga za odabir vremena
    private void prikaziTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            odabranoVreme.set(Calendar.HOUR_OF_DAY, hourOfDay);
            odabranoVreme.set(Calendar.MINUTE, minute);
            // Postavi isto vreme i na kalendare za datum početka i završetka
            odabranDatumPocetka.set(Calendar.HOUR_OF_DAY, hourOfDay);
            odabranDatumPocetka.set(Calendar.MINUTE, minute);
            odabranDatumZavrsetka.set(Calendar.HOUR_OF_DAY, hourOfDay);
            odabranDatumZavrsetka.set(Calendar.MINUTE, minute);

            updateLabel(buttonVreme, odabranoVreme);
        };

        new TimePickerDialog(this, timeSetListener, odabranoVreme.get(Calendar.HOUR_OF_DAY), odabranoVreme.get(Calendar.MINUTE), true).show();
    }

    // Metoda za prikaz dijaloga za odabir datuma
    private void prikaziDatePicker(Button buttonToUpdate, Calendar calendarToSet) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendarToSet.set(Calendar.YEAR, year);
            calendarToSet.set(Calendar.MONTH, month);
            calendarToSet.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(buttonToUpdate, calendarToSet);
        };

        new DatePickerDialog(this, dateSetListener, calendarToSet.get(Calendar.YEAR), calendarToSet.get(Calendar.MONTH), calendarToSet.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Pomoćna metoda za ažuriranje teksta na dugmetu (za vreme ili datum)
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

    // Glavna metoda koja se poziva na klik dugmeta "Kreiraj zadatak"
    private void kreirajZadatak() {
        // Čitanje vrednosti sa forme
        String naziv = editTextNazivZadatka.getText().toString().trim();
        if (naziv.isEmpty()) {
            editTextNazivZadatka.setError("Naziv zadatka je obavezan!");
            return;
        }

        String opis = editTextOpisZadatka.getText().toString().trim();
        Zadatak.Tezina tezina = (Zadatak.Tezina) spinnerTezina.getSelectedItem();
        Zadatak.Bitnost bitnost = (Zadatak.Bitnost) spinnerBitnost.getSelectedItem();
        boolean isPonavljajuci = switchPonavljajuci.isChecked();

        Kategorija odabranaKategorija = (Kategorija) spinnerKategorija.getSelectedItem();
        if (odabranaKategorija == null) {
            Toast.makeText(this, "Greška: Nije odabrana kategorija.", Toast.LENGTH_SHORT).show();
            return;
        }
        String kategorijaId = odabranaKategorija.getId();

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

        // Kreiranje finalnog objekta 'Zadatak'
        Zadatak noviZadatak = new Zadatak(
                UUID.randomUUID().toString(),
                naziv,
                opis,
                kategorijaId,
                isPonavljajuci,
                interval,
                tipPonavljanja,
                odabranDatumPocetka.getTimeInMillis(),
                odabranDatumZavrsetka.getTimeInMillis(),
                tezina,
                bitnost
        );

        // TODO: Sačuvati 'noviZadatak' u bazu podataka (SQLite/Firebase)

        // Prikaz poruke korisniku i zatvaranje ekrana
        Toast.makeText(this, "Zadatak '" + noviZadatak.getNaziv() + "' je kreiran!", Toast.LENGTH_LONG).show();
        finish();
    }
}