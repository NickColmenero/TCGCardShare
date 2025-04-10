package com.nickcolmenero.tcgcardshare.NavDrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun NavigationDrawerContent(navController: NavController) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxHeight()
            .fillMaxWidth(.5F)
            .padding(16.dp)
    ) {
        Text(text = "Navigation", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(modifier = Modifier.fillMaxWidth(), onClick = { navController.navigate("home") }) {
            Text("Home")
        }
        TextButton(modifier = Modifier.fillMaxWidth(), onClick = { navController.navigate("ShareDeckSearch") }) {
            Text("Search for a deck")
        }
        TextButton(modifier = Modifier.fillMaxWidth(), onClick = { navController.navigate("CreateDeckList") }) {
            Text("Create a decklist")
        }
        TextButton(modifier = Modifier.fillMaxWidth(), onClick = { navController.navigate("AskAI") }) {
            Text("Ask AI")
        }
        TextButton(modifier = Modifier.fillMaxWidth(), onClick = { navController.navigate("CardSearchScreen") }) {
            Text("Search for cards")
        }

        Spacer(modifier = Modifier.weight(.5f))

        TextButton(modifier = Modifier.fillMaxWidth(), onClick = { /* Handle logout logic here */ }) {
            Text("Logout")
        }
    }
}
