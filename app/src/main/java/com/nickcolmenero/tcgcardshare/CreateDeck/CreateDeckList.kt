package com.nickcolmenero.tcgcardshare.CreateDeck

import com.google.firebase.Firebase
import com.google.firebase.auth.auth


fun CreateDeck(){
    val currentUser = Firebase.auth.currentUser?.uid.toString()

}