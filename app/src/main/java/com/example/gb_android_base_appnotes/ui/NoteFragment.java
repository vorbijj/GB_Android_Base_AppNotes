package com.example.gb_android_base_appnotes.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gb_android_base_appnotes.R;
import com.example.gb_android_base_appnotes.data.CardNote;

import java.text.SimpleDateFormat;

public class NoteFragment extends Fragment {

    public static final String ARG_NOTE = "note";
    private CardNote cardNote;

    public static NoteFragment newInstance(CardNote cardNote) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NOTE, cardNote);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cardNote = getArguments().getParcelable(ARG_NOTE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_note, container, false);

        TextView titleView = view.findViewById(R.id.textView_title);
        titleView.setText(cardNote.getTitle());

        TextView timeView = view.findViewById(R.id.textView_date);
        timeView.setText(new SimpleDateFormat("dd-MM-yy").format(cardNote.getDate()));

        TextView descriptionView = view.findViewById(R.id.textView_description);
        descriptionView.setText(cardNote.getDescription());

        return view;
    }
}