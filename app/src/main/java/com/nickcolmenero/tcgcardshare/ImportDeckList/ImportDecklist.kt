package com.nickcolmenero.tcgcardshare.ImportDeckList

import android.util.Log
import androidx.navigation.NavController
import com.google.firebase.firestore.DocumentReference
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel
import java.io.File

fun importDecklist(
    file: File,                      // If using a file picker, ensure you convert Uri to File if needed.
    source: String,
    viewModel: DeckListViewModel,    // Possibly used in your parseDecklist or further processing.
    decklistRef: DocumentReference,
    uniqueDeckID: String,
    navController: NavController
) {
    // Parse the deck list from the file based on the selected source.
    val cards = parseDecklist(file, source)

    // Group cards by name and count occurrences.
    val groupedDecklist = cards.groupingBy { it.name }.eachCount()

    // Build a string representing the decklist.
    val deckString = groupedDecklist.entries.joinToString(", ") { (cardName, count) ->
        "[$cardName x$count]"
    }

    // Prepare the map to upload to Firestore.
    val deckData = mapOf(
        "DeckName" to "Imported Deck",  // Consider allowing the user to change this.
        "decklist" to deckString,
        "format" to "Standard",         // Or let the user select the format.
        "deckID" to uniqueDeckID
    )

    // Upload the deck data to Firestore.
    decklistRef.set(deckData)
        .addOnSuccessListener {
            Log.d("Firestore", "Decklist imported successfully.")
            // Navigate to the home screen on success.
            navController.navigate("home")
        }
        .addOnFailureListener { exception ->
            Log.e("Firestore", "Failed to import decklist: ${exception.message}")
            // Optionally, add user feedback for failures.
        }
}
