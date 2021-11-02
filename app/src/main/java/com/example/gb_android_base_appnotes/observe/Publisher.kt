package com.example.gb_android_base_appnotes.observe

import com.example.gb_android_base_appnotes.data.CardNote
import java.util.*

class Publisher {
    private val observers: MutableList<Observer>
    fun subscribe(observer: Observer) {
        observers.add(observer)
    }

    fun unsubscribe(observer: Observer) {
        observers.remove(observer)
    }

    fun notifySingle(cardNote: CardNote?) {
        for (observer in observers) {
            observer.updateCardNote(cardNote)
            unsubscribe(observer)
        }
    }

    init {
        observers = ArrayList()
    }
}