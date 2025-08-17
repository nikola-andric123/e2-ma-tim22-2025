package com.example.rpggame;

import android.animation.Animator;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.rpggame.domain.UserProfile;

import java.util.List;
import java.util.Random;

public class BorbaActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView slikaBosa;
    private TextView hpBosaText, ppKorisnikaText, brojacNapadaText, sansaNapadaText;
    private ProgressBar hpBosaBar;
    private Button dugmeNapad;
    private LottieAnimationView animacijaUdarca;

    private ZadatakRepository repository;
    private UserProfile trenutniKorisnik;
    private Boss trenutniBoss;

    private int maxHpBosa, trenutniHpBosa, sansaZaPogodak;
    private int preostaliNapadi = 5;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isFightShakeListenerActive = false;
    private boolean isRewardShakeListenerActive = false;
    private boolean napadUToku = false;
    private AlertDialog rewardsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borba);

        slikaBosa = findViewById(R.id.slika_bosa);
        animacijaUdarca = findViewById(R.id.animacija_udarca);
        hpBosaText = findViewById(R.id.hp_bosa_text);
        ppKorisnikaText = findViewById(R.id.pp_korisnika_text);
        brojacNapadaText = findViewById(R.id.brojac_napada_text);
        sansaNapadaText = findViewById(R.id.sansa_napada_text);
        hpBosaBar = findViewById(R.id.hp_bosa_bar);
        dugmeNapad = findViewById(R.id.dugme_napad);

        dugmeNapad.setEnabled(true);
        dugmeNapad.setText("NAPAD!");

        repository = new ZadatakRepository(getApplication());
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        ucitajPodatkeKorisnika();
        dugmeNapad.setOnClickListener(v -> pokusajNapad());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float gForce = (float) Math.sqrt(x * x + y * y + z * z);

        if (isFightShakeListenerActive && gForce > 12) {
            pokusajNapad();
        }

        if (isRewardShakeListenerActive && gForce > 12) {
            isRewardShakeListenerActive = false;
            if (rewardsDialog != null && rewardsDialog.isShowing()) {
                LottieAnimationView animacijaKovcega = rewardsDialog.findViewById(R.id.animacija_kovcega);
                if (animacijaKovcega != null && !animacijaKovcega.isAnimating()) {
                    animacijaKovcega.playAnimation();
                }
            }
        }
    }

    private void pokusajNapad() {
        if (napadUToku || preostaliNapadi <= 0 || trenutniHpBosa <= 0) {
            return;
        }
        napadUToku = true;
        izvrsiNapad();
        new Handler(Looper.getMainLooper()).postDelayed(() -> napadUToku = false, 1500);
    }

    private void izvrsiNapad() {
        preostaliNapadi--;
        brojacNapadaText.setText("Preostali napadi: " + this.preostaliNapadi + "/5");

        int random = new Random().nextInt(100);
        if (random < this.sansaZaPogodak) {
            animirajUdaracBosa();
            trenutniHpBosa -= trenutniKorisnik.getPowerPoints();
            if (trenutniHpBosa < 0) trenutniHpBosa = 0;
        } else {
            Toast.makeText(this, "Promašaj!", Toast.LENGTH_SHORT).show();
        }

        hpBosaBar.setProgress(this.trenutniHpBosa);
        hpBosaText.setText("Boss HP: " + this.trenutniHpBosa + "/" + this.maxHpBosa);

        if (trenutniHpBosa <= 0) {
            zavrsiBorbu(true);
        } else if (preostaliNapadi <= 0) {
            zavrsiBorbu(false);
        }
    }

    private void ucitajPodatkeKorisnika() {
        repository.getUserProfile(userProfile -> {
            if (userProfile != null) {
                this.trenutniKorisnik = userProfile;
                this.trenutniKorisnik.setPowerPoints(150);
                ucitajZadatkeZaRacunanjeSanse();
            } else {
                Toast.makeText(this, "Greška: Nije moguće učitati profil korisnika.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void ucitajZadatkeZaRacunanjeSanse() {
        repository.getSveZadatke(sviZadaci -> {
            this.sansaZaPogodak = izracunajSansuZaNapad(sviZadaci);
            pripremiBorbu();
        });
    }

    private void pripremiBorbu() {
        ppKorisnikaText.setText("Tvoja snaga (PP): " + trenutniKorisnik.getPowerPoints());
        sansaNapadaText.setText("Šansa za pogodak: " + this.sansaZaPogodak + "%");

        int nivoBosa = trenutniKorisnik.getLevel();
        if (nivoBosa <= 0) nivoBosa = 1;

        this.maxHpBosa = izracunajHpBosa(nivoBosa);
        this.trenutniHpBosa = this.maxHpBosa;

        this.trenutniBoss = new Boss(nivoBosa, maxHpBosa, false);

        hpBosaBar.setMax(this.maxHpBosa);
        hpBosaBar.setProgress(this.trenutniHpBosa);
        hpBosaText.setText("Boss HP: " + this.trenutniHpBosa + "/" + this.maxHpBosa);
        brojacNapadaText.setText("Preostali napadi: " + this.preostaliNapadi + "/5");
        isFightShakeListenerActive = true;
    }

    private void zavrsiBorbu(boolean pobeda) {
        isFightShakeListenerActive = false;
        dugmeNapad.setEnabled(false);
        dugmeNapad.setText("BORBA ZAVRŠENA");

        if (pobeda) {
            trenutniBoss.setDefeated(true);
            hpBosaText.setText("PORAŽEN!");
            prikaziDijalogNagrade(true);
        } else {
            hpBosaText.setText("NEMA VIŠE NAPADA");
            if ((float) trenutniHpBosa / maxHpBosa <= 0.5) {
                prikaziDijalogNagrade(false);
            } else {
                Toast.makeText(this, "Nažalost, nisi uspeo.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        repository.insert(trenutniBoss);
    }

    private void prikaziDijalogNagrade(boolean punaNagrada) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_nagrade, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        final LottieAnimationView animacijaKovcega = dialogView.findViewById(R.id.animacija_kovcega);
        final TextView tekstProtresi = dialogView.findViewById(R.id.tekst_protresi);
        final LinearLayout layoutSaNagradama = dialogView.findViewById(R.id.layout_sa_nagradama);
        final TextView nagradaNovcici = dialogView.findViewById(R.id.nagrada_novcici);
        final TextView nagradaOprema = dialogView.findViewById(R.id.nagrada_oprema);

        int dobijeniNovcici = izracunajNovcice(trenutniKorisnik.getLevel());
        String dobijenaOprema = punaNagrada ? izracunajOpremu() : null;

        if (!punaNagrada) {
            dobijeniNovcici /= 2;
        }

        nagradaNovcici.setText("+ " + dobijeniNovcici + " novčića");
        if (dobijenaOprema != null) {
            nagradaOprema.setText("+ Oprema: " + dobijenaOprema);
            nagradaOprema.setVisibility(View.VISIBLE);
        }

        trenutniKorisnik.setCollectedCoins(trenutniKorisnik.getCollectedCoins() + dobijeniNovcici);
        repository.updateUserProfile(trenutniKorisnik);

        builder.setPositiveButton("Zatvori", (dialog, which) -> finish());
        rewardsDialog = builder.create();
        rewardsDialog.show();

        isRewardShakeListenerActive = true;

        animacijaKovcega.addAnimatorListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(@NonNull Animator animation) {
                tekstProtresi.setVisibility(View.GONE);
                layoutSaNagradama.setVisibility(View.VISIBLE);
            }
            @Override public void onAnimationEnd(@NonNull Animator animation) {}
            @Override public void onAnimationCancel(@NonNull Animator animation) {}
            @Override public void onAnimationRepeat(@NonNull Animator animation) {}
        });
    }

    private void animirajUdaracBosa() {
        animacijaUdarca.setVisibility(View.VISIBLE);
        animacijaUdarca.playAnimation();
        animacijaUdarca.addAnimatorListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(@NonNull Animator animation) {}
            @Override public void onAnimationEnd(@NonNull Animator animation) { animacijaUdarca.setVisibility(View.GONE); }
            @Override public void onAnimationCancel(@NonNull Animator animation) {}
            @Override public void onAnimationRepeat(@NonNull Animator animation) {}
        });
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private int izracunajHpBosa(int nivoKorisnika) {
        if (nivoKorisnika <= 0) nivoKorisnika = 1;
        if (nivoKorisnika == 1) return 200;
        int hpPrethodnogBosa = 200;
        for (int i = 2; i <= nivoKorisnika; i++) {
            hpPrethodnogBosa = hpPrethodnogBosa * 2 + hpPrethodnogBosa / 2;
        }
        return hpPrethodnogBosa;
    }

    private int izracunajSansuZaNapad(List<Zadatak> sviZadaci) {
        if (sviZadaci == null || sviZadaci.isEmpty()) return 100;
        int uradjeni = 0;
        int ukupnoRelevantnih = 0;
        for (Zadatak z : sviZadaci) {
            if (z.getStatus() == Zadatak.Status.URADJEN || z.getStatus() == Zadatak.Status.NEURADJEN) {
                ukupnoRelevantnih++;
                if (z.getStatus() == Zadatak.Status.URADJEN) { uradjeni++; }
            }
        }
        if (ukupnoRelevantnih == 0) return 100;
        return (int) (((double) uradjeni / ukupnoRelevantnih) * 100);
    }

    private int izracunajNovcice(int nivoKorisnika) {
        if (nivoKorisnika <= 0) nivoKorisnika = 1;
        if (nivoKorisnika == 1) return 200;
        double novcici = 200;
        for (int i = 2; i <= nivoKorisnika; i++) {
            novcici *= 1.20;
        }
        return (int) Math.round(novcici);
    }

    private String izracunajOpremu() {
        if (new Random().nextInt(100) < 20) {
            if (new Random().nextInt(100) < 5) {
                return new Random().nextBoolean() ? "Mač" : "Luk i strela";
            } else {
                return "Rukavice";
            }
        }
        return null;
    }
}