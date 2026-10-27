package com.nickcolmenero.tcgcardshare.ui_elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MultiChipSelector(
    label: String,
    options: List<String>,
    selectedOptions: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = label)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selectedOptions.contains(option),
                    onClick = {
                        val newSelection = if (selectedOptions.contains(option)) {
                            selectedOptions - option
                        } else {
                            selectedOptions + option
                        }
                        onSelectionChanged(newSelection)
                    },
                    label = { Text(text = option) }
                )
            }
        }
    }
}
