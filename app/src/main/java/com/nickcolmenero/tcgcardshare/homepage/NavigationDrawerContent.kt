package com.nickcolmenero.tcgcardshare.homepage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Navigation", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate("home") }) {
            Text("Home")
        }
        TextButton(onClick = { navController.navigate("signin") }) {
            Text("Sign In")
        }
        TextButton(onClick = { navController.navigate("signup") }) {
            Text("Sign Up")
        }
        TextButton(onClick = { navController.navigate("passwordreset") }) {
            Text("Password Reset")

        }
        TextButton(onClick = { navController.navigate("CardSearchScreen") }) {
            Text("Search for cards")
        }
    }
}