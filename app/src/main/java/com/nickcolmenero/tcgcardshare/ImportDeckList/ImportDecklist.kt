package com.nickcolmenero.tcgcardshare.ImportDeckList

import android.util.Log
import androidx.navigation.NavController
import com.google.firebase.firestore.DocumentReference
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel
import java.io.File

fun importDecklist(
    file: File,
    source: String,
    viewModel: DeckListViewModel,
    decklistRef: DocumentReference,
    uniqueDeckID: String,
    navController: NavController,
    deckName:String,
    deckFormat:String
) {
    val cards = parseDecklist(file, source)
    val groupedDecklist = cards.groupingBy { it.name }.eachCount()
    val deckString = groupedDecklist.entries.joinToString(", ") { (cardName, count) ->
        "[$cardName x$count]"
    }

    val deckData = mapOf(
        "DeckName" to deckName,
        "decklist" to deckString,
        "format" to deckFormat,
        "deckID" to uniqueDeckID
    )
    decklistRef.set(deckData)
        .addOnSuccessListener {
            Log.d("Firestore", "Decklist imported successfully.")
            navController.navigate("home")
        }
        .addOnFailureListener { exception ->
            Log.e("Firestore", "Failed to import decklist: ${exception.message}")
        }
}
