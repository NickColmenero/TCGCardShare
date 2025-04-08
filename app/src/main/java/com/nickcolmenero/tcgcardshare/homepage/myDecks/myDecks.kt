package com.nickcolmenero.tcgcardshare.homepage.myDecks

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.nickcolmenero.tcgcardshare.model.DeckViewerModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDecks(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = Firebase.auth.currentUser?.uid.orEmpty().take(8)
    val deckOwnerID = Firebase.auth.currentUser?.uid.orEmpty().take(8)
    var deckList by remember { mutableStateOf(listOf<DeckViewerModel>()) }

    LaunchedEffect(Unit) {
        db.collection("users")
            .document(currentUser)
            .collection("decks")
            .get()
            .addOnSuccessListener { documents ->
                deckList = documents.map { document ->
                    DeckViewerModel(
                        id = document.id,
                        deckName =document.getString("DeckName")?:"",
                        deckFormat = document.getString("format") ?:"",
                        rawCards = document.getString("decklist") ?: "" // Fetch other fields if needed
                    )
                }
            }
    }
    DeckList(deckList = deckList,navController, deckOwnerID)
}




