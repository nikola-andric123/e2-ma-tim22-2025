package com.example.rpggame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
    private Button btnUradjen, btnOtkazan, btnIzmeni;
    private Zadatak trenutniZadatak;
    private ActivityResultLauncher<Intent> izmenaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalji_zadatka);

        // Launcher za primanje rezultata iz KreirajZadatakActivity
        izmenaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        // Preuzmi ažurirani zadatak i osveži prikaz
                        trenutniZadatak = result.getData().getParcelableExtra("AZURIRAN_ZADATAK", Zadatak.class);
                        popuniPodatke();
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
        if(trenutniZadatak == null) return;

        setTitle("Detalji: " + trenutniZadatak.getNaziv());
        txtNaziv.setText(trenutniZadatak.getNaziv());
        txtOpis.setText(trenutniZadatak.getOpis());
        txtTezina.setText(trenutniZadatak.getTezina().toString());
        txtBitnost.setText(trenutniZadatak.getBitnost().toString());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'u' HH:mm", Locale.getDefault());
        String formatiranoVreme = sdf.format(new Date(trenutniZadatak.getDatumPocetka()));
        txtVreme.setText(formatiranoVreme);
    }

    private void postaviListenere() {
        btnUradjen.setOnClickListener(v -> {
            trenutniZadatak.setStatus(Zadatak.Status.URADJEN);
            Toast.makeText(this, "Zadatak označen kao URAĐEN!", Toast.LENGTH_SHORT).show();
            vratiRezultatNazad();
        });

        btnOtkazan.setOnClickListener(v -> {
            trenutniZadatak.setStatus(Zadatak.Status.OTKAZAN);
            Toast.makeText(this, "Zadatak označen kao OTKAZAN!", Toast.LENGTH_SHORT).show();
            vratiRezultatNazad();
        });

        btnIzmeni.setOnClickListener(v -> {
            Intent intent = new Intent(DetaljiZadatkaActivity.this, KreirajZadatakActivity.class);
            intent.putExtra("ZADATAK_ZA_IZMENU", trenutniZadatak);
            izmenaLauncher.launch(intent);
        });
    }

    private void vratiRezultatNazad() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("AZURIRAN_ZADATAK", trenutniZadatak);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    // VAŽNO: Vrati rezultat i kada korisnik pritisne "back" dugme
    @Override
    public void onBackPressed() {
        vratiRezultatNazad();
        super.onBackPressed();
    }
}