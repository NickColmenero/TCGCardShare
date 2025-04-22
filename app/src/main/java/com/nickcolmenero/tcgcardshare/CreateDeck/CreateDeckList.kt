package com.nickcolmenero.tcgcardshare.CreateDeck

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel
import com.nickcolmenero.tcgcardshare.ui_elements.NavigationDrawerContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeckList(navController: NavController, viewModel: DeckListViewModel = viewModel()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Create Deck List") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                var selectedTabIndex by remember { mutableStateOf(0) }
                val deckList = remember { mutableStateOf("") } // Shared state for the decklist
                val selectedDeckFormat =
                    remember { mutableStateOf("") }
                val deckName =
                    remember { mutableStateOf("") }
                TabRow(selectedTabIndex = selectedTabIndex) {
                    listOf("Current Deck", "Search", "Suggestions").forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index }
                        )
                    }
                }

                when (selectedTabIndex) {
                    0 -> CurrentDeckTab(
                        navController,
                        viewModel,
                        deckName,
                        deckList,
                        selectedDeckFormat
                    )

                    1 -> CreateDeckSearchScreen(
                        navController,
                        deckName,
                        deckList,
                        selectedDeckFormat,
                        viewModel,
                    )

                    2 -> AIDeckHelp(
                        navController,
                        deckList,
                        selectedDeckFormat,
                        viewModel
                    )
                }
            }
        }
    }
}

