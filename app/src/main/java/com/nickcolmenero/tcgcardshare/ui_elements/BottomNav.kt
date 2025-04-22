package com.nickcolmenero.tcgcardshare.ui_elements

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share

@Composable
fun BottomNavScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.CreateDecklist.route) {
                CreateDecklistScreen()
            }
            composable(Screen.SearchDecklist.route) {
                SearchDecklistScreen()
            }
            composable(Screen.CardSearch.route) {
                CardSearchScreen()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute(navController) == Screen.Home.route,
            onClick = { navController.navigate(Screen.Home.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Add, contentDescription = "Create Decklist") },
            label = { Text("Create Decklist") },
            selected = currentRoute(navController) == Screen.CreateDecklist.route,
            onClick = { navController.navigate(Screen.CreateDecklist.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Share, contentDescription = "Find A Decklist") },
            label = { Text("Find A Decklist") },
            selected = currentRoute(navController) == Screen.SearchDecklist.route,
            onClick = { navController.navigate(Screen.SearchDecklist.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Search, contentDescription = "Card Search") },
            label = { Text("Card Search") },
            selected = currentRoute(navController) == Screen.CardSearch.route,
            onClick = { navController.navigate(Screen.CardSearch.route) }
        )
    }
}

@Composable
fun HomeScreen() {
    Text("Home Screen", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun CreateDecklistScreen() {
    Text("Create Decklist", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun SearchDecklistScreen() {
    Text("Find A Decklist", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun CardSearchScreen() {
    Text("Card Search", style = MaterialTheme.typography.bodyLarge)
}

fun currentRoute(navController: NavController): String? {
    return navController.currentBackStackEntry?.destination?.route
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CreateDecklist : Screen("CreateDeckList")
    object SearchDecklist : Screen("ShareDeckSearch")
    object CardSearch : Screen("SearchScreenNotDeckBuild")
}

@Preview(showBackground = true)
@Composable
fun PreviewBottomNavScreen() {
    BottomNavScreen()
}
