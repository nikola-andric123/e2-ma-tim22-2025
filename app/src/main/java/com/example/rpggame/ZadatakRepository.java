package com.example.rpggame;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

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
    private BossDao bossDao; // DODATO

    // Firebase instance
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ExecutorService executorService;
    private Handler mainThreadHandler;

    // Listener interfejsi
    public interface OnTasksLoadedListener { void onTasksLoaded(List<Zadatak> zadaci); }
    public interface OnCategoriesLoadedListener { void onCategoriesLoaded(List<Kategorija> kategorije); }
    // NOVI INTERFEJS ZA KORISNIKA
    public interface OnUserProfileLoadedListener { void onProfileLoaded(UserProfile userProfile); }

    public ZadatakRepository(Application application) {
        AppDatabase appDb = AppDatabase.getDatabase(application);
        this.zadatakDao = appDb.zadatakDao();
        this.kategorijaDao = appDb.kategorijaDao();
        this.bossDao = appDb.bossDao(); // DODATO

        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();

        this.executorService = Executors.newSingleThreadExecutor();
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    // --- Metode za Zadatke (Room) ---
    public void insert(Zadatak zadatak) { executorService.execute(() -> zadatakDao.insert(zadatak)); }
    public void delete(Zadatak zadatak) { executorService.execute(() -> zadatakDao.delete(zadatak)); }
    public void getSveZadatke(OnTasksLoadedListener listener) {
        executorService.execute(() -> {
            final List<Zadatak> zadaci = zadatakDao.getSveZadatke();
            mainThreadHandler.post(() -> listener.onTasksLoaded(zadaci));
        });
    }

    // --- Metode za Kategorije (Room) ---
    public void insert(Kategorija kategorija) { executorService.execute(() -> kategorijaDao.insert(kategorija)); }
    public void getSveKategorije(OnCategoriesLoadedListener listener) {
        executorService.execute(() -> {
            final List<Kategorija> kategorije = kategorijaDao.getSveKategorije();
            mainThreadHandler.post(() -> listener.onCategoriesLoaded(kategorije));
        });
    }

    // --- Metode za Bosove (Room) ---
    public void insert(Boss boss) { executorService.execute(() -> bossDao.insert(boss)); }
    // TODO: Dodati ostale metode za bosove po potrebi

    // --- NOVA METODA za UserProfile (Firebase) ---
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
                            listener.onProfileLoaded(null); // Korisnik postoji u Auth, ali ne i u Firestore
                        }
                    })
                    .addOnFailureListener(e -> {
                        listener.onProfileLoaded(null); // Greška pri dohvatanju
                    });
        } else {
            listener.onProfileLoaded(null); // Niko nije ulogovan
        }
    }
}