package com.example.gb_android_base_appnotes.data;

import android.content.res.Resources;

import com.example.gb_android_base_appnotes.R;

import java.util.ArrayList;
import java.util.List;

public class CardsSourceImpl implements CardsSource{
    private List<CardNote> dataSource;
    private Resources resources;

    public CardsSourceImpl(Resources resources) {
        dataSource = new ArrayList<>(7);
        this.resources = resources;
    }

    public CardsSourceImpl init() {
        String[] title = resources.getStringArray(R.array.titles);
        String[] date = resources.getStringArray(R.array.date);
        String[] description = resources.getStringArray(R.array.description);

        for (int i = 0; i < title.length; i++) {
            dataSource.add(new CardNote(i, title[i], date[i], description[i], false));
        }
        return this;
    }

    @Override
    public CardNote getNoteData(int position) {
        return dataSource.get(position);
    }

    @Override
    public int size() {
        return dataSource.size();
    }
}
