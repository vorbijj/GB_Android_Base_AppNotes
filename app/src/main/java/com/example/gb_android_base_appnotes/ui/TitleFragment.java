package com.example.gb_android_base_appnotes.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;


import com.example.gb_android_base_appnotes.MainActivity;
import com.example.gb_android_base_appnotes.R;
import com.example.gb_android_base_appnotes.data.CardNote;
import com.example.gb_android_base_appnotes.data.CardsSource;
import com.example.gb_android_base_appnotes.data.CardsSourceFirebaseImpl;
import com.example.gb_android_base_appnotes.data.CardsSourceResponse;
import com.example.gb_android_base_appnotes.observe.Observer;
import com.example.gb_android_base_appnotes.observe.Publisher;

public class TitleFragment extends Fragment {
    private static final int MY_DEFAULT_DURATION = 1000;
    public static final String CURRENT_NOTE = "CurrentNote";
    private CardNote currentCardNote;
    private boolean isLandscape;
    private CardsSource data;
    private NoteAdapter adapter;
    private Publisher publisher;
    private MainActivity activity;

    private boolean moveToFirstPosition;

    public static TitleFragment newInstance() {
        return new TitleFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_title, container, false);
        setHasOptionsMenu(true);
        initRecyclerView(view);

        data = new CardsSourceFirebaseImpl().init(new CardsSourceResponse() {
            @Override
            public void initialized(CardsSource cardsNote) {
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setDataSource(data);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_lines);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NoteAdapter(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.separator));
        recyclerView.addItemDecoration(itemDecoration);

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(MY_DEFAULT_DURATION);
        animator.setRemoveDuration(MY_DEFAULT_DURATION);
        recyclerView.setItemAnimator(animator);

        if (moveToFirstPosition && data.size() > 0){
            recyclerView.scrollToPosition(0);
            moveToFirstPosition = false;
        }

        adapter.SetOnItemClickListener(new NoteAdapter.OnItemClickListener(){
            public void onItemClick(View view, int position) {
                currentCardNote = new CardNote(data.getNoteData(position).getTitle(),
                        data.getNoteData(position).getDate(),
                        data.getNoteData(position).getDescription(),
                        data.getNoteData(position).isLike());
                showNote(currentCardNote);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        activity = (MainActivity)context;
        publisher = activity.getPublisher();

        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

    }

    @Override
    public void onDetach() {
        activity = null;
        publisher = null;
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_cards, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return onItemSelected(item.getItemId()) || super.onOptionsItemSelected(item);
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
        }
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
        return onItemSelected(item.getItemId()) || super.onContextItemSelected(item);
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

        FragmentManager fragmentManager = requireActivity()
                .getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_note, detail);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    private void showPortNote(CardNote currentCardNote) {
        replaceFragment(currentCardNote);
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

    private boolean onItemSelected(int menuItem) {
        switch (menuItem) {
            case R.id.action_add:
               addFragment(CardFragment.newInstance(), true);
                publisher.subscribe(new Observer() {
                    @Override
                    public void updateCardNote(CardNote cardNote) {
                        data.addCardNote(cardNote);
                        adapter.notifyItemInserted(data.size() - 1);
                        moveToFirstPosition = true;
                    }
                });
                return true;
            case R.id.action_update:
                int updatePosition = adapter.getMenuPosition();
                addFragment(CardFragment.newInstance(data.getNoteData(updatePosition)), true);
                publisher.subscribe(new Observer() {
                    @Override
                    public void updateCardNote(CardNote cardNote) {
                        data.updateCardNote(updatePosition, cardNote);
                        adapter.notifyItemChanged(updatePosition);
                    }
                });
                showEmptyNote();
                return true;
            case R.id.action_delete:
                int deletePosition = adapter.getMenuPosition();
                openDeleteDialog(deletePosition);
                return true;
            case R.id.action_clear:
                openClearDialog();
                return true;
        }
        return false;
    }

    private void openDeleteDialog(int deletePosition) {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(activity);
        deleteDialog.setTitle(R.string.exclamation)
                .setMessage(R.string.delete_question)
                .setIcon(R.drawable.ic_baseline_alert)
                .setCancelable(false)
                .setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(activity, R.string.text_no_, Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton(R.string.text_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(activity, R.string.text_yes_, Toast.LENGTH_SHORT).show();
                                data.deleteCardNote(deletePosition);
                                adapter.notifyItemRemoved(deletePosition);
                                showEmptyNote();
                            }
                        });
        AlertDialog alert = deleteDialog.create();
        alert.show();
    }

    private void openClearDialog() {
        AlertDialog.Builder clearDialog = new AlertDialog.Builder(activity);
        clearDialog.setTitle(R.string.exclamation)
                .setMessage(R.string.clear_question)
                .setIcon(R.drawable.ic_baseline_alert)
                .setCancelable(false)
                .setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(activity, R.string.text_no_, Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton(R.string.text_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(activity, R.string.text_yes_, Toast.LENGTH_SHORT).show();
                        data.clearCardNote();
                        adapter.notifyDataSetChanged();
                        showEmptyNote();
                    }
                });
        AlertDialog alert = clearDialog.create();
        alert.show();
    }

    private void addFragment(Fragment fragment, boolean useBackStack){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (currentFragment != null) {
            fragmentTransaction.remove(currentFragment);
        }
        fragmentTransaction.add(R.id.fragment_container, fragment);

        if (useBackStack) {
            int count = fragmentManager.getBackStackEntryCount();
            if (count == 0) {
                fragmentTransaction.addToBackStack(null);
            }
        }
        fragmentTransaction.commit();
    }

    private void replaceFragment(CardNote currentCardNote) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        NoteFragment detail = NoteFragment.newInstance(currentCardNote);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        fragmentTransaction.remove(currentFragment);

        fragmentTransaction.add(R.id.fragment_container, detail);

        int count = fragmentManager.getBackStackEntryCount();
        if (count == 0) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();
    }
}