package com.example.rpggame;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZadatakRepository {

    private ZadatakDao zadatakDao;
    private ExecutorService executorService;
    private Handler mainThreadHandler;

    // Definišemo interfejs za povratni poziv (listener)
    public interface OnTasksLoadedListener {
        void onTasksLoaded(List<Zadatak> zadaci);
    }

    public ZadatakRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.zadatakDao = db.zadatakDao();
        this.executorService = Executors.newSingleThreadExecutor();
        // Handler koji će nam omogućiti da rezultate vratimo na glavnu (UI) nit
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public void insert(Zadatak zadatak) {
        executorService.execute(() -> {
            zadatakDao.insert(zadatak);
        });
    }

    public void delete(Zadatak zadatak) {
        executorService.execute(() -> {
            zadatakDao.delete(zadatak);
        });
    }

    // Metoda sada prima listener kao parametar da bi vratila rezultat asinhrono
    public void getSveZadatke(OnTasksLoadedListener listener) {
        executorService.execute(() -> {
            // Operacija čitanja se dešava u pozadini
            final List<Zadatak> zadaci = zadatakDao.getSveZadatke();
            // Kada je gotovo, šaljemo rezultat na glavnu nit
            mainThreadHandler.post(() -> {
                listener.onTasksLoaded(zadaci);
            });
        });
    }
}