package com.nickcolmenero.tcgcardshare.CreateDeck

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.nickcolmenero.tcgcardshare.API.Scryfall.ScryfallDBCard
import com.nickcolmenero.tcgcardshare.API.Scryfall.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject



@HiltViewModel
class CreateDeckListViewModel  @Inject constructor() : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    var scryfallDBCard = mutableStateOf<ScryfallDBCard?>(null)
    val deck: MutableState<ScryfallDBCard?> = scryfallDBCard
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

}

