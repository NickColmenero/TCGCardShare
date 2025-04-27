package com.nickcolmenero.tcgcardshare.API.Scryfall

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
    val legalities: Map<Any, Any>,
    val set: String?,
    val artist: String?,
    val color_identity: List<String>?,
    val oracle_text: String? = null,
    val game_changer: Boolean,
    val flavor_text: String,
)

// Data model for search results
data class ScryfallSearchResult(
    val data: List<ScryfallDBCard>
)

// Data model for collection request
data class CardIdentifier(
    val name: String
)

data class CollectionRequest(
    val identifiers: List<CardIdentifier>
)



// Retrofit API interface
interface ScryfallApi {

    // Single card fuzzy search
    @GET("cards/named")
    suspend fun getCardByName(@Query("fuzzy") cardName: String): ScryfallDBCard

    // Search with a complex query (e.g., "oracle:draw format:modern")
    @GET("cards/search")
    suspend fun searchCards(@Query("q") query: String): ScryfallSearchResult

    // Fetch a batch of specific cards (up to 75 at a time)
    @POST("cards/collection")
    suspend fun getCardsByCollection(@Body request: CollectionRequest): ScryfallSearchResult
}
