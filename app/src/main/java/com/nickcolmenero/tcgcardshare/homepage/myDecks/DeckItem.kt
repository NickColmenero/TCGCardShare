package com.nickcolmenero.tcgcardshare.homepage.myDecks

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.nickcolmenero.tcgcardshare.model.DeckViewerModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckItem(deck: DeckViewerModel, navController: NavController, deckOwnerID: String) {
    val MyID = com.google.firebase.Firebase.auth.currentUser?.uid.orEmpty().take(8)
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val deckListID = deck.id
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        navController.navigate("ViewDeckList/${deckOwnerID}/${deckListID}")
                    },
                    onLongPress = {
                        showMenu = true
                    }
                )
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    showMenu = false
                    // Handle Edit
                }
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    showMenu = false
                    if (MyID == deckOwnerID) {
                        if (deckOwnerID.isNotEmpty() && deckListID.isNotEmpty()) {
                            val docRef = db.collection("users")
                                .document(deckOwnerID)
                                .collection("decks")
                                .document(deckListID)

                            docRef.delete()
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Deck deleted successfully", Toast.LENGTH_SHORT).show()
                                    navController.navigate("home")
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(context, "Error deleting deck: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
            )
        }

        Column(modifier = Modifier.padding(7.dp)) {
            Text("Deck Name: ${deck.deckName}")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Format: ${deck.deckFormat}")
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
