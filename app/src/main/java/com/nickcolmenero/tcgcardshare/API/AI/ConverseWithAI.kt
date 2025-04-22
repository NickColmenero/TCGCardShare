package com.nickcolmenero.tcgcardshare.API.AI

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nickcolmenero.tcgcardshare.ui_elements.NavigationDrawerContent
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = viewModel()
) {
    val apiService = remember { createChatGptService() }
    val userInput = remember { mutableStateOf(TextFieldValue("")) }
    val chatResponse by viewModel.chatResponse.observeAsState("")

    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Chat Screen", color = MaterialTheme.colorScheme.primary) },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues),
                horizontalAlignment = Alignment.Start
            ) {
                item {
                    TextField(
                        value = userInput.value,
                        onValueChange = { userInput.value = it },
                        label = { Text("Ask something!") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val request = ChatRequest(
                                    messages = listOf(Message("user", userInput.value.text))
                                )
                                try {
                                    val response = apiService.getChatResponse(request)
                                    val newResponse = response.choices.firstOrNull()?.message?.content ?: "No response"
                                    viewModel.updateChatResponse(newResponse)
                                } catch (e: Exception) {
                                    viewModel.updateChatResponse("Error: ${e.message}")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }


                item {
                    Text("Response:", fontWeight = FontWeight.Bold)
                }

                item {
                    SelectionContainer {
                        Text(
                            text = chatResponse,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
