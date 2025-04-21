package com.nickcolmenero.tcgcardshare.CreateDeck

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel
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
    var cardToDelete by remember { mutableStateOf<String?>(null) }
    val deckSize = viewModel.decklist.value.size
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }

    val formats = listOf(
        "standard", "future", "historic", "timeless", "gladiator", "pioneer",
        "explorer", "modern", "legacy", "pauper", "vintage", "penny",
        "commander", "oathbreaker", "standardbrawl", "brawl", "alchemy",
        "paupercommander", "duel", "oldschool", "premodern", "predh",
        "canadian highlander"
    )

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
            var showPreview by remember { mutableStateOf(false) }
            var scale by remember { mutableStateOf(1f) }
            var offsetX by remember { mutableStateOf(0f) }
            var offsetY by remember { mutableStateOf(0f) }

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
                            .clickable { showPreview = true },
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

                            if (showPreview) {
                                Popup(
                                    alignment = Alignment.Center,
                                    onDismissRequest = { showPreview = false }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.7f))
                                            .clickable { showPreview = false },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        card.image_uris?.get("normal")?.let { normalUrl ->
                                            Image(
                                                painter = rememberAsyncImagePainter(normalUrl),
                                                contentDescription = "Card Preview",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(16.dp))
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

                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "$cardName x$count",
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 12.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )

                            IconButton(
                                onClick = { cardToDelete = cardName },
                                modifier = Modifier.size(32.dp)
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
                onClick = { cardToDelete = "SAVE_DECKLIST_CONFIRM" },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Save Decklist")
            }
        }
    }

    if (cardToDelete != null && cardToDelete != "SAVE_DECKLIST_CONFIRM") {
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            confirmButton = {
                Button(onClick = {
                    viewModel.removeSingleInstanceOfCard(cardToDelete!!)
                    val updatedGrouped = viewModel.decklist.value.groupingBy { it.name }.eachCount()
                    deckList.value = updatedGrouped.entries.joinToString("\n") { (name, cnt) -> "$name x$cnt" }
                    cardToDelete = null
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { cardToDelete = null }) {
                    Text("Cancel")
                }
            },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to remove this card?") }
        )
    }

    if (cardToDelete == "SAVE_DECKLIST_CONFIRM") {
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            confirmButton = {
                Button(onClick = {
                    val groupedDecklist = viewModel.decklist.value.groupingBy { it.name }.eachCount()
                    val deckString = groupedDecklist.entries.joinToString(", ") { (cardName, count) ->
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
                    cardToDelete = null
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { cardToDelete = null }) {
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
