package com.example.gb_android_base_appnotes.data

interface CardsSource {
    fun init(cardsSourceResponse: CardsSourceResponse?): CardsSource?
    fun getNoteData(position: Int): CardNote?
    fun size(): Int
    fun deleteCardNote(position: Int)
    fun updateCardNote(position: Int, cardNote: CardNote?)
    fun addCardNote(cardNote: CardNote?)
    fun clearCardNote()
}