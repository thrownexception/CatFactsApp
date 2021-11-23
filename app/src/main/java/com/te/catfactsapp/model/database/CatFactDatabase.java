package com.te.catfactsapp.model.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.te.catfactsapp.model.CatFact;

@Database(entities = {CatFact.class}, version = 1)
public abstract class CatFactDatabase extends RoomDatabase {

    private static CatFactDatabase instance;
    public static CatFactDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    CatFactDatabase.class, "catfactdatabase").build();
        }
        return instance;
    }
    public abstract FactDao factDao();
}
