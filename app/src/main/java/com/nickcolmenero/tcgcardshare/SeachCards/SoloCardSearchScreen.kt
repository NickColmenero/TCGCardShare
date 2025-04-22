package com.nickcolmenero.tcgcardshare.SeachCards

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel
import com.nickcolmenero.tcgcardshare.ui_elements.BottomNavigationBar
import com.nickcolmenero.tcgcardshare.ui_elements.NavigationDrawerContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenNotDeckBuild(
    navController: NavController,
    deckList: MutableState<String>? = null,
    selectedFormat: MutableState<String>,
    deckViewModel: DeckListViewModel
) {
    // Remember the coroutine scope and drawer state
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(navController) // your drawer content here
        }
    ) {
        // Scaffold to handle topBar and bottomBar
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Chat Screen", color = MaterialTheme.colorScheme.primary) },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigationBar(navController) // your bottom navigation here
            }
        ) { paddingValues ->
            // Use a Row for the content (this is not typical for the full screen, but here's how to do it)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Apply padding here
            ) {
                // Inside the Row, you can place CardSearchScreen
                CardSearchScreen(
                    navController = navController,
                    deckList = deckList,
                    selectedFormat = selectedFormat,
                    deckViewModel = deckViewModel
                )
            }
        }
    }
}
