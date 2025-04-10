package com.nickcolmenero.tcgcardshare.ShareDeck

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareDeckSearchTab(navController: NavController) {
    var deckOwnerID by remember { mutableStateOf("") }
    var decklistID by remember { mutableStateOf("") }
    Scaffold { paddingValues ->
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

                Button(
                    onClick = {
                        navController.navigate("ViewDeckList/${deckOwnerID}/${decklistID}")
                    },
                ) {
                    Text("Search")
                }
            }
        }
    }
}


