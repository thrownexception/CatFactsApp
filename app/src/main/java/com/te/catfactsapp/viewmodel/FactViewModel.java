package com.te.catfactsapp.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.te.catfactsapp.model.CatFact;
import com.te.catfactsapp.model.apiservice.CatFactApiService;
import com.te.catfactsapp.model.database.CatFactDatabase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FactViewModel extends AndroidViewModel {

    public MutableLiveData<List<CatFact>> factsList = new MutableLiveData<>();
    public MutableLiveData<List<CatFact>> favouriteFacts = new MutableLiveData<>();
    public static final String SAVED_FACT = "savedFact";
    CatFactApiService factService = new CatFactApiService(facts -> factsList.setValue(facts));
    SavedStateHandle mState;

    @Inject
    public FactViewModel(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);
        this.mState = savedStateHandle;
    }

    public void getSavedFavourites(){
        AsyncTask<Void, Void, List<CatFact>> retrieveTask = new RetrieveFavouriteTask();
        retrieveTask.execute();
    }

    public void refresh(){ factService.gettingTheFactList();}

    public void storingTheState(CatFact fact){
        mState.set(SAVED_FACT, fact); }

    public CatFact restoringTheState(){
        return mState.get(SAVED_FACT); }



    private class RetrieveFavouriteTask extends AsyncTask<Void, Void, List<CatFact>>{

        @Override
        protected List<CatFact> doInBackground(Void... voids) {
            return CatFactDatabase.getInstance(getApplication()).factDao().getAllFacts();
        }

        @Override
        protected void onPostExecute(List<CatFact> catFacts) {
            super.onPostExecute(catFacts);
            favouriteFacts.setValue(catFacts);
        }
    }
}
