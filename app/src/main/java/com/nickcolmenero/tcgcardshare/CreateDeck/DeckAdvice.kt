package com.nickcolmenero.tcgcardshare.CreateDeck

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nickcolmenero.tcgcardshare.API.AI.ChatRequest
import com.nickcolmenero.tcgcardshare.API.AI.Message
import com.nickcolmenero.tcgcardshare.API.AI.createChatGptService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIDeckHelp(
    navController: NavController,
    deckList: MutableState<String>,
    selectedDeckFormat: MutableState<String>,
    viewModel: CreateDeckListViewModel = viewModel()
) {
    val apiService = remember { createChatGptService() }
    var userInput by remember { mutableStateOf(TextFieldValue()) }
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {

        item {
            Text(
                text = "Deck Format: ${selectedDeckFormat.value} \nDecklist:\n${deckList.value}",
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("Your Deck information will be sent with your question!") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Button(
                onClick = {
                    coroutineScope.launch {
                        val request = ChatRequest(
                            messages = listOf(
                                Message(
                                    "user",
                                    "Here is the decklist: ${deckList.value} in the format: ${selectedDeckFormat.value}. ${userInput.text}"
                                )
                            )
                        )
                        try {
                            val response = apiService.getChatResponse(request)
                            val newResponse = response.choices.firstOrNull()?.message?.content ?: "No response"
                            viewModel.updateChatResponse(newResponse)
                        } catch (e: Exception) {
                            viewModel.updateChatResponse("Error: ${e.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send")
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            Text("Response:", fontWeight = FontWeight.Bold)
        }

        item {
            SelectionContainer {
            Text(
                text = viewModel.chatResponse,
                modifier = Modifier.padding(8.dp)
            )
            }
        }
    }
}

