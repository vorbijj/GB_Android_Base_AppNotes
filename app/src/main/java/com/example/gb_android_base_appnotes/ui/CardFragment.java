package com.example.gb_android_base_appnotes.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.gb_android_base_appnotes.MainActivity;
import com.example.gb_android_base_appnotes.R;
import com.example.gb_android_base_appnotes.data.CardNote;
import com.example.gb_android_base_appnotes.data.CardsSource;
import com.example.gb_android_base_appnotes.data.CardsSourceImpl;
import com.example.gb_android_base_appnotes.observe.Observer;
import com.example.gb_android_base_appnotes.observe.Publisher;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;

public class CardFragment extends Fragment {

    private static final String ARG_CARD_NOTE = "Param_CardNote";

    private CardNote cardNote;
    private Publisher publisher;

    private TextInputEditText title;
    private TextInputEditText description;
    private DatePicker datePicker;

    public static CardFragment newInstance(CardNote cardNote) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CARD_NOTE, cardNote);
        fragment.setArguments(args);
        return fragment;
    }

    public static CardFragment newInstance() {
        CardFragment fragment = new CardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cardNote = getArguments().getParcelable(ARG_CARD_NOTE);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity)context;
        publisher = activity.getPublisher();
    }

    @Override
    public void onDetach() {
        publisher = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        setHasOptionsMenu(true);
        initView(view);
        if (cardNote != null) {
            populateView();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_back, menu);
        menu.removeItem(R.id.action_search);
        menu.removeItem(R.id.action_favorite);
        menu.removeItem(R.id.action_sort);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_back:
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                fm.popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        cardNote = collectCardNote();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        publisher.notifySingle(cardNote);
    }

    private CardNote collectCardNote(){
        String title = this.title.getText().toString();
        String description = this.description.getText().toString();
        Date date = getDateFromDatePicker();

        CardNote answer;
        boolean like;

        if (cardNote != null){
            like = cardNote.isLike();
            answer = new CardNote(title, date, description, like);
            answer.setId(cardNote.getId());
        } else {
            like = false;
            answer = new CardNote(title, date, description, like);
        }
        return answer;
    }

    private Date getDateFromDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, this.datePicker.getYear());
        cal.set(Calendar.MONTH, this.datePicker.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, this.datePicker.getDayOfMonth());
        return cal.getTime();
    }

    private void initView(View view) {
        title = view.findViewById(R.id.inputTitle);
        description = view.findViewById(R.id.inputDescription);
        datePicker = view.findViewById(R.id.inputDate);
    }

    private void populateView(){
        title.setText(cardNote.getTitle());
        description.setText(cardNote.getDescription());
        initDatePicker(cardNote.getDate());
    }

    private void initDatePicker(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        this.datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                null);
    }
}