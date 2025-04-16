package com.nickcolmenero.tcgcardshare.CreateDeck

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentDeckTab(
    navController: NavController,
    viewModel: DeckListViewModel = viewModel(),
    deckName: MutableState<String>,
    deckList: MutableState<String>,
    selectedFormat: MutableState<String>
) {
    val currentUser = Firebase.auth.currentUser?.uid.orEmpty().take(8)
    val firestore = Firebase.firestore
    val uniqueDeckID = UUID.randomUUID().toString().replace("-", "").take(5)
    val decklistRef = firestore
        .collection("users")
        .document(currentUser)
        .collection("decks")
        .document(uniqueDeckID)
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val deckSize = viewModel.decklist.value.size
    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val deckSizeMessage = when {
        selectedFormat.value == "commander" || selectedFormat.value == "canadian highlander" -> {
            if (deckSize != 100) "Deck must contain exactly 100 cards for ${selectedFormat.value}." else null
        }

        deckSize < 60 -> "Deck must contain at least 60 cards for ${selectedFormat.value}."
        else -> null
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = deckName.value,
                    onValueChange = { deckName.value = it }, label = { Text("Deck Name") },
                    modifier = Modifier.weight(1f)
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedFormat.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Format") },
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown Icon"
                            )
                        },
                        modifier = Modifier
                            .menuAnchor() // Need this here otherwise it doesn't expand due to number of formats.
                            .align(Alignment.CenterVertically)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        val formats = listOf(
                            "standard", "future", "historic", "timeless", "gladiator", "pioneer",
                            "explorer", "modern", "legacy", "pauper", "vintage", "penny",
                            "commander", "oathbreaker", "standardbrawl", "brawl", "alchemy",
                            "paupercommander", "duel", "oldschool", "premodern", "predh",
                            "canadian highlander"
                        )
                        formats.forEach { format ->
                            DropdownMenuItem(
                                text = { Text(format) },
                                onClick = {
                                    selectedFormat.value = format
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchCard(it)
                },
                label = { Text("Search Card") },
            )
            Spacer(modifier = Modifier.height(16.dp))

            viewModel.scryfallDBCard.value?.let { card ->
                val legality = card.legalities
                val isLegal = if (selectedFormat.value.isNotEmpty()) {
                    legality[selectedFormat.value]?.let { it == "legal" }
                        ?: false
                } else {
                    true
                }
                Card(modifier = Modifier.padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = card.name, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = card.type_line,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "Mana Cost: ${card.mana_cost ?: "N/A"}",
                            style = MaterialTheme.typography.labelMedium
                        )
                        if (card.toughness != null) {
                            Text(
                                text = "Toughness: ${card.toughness}",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        if (card.power != null) {
                            Text(
                                text = "Power: ${card.power}",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Text(
                            text = "Rarity: ${card.rarity}",
                            style = MaterialTheme.typography.labelMedium
                        )
                        card.image_uris?.get("normal")?.let {
                            Image(
                                painter = rememberAsyncImagePainter(it),
                                contentDescription = "Card Image",
                                modifier = Modifier.size(350.dp)
                            )
                        }
                        if (!isLegal) {
                            Text(
                                text = "This card is not legal in ${selectedFormat.value}.",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        Column {
                            Button(
                                onClick = {
                                    if (isLegal) {
                                        viewModel.addCardToDeck(card)
                                        val groupedDecklist =
                                            viewModel.decklist.value.groupingBy { it.name }
                                                .eachCount()
                                        deckList.value =
                                            groupedDecklist.entries.joinToString("\n") { (cardName, count) ->
                                                "$cardName x$count"
                                            }
                                        showSnackbar = true
                                    }
                                },
                                modifier = Modifier.padding(top = 8.dp),
                                enabled = isLegal
                            ) {
                                Text("Add to Deck")
                            }
                            SnackbarHost(
                                hostState = snackbarHostState,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            if (showSnackbar) {
                                LaunchedEffect(snackbarHostState) {
                                    snackbarHostState.showSnackbar("Card added successfully!")
                                    showSnackbar = false
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        val groupedDecklist = viewModel.decklist.value.groupingBy { it.name }.eachCount()

        items(groupedDecklist.entries.toList()) { (cardName, count) ->
            val card = viewModel.decklist.value.find { it.name == cardName }

            card?.let {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(80.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        colors = CardDefaults.cardColors(Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            it.image_uris?.get("normal")?.let { url ->
                                Image(
                                    painter = rememberAsyncImagePainter(url),
                                    contentDescription = "Card Image",
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$cardName x$count",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                            IconButton(onClick = {
                                viewModel.removeSingleInstanceOfCard(cardName)
                                val updatedGroupedDecklist =
                                    viewModel.decklist.value.groupingBy { it.name }.eachCount()
                                deckList.value =
                                    updatedGroupedDecklist.entries.joinToString("\n") { (cardName, count) ->
                                        "$cardName x$count"
                                    }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Card", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    val groupedDecklist =
                        viewModel.decklist.value.groupingBy { it.name }.eachCount()
                    val deckString =
                        groupedDecklist.entries.joinToString(", ") { (cardName, count) ->
                            "[$cardName x$count]"
                        }

                    if (currentUser.isNotEmpty()) {
                        val deckData = mapOf(
                            "DeckName" to deckName.value,
                            "decklist" to deckString,
                            "format" to selectedFormat.value,
                            "deckID" to uniqueDeckID
                        )

                        decklistRef.set(deckData)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Decklist saved successfully to Firestore.")
                            }
                            .addOnFailureListener { exception ->
                                Log.e(
                                    "Firestore",
                                    "Failed to save decklist: ${exception.message}"
                                )
                            }
                    } else {
                        Log.e("Firestore", "User not authenticated.")
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Save Decklist")
            }
        }
    }
}
