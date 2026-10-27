package com.nickcolmenero.tcgcardshare.SeachCards

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nickcolmenero.tcgcardshare.API.Scryfall.RetrofitInstance
import com.nickcolmenero.tcgcardshare.API.Scryfall.ScryfallDBCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    var searchResults = mutableStateOf<List<ScryfallDBCard>>(emptyList())
        private set

    var selectedCard = mutableStateOf<ScryfallDBCard?>(null)
        private set

    var selectedCardUiState = mutableStateOf(
        ScryfallDBCard(
            name = "",
            image_uris = null,
            type_line = "",
            mana_cost = null,
            power = null,
            toughness = null,
            rarity = "",
            legalities = emptyMap(),
            set = null,
            artist = null,
            oracle_text = null,
            game_changer = false,
            flavor_text = "",
            color_identity = emptyList()
        )
    )
        private set
    var isLoading = mutableStateOf(false)
        private set

    var error = mutableStateOf<String?>(null)
        private set
    fun searchCards(
        cardName: String = "",
        colorIdentity: String = "",
        manaValue: String = "",
        type: String = "",
        rarity: String = "",
        artist: String = "",
        legalFormat: String = "",
        power: String = "",
        toughness: String = "",
        cardSet: String = "",
        oracleText: String = "",
        gameChanger: Boolean = false
    ) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val queryParts = mutableListOf<String>()

                if (cardName.isNotBlank()) queryParts.add("name:\"$cardName\"")
                if (type.isNotBlank()) queryParts.add("type:\"$type\"")
                if (power.isNotBlank()) queryParts.add("power:$power")
                if (colorIdentity.isNotBlank()) queryParts.add("ci:$colorIdentity")
                if (toughness.isNotBlank()) queryParts.add("toughness:$toughness")
                if (rarity.isNotBlank() && rarity != "Any") queryParts.add("rarity:\"$rarity\"")
                if (legalFormat.isNotBlank() && legalFormat != "Any") queryParts.add("legal:\"$legalFormat\"")
                if (cardSet.isNotBlank()) queryParts.add("set:\"$cardSet\"")
                if (oracleText.isNotBlank()) queryParts.add("oracle_text:\"$oracleText\"")
                if (gameChanger) queryParts.add("is:gamechanger")
                if (manaValue.isNotBlank()) queryParts.add("cmc:$manaValue")
                val query = queryParts.joinToString(" ")
                delay(100)
                val result = RetrofitInstance.api.searchCards(query)
                searchResults.value = result.data
                error.value = null
            } catch (e: Exception) {
                error.value = e.message ?: "Unknown error"
                if (error.value == "400") {
                    error.value = "Invalid search request"
                } else if (error.value == "401") {
                    error.value = "You are unauthorized to view this search"
                }else if (error.value == "403") {
                    error.value = "You are forbidden to view this search"
                }else if (error.value == "404") {
                    error.value = "The card(s) can't be found"
                }else{
                    error.value = "Unknown error"
                }
                searchResults.value = emptyList()
            } finally {
                isLoading.value = false
            }
        }
    }
}
