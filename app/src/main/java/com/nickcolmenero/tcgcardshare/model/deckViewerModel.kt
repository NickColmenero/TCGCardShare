package com.nickcolmenero.tcgcardshare.model

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

data class DeckViewerModel(
    val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    val currentUser: String = Firebase.auth.currentUser?.uid.orEmpty().take(8),
    val id: String ="",
    val deckName:String = "",
    val deckFormat: String = "",
    val rawCards: String = ""
)