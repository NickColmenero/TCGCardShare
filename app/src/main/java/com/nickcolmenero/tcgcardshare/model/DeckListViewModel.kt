package com.nickcolmenero.tcgcardshare.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nickcolmenero.tcgcardshare.API.Scryfall.CollectionRequest
import com.nickcolmenero.tcgcardshare.API.Scryfall.ScryfallDBCard
import com.nickcolmenero.tcgcardshare.API.Scryfall.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject



@HiltViewModel
class DeckListViewModel @Inject constructor() : ViewModel() {
    var scryfallDBCard = mutableStateOf<ScryfallDBCard?>(null)
    val decklist = mutableStateOf<List<ScryfallDBCard>>(emptyList()) // Maintain decklist here
    var error = mutableStateOf<String?>(null)

    fun searchCard(cardName: String) {
        viewModelScope.launch {
            try {
                delay(100)
                val fetchedCard = RetrofitInstance.api.getCardByName(cardName)
                scryfallDBCard.value = fetchedCard
                error.value = null
            } catch (e: HttpException) {
                error.value = "HTTP error: ${e.message}"
                scryfallDBCard.value = null
            } catch (e: IOException) {
                error.value = "Network error: ${e.message}"
                scryfallDBCard.value = null
            }
        }
    }

    fun getCardsByCollection(request: CollectionRequest) {
        viewModelScope.launch {
            try {
                val batchSize = 75
                val identifiers = request.identifiers
                val batchedRequests = identifiers.chunked(batchSize) // Split into batches of 75 or less

                var allFetchedCards = mutableListOf<ScryfallDBCard>()

                // Fetch each batch sequentially
                for (batch in batchedRequests) {
                    val batchRequest = CollectionRequest(identifiers = batch)
                    val response = RetrofitInstance.api.getCardsByCollection(batchRequest)
                    allFetchedCards.addAll(response.data)
                }

                decklist.value = allFetchedCards // Update the decklist with all the fetched cards
                error.value = null
            } catch (e: HttpException) {
                error.value = "HTTP error: ${e.message}"
                decklist.value = emptyList() // Reset decklist on error
            } catch (e: IOException) {
                error.value = "Network error: ${e.message}"
                decklist.value = emptyList() // Reset decklist on error
            }
        }
    }

    fun addCardToDeck(card: ScryfallDBCard) {
        decklist.value = decklist.value + card
    }

    fun removeSingleInstanceOfCard(cardName: String) {
        val updatedDecklist = decklist.value.toMutableList()
        val cardToRemove = updatedDecklist.find { it.name == cardName } // Find one instance
        if (cardToRemove != null) {
            updatedDecklist.remove(cardToRemove)
        }
        decklist.value = updatedDecklist
    }

    var chatResponse by mutableStateOf("")
        private set

    fun updateChatResponse(newResponse: String) {
        chatResponse = newResponse
    }
}

