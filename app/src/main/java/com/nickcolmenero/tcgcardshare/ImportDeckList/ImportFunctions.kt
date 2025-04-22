package com.nickcolmenero.tcgcardshare.ImportDeckList

import com.google.gson.Gson
import java.io.File

fun parseDecklist(file: File, source: String): List<Card> {
    return when (source) {
        "Moxfield" -> parseMoxfieldFile(file)
        "Archidect" -> parseArchidectFile(file)
        "TappedOut" -> parseTappedOutFile(file)
        else -> emptyList()
    }
}

fun parseMoxfieldFile(file: File): List<Card> {
    // Parse the Moxfield JSON file (you may need a JSON parser like Moshi or Gson)
    val json = file.readText()
    val moxfieldDeck = Gson().fromJson(json,MoxfieldDeck::class.java)
    return moxfieldDeck?.cards?.map { Card(it.name, it.quantity) } ?: emptyList()
}

fun parseArchidectFile(file: File): List<Card> {
    // Parse the Archidect JSON file
    val json = file.readText()
    val archidectDeck = Gson().fromJson(json, ArchidectDeck::class.java)
    return archidectDeck.cards.map { Card(it.name, it.quantity) }
}

fun parseTappedOutFile(file: File): List<Card> {
    // Parse the TappedOut plain text or CSV format
    val lines = file.readLines()
    return lines.mapNotNull { line ->
        val parts = line.split(" x")
        if (parts.size == 2) {
            Card(parts[0].trim(), parts[1].toInt())
        } else {
            null
        }
    }
}

data class Card(val name: String, val quantity: Int)

data class MoxfieldDeck(val cards: List<MoxfieldCard>)
data class MoxfieldCard(val name: String, val quantity: Int)

data class ArchidectDeck(val cards: List<ArchidectCard>)
data class ArchidectCard(val name: String, val quantity: Int)

data class TappedOutDeck(val cards: List<TappedOutCard>)
data class TappedOutCard(val name: String, val quantity: Int)
