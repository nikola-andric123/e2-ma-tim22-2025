package com.example.rpggame;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.rpggame.domain.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZadatakRepository {

    private ZadatakDao zadatakDao;
    private KategorijaDao kategorijaDao;
    private BossDao bossDao;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Application application; // Treba nam za Toast poruke

    private ExecutorService executorService;
    private Handler mainThreadHandler;

    public interface OnTasksLoadedListener { void onTasksLoaded(List<Zadatak> zadaci); }
    public interface OnCategoriesLoadedListener { void onCategoriesLoaded(List<Kategorija> kategorije); }
    public interface OnUserProfileLoadedListener { void onProfileLoaded(UserProfile userProfile); }

    public ZadatakRepository(Application application) {
        this.application = application;
        AppDatabase appDb = AppDatabase.getDatabase(application);
        this.zadatakDao = appDb.zadatakDao();
        this.kategorijaDao = appDb.kategorijaDao();
        this.bossDao = appDb.bossDao();

        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();

        this.executorService = Executors.newSingleThreadExecutor();
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    // Metode za Zadatke...
    public void insert(Zadatak zadatak) { executorService.execute(() -> zadatakDao.insert(zadatak)); }
    public void delete(Zadatak zadatak) { executorService.execute(() -> zadatakDao.delete(zadatak)); }
    public void getSveZadatke(OnTasksLoadedListener listener) {
        executorService.execute(() -> {
            final List<Zadatak> zadaci = zadatakDao.getSveZadatke();
            mainThreadHandler.post(() -> listener.onTasksLoaded(zadaci));
        });
    }

    // Metode za Kategorije...
    public void insert(Kategorija kategorija) { executorService.execute(() -> kategorijaDao.insert(kategorija)); }
    public void getSveKategorije(OnCategoriesLoadedListener listener) {
        executorService.execute(() -> {
            final List<Kategorija> kategorije = kategorijaDao.getSveKategorije();
            mainThreadHandler.post(() -> listener.onCategoriesLoaded(kategorije));
        });
    }

    // Metode za Bosove...
    public void insert(Boss boss) { executorService.execute(() -> bossDao.insert(boss)); }

    // Metode za UserProfile...
    public void getUserProfile(OnUserProfileLoadedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            UserProfile profile = documentSnapshot.toObject(UserProfile.class);
                            listener.onProfileLoaded(profile);
                        } else {
                            listener.onProfileLoaded(null);
                        }
                    })
                    .addOnFailureListener(e -> listener.onProfileLoaded(null));
        } else {
            listener.onProfileLoaded(null);
        }
    }

    // NOVA METODA ZA AŽURIRANJE PROFILA
    public void updateUserProfile(UserProfile profile) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && profile != null) {
            String uid = currentUser.getUid();
            db.collection("users").document(uid).set(profile)
                    .addOnSuccessListener(aVoid -> {
                        // Uspešno sačuvano
                        mainThreadHandler.post(() -> Toast.makeText(application, "Nagrade sačuvane!", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> {
                        // Greška pri čuvanju
                        mainThreadHandler.post(() -> Toast.makeText(application, "Greška pri čuvanju nagrada.", Toast.LENGTH_SHORT).show());
                    });
        }
    }
}