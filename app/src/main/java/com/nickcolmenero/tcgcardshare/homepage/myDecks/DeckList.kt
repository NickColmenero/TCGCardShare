package com.nickcolmenero.tcgcardshare.homepage.myDecks

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.nickcolmenero.tcgcardshare.model.DeckViewerModel

@Composable
fun DeckList(deckList: List<DeckViewerModel> , navController: NavController, deckOwnerID: String) {
    LazyColumn {
        items(deckList) { deck ->
            DeckItem(deck = deck,navController,deckOwnerID)
        }
    }
}