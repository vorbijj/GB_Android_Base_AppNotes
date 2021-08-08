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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.example.gb_android_base_appnotes.MainActivity;
import com.example.gb_android_base_appnotes.Navigation;
import com.example.gb_android_base_appnotes.R;
import com.example.gb_android_base_appnotes.data.CardNote;
import com.example.gb_android_base_appnotes.data.CardsSource;
import com.example.gb_android_base_appnotes.data.CardsSourceImpl;
import com.example.gb_android_base_appnotes.observe.Observer;
import com.example.gb_android_base_appnotes.observe.Publisher;

public class TitleFragment extends Fragment {
    private static final int MY_DEFAULT_DURATION = 1000;
    public static final String SHARED_PREFERENCE_NAME = "SaveSelection";
    public static final String SHARED_PREFERENCE_INDEX = "IndexNote";
    public static final String CURRENT_NOTE = "CurrentNote";
    public static int INDEX_NOTE;
    private CardNote currentCardNote;
    private boolean isLandscape;
    private ManageFragment manFrag;
    private CardsSource data;
    private NoteAdapter adapter;
    private RecyclerView recyclerView;
    private Navigation navigation;
    private Publisher publisher;
    private boolean moveToLastPosition;

    public static TitleFragment newInstance() {
        return new TitleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new CardsSourceImpl(getResources()).init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_title, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_lines);
        initView(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NoteAdapter(data, this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.separator));
        recyclerView.addItemDecoration(itemDecoration);

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(MY_DEFAULT_DURATION);
        animator.setRemoveDuration(MY_DEFAULT_DURATION);
        recyclerView.setItemAnimator(animator);

        if (moveToLastPosition){
            recyclerView.smoothScrollToPosition(data.size() - 1);
            moveToLastPosition = false;
        }

        adapter.SetOnItemClickListener(new NoteAdapter.OnItemClickListener(){
            public void onItemClick(View view, int index) {
                currentCardNote = new CardNote(index,
                        data.getNoteData(index).getTitle(),
                        data.getNoteData(index).getDate(),
                        data.getNoteData(index).getDescription(),
                        data.getNoteData(index).isLike());
                showNote(currentCardNote);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        MainActivity activity = (MainActivity)context;
        navigation = activity.getNavigation();
        publisher = activity.getPublisher();

        manFrag =  (ManageFragment) requireActivity();
        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

    }

    public void onDetach() {
        navigation = null;
        publisher = null;
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_cards, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                navigation.addFragment(CardFragment.newInstance(), true);
                publisher.subscribe(new Observer() {
                    @Override
                    public void updateCardNote(CardNote cardNote) {
                        data.addCardNote(cardNote);
                        adapter.notifyItemInserted(data.size() - 1);
                        moveToLastPosition = true;
                    }
                });
                showEmptyNote();
                return true;
            case R.id.action_clear:
                data.clearCardNote();
                adapter.notifyDataSetChanged();
                showEmptyNote();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_lines);
        data = new CardsSourceImpl(getResources()).init();
        initRecyclerView();
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
                    data.getNoteData(0).getTitle(),
                    data.getNoteData(0).getDate(),
                    data.getNoteData(0).getDescription(),
                    data.getNoteData(0).isLike());
        }

//        getSelectionNote();

        if (isLandscape){
            showLandNote(currentCardNote);
        }
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v,
                                    @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.card_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = adapter.getMenuPosition();
        switch(item.getItemId()) {
            case R.id.action_update:
                navigation.addFragment(CardFragment.newInstance(data.getNoteData(position)), true);
                publisher.subscribe(new Observer() {
                    @Override
                    public void updateCardNote(CardNote cardNote) {
                        data.updateCardNote(position, cardNote);
                        adapter.notifyItemChanged(position);
                    }
                });
                showEmptyNote();
                return true;
            case R.id.action_delete:
                data.deleteCardNote(position);
                adapter.notifyItemRemoved(position);
                showEmptyNote();
                return true;
        }
        return super.onContextItemSelected(item);
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

//        saveSelection(currentCardNote);

        FragmentManager fragmentManager = requireActivity()
                .getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_note, detail);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    private void showPortNote(CardNote currentCardNote) {
//        saveSelection(currentCardNote);
        manFrag.replaceFragment(currentCardNote);
    }

    private void showEmptyNote() {

        FragmentManager fragmentManager = requireActivity()
                .getSupportFragmentManager();
        Fragment detail = fragmentManager.findFragmentById(R.id.fragment_note);

        if(detail != null){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(detail);
            fragmentTransaction.commit();
        }
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
                    data.getNoteData(INDEX_NOTE).getTitle(),
                    data.getNoteData(INDEX_NOTE).getDate(),
                    data.getNoteData(INDEX_NOTE).getDescription(),
                    data.getNoteData(INDEX_NOTE).isLike());
        }

    }

}