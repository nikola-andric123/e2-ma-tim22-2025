package com.example.rpggame;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.rpggame.dao.BossDao;
import com.example.rpggame.dao.KategorijaDao;
import com.example.rpggame.dao.ZadatakDao;
import com.example.rpggame.domain.Boss;
import com.example.rpggame.domain.Clan;
import com.example.rpggame.domain.Kategorija;
import com.example.rpggame.domain.NapredakKorisnikaUMisiji;
import com.example.rpggame.domain.SpecijalnaMisija;
import com.example.rpggame.domain.UserProfile;
import com.example.rpggame.domain.Zadatak;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
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
    public interface OnClanLoadedListener { void onClanLoaded(Clan clan); }
    public interface OnMissionStartedListener { void onMissionStarted(boolean success, String message); }

    public interface OnMissionLoadedListener { void onMissionLoaded(SpecijalnaMisija misija); }
    public interface OnTasksLoadedListener { void onTasksLoaded(List<Zadatak> zadaci); }
    public interface OnCategoriesLoadedListener { void onCategoriesLoaded(List<Kategorija> kategorije); }
    public interface OnUserProfileLoadedListener { void onProfileLoaded(UserProfile userProfile); }
    public interface OnBossesLoadedListener { void onBossesLoaded(List<Boss> bosses); }
    public interface OnResetCompleteListener { void onResetComplete(); }
    public interface OnCountLoadedListener { void onCountLoaded(int count); }
    public interface OnProgressLoadedListener { void onProgressLoaded(List<NapredakKorisnikaUMisiji> listaNapretka); }

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
    public void getAktivnaMisijaZaSavez(String clanId, OnMissionLoadedListener listener) {
        if (clanId == null || clanId.isEmpty()) {
            listener.onMissionLoaded(null);
            return;
        }

        db.collection("missions")
                .whereEqualTo("idSaveza", clanId)
                .whereEqualTo("status", "AKTIVNA")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        SpecijalnaMisija misija = queryDocumentSnapshots.getDocuments().get(0).toObject(SpecijalnaMisija.class);
                        listener.onMissionLoaded(misija);
                    } else {
                        listener.onMissionLoaded(null); // Nema aktivne misije
                    }
                })
                .addOnFailureListener(e -> listener.onMissionLoaded(null));
    }
    public void getNapredakSvihClanova(String missionId, OnProgressLoadedListener listener) {
        if (missionId == null || missionId.isEmpty()) {
            listener.onProgressLoaded(new ArrayList<>());
            return;
        }

        db.collection("missions").document(missionId).collection("clanProgress")
                .orderBy("nanetaSteta", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<NapredakKorisnikaUMisiji> lista = queryDocumentSnapshots.toObjects(NapredakKorisnikaUMisiji.class);
                        listener.onProgressLoaded(lista);
                    } else {
                        listener.onProgressLoaded(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> listener.onProgressLoaded(new ArrayList<>()));
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
    public void getClan(String clanId, OnClanLoadedListener listener) {
        db.collection("clans").document(clanId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        listener.onClanLoaded(documentSnapshot.toObject(Clan.class));
                    } else {
                        listener.onClanLoaded(null);
                    }
                })
                .addOnFailureListener(e -> listener.onClanLoaded(null));
    }

    public void startSpecijalnaMisija(UserProfile user, OnMissionStartedListener listener) {
        if (user.getClanId() == null || user.getClanId().isEmpty()) {
            listener.onMissionStarted(false, "Korisnik nije u klanu.");
            return;
        }

        // ISPRAVLJENA LOGIKA: Prvo čitamo članove iz pod-kolekcije
        db.collection("clans").document(user.getClanId()).collection("members").get()
                .addOnSuccessListener(membersQuery -> {
                    if (membersQuery.isEmpty()) {
                        listener.onMissionStarted(false, "Greška: Klan nema članova.");
                        return;
                    }

                    List<String> memberIds = new ArrayList<>();
                    for (DocumentSnapshot doc : membersQuery) {
                        memberIds.add(doc.getId());
                    }

                    // Sada kada imamo ID-jeve, radimo isto kao pre
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    for (String memberId : memberIds) {
                        tasks.add(db.collection("users").document(memberId).get());
                    }

                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
                        SpecijalnaMisija misija = new SpecijalnaMisija();
                        misija.setIdSaveza(user.getClanId());
                        misija.setStatus("AKTIVNA");
                        misija.setMaksHpBosa(100 * memberIds.size());
                        misija.setHpBosa(misija.getMaksHpBosa());
                        misija.setDatumPocetka(new Timestamp(new Date()));
                        misija.setDatumZavrsetka(new Timestamp(new Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000)));

                        WriteBatch batch = db.batch();
                        DocumentReference misijaRef = db.collection("missions").document();
                        misija.setId(misijaRef.getId());
                        batch.set(misijaRef, misija);

                        for (Object result : results) {
                            DocumentSnapshot doc = (DocumentSnapshot) result;
                            if (doc.exists()) {
                                NapredakKorisnikaUMisiji napredak = new NapredakKorisnikaUMisiji();
                                napredak.setIdKorisnika(doc.getId());
                                napredak.setKorisnickoIme(doc.getString("username"));
                                napredak.setNanetaSteta(0);
                                DocumentReference napredakRef = misijaRef.collection("clanProgress").document(doc.getId());
                                batch.set(napredakRef, napredak);
                            }
                        }

                        batch.commit()
                                .addOnSuccessListener(aVoid -> listener.onMissionStarted(true, "Misija uspešno započeta!"))
                                .addOnFailureListener(e -> listener.onMissionStarted(false, "Greška pri kreiranju misije: " + e.getMessage()));
                    }).addOnFailureListener(e -> {
                        Log.e("ZadatakRepository", "Greška pri učitavanju profila članova", e);
                        listener.onMissionStarted(false, "Greška pri učitavanju profila članova: " + e.getMessage());
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("ZadatakRepository", "Greška pri učitavanju članova iz pod-kolekcije", e);
                    listener.onMissionStarted(false, "Greška pri učitavanju članova klana: " + e.getMessage());
                });
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