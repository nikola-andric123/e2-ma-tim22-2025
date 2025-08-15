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
import java.util.stream.Collectors;

public class KalendarFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private RecyclerView recyclerView;
    private ZadatakAdapter adapter;
    private ZadatakRepository zadatakRepository;

    private List<Zadatak> sviZadaci = new ArrayList<>();
    private List<Kategorija> privremenaListaKategorija = new ArrayList<>();

    private ActivityResultLauncher<Intent> detaljiLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zadatakRepository = new ZadatakRepository(getActivity().getApplication());

        // Launcher za primanje rezultata sa DetaljiZadatkaActivity
        detaljiLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Kada se vratimo sa detalja, osvežimo i kalendar i listu
                        osveziKalendar();
                    }
                });

        // Privremene kategorije nam trebaju za adapter
        kreirajPrivremeneKategorije();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kalendar, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        recyclerView = view.findViewById(R.id.recyclerViewKalendarZadaci);

        setupRecyclerView();

        // Postavljamo listener na kalendar da znamo kada korisnik izabere novi datum
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            prikaziZadatkeZaDan(date);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        osveziKalendar();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ZadatakAdapter(new ArrayList<>(), privremenaListaKategorija);
        adapter.setOnItemClickListener(zadatak -> {
            // Klik na zadatak u listi ispod kalendara radi istu stvar
            Intent intent = new Intent(getActivity(), DetaljiZadatkaActivity.class);
            intent.putExtra("KLJUC_ZADATAK", zadatak);
            detaljiLauncher.launch(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void osveziKalendar() {
        zadatakRepository.getSveZadatke(zadaci -> {
            sviZadaci = zadaci; // Sačuvamo sve zadatke

            // Logika za dekoraciju (tačkice) ostaje ista
            calendarView.removeDecorators();
            HashSet<CalendarDay> daniSaZadacima = new HashSet<>();
            for (Zadatak zadatak : sviZadaci) {
                LocalDate localDate = Instant.ofEpochMilli(zadatak.getDatumPocetka())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                daniSaZadacima.add(CalendarDay.from(localDate));
            }
            if (!daniSaZadacima.isEmpty()) {
                calendarView.addDecorator(new EventDecorator(Color.RED, daniSaZadacima));
            }

            // Odmah prikaži zadatke za današnji dan (ili trenutno selektovani dan)
            prikaziZadatkeZaDan(calendarView.getSelectedDate());
        });
    }

    private void prikaziZadatkeZaDan(CalendarDay dan) {
        if (dan == null) {
            // Ako nijedan dan nije izabran, izaberi današnji
            dan = CalendarDay.today();
        }

        LocalDate izabraniDatum = dan.getDate();

        // Filtriramo listu svih zadataka da nađemo samo one za izabrani datum
        List<Zadatak> zadaciZaDan = sviZadaci.stream().filter(zadatak -> {
            LocalDate datumZadatka = Instant.ofEpochMilli(zadatak.getDatumPocetka())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            return datumZadatka.equals(izabraniDatum);
        }).collect(Collectors.toList());

        // Ažuriramo adapter da prikaže filtrirane zadatke
        adapter.updateZadaci(zadaciZaDan);
    }

    private void kreirajPrivremeneKategorije() {
        if (!privremenaListaKategorija.isEmpty()) return;
        Kategorija katZdravlje = new Kategorija("1", "Zdravlje", "#FF5733");
        Kategorija katUcenje = new Kategorija("2", "Učenje", "#337BFF");
        privremenaListaKategorija.add(katZdravlje);
        privremenaListaKategorija.add(katUcenje);
    }
}

// Pomoćna klasa za iscrtavanje tačkice na danu
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
        view.addSpan(new DotSpan(5, color));
    }
}