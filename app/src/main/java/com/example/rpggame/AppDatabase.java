package com.example.rpggame;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

// @Database anotacija definiše glavne parametre baze.
// 'entities' je niz svih klasa koje su tabele u bazi. Za sada imamo samo Zadatak.
// 'version' je verzija baze. Počinjemo sa 1. Ako kasnije menjamo strukturu tabele, moramo povećati verziju.
// 'exportSchema = false' isključuje izvoženje šeme baze u fajl, što nam za sada ne treba.
@Database(entities = {Zadatak.class}, version = 1, exportSchema = false)
// @TypeConverters anotacija kaže Room-u da koristi našu Converters klasu za prevođenje tipova.
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    // Apstraktna metoda koja će nam vratiti instancu našeg DAO interfejsa.
    // Room će sam generisati telo ove metode.
    public abstract ZadatakDao zadatakDao();

    // Singleton pattern počinje ovde.
    // 'volatile' osigurava da je vrednost INSTANCE uvek ažurna za sve niti.
    private static volatile AppDatabase INSTANCE;

    // Metoda koja kreira i/ili vraća jedinu instancu baze podataka.
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // 'synchronized' osigurava da samo jedna nit može da kreira instancu baze,
            // kako ne bismo slučajno napravili dve.
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // Kreiranje baze podataka.
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "rpg_game_database") // Ime fajla baze podataka
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}