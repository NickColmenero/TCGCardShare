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
    val lines = file.readLines()
    return lines.mapNotNull { line ->
        val regex = Regex("""(\d+)x (.*?) \(""") // Matches '1x Card Name ('
        val matchResult = regex.find(line)
        matchResult?.let {
            val (quantityStr, name) = it.destructured
            Card(name.trim(), quantityStr.toInt())
        }
    }
}


fun parseArchidectFile(file: File): List<Card> {
    val lines = file.readLines()
    return lines.mapNotNull { line ->
        val regex = Regex("""(\d+)x (.*?) \(""")
        val matchResult = regex.find(line)
        matchResult?.let {
            val (quantityStr, name) = it.destructured
            Card(name.trim(), quantityStr.toInt())
        }
    }
}


fun parseTappedOutFile(file: File): List<Card> {
    val lines = file.readLines()
    return lines.mapNotNull { line ->
        val regex = Regex("""(\d+)x (.*?)($|$)""") // TappedOut might just be '2x Card Name'
        val matchResult = regex.find(line)
        matchResult?.let {
            val (quantityStr, name) = it.destructured
            Card(name.trim(), quantityStr.toInt())
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
