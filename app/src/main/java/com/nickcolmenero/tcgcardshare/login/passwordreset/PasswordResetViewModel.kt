package com.nickcolmenero.tcgcardshare.login.passwordreset

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PasswordResetViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<PasswordResetState>(PasswordResetState.Nothing)
    val state = _state.asStateFlow()

    fun resetPassword(email: String) {
        _state.value = PasswordResetState.Loading
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.value = PasswordResetState.Success
                } else {
                    _state.value = PasswordResetState.Error
                }
            }
    }
}

sealed class PasswordResetState {
    object Nothing : PasswordResetState()
    object Loading : PasswordResetState()
    object Success : PasswordResetState()
    object Error : PasswordResetState()
}
