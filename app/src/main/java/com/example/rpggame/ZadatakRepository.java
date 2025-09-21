package com.example.rpggame;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.rpggame.dao.BossDao;
import com.example.rpggame.dao.KategorijaDao;
import com.example.rpggame.dao.ZadatakDao;
import com.example.rpggame.domain.Boss;
import com.example.rpggame.domain.Kategorija;
import com.example.rpggame.domain.UserProfile;
import com.example.rpggame.domain.Zadatak;
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
    private Application application;
    private ExecutorService executorService;
    private Handler mainThreadHandler;

    public interface OnTasksLoadedListener { void onTasksLoaded(List<Zadatak> zadaci); }
    public interface OnCategoriesLoadedListener { void onCategoriesLoaded(List<Kategorija> kategorije); }
    public interface OnUserProfileLoadedListener { void onProfileLoaded(UserProfile userProfile); }
    public interface OnBossesLoadedListener { void onBossesLoaded(List<Boss> bosses); }
    public interface OnResetCompleteListener { void onResetComplete(); }
    public interface OnCountLoadedListener { void onCountLoaded(int count); }

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

    public void getZadatkeOd(long timestamp, OnTasksLoadedListener listener) {
        executorService.execute(() -> {
            final List<Zadatak> zadaci = zadatakDao.getZadatkeOd(timestamp);
            mainThreadHandler.post(() -> listener.onTasksLoaded(zadaci));
        });
    }
    public void getActiveTaskCountForCategory(String kategorijaId, OnCountLoadedListener listener) {
        executorService.execute(() -> {
            final int count = zadatakDao.getActiveTaskCountForCategory(kategorijaId);
            mainThreadHandler.post(() -> listener.onCountLoaded(count));
        });
    }
    public void insert(Kategorija kategorija) {
        executorService.execute(() -> kategorijaDao.insert(kategorija));
    }

    public void getSveKategorije(OnCategoriesLoadedListener listener) {
        executorService.execute(() -> {
            final List<Kategorija> kategorije = kategorijaDao.getSveKategorije();
            mainThreadHandler.post(() -> listener.onCategoriesLoaded(kategorije));
        });
    }
    public void delete(Kategorija kategorija) {
        executorService.execute(() -> kategorijaDao.delete(kategorija));
    }

    public void insert(Boss boss) {
        executorService.execute(() -> bossDao.insert(boss));
    }

    public void getNeporazeneBosove(OnBossesLoadedListener listener) {
        executorService.execute(() -> {
            final List<Boss> bosovi = bossDao.getNeporazeniBosovi();
            mainThreadHandler.post(() -> listener.onBossesLoaded(bosovi));
        });
    }

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

    public void updateUserProfile(UserProfile profile) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && profile != null) {
            String uid = currentUser.getUid();
            db.collection("users").document(uid).set(profile)
                    .addOnSuccessListener(aVoid -> {
                        mainThreadHandler.post(() -> Toast.makeText(application, "Profil sačuvan!", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> {
                        mainThreadHandler.post(() -> Toast.makeText(application, "Greška pri čuvanju profila.", Toast.LENGTH_SHORT).show());
                    });
        }
    }

    public void resetLokalnuBazu() {
        executorService.execute(() -> {
            zadatakDao.deleteAll();
            kategorijaDao.deleteAll();
            bossDao.deleteAll();
        });
    }

    public void resetUserProfileNaPocetnoStanje(OnResetCompleteListener listener) {
        getUserProfile(userProfile -> {
            if (userProfile != null) {
                userProfile.setLevel(0);
                userProfile.setExperiencePoints(0);
                userProfile.setPowerPoints(0);
                userProfile.setCollectedCoins(0);
                userProfile.setNumberOfBadges(0);

                updateUserProfile(userProfile);
                mainThreadHandler.post(listener::onResetComplete);
            } else {
                mainThreadHandler.post(listener::onResetComplete);
            }
        });
    }
}