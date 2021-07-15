package com.example.gb_android_base_appnotes;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TitleFragment extends Fragment {
    public static final String CURRENT_NOTE = "CurrentNote";
    private Note currentNote;
    private boolean isLandscape;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_title, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initlist((LinearLayout) view);
    }

    private void initlist(LinearLayout view) {

        String[] titles = getResources().getStringArray(R.array.titles);

        for (int i = 0; i < titles.length; i++) {
            String title = titles[i];
            TextView tv = new TextView(getContext());
            tv.setText(title);
            tv.setTextSize(30);
            tv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            view.addView(tv);
            final int index = i;
            tv.setOnClickListener(v -> {
                currentNote = new Note(index,
                        getResources().getStringArray(R.array.titles)[index],
                        getResources().getStringArray(R.array.date)[index],
                        getResources().getStringArray(R.array.description)[index]);
                showNote(currentNote);
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(CURRENT_NOTE, currentNote);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            currentNote = savedInstanceState.getParcelable(CURRENT_NOTE);
        } else {
            currentNote = new Note (0,
                    getResources().getStringArray(R.array.titles)[0],
                    getResources().getStringArray(R.array.date)[0],
                    getResources().getStringArray(R.array.description)[0]);
        }

        if (isLandscape){
            showLandNote(currentNote);
        }
    }

    private void showNote(Note currentNote) {
        if (isLandscape) {
            showLandNote(currentNote);
        } else {
            showPortNote(currentNote);
        }
    }

    private void showLandNote(Note currentNote) {
        NoteFragment detail = NoteFragment.newInstance(currentNote);

        FragmentManager fragmentManager = requireActivity()
                .getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.note, detail);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();

    }

    private void showPortNote(Note currentNote) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), NoteActivity.class);
        intent.putExtra(NoteFragment.ARG_NOTE, currentNote);
        startActivity(intent);
    }

}