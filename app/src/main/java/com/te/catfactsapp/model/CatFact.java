package com.te.catfactsapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class CatFact implements Serializable {

    @ColumnInfo(name = "fact")
    @SerializedName("fact")
    public String fact;

    @ColumnInfo(name = "factLength")
    @SerializedName("length")
    public int factLength;

    @PrimaryKey(autoGenerate = true)
    public int factId;

    public CatFact(String fact, int factLength) {
        this.fact = fact;
        this.factLength = factLength;
    }

    public String getFact() {
        return fact;
    }

    public void setFact(String fact) {
        this.fact = fact;
    }
}
