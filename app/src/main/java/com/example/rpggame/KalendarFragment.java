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
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

            // --- POČETAK NOVE ISPRAVLJENE LOGIKE ---

            // Mapa koja čuva dane koje treba obojiti, grupisane po boji
            Map<Integer, HashSet<CalendarDay>> daniPoBoji = new HashMap<>();
            // Mapa za lakši pronalazak kategorije po ID-ju
            Map<String, Kategorija> mapaKategorija = sveKategorijeIzBaze.stream()
                    .collect(Collectors.toMap(Kategorija::getId, kategorija -> kategorija));

            // Prolazimo kroz sve zadatke iz baze
            for (Zadatak zadatak : sviZadaci) {
                // Pronađi boju za ovaj zadatak
                Kategorija kategorija = mapaKategorija.get(zadatak.getKategorijaId());
                int boja = Color.GRAY; // Default boja
                if (kategorija != null) {
                    try {
                        boja = Color.parseColor(kategorija.getBoja());
                    } catch (Exception e) { /* ostaje siva */ }
                }

                // Ako zadatak nije ponavljajući, dodaj samo jedan dan
                if (!zadatak.isPonavljajuci()) {
                    LocalDate dan = Instant.ofEpochMilli(zadatak.getDatumPocetka()).atZone(ZoneId.systemDefault()).toLocalDate();
                    daniPoBoji.computeIfAbsent(boja, k -> new HashSet<>()).add(CalendarDay.from(dan));
                } else {
                    // Ako je ponavljajući, izračunaj sve datume
                    Calendar iterator = Calendar.getInstance();
                    iterator.setTimeInMillis(zadatak.getDatumPocetka());

                    long krajnjiDatum = zadatak.getDatumZavrsetka();

                    while (iterator.getTimeInMillis() <= krajnjiDatum) {
                        LocalDate dan = Instant.ofEpochMilli(iterator.getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
                        daniPoBoji.computeIfAbsent(boja, k -> new HashSet<>()).add(CalendarDay.from(dan));

                        // Pomeri iterator za sledeći datum
                        if (zadatak.getTipPonavljanja() == Zadatak.TipPonavljanja.DAN) {
                            iterator.add(Calendar.DAY_OF_YEAR, zadatak.getIntervalPonavljanja());
                        } else if (zadatak.getTipPonavljanja() == Zadatak.TipPonavljanja.NEDELJA) {
                            iterator.add(Calendar.WEEK_OF_YEAR, zadatak.getIntervalPonavljanja());
                        } else {
                            break; // Nepoznat tip, prekini petlju
                        }
                    }
                }
            }

            // Kreiraj dekoratore za svaku boju
            List<DayViewDecorator> dekoratori = new ArrayList<>();
            for (Map.Entry<Integer, HashSet<CalendarDay>> entry : daniPoBoji.entrySet()) {
                dekoratori.add(new EventDecorator(entry.getKey(), entry.getValue()));
            }
            calendarView.addDecorators(dekoratori);

            // --- KRAJ NOVE ISPRAVLJENE LOGIKE ---

            prikaziZadatkeZaDan(calendarView.getSelectedDate());
        });
    }

    private void prikaziZadatkeZaDan(CalendarDay dan) {
        if (dan == null) {
            dan = CalendarDay.today();
        }

        LocalDate izabraniDatum = dan.getDate();
        List<Zadatak> zadaciZaDan = new ArrayList<>();

        // Prolazimo kroz sve zadatke i proveravamo da li se neki od njih dešava na izabrani dan
        for (Zadatak zadatak : sviZadaci) {
            if (!zadatak.isPonavljajuci()) {
                LocalDate datumZadatka = Instant.ofEpochMilli(zadatak.getDatumPocetka()).atZone(ZoneId.systemDefault()).toLocalDate();
                if (datumZadatka.equals(izabraniDatum)) {
                    zadaciZaDan.add(zadatak);
                }
            } else {
                Calendar iterator = Calendar.getInstance();
                iterator.setTimeInMillis(zadatak.getDatumPocetka());
                long krajnjiDatum = zadatak.getDatumZavrsetka();

                while (iterator.getTimeInMillis() <= krajnjiDatum) {
                    LocalDate trenutniDatum = Instant.ofEpochMilli(iterator.getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
                    if (trenutniDatum.equals(izabraniDatum)) {
                        zadaciZaDan.add(zadatak);
                        break; // Našli smo da se dešava, ne moramo dalje da proveravamo za ovaj zadatak
                    }
                    if (trenutniDatum.isAfter(izabraniDatum)) {
                        break; // Preskočili smo izabrani datum, nema potrebe dalje proveravati
                    }

                    if (zadatak.getTipPonavljanja() == Zadatak.TipPonavljanja.DAN) {
                        iterator.add(Calendar.DAY_OF_YEAR, zadatak.getIntervalPonavljanja());
                    } else if (zadatak.getTipPonavljanja() == Zadatak.TipPonavljanja.NEDELJA) {
                        iterator.add(Calendar.WEEK_OF_YEAR, zadatak.getIntervalPonavljanja());
                    } else {
                        break;
                    }
                }
            }
        }

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
        view.addSpan(new DotSpan(8, color));
    }
}