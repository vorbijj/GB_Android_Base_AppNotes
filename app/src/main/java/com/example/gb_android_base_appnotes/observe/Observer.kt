package com.example.gb_android_base_appnotes.observe

import com.example.gb_android_base_appnotes.data.CardNote

interface Observer {
    fun updateCardNote(cardNote: CardNote?)
}