package com.nickcolmenero.tcgcardshare


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.nickcolmenero.tcgcardshare.API.AI.AIDeckHelp
import com.nickcolmenero.tcgcardshare.API.AI.ChatScreen
import com.nickcolmenero.tcgcardshare.CreateDeck.CreateDeckList
import com.nickcolmenero.tcgcardshare.SeachCards.CardSearchScreen
import com.nickcolmenero.tcgcardshare.homepage.Homepage
import com.nickcolmenero.tcgcardshare.login.passwordreset.PasswordResetScreen
import com.nickcolmenero.tcgcardshare.login.signin.SignInScreen
import com.nickcolmenero.tcgcardshare.login.signup.SignUpScreen



@Composable
fun MainApp() {
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
            // FOR SIGNIN------
            composable("signin") {
                SignInScreen(navController)
            }
            composable("signup") {
                SignUpScreen(navController)
            }
            composable("passwordreset") {
                PasswordResetScreen(navController)
            }
            //---------------

            composable("CardSearchScreen") {
                CardSearchScreen(navController)
            }
            composable("CreateDeckList") {
                CreateDeckList(navController)
            }
            composable("AskAI") {
                ChatScreen(navController)
            }
            composable("AIDeckHelp/{decklist}") { backStackEntry ->
                val decklist = backStackEntry.arguments?.getString("deckListCompleted") ?: ""
                AIDeckHelp(navController,decklist)
            }
            // 100% work in progress.
            /*java.lang.IllegalArgumentException: Navigation destination that matches request NavDeepLinkRequest{ uri=android-app://androidx.navigation/AIDeckHelp/Fire // Ice, Fireball } cannot be found in the navigation graph NavGraph(0x0) startDestination={Destination(0x78d845ec) route=home}
            at androidx.navigation.NavController.navigate(NavController.kt:1664)
            at
*/
            /*
            like this for bit ones
            composable("viewCommissions/{commissionerID}") { backStackEntry ->
                val commissionerID = backStackEntry.arguments?.getString("commissionerID") ?: ""
                val viewModel: CommissionViewModel = hiltViewModel()
                println("Commissioner ID: $commissionerID")
                ViewCommissionsScreen(
                    viewModel = viewModel,
                    commissionerID = commissionerID,
                    navController = navController
                )
            }
            */
        }
    }
}