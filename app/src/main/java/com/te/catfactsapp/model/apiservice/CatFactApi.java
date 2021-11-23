package com.te.catfactsapp.model.apiservice;

import com.te.catfactsapp.model.CatFactGroup;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CatFactApi {

    @GET("facts?limit=500")
    Call<CatFactGroup>  getFactList();
}
