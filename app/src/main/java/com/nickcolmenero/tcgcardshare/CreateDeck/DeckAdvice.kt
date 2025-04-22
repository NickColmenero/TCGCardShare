package com.nickcolmenero.tcgcardshare.CreateDeck

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nickcolmenero.tcgcardshare.API.AI.ChatRequest
import com.nickcolmenero.tcgcardshare.API.AI.Message
import com.nickcolmenero.tcgcardshare.API.AI.createChatGptService
import com.nickcolmenero.tcgcardshare.API.Scryfall.CardIdentifier
import com.nickcolmenero.tcgcardshare.API.Scryfall.CardViewModel
import com.nickcolmenero.tcgcardshare.API.Scryfall.CollectionRequest
import com.nickcolmenero.tcgcardshare.API.Scryfall.ScryfallDBCard
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeoutException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIDeckHelp(
    navController: NavController,
    deckList: MutableState<String>,
    selectedDeckFormat: MutableState<String>,
    viewModel: DeckListViewModel = viewModel(),
    cardViewModel: CardViewModel = viewModel()
) {
    val apiService = remember { createChatGptService() }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var isFirstLoad by remember { mutableStateOf(true) }

    val aiResponseCards = remember { mutableStateListOf<ScryfallDBCard>() }

    suspend fun fetchAIResponse() {
        try {
            isLoading = true

            val request = ChatRequest(
                messages = listOf(
                    Message(
                        "user",
                        "Here is the decklist: ${deckList.value} in the format: ${selectedDeckFormat.value}. Please suggest cards for this deck. Format them like [CardName]xN"
                    )
                )
            )
            val response = apiService.getChatResponse(request)
            val newResponse = response.choices.firstOrNull()?.message?.content ?: "No response"
            viewModel.updateChatResponse(newResponse)

            aiResponseCards.clear()

            val regex = """\[(.*?)\]x(\d+)""".toRegex()
            val matches = regex.findAll(newResponse).toList()
            val cardNames = matches.map { it.groupValues[1].trim() }.distinct()

            if (cardNames.isNotEmpty()) {
                val identifiers = cardNames.map { CardIdentifier(it) }
                val requestBody = CollectionRequest(identifiers)

                val result = cardViewModel.getCardsByCollection(requestBody)
                aiResponseCards.addAll(result?.data ?: emptyList())

                aiResponseCards.forEach {
                    Log.d("AIDeckHelp", "Fetched card: ${it.name}")
                }
            }

        } catch (e: TimeoutException) {
            viewModel.updateChatResponse("Request timed out. Please try again later.")
        } catch (e: Exception) {
            viewModel.updateChatResponse("Error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(isFirstLoad) {
        if (isFirstLoad) {
            coroutineScope.launch {
                fetchAIResponse()
            }
            isFirstLoad = false
        }
    }

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
            if (isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Text("Response:", fontWeight = FontWeight.Bold)
            }
        }

        item {
            SelectionContainer {
                Text(
                    text = viewModel.chatResponse,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        items(aiResponseCards) { card ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = card.name,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    // Show button only if the card is fully loaded
                    if (card.name.isNotBlank()) {
                        Button(
                            onClick = {
                                val newDecklist = buildString {
                                    append(deckList.value)
                                    append("\n")
                                    append("${card.name} x1")
                                }
                                deckList.value = newDecklist
                                Log.d("AIDeckHelp", "Added card: ${card.name}")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add ${card.name} to Deck")
                        }
                    } else {
                        Text(
                            text = "Loading card data...",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    if (!isLoading) {
                        coroutineScope.launch {
                            fetchAIResponse()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Refresh Suggestions")
            }
        }
    }
}
