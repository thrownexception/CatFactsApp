package com.te.catfactsapp.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.te.catfactsapp.model.CatFact;
import com.te.catfactsapp.model.database.CatFactDatabase;

import java.util.List;

public class FavouritesListViewModel extends AndroidViewModel {

    public MutableLiveData<List<CatFact>> favFacts = new MutableLiveData<>();
    public MutableLiveData<Integer> dBSize = new MutableLiveData<>();
    private AsyncTask<Void, Void, Integer> dbSizeTask;

    public FavouritesListViewModel(@NonNull Application application) {
        super(application);
    }

    private void retrieveFacts(List<CatFact> favFactsList){
        favFacts.setValue(favFactsList);
        dBSize.setValue(favFactsList.size());
    }

    public void fetchFromDatabase(){
        AsyncTask<Void, Void, List<CatFact>> retrieveTask = new RetrieveTask();
        retrieveTask.execute();
    }


    private class RetrieveTask extends AsyncTask<Void, Void, List<CatFact>>{
        @Override
        protected List<CatFact> doInBackground(Void... voids) {
            return CatFactDatabase.getInstance(getApplication()).factDao().getAllFacts();
        }

        @Override
        protected void onPostExecute(List<CatFact> catFacts) {
            retrieveFacts(catFacts);
        }
    }

}
