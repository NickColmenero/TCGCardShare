package com.nickcolmenero.tcgcardshare.viewDeck

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
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
import com.nickcolmenero.tcgcardshare.API.Scryfall.ScryfallDBCard
import com.nickcolmenero.tcgcardshare.model.CardListSeperator
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDeckListTab(
    navController: NavController,
    deckOwnerID: String,
    decklistID: String,
    deckListToItems: String,
    viewModel: DeckListViewModel = viewModel()
) {
    val subTypes = listOf(
        "Creature",
        "Planeswalker",
        "Sorcery",
        "Instant",
        "Land",
        "Artifact",
        "Enchantment",
        "Battle"
    )

    val cardItems = remember(deckListToItems) {
        deckListToItems
            .removePrefix("[")
            .removeSuffix("]")
            .split("], [")
            .mapNotNull { entry ->
                val lastIndex = entry.lastIndexOf(" x")
                if (lastIndex != -1) {
                    val name = entry.substring(0, lastIndex).trim()
                    val count = entry.substring(lastIndex + 2).toIntOrNull()
                    count?.let { CardListSeperator(name, it) }
                } else null
            }
    }

    val cardCountMap = remember(cardItems) {
        cardItems.associate { it.name to it.count }
    }

    val cardNames = cardItems.map { it.name }

    val cardThumbnails = remember { mutableStateMapOf<String, String>() }
    val cardFullArtwork = remember { mutableStateMapOf<String, String>() }
    var isLoading by remember { mutableStateOf(true) }
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    var previewCard by remember { mutableStateOf<ScryfallDBCard?>(null) }

    previewCard?.let { card ->
        previewCard?.let { card ->
            val previewUrl = cardFullArtwork[card.name]
            if (previewUrl != null) {
                Popup() {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding()
                            .background(Color.Black.copy(alpha = 0.85f))
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    scale *= zoom
                                    offsetX += pan.x
                                    offsetY += pan.y
                                }
                            }
                            .clickable { previewCard = null },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(previewUrl),
                            contentDescription = "Card Preview",
                            modifier = Modifier
                                .fillMaxHeight(0.9f)
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



    LaunchedEffect(cardNames) {
        val collectionRequest = CollectionRequest(
            identifiers = cardNames.map { CardIdentifier(it) }
        )
        viewModel.getCardsByCollection(collectionRequest)
    }

    LaunchedEffect(viewModel.decklist.value) {
        val fetchedCards = viewModel.decklist.value

        fetchedCards.forEach { card ->
            card.image_uris?.get("art_crop")?.let { thumbnailUrl ->
                cardThumbnails[card.name] = thumbnailUrl
            }
            card.image_uris?.get("normal")?.let { normalUrl ->
                cardFullArtwork[card.name] = normalUrl
            }
        }
        isLoading = false
    }

    val groupedBySubTypes = viewModel.decklist.value.groupBy { card ->
        subTypes.find { card.type_line?.contains(it, ignoreCase = true) == true } ?: "Unknown"
    }

    val types = subTypes.filter { groupedBySubTypes.contains(it) }

    if (isLoading || types.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            types.forEach { type ->
                item {
                    Text(
                        text = type,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(groupedBySubTypes[type] ?: emptyList()) { card ->
                    val thumbnailUrl = cardThumbnails[card.name]
                    val count = cardCountMap[card.name] ?: 1

                    thumbnailUrl?.let { url ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        previewCard = card
                                        scale = 1f
                                        offsetX = 0f
                                        offsetY = 0f
                                    },
                                colors = CardDefaults.cardColors(Color.Transparent)
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
                                        text = "${card.name} x$count",
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 12.dp)
                                    )
                                }
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
