package com.example.rpggame;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

// AŽURIRANO: Dodata Kategorija.class u niz entiteta i povećana verzija baze na 2
@Database(entities = {Zadatak.class, Kategorija.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    // AŽURIRANO: Dodata apstraktna metoda za KategorijaDao
    public abstract ZadatakDao zadatakDao();
    public abstract KategorijaDao kategorijaDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "rpg_game_database")
                            // Kada menjamo strukturu baze, najlakši način za razvoj je
                            // da dozvolimo Room-u da uništi i ponovo napravi bazu.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}