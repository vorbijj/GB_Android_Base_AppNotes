package com.example.gb_android_base_appnotes.data

import com.google.firebase.Timestamp
import java.util.*

object CardNoteMapping {
    @JvmStatic
    fun toCardNote(id: String?, doc: Map<String?, Any?>): CardNote {
        val timeStamp = doc[Fields.DATE] as Timestamp?
        val answer = CardNote(doc[Fields.TITLE] as String?,
                timeStamp!!.toDate(),
                doc[Fields.DESCRIPTION] as String?,
                doc[Fields.LIKE] as Boolean)
        answer.id = id
        return answer
    }

    @JvmStatic
    fun toDocument(cardNote: CardNote): Map<String, Any?> {
        val answer: MutableMap<String, Any?> = HashMap()
        answer[Fields.TITLE] = cardNote.title
        answer[Fields.DESCRIPTION] = cardNote.description
        answer[Fields.LIKE] = cardNote.isLike
        answer[Fields.DATE] = cardNote.date
        return answer
    }

    object Fields {
        const val TITLE = "title"
        const val DATE = "date"
        const val DESCRIPTION = "description"
        const val LIKE = "like"
    }
}