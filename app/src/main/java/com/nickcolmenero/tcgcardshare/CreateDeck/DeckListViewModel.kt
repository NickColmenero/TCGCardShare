package com.nickcolmenero.tcgcardshare.CreateDeck

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

