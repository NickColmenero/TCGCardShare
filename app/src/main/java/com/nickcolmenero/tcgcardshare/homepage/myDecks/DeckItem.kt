package com.nickcolmenero.tcgcardshare.homepage.myDecks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nickcolmenero.tcgcardshare.model.DeckViewerModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckItem(deck: DeckViewerModel,navController: NavController,deckOwnerID:String,) {
    val deckOwnerID = deckOwnerID
    val deckListID = deck.id
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("ViewDeckList/${deckOwnerID}/${deckListID}") }
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier
            .padding(16.dp)) {
            Text("Deck Name: ${deck.deckName}")
            Text(
                text = "Deck: ${deck.id}",
            )

            Spacer(modifier = Modifier
                .width(8.dp))
            Text(text = "Format: ${deck.deckFormat}")
            Spacer(modifier = Modifier
                .height(4.dp))
        }
    }
}