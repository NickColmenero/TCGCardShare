package com.nickcolmenero.tcgcardshare.ImportDeckList

import android.util.Log
import com.google.gson.Gson
import java.io.File

fun parseDecklist(file: File, source: String): List<Cardclass> {
    return when (source) {
        "Moxfield" -> parseMoxfieldFile(file)
        "Archidect" -> parseArchidectFile(file)
        "TappedOut" -> parseTappedOutFile(file)
        else -> emptyList()
    }
}


fun parseArchidectFile(file: File): List<Cardclass> {
    val lines = file.readLines()
    return lines.mapNotNull { line ->
        val regex = Regex("""(\d+)x (.*?) \(""")
        val matchResult = regex.find(line)
        matchResult?.let {
            val (quantityStr, name) = it.destructured
            Cardclass(name.trim(), quantityStr.toInt())
        }
    }
}


fun parseMoxfieldFile(file: File): List<Cardclass> {
    val lines = file.readLines()
    return lines.mapNotNull { line ->
        val regex = Regex("""^(\d+)\s+(.+)$""")
        val matchResult = regex.find(line.trim())
        matchResult?.let {
            val (quantityStr, name) = it.destructured
            Cardclass(name.trim(), quantityStr.toInt())
        }
    }
}

fun parseTappedOutFile(file: File): List<Cardclass> {
    val lines = file.readLines()
    return lines.mapNotNull { line ->
        val cleanLine = line.removePrefix("SB:").trim()
        val regex = Regex("""^(\d+)\s+(.+)$""")
        val matchResult = regex.find(cleanLine)
        matchResult?.let {
            val (quantityStr, name) = it.destructured
            Log.e("Cards imported as objects","${Cardclass(name,quantityStr.toInt())}")
            Cardclass(name.trim(), quantityStr.toInt())

        }
    }
}



data class Cardclass(val name: String, val quantity: Int)

data class MoxfieldDeck(val cards: List<MoxfieldCard>)
data class MoxfieldCard(val name: String, val quantity: Int)

data class ArchidectDeck(val cards: List<ArchidectCard>)
data class ArchidectCard(val name: String, val quantity: Int)

data class TappedOutDeck(val cards: List<TappedOutCard>)
data class TappedOutCard(val name: String, val quantity: Int)
