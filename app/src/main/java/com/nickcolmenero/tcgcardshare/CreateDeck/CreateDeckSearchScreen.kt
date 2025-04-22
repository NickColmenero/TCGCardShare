package com.nickcolmenero.tcgcardshare.CreateDeck

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.nickcolmenero.tcgcardshare.SeachCards.CardSearchScreen
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateDeckSearchScreen(
    navController: NavController,
    deckName: MutableState<String>,
    deckList: MutableState<String>,
    selectedFormat: MutableState<String>,
    deckViewModel: DeckListViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Search by Name", "Advanced Search")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, color = MaterialTheme.colorScheme.primary) }
                )
            }
        }

        Log.d("SelectedFormat", "Selected format: ${selectedFormat.value}")
        when (selectedTabIndex) {
            0 -> {
                QuickSearchSection(
                    navController = navController,
                    deckList = deckList,
                    selectedFormat = selectedFormat,  // Pass selectedFormat here
                    deckViewModel = deckViewModel
                )
            }
            1 -> {
                CardSearchScreen(
                    navController = navController,
                    deckList = deckList,
                    selectedFormat = selectedFormat,  // Pass selectedFormat here
                    deckViewModel = deckViewModel,
                    viewModel()
                )
            }
        }
    }
}
