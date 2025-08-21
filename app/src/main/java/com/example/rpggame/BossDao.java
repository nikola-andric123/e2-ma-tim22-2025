package com.example.rpggame;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface BossDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Boss boss);

    @Query("SELECT * FROM boss_table WHERE level = :level")
    Boss getBossByLevel(int level);

    @Query("SELECT * FROM boss_table WHERE isDefeated = 0 ORDER BY level ASC")
    List<Boss> getNeporazeniBosovi();

    @Query("DELETE FROM boss_table")
    void deleteAll();
}