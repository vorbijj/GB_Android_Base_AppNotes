package com.example.gb_android_base_appnotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.gb_android_base_appnotes.ui.NoteAdapter;

public class TitleFragment extends Fragment {
    public static final String SHARED_PREFERENCE_NAME = "SaveSelection";
    public static final String SHARED_PREFERENCE_INDEX = "IndexNote";
    public static final String CURRENT_NOTE = "CurrentNote";
    public static int INDEX_NOTE;
    private Note currentNote;
    private boolean isLandscape;
    private ManageFragment manFrag;

    public static TitleFragment newInstance() {
        return new TitleFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_title, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_lines);
        String[] data = getResources().getStringArray(R.array.titles);
        initRecyclerView(recyclerView, data);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void initRecyclerView(RecyclerView recyclerView, String[] data) {
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        final NoteAdapter adapter = new NoteAdapter(data);
        recyclerView.setAdapter(adapter);

        adapter.SetOnItemClickListener(new NoteAdapter.OnItemClickListener(){
            public void onItemClick(View view, int index) {
                currentNote = new Note(index,
                        getResources().getStringArray(R.array.titles)[index],
                        getResources().getStringArray(R.array.date)[index],
                        getResources().getStringArray(R.array.description)[index]);
                showNote(currentNote);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        manFrag =  (ManageFragment) requireActivity();
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

        getSelectionNote();

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

        saveSelection(currentNote);

        FragmentManager fragmentManager = requireActivity()
                .getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_note, detail);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    private void showPortNote(Note currentNote) {
        saveSelection(currentNote);
        manFrag.replaceFragment(currentNote);
    }

    private void saveSelection(Note currentNote) {
        SharedPreferences sharedPref = requireActivity()
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(SHARED_PREFERENCE_INDEX, currentNote.getIndexNote());
        editor.commit();
    }

    private void getSelectionNote() {
        SharedPreferences sharedPref = requireActivity()
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

        INDEX_NOTE = sharedPref.getInt(SHARED_PREFERENCE_INDEX, currentNote.getIndexNote());

        if (INDEX_NOTE >= 0) {
            currentNote = new Note (INDEX_NOTE,
                    getResources().getStringArray(R.array.titles)[INDEX_NOTE],
                    getResources().getStringArray(R.array.date)[INDEX_NOTE],
                    getResources().getStringArray(R.array.description)[INDEX_NOTE]);
        }

    }

}