package com.nickcolmenero.tcgcardshare.SeachCards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.nickcolmenero.tcgcardshare.API.Scryfall.Card
import com.nickcolmenero.tcgcardshare.API.Scryfall.CardViewModel
import com.nickcolmenero.tcgcardshare.NavDrawer.NavigationDrawerContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardSearchScreen(navController: NavController, viewModel: CardViewModel = viewModel()) {
    var cardName by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    val card by remember { viewModel.card }
    val error by remember { viewModel.error }
    // for navigation on side
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = Modifier,
        drawerContent = {
            NavigationDrawerContent(navController)
        }

    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Card Search") },
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isSearching && card == null && error == null) {
                        CircularProgressIndicator()
                    }
                    card?.let {
                        CardInfoDisplay(card = it)
                    }
                    error?.let {
                        Text(text = it, color = Color.Red)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = cardName,
                        onValueChange = { cardName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {

                        Button(onClick = {
                            if (cardName.isNotEmpty()) {
                                viewModel.searchCard(cardName)
                                isSearching = true
                            }
                        }){
                            Text("Search")
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun CardInfoDisplay(card: Card) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = card.name, fontSize = 16.sp)
        Text(text = "Type: ${card.type_line}")
        Text(text = "Mana Cost: ${card.mana_cost ?: "N/A"}")
        Text(text = "Rarity: ${card.rarity}")
        Row {
            if (card.power != null) {
                Text(text = "Power: ${card.power}")
            }
            if (card.toughness != null) {
                Text(text = " Toughness: ${card.toughness}")
            }
        }
        card.image_uris?.get("normal")?.let {
            Image(painter = rememberAsyncImagePainter(it),
                contentDescription = "Card Image",
                modifier = Modifier.size(600.dp)
            )
        }
    }
}
