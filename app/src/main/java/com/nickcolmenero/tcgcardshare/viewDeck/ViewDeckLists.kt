package com.nickcolmenero.tcgcardshare.viewDeck

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.nickcolmenero.tcgcardshare.model.DeckViewerModel
import com.nickcolmenero.tcgcardshare.ui_elements.BottomNavigationBar
import com.nickcolmenero.tcgcardshare.ui_elements.NavigationDrawerContent
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDeckList(navController: NavController, deckOwnerID: String, decklistID: String) {
    val currentUser = Firebase.auth.currentUser?.uid.orEmpty()
        .take(8) // for comparison in the future to allow users to edit decklists.
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()
    var deckInfo by remember { mutableStateOf(listOf<DeckViewerModel>()) }
    var deckListToItems by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var deckName by remember { mutableStateOf("") }
    var deckFormat: String = ""

    Log.d("Debug", "deckOwnerID: ${deckOwnerID}")
    Log.d("Debug", "decklistID: ${decklistID}")

    if (deckOwnerID.isNotEmpty() && decklistID.isNotEmpty()) {
        val docRef = db.collection("users")
            .document(deckOwnerID)
            .collection("decks")
            .document(decklistID)

        Log.d("Firestore", "Fetching data from: ${docRef.path}")

        LaunchedEffect(decklistID) {
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val deck = DeckViewerModel(
                            id = document.id,
                            deckName = document.getString("DeckName") ?: "",
                            deckFormat = document.getString("format") ?: "",
                            rawCards = document.getString("decklist") ?: ""
                        )
                        deckName = deck.deckName
                        Log.e("Deck name", "$deckName IS the name")
                        deckInfo = listOf(deck)
                        deckListToItems = deck.rawCards
                        deckFormat = deck.deckFormat
                        Log.e("deckListToItems", "The imported decklist is: ${deckListToItems}")

                        isLoading = false
                        Log.e("firebase", "Data was loaded: $deck")
                    } else {
                        Log.e("firebase", "Document does not exist!")
                        isLoading = false
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("firebase", "Error retrieving document: ${exception.message}")
                    isLoading = false
                }
        }
    } else {
        Log.e("firebase", "Invalid deckOwnerID or decklistID. Cannot fetch data.")
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
                    title = { Text(deckName, color = MaterialTheme.colorScheme.primary) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { navController.popBackStack() }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Menu"
                            )
                        }
                    },

                    actions = {
                        if (currentUser == deckOwnerID) {
                            IconButton(onClick = {
                                navController.navigate(
                                    "EditDeckList/$deckOwnerID/$decklistID"
                                )
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Deck")
                            }
                        } else {
                            Log.e("NOT THE OWNER", "Not yours")
                        }
                    }
                )
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                var selectedTabIndex by remember { mutableStateOf(0) }
                TabRow(selectedTabIndex = selectedTabIndex) {
                    listOf("Decklist", "Share").forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title, color = MaterialTheme.colorScheme.primary) },
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index }
                        )
                    }
                }
                when (selectedTabIndex) {
                    0 -> ViewDeckListTab(
                        navController, deckOwnerID, decklistID, deckListToItems
                    )

                    1 -> ShareDeckListTab(navController, deckOwnerID, decklistID)
                }
            }
        }
    }
}
