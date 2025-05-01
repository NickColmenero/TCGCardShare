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
import androidx.compose.runtime.getValue
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
                    listOf("Current Deck", "Search", "Suggestions").forEachIndexed { i, title ->
                        Tab(
                            text = { Text(title) },
                            selected = selectedTabIndex == i,
                            onClick = { selectedTabIndex = i }
                        )
                    }
                }
                when (selectedTabIndex) {
                    0 -> CurrentDeckTab(
                        navController = navController,
                        viewModel = viewModel,
                        deckName = deckName,
                        deckList = rawDeckList,
                        selectedFormat = selectedFormat,deckListID
                    )
                    1 -> CreateDeckSearchScreen(
                        navController = navController,
                        deckName = deckName,
                        deckList = rawDeckList,
                        selectedFormat = selectedFormat,
                        viewModel
                    )
                    2 -> AIDeckHelp(
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