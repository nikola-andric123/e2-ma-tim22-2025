package com.example.rpggame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpggame.activity.DetaljiZadatkaActivity;
import com.example.rpggame.domain.Kategorija;
import com.example.rpggame.domain.Zadatak;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KalendarFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private RecyclerView recyclerView;
    private ZadatakAdapter adapter;
    private ZadatakRepository zadatakRepository;

    private List<Zadatak> sviZadaci = new ArrayList<>();
    private List<Kategorija> sveKategorijeIzBaze = new ArrayList<>();

    private ActivityResultLauncher<Intent> detaljiLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zadatakRepository = new ZadatakRepository(getActivity().getApplication());
        detaljiLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        ucitajKategorije();
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kalendar, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        recyclerView = view.findViewById(R.id.recyclerViewKalendarZadaci);

        setupRecyclerView();

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            prikaziZadatkeZaDan(date);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ucitajKategorije();
    }

    private void ucitajKategorije() {
        zadatakRepository.getSveKategorije(kategorije -> {
            sveKategorijeIzBaze = kategorije;
            adapter.setKategorije(sveKategorijeIzBaze);
            osveziKalendar();
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ZadatakAdapter(new ArrayList<>(), new ArrayList<>());
        adapter.setOnItemClickListener(zadatak -> {
            Intent intent = new Intent(getActivity(), DetaljiZadatkaActivity.class);
            intent.putExtra("KLJUC_ZADATAK", zadatak);
            detaljiLauncher.launch(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void osveziKalendar() {
        zadatakRepository.getSveZadatke(zadaci -> {
            sviZadaci = zadaci;
            calendarView.removeDecorators();

            // --- POČETAK NOVE LOGIKE ZA BOJE ---

            // 1. Kreiramo mapu za lakši pronalazak kategorije po ID-ju
            Map<String, Kategorija> mapaKategorija = sveKategorijeIzBaze.stream()
                    .collect(Collectors.toMap(Kategorija::getId, kategorija -> kategorija));

            // 2. Grupišemo sve zadatke po datumu
            Map<LocalDate, List<Zadatak>> zadaciPoDanima = sviZadaci.stream()
                    .collect(Collectors.groupingBy(zadatak ->
                            Instant.ofEpochMilli(zadatak.getDatumPocetka())
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()));

            // 3. Kreiramo listu dekoratora, po jedan za svaki dan koji ima zadatke
            List<DayViewDecorator> dekoratori = new ArrayList<>();
            for (Map.Entry<LocalDate, List<Zadatak>> entry : zadaciPoDanima.entrySet()) {
                LocalDate dan = entry.getKey();
                List<Zadatak> zadaciNaDan = entry.getValue();

                if (!zadaciNaDan.isEmpty()) {
                    // Uzimamo kategoriju prvog zadatka u danu
                    Zadatak prviZadatak = zadaciNaDan.get(0);
                    Kategorija kategorija = mapaKategorija.get(prviZadatak.getKategorijaId());

                    int boja = Color.GRAY; // Default boja ako nešto krene naopako
                    if (kategorija != null) {
                        try {
                            boja = Color.parseColor(kategorija.getBoja());
                        } catch (Exception e) {
                            // ostaje siva ako je heks kod pogrešan
                        }
                    }

                    // Kreiramo dekorator sa specifičnom bojom samo za taj jedan dan
                    HashSet<CalendarDay> danSet = new HashSet<>();
                    danSet.add(CalendarDay.from(dan));
                    dekoratori.add(new EventDecorator(boja, danSet));
                }
            }

            // 4. Dodajemo sve kreirane dekoratore na kalendar
            calendarView.addDecorators(dekoratori);

            // --- KRAJ NOVE LOGIKE ZA BOJE ---

            prikaziZadatkeZaDan(calendarView.getSelectedDate());
        });
    }

    private void prikaziZadatkeZaDan(CalendarDay dan) {
        if (dan == null) {
            dan = CalendarDay.today();
        }
        LocalDate izabraniDatum = dan.getDate();
        List<Zadatak> zadaciZaDan = sviZadaci.stream().filter(zadatak -> {
            LocalDate datumZadatka = Instant.ofEpochMilli(zadatak.getDatumPocetka())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            return datumZadatka.equals(izabraniDatum);
        }).collect(Collectors.toList());
        adapter.updateZadaci(zadaciZaDan);
    }
}

class EventDecorator implements DayViewDecorator {
    private final int color;
    private final HashSet<CalendarDay> dates;

    public EventDecorator(int color, Collection<CalendarDay> dates) {
        this.color = color;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(8, color)); // Malo sam povećao tačkicu
    }
}