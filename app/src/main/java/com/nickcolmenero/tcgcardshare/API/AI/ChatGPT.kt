package com.nickcolmenero.tcgcardshare.API.AI

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatGptApiService {
    @Headers("Authorization: Bearer ") // API KEY GOES AFTER BEARER! (make a user setting for pasting in the future.)
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

