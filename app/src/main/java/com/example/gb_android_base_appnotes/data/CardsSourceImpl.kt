package com.example.gb_android_base_appnotes.data;

import android.content.res.Resources;

import com.example.gb_android_base_appnotes.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CardsSourceImpl implements CardsSource {
    private List<CardNote> dataSource;
    private Resources resources;

    public CardsSourceImpl(Resources resources) {
        dataSource = new ArrayList<>(7);
        this.resources = resources;
    }

    public CardsSourceImpl init(CardsSourceResponse cardsSourceResponse) {
        String[] title = resources.getStringArray(R.array.titles);
        String[] description = resources.getStringArray(R.array.description);

        for (int i = 0; i < title.length; i++) {
            dataSource.add(new CardNote(title[i], Calendar.getInstance().getTime(), description[i], false));
        }

        if (cardsSourceResponse != null) {
            cardsSourceResponse.initialized(this);
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

    @Override
    public void deleteCardNote(int position) {
        dataSource.remove(position);
    }

    @Override
    public void updateCardNote(int position, CardNote cardNote) {
        dataSource.set(position, cardNote);
    }

    @Override
    public void addCardNote(CardNote cardNote) {
        dataSource.add(cardNote);
    }

    @Override
    public void clearCardNote() {
        dataSource.clear();
    }
}
