package com.example.gb_android_base_appnotes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NoteFragment extends Fragment {

    public static final String ARG_NOTE = "note";
    private Note note;

    public static NoteFragment newInstance(Note note) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NOTE, note);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            note = getArguments().getParcelable(ARG_NOTE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_note, container, false);

        TextView titleView = view.findViewById(R.id.textView_title);
        titleView.setText(note.getTitle());

        TextView timeView = view.findViewById(R.id.textView_date);
        timeView.setText(note.getDate());

        TextView descriptionView = view.findViewById(R.id.textView_description);
        descriptionView.setText(note.getDescription());

        return view;
    }
}