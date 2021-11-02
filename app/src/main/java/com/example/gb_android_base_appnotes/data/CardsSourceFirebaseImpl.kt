package com.example.gb_android_base_appnotes.data

import android.util.Log
import com.example.gb_android_base_appnotes.data.CardNoteMapping.toCardNote
import com.example.gb_android_base_appnotes.data.CardNoteMapping.toDocument
import com.google.firebase.firestore.*
import java.util.*

class CardsSourceFirebaseImpl : CardsSource {
    private val store = FirebaseFirestore.getInstance()
    private val collection = store.collection(CARDS_COLLECTION)
    private var cardsNote: MutableList<CardNote>? = ArrayList()
    override fun init(cardsSourceResponse: CardsSourceResponse?): CardsSource? {
        collection.orderBy(CardNoteMapping.Fields.DATE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        cardsNote = ArrayList()
                        for (document in task.result!!) {
                            val doc = document.data
                            val id = document.id
                            val cardNote = toCardNote(id, doc)
                            (cardsNote as ArrayList<CardNote>).add(cardNote)
                        }
                        Log.d(TAG, "success " + (cardsNote as ArrayList<CardNote>).size + " qnt")
                        cardsSourceResponse!!.initialized(this@CardsSourceFirebaseImpl)
                    } else {
                        Log.d(TAG, "get failed with ", task.exception)
                    }
                }
                .addOnFailureListener { e -> Log.d(TAG, "get failed with ", e) }
        return this
    }

    override fun getNoteData(position: Int): CardNote? {
        return cardsNote!![position]
    }

    override fun size(): Int {
        return if (cardsNote == null) {
            0
        } else cardsNote!!.size
    }

    override fun deleteCardNote(position: Int) {
        collection.document(cardsNote!![position].id!!).delete()
        cardsNote!!.removeAt(position)
    }

    override fun updateCardNote(position: Int, cardNote: CardNote?) {
        val id = cardNote!!.id
        collection.document(id!!).set(toDocument(cardNote))
    }

    override fun addCardNote(cardNote: CardNote?) {
        collection.add(toDocument(cardNote!!))
                .addOnSuccessListener { documentReference -> cardNote.id = documentReference.id }
    }

    override fun clearCardNote() {
        for (cardNote in cardsNote!!) {
            collection.document(cardNote.id!!).delete()
        }
        cardsNote = ArrayList()
    }

    companion object {
        private const val CARDS_COLLECTION = "cards"
        private const val TAG = "[FirebaseImpl]"
    }
}