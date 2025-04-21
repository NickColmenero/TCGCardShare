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
    val legalities: Map<Any, Any>,
    val set: String?,
    val artist: String?,
    val color_identity: List<String>?,
    val oracle_text: String? = null,
    val game_changer:Boolean,
    val flavor_text: String,
)

data class ScryfallSearchResult(
    val data: List<ScryfallDBCard>
)

interface ScryfallApi {
    @GET("cards/named")
    suspend fun getCardByName(@Query("fuzzy") cardName: String): ScryfallDBCard

    @GET("cards/search")
    suspend fun searchCards(@Query("q") query: String): ScryfallSearchResult

}
