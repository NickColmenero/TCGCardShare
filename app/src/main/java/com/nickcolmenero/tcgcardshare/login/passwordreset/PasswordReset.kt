package com.nickcolmenero.tcgcardshare.login.passwordreset


import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nickcolmenero.tcgcardshare.R


@Composable
fun PasswordResetScreen(navController: NavController) {
    val viewModel: PasswordResetViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsState()
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.tcgcardsharelogo),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(16.dp))
            if (uiState.value == PasswordResetState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.resetPassword(email) },
                    enabled = email.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Reset Password")
                }
                TextButton(
                    onClick = { navController.navigate("signin") }
                ) {
                    Text(text = "Back to Sign In")
                }
            }
            when (uiState.value) {
                is PasswordResetState.Success -> {
                    Toast.makeText(context, "Password reset email sent", Toast.LENGTH_SHORT).show()
                }
                is PasswordResetState.Error -> {
                    Toast.makeText(context, "Password reset failed", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
}
