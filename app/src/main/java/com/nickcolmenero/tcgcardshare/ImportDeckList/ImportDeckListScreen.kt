package com.nickcolmenero.tcgcardshare.ImportDeckList

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel
import com.nickcolmenero.tcgcardshare.ui_elements.BottomNavigationBar
import com.nickcolmenero.tcgcardshare.ui_elements.NavigationDrawerContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportDecklistScreen(viewModel: DeckListViewModel, navController: NavController) {
    val context = LocalContext.current
    val currentUser = Firebase.auth.currentUser?.uid.orEmpty().take(8)
    val firestore = Firebase.firestore
    val uniqueDeckID = UUID.randomUUID().toString().take(5)
    val decklistRef = firestore
        .collection("users")
        .document(currentUser)
        .collection("decks")
        .document(uniqueDeckID)
    var decklistUri by remember { mutableStateOf<Uri?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var selectedFormat by remember { mutableStateOf("") }
    var deckName by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var deckFormat by remember { mutableStateOf("") }
    var formatExpanded by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val formats = listOf(
        "standard", "future", "historic", "timeless", "gladiator", "pioneer",
        "explorer", "modern", "legacy", "pauper", "vintage", "penny",
        "commander", "oathbreaker", "standardbrawl", "brawl", "alchemy",
        "paupercommander", "duel", "oldschool", "premodern", "predh",
        "canadian highlander"
    )

    val siteNames = listOf("Moxfield", "Archidect", "TappedOut")
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        decklistUri = uri
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Import Decklist", color = MaterialTheme.colorScheme.primary) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { navController.popBackStack() }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            }, bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {

                Row(
                    modifier = Modifier
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = deckName,
                        onValueChange = { deckName = it },
                        label = { Text("Deck Name") },
                        modifier = Modifier
                            .weight(1f)
                    )

                    ExposedDropdownMenuBox(
                        expanded = formatExpanded,
                        onExpandedChange = { formatExpanded = !formatExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = deckFormat,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Deck Format") },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown Icon"
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = formatExpanded,
                            onDismissRequest = { formatExpanded = false }
                        ) {
                            formats.forEach { format ->
                                DropdownMenuItem(
                                    onClick = {
                                        deckFormat = format
                                        formatExpanded = false
                                    },
                                    text = { Text(format.replaceFirstChar { it.uppercase() }) }
                                )
                            }
                        }
                    }
                }



                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedFormat,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Source Format") },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Icon")
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        siteNames.forEach { format ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedFormat = format
                                    expanded = false
                                },
                                text = { Text(format) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { filePickerLauncher.launch("*/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Decklist File")
                }

                decklistUri?.let {
                    Text(
                        text = "Selected file: $it",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        if (decklistUri == null || selectedFormat.isEmpty() || deckName.isEmpty() || deckFormat.isEmpty()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please fill all fields and select a file!")
                            }
                        } else {
                            coroutineScope.launch {
                                val file = withContext(Dispatchers.IO) {
                                    uriToFile(context, decklistUri!!)
                                }
                                file?.let {
                                    importDecklist(
                                        file,
                                        selectedFormat,
                                        viewModel,
                                        decklistRef,
                                        uniqueDeckID,
                                        navController,
                                        deckName,
                                        deckFormat,
                                        snackbarHostState,
                                        coroutineScope
                                    )
                                } ?: snackbarHostState.showSnackbar("Failed to process file!")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Import Decklist")
                }
                SnackbarHost(hostState = snackbarHostState)
            }
        }
    }
}


suspend fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val tempFile = File(context.cacheDir, "tempDecklistFile")
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
