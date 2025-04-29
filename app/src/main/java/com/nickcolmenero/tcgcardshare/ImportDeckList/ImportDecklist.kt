package com.nickcolmenero.tcgcardshare.ImportDeckList

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import com.google.firebase.firestore.DocumentReference
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

fun importDecklist(
    file: File,
    source: String,
    viewModel: DeckListViewModel,
    decklistRef: DocumentReference,
    uniqueDeckID: String,
    navController: NavController,
    deckName: String,
    deckFormat: String,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    if (source !in listOf("Moxfield", "Archidect", "TappedOut")) {
        Log.e("DecklistSource", "Invalid source selected. Please choose a valid source.")
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Invalid source selected. Please choose a valid source.")
        }
        return
    }

    val cards = parseDecklist(file, source)

    val isValidFormat = cards.all { card ->
        val regex = Regex("""\[[^\]]+\s+x\d+\]""")
        regex.matches("[${card.name} x${card.quantity}]")
    }

    if (!isValidFormat) {
        Log.e("DecklistFormat", "Invalid format in decklist file. Expected format: [Cardname xN]")
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Decklist format is invalid. Expected format: [Cardname xN]")
        }
        return
    }

    val cardMap = mutableMapOf<String, Int>()
    cards.forEach { card ->
        cardMap[card.name] = cardMap.getOrDefault(card.name, 0) + card.quantity
    }

    if (cardMap.isEmpty()) {
        Log.e("Decklist", "Decklist is empty or you choose the wrong website")
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Decklist is empty or invalid.")
        }
        return
    }
    val deckString = cardMap.entries.joinToString(", ") { (cardName, count) ->
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
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Failed to upload decklist. Please try again.")
            }
        }
}
