package com.example.rpggame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetaljiZadatkaActivity extends AppCompatActivity {

    private TextView txtNaziv, txtOpis, txtTezina, txtBitnost, txtVreme;
    private Button btnUradjen, btnOtkazan, btnIzmeni, btnPauzirajAktiviraj, btnObrisi;
    private Zadatak trenutniZadatak;
    private ZadatakRepository zadatakRepository;
    private ActivityResultLauncher<Intent> izmenaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalji_zadatka);

        zadatakRepository = new ZadatakRepository(getApplication());

        izmenaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        trenutniZadatak = result.getData().getParcelableExtra("AZURIRAN_ZADATAK", Zadatak.class);
                        if(trenutniZadatak != null) {
                            popuniPodatke();
                        }
                    }
                });

        // Povezivanje elemenata
        txtNaziv = findViewById(R.id.detalji_naziv);
        txtOpis = findViewById(R.id.detalji_opis);
        txtTezina = findViewById(R.id.detalji_tezina);
        txtBitnost = findViewById(R.id.detalji_bitnost);
        txtVreme = findViewById(R.id.detalji_vreme);
        btnUradjen = findViewById(R.id.btn_uradjen);
        btnOtkazan = findViewById(R.id.btn_otkazan);
        btnIzmeni = findViewById(R.id.btn_izmeni);
        btnPauzirajAktiviraj = findViewById(R.id.btn_pauziraj_aktiviraj);
        btnObrisi = findViewById(R.id.btn_obrisi);

        trenutniZadatak = getIntent().getParcelableExtra("KLJUC_ZADATAK", Zadatak.class);

        if (trenutniZadatak != null) {
            popuniPodatke();
            postaviListenere();
        } else {
            Toast.makeText(this, "Greška pri učitavanju zadatka!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void popuniPodatke() {
        if (trenutniZadatak == null) return;

        setTitle("Detalji: " + trenutniZadatak.getNaziv());
        txtNaziv.setText(trenutniZadatak.getNaziv());
        txtOpis.setText(trenutniZadatak.getOpis());
        txtTezina.setText(trenutniZadatak.getTezina().toString());
        txtBitnost.setText(trenutniZadatak.getBitnost().toString());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'u' HH:mm", Locale.getDefault());
        String formatiranoVreme = sdf.format(new Date(trenutniZadatak.getDatumPocetka()));
        txtVreme.setText(formatiranoVreme);

        podesiDugmePauza();
    }

    private void postaviListenere() {
        btnUradjen.setOnClickListener(v -> {
            trenutniZadatak.setStatus(Zadatak.Status.URADJEN);
            zadatakRepository.insert(trenutniZadatak);
            Toast.makeText(this, "Zadatak označen kao URAĐEN!", Toast.LENGTH_SHORT).show();
            vratiRezultatNazad();
        });

        btnOtkazan.setOnClickListener(v -> {
            trenutniZadatak.setStatus(Zadatak.Status.OTKAZAN);
            zadatakRepository.insert(trenutniZadatak);
            Toast.makeText(this, "Zadatak označen kao OTKAZAN!", Toast.LENGTH_SHORT).show();
            vratiRezultatNazad();
        });

        btnIzmeni.setOnClickListener(v -> {
            Intent intent = new Intent(DetaljiZadatkaActivity.this, KreirajZadatakActivity.class);
            intent.putExtra("ZADATAK_ZA_IZMENU", trenutniZadatak);
            izmenaLauncher.launch(intent);
        });

        btnPauzirajAktiviraj.setOnClickListener(v -> {
            if (trenutniZadatak.getStatus() == Zadatak.Status.AKTIVAN) {
                trenutniZadatak.setStatus(Zadatak.Status.PAUZIRAN);
                Toast.makeText(this, "Zadatak je pauziran.", Toast.LENGTH_SHORT).show();
            } else if (trenutniZadatak.getStatus() == Zadatak.Status.PAUZIRAN) {
                trenutniZadatak.setStatus(Zadatak.Status.AKTIVAN);
                Toast.makeText(this, "Zadatak je ponovo aktivan.", Toast.LENGTH_SHORT).show();
            }
            zadatakRepository.insert(trenutniZadatak);
            podesiDugmePauza();
        });

        btnObrisi.setOnClickListener(v -> {
            prikaziDijalogZaPotvrduBrisanja();
        });
    }

    private void prikaziDijalogZaPotvrduBrisanja() {
        new AlertDialog.Builder(this)
                .setTitle("Potvrda brisanja")
                .setMessage("Da li ste sigurni da želite da trajno obrišete ovaj zadatak?")
                .setPositiveButton("Obriši", (dialog, which) -> {
                    // Kod koji se izvršava ako korisnik klikne "Obriši"
                    zadatakRepository.delete(trenutniZadatak);
                    Toast.makeText(this, "Zadatak obrisan.", Toast.LENGTH_SHORT).show();
                    // Postavi rezultat da bi se lista osvežila, ali ne šaljemo nazad obrisani zadatak
                    setResult(Activity.RESULT_OK);
                    finish();
                })
                .setNegativeButton("Odustani", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void podesiDugmePauza() {
        if (trenutniZadatak.isPonavljajuci()) {
            btnPauzirajAktiviraj.setVisibility(View.VISIBLE);
            if (trenutniZadatak.getStatus() == Zadatak.Status.AKTIVAN) {
                btnPauzirajAktiviraj.setText("Pauziraj");
                btnPauzirajAktiviraj.setEnabled(true);
            } else if (trenutniZadatak.getStatus() == Zadatak.Status.PAUZIRAN) {
                btnPauzirajAktiviraj.setText("Aktiviraj");
                btnPauzirajAktiviraj.setEnabled(true);
            } else {
                btnPauzirajAktiviraj.setText("Pauziraj");
                btnPauzirajAktiviraj.setEnabled(false);
            }
        } else {
            btnPauzirajAktiviraj.setVisibility(View.GONE);
        }
    }

    private void vratiRezultatNazad() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("AZURIRAN_ZADATAK", trenutniZadatak);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        vratiRezultatNazad();
        super.onBackPressed();
    }
}