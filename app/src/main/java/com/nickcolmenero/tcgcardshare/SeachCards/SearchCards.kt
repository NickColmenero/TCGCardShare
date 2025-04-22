package com.nickcolmenero.tcgcardshare.SeachCards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel
import com.nickcolmenero.tcgcardshare.ui_elements.ChipSelector


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardSearchScreen(
    navController: NavController,
    deckList: MutableState<String>? = null,
    selectedFormat: MutableState<String>,
    deckViewModel: DeckListViewModel,
    viewModel: SearchViewModel = viewModel()
) {
    var cardName by remember { mutableStateOf("") }
    var cardType by remember { mutableStateOf("") }
    var oracleText by remember { mutableStateOf("") }
    var selectedColors by remember { mutableStateOf(setOf<String>()) }
    var manaCost by remember { mutableStateOf("") }
    var power by remember { mutableStateOf("") }
    var toughness by remember { mutableStateOf("") }
    var isGameChanger by remember { mutableStateOf(false) }
    var selectedRarity by remember { mutableStateOf("Any") }
    var legalFormat by remember { mutableStateOf("Any") }
    var cardSet by remember { mutableStateOf("") }

    val rarityOptions = listOf("Any", "Common", "Uncommon", "Rare", "Mythic")
    val legalFormatsOptions = listOf("Any", "Standard", "Modern", "Legacy", "Vintage", "Commander")
    val colorOptions = listOf("W", "U", "B", "R", "G")

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = cardName,
            onValueChange = { cardName = it },
            label = { Text("Card Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = cardType,
            onValueChange = { cardType = it },
            label = { Text("Type") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = oracleText,
            onValueChange = { oracleText = it },
            label = { Text("Oracle Text") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Text("Colors")
        FlowRow() {
            colorOptions.forEach { color ->
                FilterChip(
                    selected = selectedColors.contains(color),
                    onClick = {
                        selectedColors = if (selectedColors.contains(color)) {
                            selectedColors - color
                        } else {
                            selectedColors + color
                        }
                    },
                    label = { Text(color) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = manaCost,
            onValueChange = { manaCost = it },
            label = { Text("Mana Cost") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = power,
                onValueChange = { power = it },
                label = { Text("Power") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = toughness,
                onValueChange = { toughness = it },
                label = { Text("Toughness") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isGameChanger,
                onCheckedChange = { isGameChanger = it }
            )
            Text("Game Changer")
        }
        Spacer(Modifier.height(8.dp))

        ChipSelector(
            label = "Rarity",
            options = rarityOptions,
            selectedOption = selectedRarity,
            onOptionSelected = { selectedRarity = it }
        )
        Spacer(Modifier.height(8.dp))

        ChipSelector(
            label = "Legal Format",
            options = legalFormatsOptions,
            selectedOption = legalFormat,
            onOptionSelected = { legalFormat = it }
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = cardSet,
            onValueChange = { cardSet = it },
            label = { Text("Set") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.searchCards(
                    cardName = cardName,
                    colorIdentity = selectedColors.joinToString(","),
                    manaValue = manaCost,
                    type = cardType,
                    rarity = if (selectedRarity == "Any") "" else selectedRarity,
                    artist = "",
                    legalFormat = if (legalFormat == "Any") "" else legalFormat,
                    power = power,
                    toughness = toughness,
                    cardSet = cardSet,
                    oracleText = oracleText,
                    gameChanger = isGameChanger
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }
        Spacer(modifier = Modifier.height(16.dp))
        viewModel.error.value?.let { errorMsg ->
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(
                text = errorMsg,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            }
        }

        viewModel.searchResults.value.forEach { card ->
            var showInfo by remember { mutableStateOf(false) }

            Card(
                colors = CardDefaults.cardColors(Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = card.name, style = MaterialTheme.typography.headlineMedium)

                    card.image_uris?.get("normal")?.let { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = card.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }

                    Button(
                        onClick = { showInfo = !showInfo },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(if (showInfo) "Hide Info" else "Show Info")
                    }

                    if (showInfo) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Type: ${card.type_line}")
                        card.mana_cost?.let { Text("Mana Cost: $it") }
                        if (!card.power.isNullOrEmpty() && !card.toughness.isNullOrEmpty()) {
                            Text("Power/Toughness: ${card.power} / ${card.toughness}")
                        }
                        Text("Rarity: ${card.rarity}")
                        card.set?.let { Text("Set: $it") }
                        card.oracle_text?.let { Text("Oracle Text: $it") }
                        Text(if (card.game_changer) "Game Changer" else "Not a Game Changer")
                    }
                    if (deckList != null && selectedFormat != null) {
                        val isLegal =
                            card.legalities[selectedFormat.value]?.let { it == "legal" } ?: true

                        Button(
                            onClick = {
                                if (isLegal) {
                                    deckViewModel.addCardToDeck(card) // <- use deck viewmodel

                                    val grouped =
                                        deckViewModel.decklist.value.groupingBy { it.name }
                                            .eachCount()
                                    deckList.value =
                                        grouped.entries.joinToString("\n") { (name, count) -> "$name x$count" }
                                }
                            },
                            enabled = isLegal,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add to Deck")
                        }
                    }
                }
            }
        }
        HorizontalDivider()
    }
}