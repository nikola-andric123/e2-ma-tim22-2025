package com.example.rpggame;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZadatakRepository {

    private ZadatakDao zadatakDao;
    private KategorijaDao kategorijaDao; // DODATO
    private ExecutorService executorService;
    private Handler mainThreadHandler;

    public interface OnTasksLoadedListener {
        void onTasksLoaded(List<Zadatak> zadaci);
    }

    // NOVI INTERFEJS
    public interface OnCategoriesLoadedListener {
        void onCategoriesLoaded(List<Kategorija> kategorije);
    }

    public ZadatakRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.zadatakDao = db.zadatakDao();
        this.kategorijaDao = db.kategorijaDao(); // DODATO
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    // --- Metode za Zadatke (ostaju iste) ---
    public void insert(Zadatak zadatak) {
        executorService.execute(() -> zadatakDao.insert(zadatak));
    }
    public void delete(Zadatak zadatak) {
        executorService.execute(() -> zadatakDao.delete(zadatak));
    }
    public void getSveZadatke(OnTasksLoadedListener listener) {
        executorService.execute(() -> {
            final List<Zadatak> zadaci = zadatakDao.getSveZadatke();
            mainThreadHandler.post(() -> listener.onTasksLoaded(zadaci));
        });
    }

    // --- NOVE Metode za Kategorije ---
    public void insert(Kategorija kategorija) {
        executorService.execute(() -> kategorijaDao.insert(kategorija));
    }
    public void getSveKategorije(OnCategoriesLoadedListener listener) {
        executorService.execute(() -> {
            final List<Kategorija> kategorije = kategorijaDao.getSveKategorije();
            mainThreadHandler.post(() -> listener.onCategoriesLoaded(kategorije));
        });
    }
}