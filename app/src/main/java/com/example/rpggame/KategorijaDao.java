package com.example.rpggame;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface KategorijaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Kategorija kategorija);

    @Update
    void update(Kategorija kategorija);

    @Query("SELECT * FROM kategorija_table ORDER BY naziv ASC")
    List<Kategorija> getSveKategorije();

    @Query("DELETE FROM kategorija_table")
    void deleteAll();
}