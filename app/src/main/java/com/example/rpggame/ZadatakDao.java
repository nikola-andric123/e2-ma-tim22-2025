package com.example.rpggame;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// @Dao anotacija kaže Room-u da je ovo Data Access Object interfejs.
@Dao
public interface ZadatakDao {

    // OnConflictStrategy.REPLACE znači da ako pokušamo da ubacimo zadatak sa ID-jem
    // koji već postoji, Room će stari zadatak zameniti novim.
    // Ovo je korisno jer istu metodu možemo koristiti i za čuvanje izmena.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Zadatak zadatak);

    // Eksplicitna metoda za ažuriranje.
    @Update
    void update(Zadatak zadatak);

    // Metoda za brisanje. Room prepoznaje koji zadatak treba obrisati na osnovu primarnog ključa.
    @Delete
    void delete(Zadatak zadatak);

    // @Query nam dozvoljava da pišemo sopstvene SQL upite.
    // Ova metoda će vratiti sve zadatke iz tabele.
    @Query("SELECT * FROM zadatak_table ORDER BY datum_pocetka ASC")
    List<Zadatak> getSveZadatke();

    // Metoda za brisanje svih zadataka iz tabele.
    @Query("DELETE FROM zadatak_table")
    void deleteAll();

    // Primer metode za pronalaženje zadatka po ID-ju.
    @Query("SELECT * FROM zadatak_table WHERE id = :zadatakId")
    Zadatak getZadatakById(String zadatakId);
}