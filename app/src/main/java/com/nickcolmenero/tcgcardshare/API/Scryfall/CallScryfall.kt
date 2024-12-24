package com.nickcolmenero.tcgcardshare.API.Scryfall

import retrofit2.http.GET
import retrofit2.http.Query

// if I want to add more fields use https://scryfall.com/docs/api/cards as a reference
data class Card(
    val name: String,
    val image_uris: Map<String, String>?,
    val type_line: String,
    val mana_cost: String?,
    val power: String?,
    val toughness: String?,
    val rarity: String
)

interface ScryfallApi {
    @GET("cards/named")
    suspend fun getCardByName(@Query("fuzzy") cardName: String): Card
}
