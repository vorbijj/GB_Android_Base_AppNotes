package com.example.gb_android_base_appnotes.observe;

import com.example.gb_android_base_appnotes.data.CardNote;

import java.util.ArrayList;
import java.util.List;

public class Publisher {
    private List<Observer> observers;

    public Publisher() {
        observers = new ArrayList<>();
    }

    public void subscribe(Observer observer) {
        observers.add(observer);
    }

    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    public void notifySingle(CardNote cardNote) {
        for (Observer observer : observers) {
            observer.updateCardNote(cardNote);
            unsubscribe(observer);
        }
    }
}
