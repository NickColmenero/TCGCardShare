package com.nickcolmenero.tcgcardshare.ImportDeckList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel
import java.io.File
import java.util.UUID

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ImportDecklistScreen(viewModel: DeckListViewModel, navController: NavController) {
//    val currentUser = Firebase.auth.currentUser?.uid.orEmpty().take(8)
//    val firestore = Firebase.firestore
//    val uniqueDeckID = UUID.randomUUID().toString().replace("-", "").take(5)
//    val decklistRef = firestore
//        .collection("users")
//        .document(currentUser)
//        .collection("decks")
//        .document(uniqueDeckID)
//
//    var expanded by remember { mutableStateOf(false) }
//    var selectedSource by remember { mutableStateOf("") }
//    var decklistFile by remember { mutableStateOf<File?>(null) }
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    // List of sources to display in the dropdown
//    val sources = listOf("Moxfield", "Archidect", "TappedOut")
//
//    // UI to pick import source and select file
//    Column(
//        modifier = Modifier.padding(16.dp)
//    ) {
//        Text(text = "Import Decklist", fontSize = 24.sp)
//
//        // Dropdown to select the source
//        ExposedDropdownMenuBox(
//            expanded = expanded,
//            onExpandedChange = { expanded = !expanded }
//        ) {
//            TextField(
//                value = selectedSource,
//                onValueChange = { selectedSource = it },
//                label = { Text("Select Source") },
//                trailingIcon = {
//                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
//                },
//                readOnly = true,
//                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }
//            )
//            ExposedDropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false }
//            ) {
//                // Dynamically create menu items for each source
//                sources.forEach { source ->
//                    DropdownMenuItem(onClick = {
//                        selectedSource = source
//                        expanded = false
//                    }) {
//                        // Text is the composable content for the DropdownMenuItem
//                        Text(text = source)
//                    }
//                }
//            }
//        }
//
//        // File Picker Button (simulate it with a button)
//        Button(
//            onClick = {
//                // Open file picker (You can implement this via the `FilePicker` API for Android)
//                // This will return a file selected by the user (you need permissions)
//            }
//        ) {
//            Text("Select Decklist File")
//        }
//
//        // Handle Decklist Import
//        Button(
//            onClick = {
//                if (decklistFile != null && selectedSource.isNotEmpty()) {
//                    importDecklist(decklistFile!!, selectedSource, viewModel, decklistRef, uniqueDeckID, navController)
//                } else {
//                    // Show a snackbar if no file is selected or source is missing
//                    snackbarHostState.showSnackbar("Please select a file and source!")
//                }
//            }
//        ) {
//            Text("Import Decklist")
//        }
//    }
//
//    SnackbarHost(hostState = snackbarHostState)
//}
