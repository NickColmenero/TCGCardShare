package com.nickcolmenero.tcgcardshare.viewDeck
// THIS IS WHAT I AM CURRENTLY WORKIN ON!
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nickcolmenero.tcgcardshare.CreateDeck.DeckListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDeckListTab(
    navController: NavController,
    deckOwnerID: String,
    decklistID: String,
    deckListToItems: String,
    viewModel: DeckListViewModel = viewModel()
) {
    data class CardItem(val name: String, val count: Int)

    fun parseCardItems(deckListToItems: String): List<CardItem> {
        val rawItems = deckListToItems.removePrefix("[").removeSuffix("]").split(",").map { it.trim() }
        return rawItems.mapNotNull {
            val lastIndex = it.lastIndexOf(" x") // Find the LAST occurrence
            if (lastIndex != -1) {
                val name = it.substring(0, lastIndex).trim()
                val count = it.substring(lastIndex + 2).toIntOrNull()
                count?.let { CardItem(name, it) }
            } else {
                null
            }
        }
    }


    val cardItems = parseCardItems(deckListToItems)
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(cardItems) { cardItem ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(MaterialTheme.shapes.medium),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "${cardItem.name}, x ${cardItem.count}",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}
