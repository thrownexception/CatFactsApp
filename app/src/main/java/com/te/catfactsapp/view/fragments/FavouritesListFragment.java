package com.te.catfactsapp.view.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.te.catfactsapp.R;
import com.te.catfactsapp.databinding.FragmentFavouritesListBinding;
import com.te.catfactsapp.model.CatFact;
import com.te.catfactsapp.model.database.CatFactDatabase;
import com.te.catfactsapp.model.database.FactDao;
import com.te.catfactsapp.view.FavFactsListAdapter;
import com.te.catfactsapp.viewmodel.FavouritesListViewModel;
import java.util.ArrayList;


public class FavouritesListFragment extends Fragment {

    private FavouritesListViewModel viewModel;
    private FavFactsListAdapter favFactsListAdapter = new FavFactsListAdapter(new ArrayList<>());
    private FragmentFavouritesListBinding binding;
    private AsyncTask<String, Void, Integer> deletingOneFactTask;
    private MutableLiveData<Integer> dbSize = new MutableLiveData<>();
    private MenuItem menuItem;

    public FavouritesListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourites_list, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FavouritesListViewModel.class);
        viewModel.fetchFromDatabase();
        binding.recyclerViewFavFactsList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewFavFactsList.setAdapter(favFactsListAdapter);
        observeViewModel();
        managingItemTouchHelper();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.favlist_menu, menu);
        menuItem = menu.findItem(R.id.deleteAll);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        disablingMenuItem(R.id.deleteAll, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.deleteAll) {
            AsyncTask<Void, Void, Void> deleteTask = new DeleteTask();
            deleteTask.execute();
            viewModel.fetchFromDatabase();
            binding.listConstraintLayout.setBackgroundResource(R.drawable.ic_kot);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void disablingMenuItem(int i, Menu menu){
        dbSize.observe(getViewLifecycleOwner(), integer -> {
            if (integer == 0){
                menu.findItem(i).setVisible(false);
            }
        });
    }

    private void observeViewModel() {

        viewModel.favFacts.observe(getViewLifecycleOwner(), catFacts -> {
            if (catFacts != null) {
                favFactsListAdapter.updateFavFactsList(catFacts);
            }
            if (catFacts.size() == 0) {
                binding.listConstraintLayout.setBackgroundResource(R.drawable.ic_kot);
                menuItem.setVisible(false);
            }
        });
    }

    private void managingItemTouchHelper() {
        ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();
                CatFact factToDelete = favFactsListAdapter.removeItem(position);
                deletingOneFactTask = new DeletingOneFact();
                deletingOneFactTask.execute(factToDelete.fact);
                Toast.makeText(getContext(), "Fact deleted from favourites", Toast.LENGTH_SHORT).show();

                dbSize.observe(getViewLifecycleOwner(), integer -> {
                    try {
                        if (integer == 0) {
                            binding.listConstraintLayout.setBackgroundResource(R.drawable.ic_kot);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                });
            }
        };

        ItemTouchHelper itemTouchHelper1 = new ItemTouchHelper(itemTouchHelper);
        itemTouchHelper1.attachToRecyclerView(binding.recyclerViewFavFactsList);
        viewModel.fetchFromDatabase();
    }

    private void settingTheSize(Integer size) {
        dbSize.setValue(size);
    }

    private class DeleteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            FactDao dao = CatFactDatabase.getInstance(getContext()).factDao();
            dao.deleteAll();
            return null;
        }
    }

    private class DeletingOneFact extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            String factToDelete = strings[0];
            CatFactDatabase.getInstance(getContext()).factDao().deleteFact(factToDelete);
            int size = CatFactDatabase.getInstance(getContext()).factDao().getAllFacts().size();
            return size;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            settingTheSize(integer);
        }
    }
}