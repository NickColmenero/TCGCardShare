package com.nickcolmenero.tcgcardshare.login.signup

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import com.nickcolmenero.tcgcardshare.model.UserData

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<SignUpState>(SignUpState.Nothing)
    val state = _state.asStateFlow()

    fun signUp(name: String, email: String, password: String) {
        _state.value = SignUpState.Loading
        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.user?.let { user ->
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    val db = FirebaseFirestore.getInstance()
                                    val userData = UserData(
                                        id = user.uid,
                                        username = name
                                    )
                                } else {
                                    _state.value = SignUpState.Error("Profile update failed")
                                }
                            }
                        return@addOnCompleteListener
                    }
                    _state.value = SignUpState.Error("User not found")
                } else {
                    _state.value = SignUpState.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }
}


sealed class SignUpState {
    object Nothing : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}

