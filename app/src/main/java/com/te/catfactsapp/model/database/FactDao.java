package com.te.catfactsapp.model.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.te.catfactsapp.model.CatFact;

import java.util.List;

@Dao
public interface FactDao {

    @Insert
    List<Long> insertAll(CatFact... facts);

    @Insert
    Long insert(CatFact fact);

    @Query("SELECT * FROM catfact")
    CatFact getACatFact();

    @Query("SELECT * FROM catfact")
    List<CatFact> getAllFacts();

    @Query("SELECT * FROM catfact WHERE factId = :factId")
    CatFact getFact(int factId);

    @Query("SELECT * FROM catfact WHERE fact = :fact")
    CatFact getSameFact(String fact);

    @Query("SELECT * FROM catfact WHERE factLength = :length")
    CatFact getSameLengthFact(int length);

    @Query("DELETE FROM catfact WHERE fact = :fact")
    void deleteFact(String fact);

    @Query("DELETE FROM catfact")
    void deleteAll();

}
