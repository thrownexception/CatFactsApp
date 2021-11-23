package com.te.catfactsapp.model.apiservice;

import com.te.catfactsapp.model.CatFact;
import com.te.catfactsapp.model.CatFactGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CatFactApiService {

    public static final String BASE_URL = "https://catfact.ninja/";
    private final CatFactApi api;
    private List<CatFact> retrievedList = new ArrayList<>();
    public ResponseListener mCallback;

    public interface ResponseListener{
        void onDataReceived(List<CatFact> facts);
    }

    public CatFactApiService(ResponseListener callback){
        api = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(CatFactApi.class);
        this.mCallback = callback;
    }

    public void gettingTheFactList(){
        Call<CatFactGroup> call2 = api.getFactList();
        call2.enqueue(new Callback<CatFactGroup>() {
            @Override
            public void onResponse(Call<CatFactGroup> call, Response<CatFactGroup> response) {
                if (response.body() != null){
                    retrievedList = response.body().catArray;
                    mCallback.onDataReceived(retrievedList);
                }
            }

            @Override
            public void onFailure(Call<CatFactGroup> call, Throwable t) {

            }
        });
    }
}
