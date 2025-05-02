package com.nickcolmenero.tcgcardshare.EditDeck

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.nickcolmenero.tcgcardshare.CreateDeck.AIDeckHelp
import com.nickcolmenero.tcgcardshare.CreateDeck.CreateDeckSearchScreen
import com.nickcolmenero.tcgcardshare.CreateDeck.CurrentDeckTab
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel
import com.nickcolmenero.tcgcardshare.ui_elements.BottomNavigationBar
import com.nickcolmenero.tcgcardshare.ui_elements.NavigationDrawerContent
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.nickcolmenero.tcgcardshare.API.Scryfall.CardIdentifier
import com.nickcolmenero.tcgcardshare.API.Scryfall.CollectionRequest
import com.nickcolmenero.tcgcardshare.API.Scryfall.ScryfallDBCard
import com.nickcolmenero.tcgcardshare.model.CardListSeperator
import com.nickcolmenero.tcgcardshare.viewDeck.ViewDeckListTab
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDeckList(
    navController: NavController,
    deckOwnerID: String,
    deckListID: String,
    viewModel: DeckListViewModel
) {
    val deckName = remember { mutableStateOf("") }
    val rawDeckList = remember { mutableStateOf("") }
    val selectedFormat = remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()





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

    val cardNames = cardItems.map { it.name }
    val cardThumbnails = remember { mutableStateMapOf<String, String>() }
    val cardFullArtwork = remember { mutableStateMapOf<String, String>() }

    var previewCard by remember { mutableStateOf<ScryfallDBCard?>(null) }

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


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Deck") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->
            var selectedTabIndex by remember { mutableStateOf(0) }

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {

                TabRow(selectedTabIndex = selectedTabIndex) {
                    listOf("View Deck", "Edit Deck","Search For Cards", "AI Help").forEachIndexed { i, title ->
                        Tab(
                            text = { Text(title) },
                            selected = selectedTabIndex == i,
                            onClick = { selectedTabIndex = i }
                        )
                    }
                }

                when (selectedTabIndex) {


                    1 -> CurrentDeckTab(
                        navController = navController,
                        deckName = deckName,
                        deckList = rawDeckList,
                        selectedFormat = selectedFormat,
                        uniqueDeckID = deckListID,
                        viewModel = viewModel
                    )


                    0 ->
                        EditDeckTab(
                            navController = navController,
                            deckOwnerID = deckOwnerID,
                            deckListID = deckListID,
                            deckName = deckName,
                            rawDeckList = rawDeckList,
                            selectedFormat = selectedFormat,
                            viewModel
                        )


                    2 ->  CreateDeckSearchScreen(
                        navController = navController,
                        deckName = deckName,
                        deckList = rawDeckList,
                        selectedFormat = selectedFormat,
                        viewModel
                    )

                    3 -> AIDeckHelp(
                        navController = navController,
                        deckList = rawDeckList,
                         selectedFormat,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
