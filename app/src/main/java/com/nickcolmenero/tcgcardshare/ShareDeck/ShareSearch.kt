package com.nickcolmenero.tcgcardshare.ShareDeck

import androidx.compose.foundation.layout.Column
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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.nickcolmenero.tcgcardshare.ui_elements.BottomNavigationBar
import com.nickcolmenero.tcgcardshare.ui_elements.NavigationDrawerContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareDeckSearch(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentUser = Firebase.auth.currentUser?.uid.orEmpty().take(8)


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Search for a deck", color = MaterialTheme.colorScheme.primary) },
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
            },bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Scaffold(
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        var selectedTabIndex by remember { mutableStateOf(0) }
                        TabRow(selectedTabIndex = selectedTabIndex) {
                            listOf("ID Search", "QR Search").forEachIndexed { index, title ->
                                Tab(
                                    text = { Text(title, color = MaterialTheme.colorScheme.primary) },
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index }
                                )
                            }
                        }
                        when (selectedTabIndex) {
                            0 -> ShareDeckSearchTab(navController)
                            1 -> QRSearch(navController)
                        }
                    }
                }
            }
        }


    }
}
