package com.example.rpggame;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetaljiZadatkaActivity extends AppCompatActivity {

    private TextView txtNaziv, txtOpis, txtTezina, txtBitnost, txtVreme;
    private Zadatak trenutniZadatak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalji_zadatka);

        // Poveži TextView elemente sa layout-om
        txtNaziv = findViewById(R.id.detalji_naziv);
        txtOpis = findViewById(R.id.detalji_opis);
        txtTezina = findViewById(R.id.detalji_tezina);
        txtBitnost = findViewById(R.id.detalji_bitnost);
        txtVreme = findViewById(R.id.detalji_vreme);

        // Preuzmi "paket" (Intent) koji je pokrenuo ovu aktivnost
        trenutniZadatak = getIntent().getParcelableExtra("KLJUC_ZADATAK");

        // Proveri da li je zadatak uspešno primljen
        if (trenutniZadatak != null) {
            popuniPodatke();
        } else {
            // Ako se desila greška, obavesti korisnika i zatvori ekran
            Toast.makeText(this, "Greška pri učitavanju zadatka!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void popuniPodatke() {
        txtNaziv.setText(trenutniZadatak.getNaziv());
        txtOpis.setText(trenutniZadatak.getOpis());
        txtTezina.setText(trenutniZadatak.getTezina().toString());
        txtBitnost.setText(trenutniZadatak.getBitnost().toString());

        // Formatiraj vreme iz long u čitljiv format
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'u' HH:mm", Locale.getDefault());
        String formatiranoVreme = sdf.format(new Date(trenutniZadatak.getDatumPocetka()));
        txtVreme.setText(formatiranoVreme);

        // TODO: Dodati logiku za dugmad (Urađen, Otkazan, Izmeni)
    }
}