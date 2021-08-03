package com.example.gb_android_base_appnotes.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.gb_android_base_appnotes.R;
import com.example.gb_android_base_appnotes.data.CardNote;
import com.example.gb_android_base_appnotes.data.CardsSource;
import com.example.gb_android_base_appnotes.data.CardsSourceImpl;

public class TitleFragment extends Fragment {
    public static final String SHARED_PREFERENCE_NAME = "SaveSelection";
    public static final String SHARED_PREFERENCE_INDEX = "IndexNote";
    public static final String CURRENT_NOTE = "CurrentNote";
    public static int INDEX_NOTE;
    private CardNote currentCardNote;
    private boolean isLandscape;
    private ManageFragment manFrag;

    public static TitleFragment newInstance() {
        return new TitleFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_title, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_lines);
        CardsSource data = new CardsSourceImpl(getResources()).init();
        initRecyclerView(recyclerView, data);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void initRecyclerView(RecyclerView recyclerView, CardsSource data) {
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        final NoteAdapter adapter = new NoteAdapter(data);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.separator));
        recyclerView.addItemDecoration(itemDecoration);


        adapter.SetOnItemClickListener(new NoteAdapter.OnItemClickListener(){
            public void onItemClick(View view, int index) {
                currentCardNote = new CardNote(index,
                        getResources().getStringArray(R.array.titles)[index],
                        getResources().getStringArray(R.array.date)[index],
                        getResources().getStringArray(R.array.description)[index],false);
                showNote(currentCardNote);
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
        outState.putParcelable(CURRENT_NOTE, currentCardNote);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            currentCardNote = savedInstanceState.getParcelable(CURRENT_NOTE);
        } else {
            currentCardNote = new CardNote(0,
                    getResources().getStringArray(R.array.titles)[0],
                    getResources().getStringArray(R.array.date)[0],
                    getResources().getStringArray(R.array.description)[0],
                    false);
        }

        getSelectionNote();

        if (isLandscape){
            showLandNote(currentCardNote);
        }
    }

    private void showNote(CardNote currentCardNote) {
        if (isLandscape) {
            showLandNote(currentCardNote);
        } else {
            showPortNote(currentCardNote);
        }
    }

    private void showLandNote(CardNote currentCardNote) {
        NoteFragment detail = NoteFragment.newInstance(currentCardNote);

        saveSelection(currentCardNote);

        FragmentManager fragmentManager = requireActivity()
                .getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_note, detail);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    private void showPortNote(CardNote currentCardNote) {
        saveSelection(currentCardNote);
        manFrag.replaceFragment(currentCardNote);
    }

    private void saveSelection(CardNote currentCardNote) {
        SharedPreferences sharedPref = requireActivity()
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(SHARED_PREFERENCE_INDEX, currentCardNote.getIndexNote());
        editor.commit();
    }

    private void getSelectionNote() {
        SharedPreferences sharedPref = requireActivity()
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

        INDEX_NOTE = sharedPref.getInt(SHARED_PREFERENCE_INDEX, currentCardNote.getIndexNote());

        if (INDEX_NOTE >= 0) {
            currentCardNote = new CardNote(INDEX_NOTE,
                    getResources().getStringArray(R.array.titles)[INDEX_NOTE],
                    getResources().getStringArray(R.array.date)[INDEX_NOTE],
                    getResources().getStringArray(R.array.description)[INDEX_NOTE],
                    false);
        }

    }

}