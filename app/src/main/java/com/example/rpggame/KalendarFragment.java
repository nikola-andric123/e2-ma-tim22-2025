package com.example.rpggame;

import org.threeten.bp.LocalDate;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class KalendarFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private List<Zadatak> privremenaListaZadataka = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kalendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);

        // Koristimo iste privremene podatke kao u listi
        kreirajPrivremenePodatke();

        HashSet<CalendarDay> daniSaZadacima = new HashSet<>();
        Calendar cal = Calendar.getInstance();
        for (Zadatak zadatak : privremenaListaZadataka) {
            cal.setTimeInMillis(zadatak.getDatumPocetka());


            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            LocalDate localDate = LocalDate.of(year, month, day);


            daniSaZadacima.add(CalendarDay.from(localDate));

        }

        // Dodajemo dekoraciju (tačkicu) na te dane
        calendarView.addDecorator(new EventDecorator(Color.RED, daniSaZadacima));

        return view;
    }

    private void kreirajPrivremenePodatke() {
        // Privremene kategorije (samo da bismo mogli kreirati Zadatak objekte)
        Kategorija katZdravlje = new Kategorija("1", "Zdravlje", "#FF5733");
        Kategorija katUcenje = new Kategorija("2", "Učenje", "#337BFF");

        // Privremeni zadaci
        Calendar cal = Calendar.getInstance(); // Današnji datum
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        privremenaListaZadataka.add(new Zadatak("z1", "Jutarnje trčanje", "5km", katZdravlje.getId(), false, 0, null, cal.getTimeInMillis(), 0, Zadatak.Tezina.LAK, Zadatak.Bitnost.NORMALAN));

        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 30);
        privremenaListaZadataka.add(new Zadatak("z2", "Učenje za ispit", "Poglavlje 5", katUcenje.getId(), false, 0, null, cal.getTimeInMillis(), 0, Zadatak.Tezina.TEZAK, Zadatak.Bitnost.VAZAN));

        // Dodajmo zadatak za sutra
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 14);
        privremenaListaZadataka.add(new Zadatak("z3", "Projekat MA", "Uraditi kalendar", katUcenje.getId(), false, 0, null, cal.getTimeInMillis(), 0, Zadatak.Tezina.TEZAK, Zadatak.Bitnost.VAZAN));
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