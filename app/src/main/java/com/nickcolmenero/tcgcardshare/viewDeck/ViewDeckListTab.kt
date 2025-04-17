package com.nickcolmenero.tcgcardshare.viewDeck

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel
import com.nickcolmenero.tcgcardshare.model.CardListSeperator
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDeckListTab(
    navController: NavController,
    deckOwnerID: String,
    decklistID: String,
    deckListToItems: String,
    viewModel: DeckListViewModel = viewModel()
) {
    fun parseCardItems(deckListToItems: String): List<CardListSeperator> {
        val rawItems = deckListToItems.removePrefix("[").removeSuffix("]").split("], [").map { it.trim() }
        return rawItems.mapNotNull {
            val lastIndex = it.lastIndexOf(" x")
            if (lastIndex != -1) {
                val name = it.substring(0, lastIndex).trim()
                val count = it.substring(lastIndex + 2).toIntOrNull()
                count?.let { CardListSeperator(name, it) }
            } else null
        }
    }

    val cardItems = parseCardItems(deckListToItems)
    val cardImages = remember { mutableStateMapOf<String, String>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(cardItems) {
        cardItems.forEachIndexed { index, cardItem ->
            delay(100L * index) // Ensure 100ms delay per image call
            if (!cardImages.contains(cardItem.name)) {
                viewModel.searchCard(cardItem.name)
            }
        }
    }

    LaunchedEffect(viewModel.scryfallDBCard.value) {
        viewModel.scryfallDBCard.value?.let { fetchedCard ->
            fetchedCard.image_uris?.get("art_crop")?.let { imageUrl ->
                cardImages[fetchedCard.name] = imageUrl
            }
        }
        // Check if all images are fetched
        if (cardImages.size == cardItems.size) {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(8.dp)
        ) {
            items(cardItems) { cardItem ->
                val cardImageUrl = cardImages[cardItem.name]
                Box(
                    modifier = Modifier.padding(8.dp).fillMaxWidth()
                ) {
                    cardImageUrl?.let { url ->
                        Image(
                            painter = rememberAsyncImagePainter(url),
                            contentDescription = "Card Image",
                            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp))
                                .align(Alignment.CenterStart)
                        )
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(start = 110.dp).clip(MaterialTheme.shapes.medium),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Text(
                                text = "${cardItem.name} x ${cardItem.count}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

