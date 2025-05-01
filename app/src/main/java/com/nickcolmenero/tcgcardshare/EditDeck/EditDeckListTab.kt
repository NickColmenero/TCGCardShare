package com.nickcolmenero.tcgcardshare.EditDeck

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.nickcolmenero.tcgcardshare.API.Scryfall.CardIdentifier
import com.nickcolmenero.tcgcardshare.API.Scryfall.CollectionRequest
import com.nickcolmenero.tcgcardshare.API.Scryfall.ScryfallDBCard
import com.nickcolmenero.tcgcardshare.model.CardListSeperator
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDeckTab(
    navController: NavController,
    deckOwnerID: String,
    deckListID: String,
    deckName: MutableState<String>,
    rawDeckList: MutableState<String>,
    selectedFormat: MutableState<String>,
    viewModel: DeckListViewModel
) {
    val db = FirebaseFirestore.getInstance()
    var isLoading by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    val formats = listOf(
        "standard", "future", "historic", "timeless", "gladiator", "pioneer",
        "explorer", "modern", "legacy", "pauper", "vintage", "penny",
        "commander", "oathbreaker", "standardbrawl", "brawl", "alchemy",
        "paupercommander", "duel", "oldschool", "premodern", "predh",
        "canadian highlander"
    )

    val subTypes = listOf(
        "Creature",
        "Planeswalker",
        "Sorcery",
        "Instant",
        "Land",
        "Artifact",
        "Enchantment",
        "Battle"
    )

    // Fetch deck details from Firestore
    LaunchedEffect(deckListID) {
        val docRef = db.collection("users")
            .document(deckOwnerID)
            .collection("decks")
            .document(deckListID)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    deckName.value = document.getString("DeckName") ?: ""
                    rawDeckList.value = document.getString("decklist") ?: ""
                    selectedFormat.value = document.getString("format") ?: ""
                    isLoading = false
                }
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    // Parse raw decklist into card names and counts
    val cardItems = remember(rawDeckList.value) {
        rawDeckList.value
            .removePrefix("[")
            .removeSuffix("]")
            .split("], [")
            .mapNotNull { entry ->
                val lastIndex = entry.lastIndexOf(" x")
                if (lastIndex != -1) {
                    val name = entry.substring(0, lastIndex).trim()
                    val count = entry.substring(lastIndex + 2).toIntOrNull()
                    count?.let { CardListSeperator(name, it) }
                } else null
            }
    }

    val cardCountMap = remember(cardItems) {
        cardItems.associate { it.name to it.count }
    }.toMutableMap()

    val cardNames = cardItems.map { it.name }
    val cardThumbnails = remember { mutableStateMapOf<String, String>() }
    val cardFullArtwork = remember { mutableStateMapOf<String, String>() }

    var previewCard by remember { mutableStateOf<ScryfallDBCard?>(null) }

    // Fetch card details for thumbnails and artwork
    LaunchedEffect(cardNames) {
        val collectionRequest = CollectionRequest(
            identifiers = cardNames.map { CardIdentifier(it) }
        )
        viewModel.getCardsByCollection(collectionRequest)
    }

    LaunchedEffect(viewModel.decklist.value) {
        val fetchedCards = viewModel.decklist.value
        fetchedCards.forEach { card ->
            card.image_uris?.get("art_crop")?.let { thumbnailUrl ->
                cardThumbnails[card.name] = thumbnailUrl
            }
            card.image_uris?.get("normal")?.let { normalUrl ->
                cardFullArtwork[card.name] = normalUrl
            }
        }
    }
    // Display deck edit interface
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = deckName.value,
                    onValueChange = { deckName.value = it },
                    label = { Text("Deck Name") },
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
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Icon")
                        },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
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
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Cards",
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(cardItems) { card ->
                    val thumbnailUrl = cardThumbnails[card.name]
                    val count = cardCountMap[card.name] ?: 1

                    thumbnailUrl?.let { url ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .fillMaxWidth()
                                .height(60.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        previewCard =
                                            viewModel.decklist.value.find { it.name == card.name }
                                    },
                                colors = CardDefaults.cardColors(Color.Transparent)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(url),
                                        contentDescription = "Card Thumbnail",
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = card.name,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    val newCount =
                                                        (cardCountMap[card.name] ?: 1) + 1
                                                    cardCountMap[card.name] = newCount
                                                    rawDeckList.value = rawDeckList.value.replace(
                                                        "${card.name} x${count}",
                                                        "${card.name} x$newCount"
                                                    )
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Default.Add,
                                                    contentDescription = "Increase Count"
                                                )
                                            }
                                            Text(
                                                text = "x$count",
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            )
                                            IconButton(
                                                onClick = {
                                                    val newCount =
                                                        (cardCountMap[card.name] ?: 1) - 1
                                                    if (newCount > 0) {
                                                        cardCountMap[card.name] = newCount
                                                        rawDeckList.value =
                                                            rawDeckList.value.replace(
                                                                "${card.name} x${count}",
                                                                "${card.name} x$newCount"
                                                            )
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Decrease Count"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    // Save updated decklist to Firestore
                    val deckRef = db.collection("users")
                        .document(deckOwnerID)
                        .collection("decks")
                        .document(deckListID)

                    deckRef.update("decklist", rawDeckList.value)
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener {
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }

    // Preview Card (when clicked)
    previewCard?.let { card ->
        val previewUrl = cardFullArtwork[card.name]
        if (previewUrl != null) {
            Popup() {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .clickable { previewCard = null }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(previewUrl),
                        contentDescription = "Card Preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.9f)
                    )
                }
            }
        }
    }
}
