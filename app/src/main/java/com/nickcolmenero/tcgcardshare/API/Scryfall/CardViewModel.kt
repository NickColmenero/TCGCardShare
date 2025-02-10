package com.nickcolmenero.tcgcardshare.API.Scryfall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import androidx.compose.runtime.mutableStateOf

class CardViewModel : ViewModel() {
    var scryfallDBCard = mutableStateOf<ScryfallDBCard?>(null)
    var error = mutableStateOf<String?>(null)
    var decklist = mutableStateOf<List<ScryfallDBCard>>(emptyList())

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

    fun removeCardFromDeck(card: ScryfallDBCard) {
        decklist.value = decklist.value.filterNot { it.name == card.name }
    }
}
