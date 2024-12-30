package com.nickcolmenero.tcgcardshare


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.nickcolmenero.tcgcardshare.SeachCards.CardSearchScreen
import com.nickcolmenero.tcgcardshare.homepage.Homepage
import com.nickcolmenero.tcgcardshare.login.passwordreset.PasswordResetScreen
import com.nickcolmenero.tcgcardshare.login.signin.SignInScreen
import com.nickcolmenero.tcgcardshare.login.signup.SignUpScreen


@Composable
fun MainApp() {
    val  darkTheme = 
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val navController = rememberNavController()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val start = if (currentUser != null) "home" else "signin"
        NavHost(
            navController = navController,
            startDestination = start
        ) {
            composable("home") {
                Homepage(navController)
            }
            composable("signin") {
                SignInScreen(navController)
            }
            composable("signup") {
                SignUpScreen(navController)
            }
            composable("passwordreset") {
                PasswordResetScreen(navController)
            }
            composable("CardSearchScreen") {
                CardSearchScreen(navController)
            }
        }
    }
}