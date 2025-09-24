package com.example.rpggame.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.rpggame.domain.Zadatak;

import java.util.List;

@Dao
public interface ZadatakDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Zadatak zadatak);

    @Update
    void update(Zadatak zadatak);

    @Delete
    void delete(Zadatak zadatak);

    @Query("SELECT * FROM zadatak_table ORDER BY datum_pocetka ASC")
    List<Zadatak> getSveZadatke();

    // NOVA METODA KOJA JE NEDOSTAJALA
    @Query("SELECT * FROM zadatak_table WHERE datum_pocetka >= :timestamp")
    List<Zadatak> getZadatkeOd(long timestamp);

    @Query("DELETE FROM zadatak_table")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM zadatak_table WHERE kategorija_id = :kategorijaId AND status = 'AKTIVAN'")
    int getActiveTaskCountForCategory(String kategorijaId);
    @Query("SELECT * FROM zadatak_table WHERE id = :zadatakId")
    Zadatak getZadatakById(String zadatakId);
}