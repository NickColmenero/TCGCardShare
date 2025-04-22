package com.nickcolmenero.tcgcardshare.CreateDeck

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuickSearchSection(
    navController: NavController,
    deckList: MutableState<String>,
    selectedFormat: MutableState<String>,
    deckViewModel: DeckListViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    Log.d("SelectedFormat", "Selected format: ${selectedFormat.value}")

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                deckViewModel.searchCard(it)
            },
            label = { Text("Search Card") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        deckViewModel.scryfallDBCard.value?.let { card ->
            val isLegal = selectedFormat.value.isEmpty() ||
                    (card.legalities[selectedFormat.value] ?: "legal") == "legal"

            Card(modifier = Modifier.padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(card.name, fontWeight = FontWeight.Bold)
                    Text(card.type_line ?: "")
                    Text("Mana Cost: ${card.mana_cost ?: "N/A"}")
                    if (!card.toughness.isNullOrEmpty()) Text("Toughness: ${card.toughness}")
                    if (!card.power.isNullOrEmpty()) Text("Power: ${card.power}")
                    Text("Rarity: ${card.rarity ?: "Unknown"}")

                    card.image_uris?.get("normal")?.let {
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = "Card Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }

                    if (!isLegal) {
                        Text(
                            text = "This card is not legal in ${selectedFormat.value}.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {
                            if (isLegal) {
                                deckViewModel.addCardToDeck(card)
                                val groupedDecklist = deckViewModel.decklist.value
                                    .groupingBy { it.name }.eachCount()
                                deckList.value = groupedDecklist.entries.joinToString("\n") {
                                        (name, count) -> "$name x$count"
                                }
                                showSnackbar = true
                            }
                        },
                        modifier = Modifier.padding(top = 8.dp),
                        enabled = isLegal
                    ) {
                        Text("Add to Deck")
                    }

                    if (showSnackbar) {
                        LaunchedEffect(snackbarHostState) {
                            snackbarHostState.showSnackbar("Card added successfully!")
                            showSnackbar = false
                        }
                    }

                    SnackbarHost(hostState = snackbarHostState)
                }
            }
        }
    }
}
