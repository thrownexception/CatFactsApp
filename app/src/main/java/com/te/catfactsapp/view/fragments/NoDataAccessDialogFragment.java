package com.te.catfactsapp.view.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.te.catfactsapp.R;

public class NoDataAccessDialogFragment extends DialogFragment {



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Problem encountered")
                .setMessage(R.string.dialog_question)
                .setPositiveButton(R.string.wait, (dialogInterface, i) -> NoDataAccessDialogFragment.this.getDialog().cancel())
                .setNegativeButton(R.string.close_app, (dialogInterface, i) -> {
                    Intent closeIntent = new Intent(Intent.ACTION_MAIN);
                    closeIntent.addCategory(Intent.CATEGORY_HOME);
                    closeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(closeIntent);
                });
        return builder.create();
    }
}
