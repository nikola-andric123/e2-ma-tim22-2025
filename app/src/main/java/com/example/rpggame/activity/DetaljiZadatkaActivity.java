package com.example.rpggame.activity;

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

import com.example.rpggame.LevelUpHelper;
import com.example.rpggame.R;
import com.example.rpggame.domain.UserProfile;
import com.example.rpggame.domain.Zadatak;
import com.example.rpggame.ZadatakRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetaljiZadatkaActivity extends AppCompatActivity {

    private TextView txtNaziv, txtOpis, txtTezina, txtBitnost, txtVreme;
    private Button btnUradjen, btnOtkazan, btnIzmeni, btnPauzirajAktiviraj, btnObrisi;
    private Zadatak trenutniZadatak;
    private ZadatakRepository repository;
    private ActivityResultLauncher<Intent> izmenaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalji_zadatka);

        repository = new ZadatakRepository(getApplication());

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

    private void postaviListenere() {
        btnUradjen.setOnClickListener(v -> {
            // --- POČETAK NOVE LOGIKE ---
            // Provera da li je vreme zadatka prošlo
            long sada = System.currentTimeMillis();
            long vremeZadatka = trenutniZadatak.getDatumPocetka();

            if (vremeZadatka > sada) {
                Toast.makeText(this, "Ne možete kompletirati zadatak koji još nije počeo.", Toast.LENGTH_SHORT).show();
                return; // Prekini dalje izvršavanje
            }
            // --- KRAJ NOVE LOGIKE ---

            btnUradjen.setEnabled(false);
            trenutniZadatak.setStatus(Zadatak.Status.URADJEN);
            repository.insert(trenutniZadatak);

            repository.getUserProfile(userProfile -> {
                if (userProfile != null) {
                    nanesiStetuZaZadatak(userProfile, trenutniZadatak);
                    boolean leveledUp = LevelUpHelper.addXpPointsAndCheckForLevelUp(userProfile, trenutniZadatak);
                    repository.updateUserProfile(userProfile);
                    Toast.makeText(this, "Zadatak označen kao URAĐEN!", Toast.LENGTH_SHORT).show();

                    if (leveledUp) {
                        Toast.makeText(this, "NOVI NIVO! Sledi borba sa bosom!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(DetaljiZadatkaActivity.this, BorbaActivity.class);
                        startActivity(intent);
                        vratiRezultatNazad();
                    } else {
                        vratiRezultatNazad();
                    }
                } else {
                    vratiRezultatNazad();
                }
            });
        });

        btnOtkazan.setOnClickListener(v -> {
            trenutniZadatak.setStatus(Zadatak.Status.OTKAZAN);
            repository.insert(trenutniZadatak);
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
            repository.insert(trenutniZadatak);
            popuniPodatke();
        });

        btnObrisi.setOnClickListener(v -> {
            prikaziDijalogZaPotvrduBrisanja();
        });
    }
    private void nanesiStetuZaZadatak(UserProfile user, Zadatak zadatak) {
        if (user.getClanId() == null || user.getClanId().isEmpty()) {
            return;
        }

        ZadatakRepository.AkcijaMisije akcija;
        Zadatak.Tezina tezina = zadatak.getTezina();
        Zadatak.Bitnost bitnost = zadatak.getBitnost();

        if (tezina == Zadatak.Tezina.VEOMA_LAK || tezina == Zadatak.Tezina.LAK || bitnost == Zadatak.Bitnost.NORMALAN || bitnost == Zadatak.Bitnost.VAZAN) {
            akcija = ZadatakRepository.AkcijaMisije.LAKSI_ZADATAK;
        } else {
            akcija = ZadatakRepository.AkcijaMisije.TEZI_ZADATAK;
        }

        repository.nanesiStetuMisiji(user.getClanId(), akcija, zadatak, (success, message, damage) -> {
            if (success) {
                Toast.makeText(this, "Naneo si " + damage + " HP štete bosu misije!", Toast.LENGTH_SHORT).show();
            }
        });
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
        podesiStanjeDugmadi();
    }

    private void podesiStanjeDugmadi() {
        Zadatak.Status status = trenutniZadatak.getStatus();
        if (status == Zadatak.Status.AKTIVAN || status == Zadatak.Status.PAUZIRAN) {
            btnUradjen.setEnabled(true);
            btnOtkazan.setEnabled(true);
            btnIzmeni.setEnabled(true);
            btnObrisi.setEnabled(true);
        } else {
            btnUradjen.setEnabled(false);
            btnOtkazan.setEnabled(false);
            btnIzmeni.setEnabled(false);
            btnObrisi.setEnabled(false);
        }
        podesiDugmePauza();
    }

    private void prikaziDijalogZaPotvrduBrisanja() {
        new AlertDialog.Builder(this)
                .setTitle("Potvrda brisanja")
                .setMessage("Da li ste sigurni da želite da trajno obrišete ovaj zadatak?")
                .setPositiveButton("Obriši", (dialog, which) -> {
                    repository.delete(trenutniZadatak);
                    Toast.makeText(this, "Zadatak obrisan.", Toast.LENGTH_SHORT).show();
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