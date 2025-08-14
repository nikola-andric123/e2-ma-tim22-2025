package com.example.rpggame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class ListaZadatakaFragment extends Fragment {

    private RecyclerView recyclerView;
    private ZadatakAdapter adapter;
    private List<Zadatak> privremenaListaZadataka = new ArrayList<>();
    private List<Kategorija> privremenaListaKategorija = new ArrayList<>();

    private ActivityResultLauncher<Intent> detaljiLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        detaljiLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Zadatak azuriranZadatak = result.getData().getParcelableExtra("AZURIRAN_ZADATAK", Zadatak.class);
                        if (azuriranZadatak != null) {
                            for (int i = 0; i < privremenaListaZadataka.size(); i++) {
                                if (privremenaListaZadataka.get(i).getId().equals(azuriranZadatak.getId())) {
                                    privremenaListaZadataka.set(i, azuriranZadatak);
                                    break;
                                }
                            }
                            osveziListuZadataka();
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_zadataka, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewZadaci);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        kreirajPrivremenePodatke();

        adapter = new ZadatakAdapter(new ArrayList<>(), privremenaListaKategorija);
        adapter.setOnItemClickListener(zadatak -> {
            Intent intent = new Intent(getActivity(), DetaljiZadatkaActivity.class);
            intent.putExtra("KLJUC_ZADATAK", zadatak);
            detaljiLauncher.launch(intent);
        });

        recyclerView.setAdapter(adapter);
        osveziListuZadataka();

        return view;
    }

    private void osveziListuZadataka() {
        List<Zadatak> filtriranaLista = privremenaListaZadataka.stream()
                .filter(z -> z.getStatus() == Zadatak.Status.AKTIVAN || z.getStatus() == Zadatak.Status.PAUZIRAN)
                .collect(Collectors.toList());
        adapter.updateZadaci(filtriranaLista);
    }

    private void kreirajPrivremenePodatke() {
        if (!privremenaListaZadataka.isEmpty()) return;

        Kategorija katZdravlje = new Kategorija("1", "Zdravlje", "#FF5733");
        Kategorija katUcenje = new Kategorija("2", "Učenje", "#337BFF");
        privremenaListaKategorija.add(katZdravlje);
        privremenaListaKategorija.add(katUcenje);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        privremenaListaZadataka.add(new Zadatak("z1", "Jutarnje trčanje", "5km", katZdravlje.getId(), false, 0, null, cal.getTimeInMillis(), 0, Zadatak.Tezina.LAK, Zadatak.Bitnost.NORMALAN));

        cal.set(Calendar.HOUR_OF_DAY, 10);
        privremenaListaZadataka.add(new Zadatak("z2", "Učenje za ispit", "Poglavlje 5", katUcenje.getId(), false, 0, null, cal.getTimeInMillis(), 0, Zadatak.Tezina.TEZAK, Zadatak.Bitnost.VAZAN));
    }
}