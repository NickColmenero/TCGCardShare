package com.nickcolmenero.tcgcardshare.ShareDeck

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.nickcolmenero.tcgcardshare.NavDrawer.NavigationDrawerContent
import com.nickcolmenero.tcgcardshare.model.DeckViewerModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareDeckSearch(navController: NavController) {
    var deckOwnerID by remember { mutableStateOf("") }
    var decklistID by remember { mutableStateOf("") }
    val currentUser = Firebase.auth.currentUser?.uid.orEmpty().take(8)
    val firestore = Firebase.firestore
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var deckList by remember { mutableStateOf(listOf<DeckViewerModel>()) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Deck Search") },
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

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = deckOwnerID,
                        onValueChange = { deckOwnerID = it },
                        label = { Text("User ID") },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(0.8f)
                    )

                    OutlinedTextField(
                        value = decklistID,
                        onValueChange = { decklistID = it },
                        label = { Text("Decklist ID") },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search button
                    Button(
                        onClick = {
                            // Use deckOwnerID and decklistID in the navigation
                            navController.navigate("ViewDeckList/${deckOwnerID}/${decklistID}")
                        },
                    ) {
                        Text("Search")
                    }
                }
            }
        }
    }
}

