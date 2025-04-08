package com.nickcolmenero.tcgcardshare.viewDeck
// THIS IS WHAT I AM CURRENTLY WORKIN ON!
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.nickcolmenero.tcgcardshare.model.DeckViewerModel

@Composable
fun ViewDeckListTab(
    navController: NavController,
    deckOwnerID: String,
    decklistID: String,
) {
    val currentUser = Firebase.auth.currentUser?.uid.orEmpty().take(8)
    val db = FirebaseFirestore.getInstance()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var deckList by remember { mutableStateOf(listOf<DeckViewerModel>()) }
    var isLoading by remember { mutableStateOf(true) }

    Log.d("Debug", "deckOwnerID: ${deckOwnerID}")
    Log.d("Debug", "decklistID: ${decklistID}")

    if (deckOwnerID.isNotEmpty() && decklistID.isNotEmpty()) {
        val docRef = db.collection("users")
            .document(deckOwnerID)
            .collection("decks")
            .document(decklistID)

        Log.d("Firestore", "Fetching data from: ${docRef.path}")

        LaunchedEffect(decklistID) {
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val deck = DeckViewerModel(
                            id = document.id,
                            deckName = document.getString("DeckName") ?: "",
                            deckFormat = document.getString("format") ?: "",
                            rawCards = document.getString("decklist") ?: ""
                        )
                        deckList = listOf(deck)
                        isLoading = false
                        Log.e("firebase", "Data was loaded: $deck")
                    } else {
                        Log.e("firebase", "Document does not exist!")
                        isLoading = false
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("firebase", "Error retrieving document: ${exception.message}")
                    isLoading = false
                }
        }
    } else {
        Log.e("firebase", "Invalid deckOwnerID or decklistID. Cannot fetch data.")
    }
    //cosmetic front end stuff deal with after backend works.
    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Column {
            deckList.forEach { deck ->
                Text(text = "Deck Name: ${deck.deckName}")
                Text(text = "Deck Format: ${deck.deckFormat}")
            }
        }
    }
}
