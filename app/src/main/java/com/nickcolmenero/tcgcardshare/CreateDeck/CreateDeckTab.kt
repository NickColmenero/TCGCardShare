package com.nickcolmenero.tcgcardshare.CreateDeck

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.nickcolmenero.tcgcardshare.API.Scryfall.CardIdentifier
import com.nickcolmenero.tcgcardshare.API.Scryfall.CollectionRequest
import com.nickcolmenero.tcgcardshare.API.Scryfall.CollectionRequestS
import com.nickcolmenero.tcgcardshare.API.Scryfall.ScryfallDBCard
import com.nickcolmenero.tcgcardshare.model.CardListSeperator
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentDeckTab(
    navController: NavController,
    viewModel: DeckListViewModel,
    deckName: MutableState<String>,
    deckList: MutableState<String>,
    selectedFormat: MutableState<String>,
    uniqueDeckID: String
) {


    Log.e("THE CURRENT deckList value is:", "${deckList}")
    val currentUser = Firebase.auth.currentUser?.uid.orEmpty().take(8)
    val firestore = Firebase.firestore

    val decklistRef = firestore
        .collection("users")
        .document(currentUser)
        .collection("decks")
        .document(uniqueDeckID)

    var expanded by remember { mutableStateOf(false) }
    var specificCard by remember { mutableStateOf<String?>(null) }
    val deckSize = viewModel.decklist.value.size
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    var previewCard by remember { mutableStateOf<ScryfallDBCard?>(null) }
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var importDeck = remember { mutableStateOf("") }


    val formats = listOf(
        "standard", "future", "historic", "timeless", "gladiator", "pioneer",
        "explorer", "modern", "legacy", "pauper", "vintage", "penny",
        "commander", "oathbreaker", "standardbrawl", "brawl", "alchemy",
        "paupercommander", "duel", "oldschool", "premodern", "predh",
        "canadian highlander"
    )

    if (previewCard != null) {
        Popup() {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable { previewCard = null },
                contentAlignment = Alignment.Center
            ) {
                previewCard?.image_uris?.get("normal")?.let { normalUrl ->
                    Image(
                        painter = rememberAsyncImagePainter(normalUrl),
                        contentDescription = "Card Preview",
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    scale *= zoom
                                    offsetX += pan.x
                                    offsetY += pan.y
                                }
                            }
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offsetX
                                translationY = offsetY
                            }
                    )
                }
            }
        }
    }

    val deckSizeMessage = when {
        selectedFormat.value in listOf("commander", "canadian highlander") && deckSize != 100 ->
            "Deck must contain exactly 100 cards for ${selectedFormat.value}."

        selectedFormat.value !in listOf("commander", "canadian highlander") && deckSize < 60 ->
            "Deck must contain at least 60 cards for ${selectedFormat.value}."

        else -> null
    }
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
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
            HorizontalDivider()
        }

        val groupedDecklist = viewModel.decklist.value.groupingBy { it.name }.eachCount()

        items(groupedDecklist.entries.toList()) { (cardName, count) ->
            val card = viewModel.decklist.value.find { it.name == cardName }

            card?.let {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                previewCard = card
                                scale = 1f
                                offsetX = 0f
                                offsetY = 0f
                            },
                        colors = CardDefaults.cardColors(Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            card.image_uris?.get("art_crop")?.let { url ->
                                Image(
                                    painter = rememberAsyncImagePainter(url),
                                    contentDescription = "Card Image",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "$cardName x$count",
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 12.dp),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            IconButton(
                                onClick = {
                                    viewModel.addCardToDeck(card)
                                    showSnackbar = true
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Another Card",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            IconButton(
                                onClick = { specificCard = cardName },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Card",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
                HorizontalDivider()
            }
        }

        item {
            Button(
                onClick = { specificCard = "SAVE_DECKLIST_CONFIRM" },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Save Decklist")
            }
        }
    }

    if (specificCard != null && specificCard != "SAVE_DECKLIST_CONFIRM") {
        AlertDialog(
            onDismissRequest = { specificCard = null },
            confirmButton = {
                Button(onClick = {
                    viewModel.removeSingleInstanceOfCard(specificCard!!)
                    val updatedGrouped = viewModel.decklist.value.groupingBy { it.name }.eachCount()
                    deckList.value =
                        updatedGrouped.entries.joinToString("\n") { (name, cnt) -> "$name x$cnt" }
                    specificCard = null
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { specificCard = null }) {
                    Text("Cancel")
                }
            },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to remove this card?") }
        )
    }

    if (specificCard == "SAVE_DECKLIST_CONFIRM") {
        AlertDialog(
            onDismissRequest = { specificCard = null },
            confirmButton = {
                Button(onClick = {
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
                                navController.navigate("home")
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firestore", "Failed to save decklist: ${exception.message}")
                            }
                    } else {
                        Log.e("Firestore", "User not authenticated.")
                    }
                    specificCard = null
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { specificCard = null }) {
                    Text("Cancel")
                }
            },
            title = { Text("Confirm Save") },
            text = { Text("Are you sure you want to save this decklist?") }
        )
    }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(
                "Card added successfully!",
                duration = SnackbarDuration.Short
            )
            showSnackbar = false
        }
    }
}
