package com.nickcolmenero.tcgcardshare.API.AI

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface ChatGptApiService {
    @Headers("Authorization: Bearer 7") // API KEY GOES AFTER BEARER! (make a user setting for pasting in the future.)
    @POST("v1/chat/completions")
    suspend fun getChatResponse(@Body request: ChatRequest): ChatResponse
}

data class ChatRequest(val model: String = "gpt-4o", val messages: List<Message>)
data class Message(val role: String, val content: String)
data class ChatResponse(val choices: List<Choice>)
data class Choice(val message: Message)

fun createChatGptService(): ChatGptApiService {
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)  // Set connection timeout
        .writeTimeout(30, TimeUnit.SECONDS)    // Set write timeout
        .readTimeout(30, TimeUnit.SECONDS)     // Set read timeout
        .build()

    return Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ChatGptApiService::class.java)
}
