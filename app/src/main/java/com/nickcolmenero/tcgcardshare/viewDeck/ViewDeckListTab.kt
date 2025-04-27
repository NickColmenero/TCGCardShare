package com.nickcolmenero.tcgcardshare.viewDeck

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.nickcolmenero.tcgcardshare.API.Scryfall.CardIdentifier
import com.nickcolmenero.tcgcardshare.API.Scryfall.CollectionRequest
import com.nickcolmenero.tcgcardshare.model.CardListSeperator
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel
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
        val rawItems = deckListToItems
            .removePrefix("[")
            .removeSuffix("]")
            .split("], [")
            .map { it.trim() }
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
    val cardThumbnails = remember { mutableStateMapOf<String, String>() }
    val cardFullArtwork = remember { mutableStateMapOf<String, String>() }
    var isLoading by remember { mutableStateOf(true) }

    // Collect all card names for the collection request
    val cardNames = cardItems.map { it.name }

    LaunchedEffect(cardNames) {
        val collectionRequest = CollectionRequest(
            identifiers = cardNames.map { CardIdentifier(it) }
        )
        viewModel.getCardsByCollection(collectionRequest)
    }

    LaunchedEffect(viewModel.decklist.value) {
        val fetchedCards = viewModel.decklist.value
        fetchedCards.forEach { fetchedCard ->
            fetchedCard.image_uris?.get("art_crop")?.let { thumbnailUrl ->
                cardThumbnails[fetchedCard.name] = thumbnailUrl
            }
            fetchedCard.image_uris?.get("normal")?.let { normalUrl ->
                cardFullArtwork[fetchedCard.name] = normalUrl
            }
        }

        if (cardThumbnails.size == cardItems.size && cardFullArtwork.size == cardItems.size) {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(cardItems) { cardItem ->
                val thumbnailUrl = cardThumbnails[cardItem.name]
                thumbnailUrl?.let { url ->
                    var showPreview by remember { mutableStateOf(false) }
                    var scale by remember { mutableStateOf(1f) }
                    var offsetX by remember { mutableStateOf(0f) }
                    var offsetY by remember { mutableStateOf(0f) }
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { showPreview = true },
                            colors = CardDefaults.cardColors(Color.Transparent),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(url),
                                    contentDescription = "Card Thumbnail",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "${cardItem.name} x ${cardItem.count}",
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 12.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                if (showPreview) {
                                    val normalUrl = cardFullArtwork[cardItem.name]
                                    normalUrl?.let { previewUrl ->
                                        Popup(
                                            alignment = Alignment.Center,
                                            onDismissRequest = { showPreview = false }
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color.Black.copy(alpha = 0.7f))
                                                    .clickable { showPreview = false },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Image(
                                                    painter = rememberAsyncImagePainter(previewUrl),
                                                    contentDescription = "Card Preview (Normal Artwork)",
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .pointerInput(Unit) {
                                                            detectTransformGestures { _, pan, zoom, _ ->
                                                                scale *= zoom
                                                                offsetX += pan.x
                                                                offsetY += pan.y
                                                            }
                                                        }
                                                        .graphicsLayer {
                                                            scaleX = scale
                                                            scaleY = scale
                                                            translationX = offsetX
                                                            translationY = offsetY
                                                        }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}
