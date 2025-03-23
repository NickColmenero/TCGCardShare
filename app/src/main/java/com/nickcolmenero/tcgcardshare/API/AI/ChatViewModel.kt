package com.nickcolmenero.tcgcardshare.API.AI
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData

class ChatViewModel : ViewModel() {
    val chatResponse = MutableLiveData<String>()

    fun updateChatResponse(response: String) {
        chatResponse.value = response
    }
}
