package com.nickcolmenero.tcgcardshare.API.Scryfall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import androidx.compose.runtime.mutableStateOf

class CardViewModel : ViewModel() {

    var card = mutableStateOf<Card?>(null)
    var error = mutableStateOf<String?>(null)

    fun searchCard(cardName: String) {
        viewModelScope.launch {
            try {
                delay(100)
                val fetchedCard = RetrofitInstance.api.getCardByName(cardName)
                card.value = fetchedCard
                error.value = null
            } catch (e: HttpException) {
                error.value = "HTTP error: ${e.message}"
                card.value = null
            } catch (e: IOException) {
                error.value = "Network error: ${e.message}"
                card.value = null
            }
        }
    }
}
