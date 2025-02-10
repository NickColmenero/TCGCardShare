package com.nickcolmenero.tcgcardshare.API.AI

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatGptApiService {
    @Headers("Authorization: Bearer ")
    @POST("v1/chat/completions")
    suspend fun getChatResponse(@Body request: ChatRequest): ChatResponse
}

data class ChatRequest(val model: String = "gpt-4o-mini", val messages: List<Message>)
data class Message(val role: String, val content: String)
data class ChatResponse(val choices: List<Choice>)
data class Choice(val message: Message)

fun createChatGptService(): ChatGptApiService {
    return Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ChatGptApiService::class.java)
}

@Composable
fun ChatScreen(navController: NavController) {
    val apiService = remember { createChatGptService() }
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    var chatResponse by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Enter message") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            coroutineScope.launch {
                try {
                    val request = ChatRequest(messages = listOf(Message("user", userInput.text)))
                    val response = apiService.getChatResponse(request)
                    chatResponse = response.choices.firstOrNull()?.message?.content ?: "No response"
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Send")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Response:")
        Text(chatResponse, modifier = Modifier.padding(8.dp))
    }
}
