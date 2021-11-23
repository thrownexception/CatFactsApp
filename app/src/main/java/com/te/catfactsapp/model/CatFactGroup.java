package com.te.catfactsapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CatFactGroup {

    @SerializedName("current_page")
    public String page;

    @SerializedName("data")
    public List<CatFact> catArray= null;

    public CatFactGroup() {
    }
}
