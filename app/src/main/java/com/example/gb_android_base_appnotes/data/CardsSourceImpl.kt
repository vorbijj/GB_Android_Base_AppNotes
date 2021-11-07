package com.example.gb_android_base_appnotes.data

import android.content.res.Resources
import com.example.gb_android_base_appnotes.R
import java.util.*

class CardsSourceImpl(resources: Resources) : CardsSource {
    private val dataSource: MutableList<CardNote?>
    private val resources: Resources
    override fun init(cardsSourceResponse: CardsSourceResponse?): CardsSourceImpl? {
        val title = resources.getStringArray(R.array.titles)
        val description = resources.getStringArray(R.array.description)
        for (i in title.indices) {
            dataSource.add(CardNote(title[i], Calendar.getInstance().time, description[i], false))
        }
        cardsSourceResponse?.initialized(this)
        return this
    }

    override fun getNoteData(position: Int): CardNote? {
        return dataSource[position]
    }

    override fun size(): Int {
        return dataSource.size
    }

    override fun deleteCardNote(position: Int) {
        dataSource.removeAt(position)
    }

    override fun updateCardNote(position: Int, cardNote: CardNote?) {
        dataSource[position] = cardNote
    }

    override fun addCardNote(cardNote: CardNote?) {
        dataSource.add(cardNote)
    }

    override fun clearCardNote() {
        dataSource.clear()
    }

    init {
        dataSource = ArrayList(7)
        this.resources = resources
    }
}