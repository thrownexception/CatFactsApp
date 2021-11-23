package com.te.catfactsapp.view.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.te.catfactsapp.R;
import com.te.catfactsapp.databinding.FragmentFactBinding;
import com.te.catfactsapp.model.CatFact;
import com.te.catfactsapp.model.database.CatFactDatabase;
import com.te.catfactsapp.model.database.FactDao;
import com.te.catfactsapp.util.CatFactListener;
import com.te.catfactsapp.viewmodel.FactViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class FactFragment extends Fragment implements CatFactListener {

    private FactViewModel viewModel;
    private FragmentFactBinding binding;
    private List<CatFact> retrievedFacts = new ArrayList<>();
    private List<CatFact> savedFavourites = new ArrayList<>();
    private CatFact savedFact;
    private MenuItem addToFavourite;
    private MenuItem favourite;
    private MenuItem shareFact;
    public static String TAG = "NoDataAccessDialog";


    public FactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fact, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FactViewModel.class);
        savedFact = viewModel.restoringTheState();
        viewModel.refresh();
        viewModel.getSavedFavourites();
        binding.setListener(this);
        observeViewModel();
    }

    private void observeViewModel() {

        viewModel.getSavedFavourites();
        viewModel.factsList.observe(getViewLifecycleOwner(), facts -> {
            retrievedFacts = facts;

            int listSize = retrievedFacts.size();
            Random random = new Random();
            int randomNum;

            if (savedFact != null) {
                binding.setFact(savedFact);
                binding.progressBar.setVisibility(View.GONE);
                observeTheFavourites();
            } else {
                for (int i = 0; i < listSize; i++) {
                    randomNum = random.nextInt(listSize);
                    binding.setFact(retrievedFacts.get(randomNum));
                    binding.progressBar.setVisibility(View.GONE);
                    observeTheFavourites();
                }
            }
        });
    }

    private void observeTheFavourites() {

        viewModel.favouriteFacts.observe(getViewLifecycleOwner(), facts -> {
            savedFavourites = facts;

            if (savedFavourites.size() > 0) {
                for (CatFact fact : savedFavourites) {
                    if (binding.getFact().fact.equals(fact.fact)) {
                        addToFavourite.setEnabled(false);
                        addToFavourite.setVisible(false);
                        favourite.setVisible(true);
                        favourite.setEnabled(true);
                    } else {
                        favourite.setVisible(false);
                        favourite.setEnabled(false);
                        addToFavourite.setVisible(true);
                        addToFavourite.setEnabled(true);
                    }
                }
            }
        });
    }

    @Override
    public void onButtonClicked(View view) {
        settingNewFact();
    }

    public void settingNewFact() {
        viewModel.getSavedFavourites();
        int listSize = retrievedFacts.size();
        Random random = new Random();
        int randomNum;
        for (int i = 0; i < listSize; i++) {
            randomNum = random.nextInt(listSize);
            binding.setFact(retrievedFacts.get(randomNum));
            binding.progressBar.setVisibility(View.GONE);
            observeTheFavourites();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fact_menu, menu);
        addToFavourite = menu.findItem(R.id.addToFavourites);
        favourite = menu.findItem(R.id.favourite);
        shareFact = menu.findItem(R.id.shareACatFact);
        favourite.setVisible(false);

        /*
         If there's no data available, AlertDialog will allow
         user to close the app.
        */

        final Handler handler = new Handler(Looper.myLooper());
        handler.postDelayed(() -> {
            try {
                if (savedFact == null && retrievedFacts.size() == 0) {
                    Thread.sleep(3000);
                    if (savedFact == null && retrievedFacts.size() == 0) {
                        addToFavourite.setVisible(false);
                        addToFavourite.setEnabled(false);
                        shareFact.setVisible(false);
                        shareFact.setEnabled(false);
                        binding.NextCatFactButton.setEnabled(false);
                        binding.NextCatFactButton.setVisibility(View.GONE);
                        new NoDataAccessDialogFragment().show(getChildFragmentManager(), TAG);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 3000);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.shareACatFact:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Did you know that: \n\n " + binding.getFact().fact);
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                return true;

            case R.id.showList:
                NavDirections actionList = FactFragmentDirections.actionFactToList();
                Navigation.findNavController(getView()).navigate(actionList);
                return true;

            case R.id.addToFavourites:
                CatFact favFact = binding.getFact();
                AsyncTask<CatFact, Void, CatFact> insertTask = new InsertFactTask();
                insertTask.execute(favFact);

                addToFavourite.setEnabled(false);
                addToFavourite.setVisible(false);
                favourite.setVisible(true);
                favourite.setEnabled(true);
                Toast.makeText(getContext(), "Fact added to favourites", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.favourite:
                String deleteFromFav = binding.getFact().fact;
                AsyncTask<String, Void, Void> deleteTask = new DeleteFactTask();
                deleteTask.execute(deleteFromFav);
                favourite.setVisible(false);
                favourite.setEnabled(false);
                addToFavourite.setEnabled(true);
                addToFavourite.setVisible(true);
                Toast.makeText(getContext(), "Fact deleted from favourites", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        storingFact();
    }

    public void storingFact() {
        viewModel.storingTheState(binding.getFact());
    }

    private class InsertFactTask extends AsyncTask<CatFact, Void, CatFact> {

        @Override
        protected CatFact doInBackground(CatFact... catFacts) {
            CatFact fact = catFacts[0];
            FactDao dao = CatFactDatabase.getInstance(getContext()).factDao();
            dao.insert(fact);
            return null;
        }
    }

    private class DeleteFactTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String factToDelete = strings[0];
            CatFactDatabase.getInstance(getContext()).factDao().deleteFact(factToDelete);
            return null;
        }
    }
}