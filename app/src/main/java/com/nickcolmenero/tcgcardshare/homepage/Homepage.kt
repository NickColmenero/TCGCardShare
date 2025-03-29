package com.nickcolmenero.tcgcardshare.homepage

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
import com.nickcolmenero.tcgcardshare.NavDrawer.NavigationDrawerContent
import com.nickcolmenero.tcgcardshare.homepage.myDecks.MyDecks
import com.nickcolmenero.tcgcardshare.homepage.news.news
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Homepage(navController: NavController) {
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
                    title = { Text("Home") },
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

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        NavigationDrawerContent(navController)
                    }
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
                                listOf("My Decks", "News").forEachIndexed { index, title ->
                                    Tab(
                                        text = { Text(title) },
                                        selected = selectedTabIndex == index,
                                        onClick = { selectedTabIndex = index }
                                    )
                                }
                            }
                            when (selectedTabIndex) {
                                0 -> MyDecks(navController) // Pass format to Tab 0
                                1 -> news(navController)// Pass format to Tab 1
                            }
                        }
                    }
                }
            }


        }
    }

}

