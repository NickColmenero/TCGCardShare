package com.nickcolmenero.tcgcardshare.CreateDeck

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.nickcolmenero.tcgcardshare.API.Scryfall.CardViewModel
import com.nickcolmenero.tcgcardshare.NavDrawer.NavigationDrawerContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeckList(navController: NavController, viewModel: CardViewModel = viewModel()) {
    val currentUser = Firebase.auth.currentUser?.uid.toString()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFormat by remember { mutableStateOf("") }
    val deckSize = viewModel.decklist.value.size  // Get current deck size

    // Validate deck size based on format
    val deckSizeMessage = when {
        selectedFormat == "commander" || selectedFormat == "canadian highlander" -> {
            if (deckSize != 100) "Deck must contain exactly 100 cards for $selectedFormat." else null
        }

        deckSize < 60 -> "Deck must contain at least 60 cards for $selectedFormat."
        else -> null
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { NavigationDrawerContent(navController) }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Create Deck") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Header: Search bar and format selection
                item {
                    Row {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More options")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                listOf(
                                    "standard",
                                    "future",
                                    "historic",
                                    "timeless",
                                    "gladiator",
                                    "pioneer",
                                    "explorer",
                                    "modern",
                                    "legacy",
                                    "pauper",
                                    "vintage",
                                    "penny",
                                    "commander",
                                    "oathbreaker",
                                    "standardbrawl",
                                    "brawl",
                                    "alchemy",
                                    "paupercommander",
                                    "duel",
                                    "oldschool",
                                    "premodern",
                                    "predh",
                                    "canadian highlander"
                                ).forEach { format ->
                                    DropdownMenuItem(
                                        text = { Text(format) },
                                        onClick = {
                                            selectedFormat = format
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (selectedFormat.isNotEmpty()) "Selected format: $selectedFormat" else "No format selected",
                            modifier = Modifier.padding(start = 8.dp, top = 16.dp)
                        )
                    }

                    // Search Bar for adding cards
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.searchCard(it)  // Trigger the search on text change
                        },
                        label = { Text("Search Card") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    viewModel.scryfallDBCard.value?.let { card ->
                        val legality = card.legalities
                        val isLegal = if (selectedFormat.isNotEmpty()) {
                            legality[selectedFormat]?.let { it == "legal" }
                                ?: false  // Default to false if key is missing
                        } else {
                            true
                        }

                        Card(modifier = Modifier.padding(8.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Log.d("API_Legalities", "Legalities: ${card.legalities}")
                                Text(text = card.name, fontWeight = FontWeight.Bold)
                                Text(text = card.type_line)
                                Text(text = "Mana Cost: ${card.mana_cost ?: "N/A"}")
                                if (card.toughness != null) {
                                    Text(text = "Toughness: ${card.toughness ?: "N/A"}")
                                }
                                if (card.power != null) {
                                    Text(text = "Power ${card.power ?: "N/A"}")
                                }
                                Text(text = "Rarity: ${card.rarity}")
                                card.image_uris?.get("normal")?.let {
                                    Image(
                                        painter = rememberAsyncImagePainter(it),
                                        contentDescription = "Card Image",
                                        modifier = Modifier.size(600.dp)
                                    )
                                }

                                // Show a message if the card is not legal in the selected format
                                if (!isLegal) {
                                    Text(
                                        text = "This card is not legal in $selectedFormat.",
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }

                                // Button to add card to deck
                                Button(
                                    onClick = {
                                        if (isLegal) {
                                            viewModel.addCardToDeck(card)  // Add card if legal
                                        }
                                    },
                                    modifier = Modifier.padding(top = 8.dp),
                                    enabled = isLegal  // Disable button if the card is not legal
                                ) {
                                    Text("Add to Deck")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Display decklist title
                    Text("Decklist", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))

                    // Show deck size message if invalid
                    deckSizeMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                items(viewModel.decklist.value) { card ->
                    Card(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = card.name, modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.removeCardFromDeck(card) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Card")
                            }
                        }
                    }
                }
            }
        }
    }
}
