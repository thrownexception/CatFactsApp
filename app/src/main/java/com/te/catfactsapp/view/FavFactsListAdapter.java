package com.te.catfactsapp.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.te.catfactsapp.R;
import com.te.catfactsapp.databinding.ItemFactBinding;
import com.te.catfactsapp.model.CatFact;
import com.te.catfactsapp.model.database.CatFactDatabase;
import com.te.catfactsapp.util.PopUpMenuListener;

import java.util.ArrayList;
import java.util.List;

public class FavFactsListAdapter extends RecyclerView.Adapter<FavFactsListAdapter.FactViewHolder>{

    private ArrayList<CatFact> favFactsList;
    ItemFactBinding itemFactBinding;
    private CatFactDatabase catFactDatabase;

    public FavFactsListAdapter(ArrayList<CatFact> favFactsList) {
        this.favFactsList = favFactsList;
    }

    public void updateFavFactsList(List<CatFact> newFavFacts){
        favFactsList.clear();
        favFactsList.addAll(newFavFacts);
        notifyDataSetChanged();
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public FactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        itemFactBinding = DataBindingUtil.inflate(inflater, R.layout.item_fact, parent, false);
        catFactDatabase = CatFactDatabase.getInstance(itemFactBinding.getRoot().getContext());

        int nightModeFlags =
                parent.getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
//               itemFactBinding.getRoot().setBackgroundColor(R.color.secondaryLightColor);
                itemFactBinding.favouriteFactLayout.setBackgroundResource(R.color.secondaryColor);
                itemFactBinding.favListContextMenu.setBackgroundResource(R.color.secondaryColor);
                break;

            case Configuration.UI_MODE_NIGHT_NO:

                break;

        }


        return new FactViewHolder(itemFactBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FactViewHolder holder, int position) {
        holder.itemView.setFavCatFact(favFactsList.get(position));
    }

    @Override
    public int getItemCount() {
        return favFactsList.size();
    }

    public CatFact removeItem(int position){
        CatFact factToDelete = favFactsList.get(position);
        favFactsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, favFactsList.size());
        return factToDelete;
    }


    class FactViewHolder extends RecyclerView.ViewHolder implements PopUpMenuListener,
            androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener{

        ItemFactBinding itemView;

        public FactViewHolder(@NonNull ItemFactBinding itemView){
            super(itemView.getRoot());
            this.itemView = itemView;
            itemView.setListener(this::onMenuClicked);
        }

        public void showPopupMenu(View v, Context context){
            PopupMenu popupMenu = new PopupMenu(context, v);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.favlist_popumenu, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.deleteAFactPopUp:
                    AsyncTask<String, Void, Void> deletePopupTask = new DeleteOneFact();
                    deletePopupTask.execute(itemView.getFavCatFact().fact);
                    favFactsList.remove(itemView.getFavCatFact());
                    notifyDataSetChanged();
                    return true;

                case R.id.shareAFactPopUp:
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "Did you know that: \n\n"
                            + itemView.getFavCatFact().fact);
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    itemView.getRoot().getContext().startActivity(shareIntent);
                    return true;

            }
            return false;
        }

        @Override
        public void onMenuClicked(View view) {
            showPopupMenu(view, view.getContext());
        }
    }

    private class DeleteOneFact extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... strings) {
            String factToDelete = strings[0];
            catFactDatabase.factDao().deleteFact(factToDelete);
            return null;
        }
    }
}
