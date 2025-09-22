package com.example.rpggame.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rpggame.NapredakAdapter;
import com.example.rpggame.R;
import com.example.rpggame.ZadatakRepository;
import com.example.rpggame.domain.SpecijalnaMisija;
import com.example.rpggame.domain.UserProfile;

public class SpecijalnaMisijaActivity extends AppCompatActivity {

    private TextView statusMisijeText, hpBosaMisijeLabel;
    private ProgressBar hpBosaMisijeBar;
    private RecyclerView clanoviMisijaRecyclerview;
    private NapredakAdapter napredakAdapter;
    private ZadatakRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specijalna_misija);

        statusMisijeText = findViewById(R.id.status_misije_text);
        hpBosaMisijeLabel = findViewById(R.id.hp_bosa_misije_label);
        hpBosaMisijeBar = findViewById(R.id.hp_bosa_misije_bar);
        clanoviMisijaRecyclerview = findViewById(R.id.clanovi_misija_recyclerview);

        repository = new ZadatakRepository(getApplication());

        setupRecyclerView();
        ucitajStatusMisije();
    }

    private void setupRecyclerView() {
        clanoviMisijaRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        napredakAdapter = new NapredakAdapter();
        clanoviMisijaRecyclerview.setAdapter(napredakAdapter);
    }

    private void ucitajStatusMisije() {
        repository.getUserProfile(userProfile -> {
            if (userProfile != null && userProfile.getClanId() != null && !userProfile.getClanId().isEmpty()) {
                repository.getAktivnaMisijaZaSavez(userProfile.getClanId(), misija -> {
                    if (misija != null) {
                        prikaziPodatkeMisije(misija);
                    } else {
                        statusMisijeText.setText("Trenutno nema aktivnih misija za tvoj savez.");
                        hpBosaMisijeBar.setVisibility(View.GONE);
                        hpBosaMisijeLabel.setVisibility(View.GONE);
                    }
                });
            } else {
                statusMisijeText.setText("Nisi član nijednog saveza.");
                hpBosaMisijeBar.setVisibility(View.GONE);
                hpBosaMisijeLabel.setVisibility(View.GONE);
            }
        });
    }

    private void prikaziPodatkeMisije(SpecijalnaMisija misija) {
        statusMisijeText.setText("Misija je aktivna!");
        hpBosaMisijeBar.setVisibility(View.VISIBLE);
        hpBosaMisijeLabel.setVisibility(View.VISIBLE);
        hpBosaMisijeBar.setMax(misija.getMaksHpBosa());
        hpBosaMisijeBar.setProgress(misija.getHpBosa());
        hpBosaMisijeLabel.setText("HP Bosa: " + misija.getHpBosa() + " / " + misija.getMaksHpBosa());

        repository.getNapredakSvihClanova(misija.getId(), listaNapretka -> {
            napredakAdapter.setListaNapretka(listaNapretka);
        });
    }
}