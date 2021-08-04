package com.example.gb_android_base_appnotes.data;

public interface CardsSource {
    CardNote getNoteData(int position);
    int size();
    void deleteCardNote(int position);
    void updateCardNote(int position, CardNote cardNote);
    void addCardNote(CardNote cardNote);
    void clearCardNote();
}
