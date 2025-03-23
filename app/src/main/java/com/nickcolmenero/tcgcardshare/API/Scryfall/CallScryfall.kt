package com.nickcolmenero.tcgcardshare.API.Scryfall

import retrofit2.http.GET
import retrofit2.http.Query

// if I want to add more fields use https://scryfall.com/docs/api/cards as a reference
data class ScryfallDBCard(
    val name: String,
    val image_uris: Map<String, String>?,
    val type_line: String,
    val mana_cost: String?,
    val power: String?,
    val toughness: String?,
    val rarity: String,
    val legalities: Map<Any, Any>,  // Assume it's a Map for simplicity
    val set: String?,                    // New field for the card's set
    val artist: String?,                 // New field for the artist's name
    val flavor_text: String?             // New field for the flavor text
)

interface ScryfallApi {
    @GET("cards/named")
    suspend fun getCardByName(@Query("fuzzy") cardName: String): ScryfallDBCard
}
