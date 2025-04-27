package com.nickcolmenero.tcgcardshare


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.nickcolmenero.tcgcardshare.API.AI.ChatScreen
import com.nickcolmenero.tcgcardshare.CreateDeck.CreateDeckList
import com.nickcolmenero.tcgcardshare.ImportDeckList.ImportDecklistScreen
import com.nickcolmenero.tcgcardshare.SeachCards.CardSearchScreen
import com.nickcolmenero.tcgcardshare.SeachCards.SearchScreenNotDeckBuild
import com.nickcolmenero.tcgcardshare.ShareDeck.ShareDeckSearch
import com.nickcolmenero.tcgcardshare.homepage.Homepage
import com.nickcolmenero.tcgcardshare.login.passwordreset.PasswordResetScreen
import com.nickcolmenero.tcgcardshare.login.signin.SignInScreen
import com.nickcolmenero.tcgcardshare.login.signup.SignUpScreen
import com.nickcolmenero.tcgcardshare.model.DeckListViewModel
import com.nickcolmenero.tcgcardshare.viewDeck.ViewDeckList


@Composable
fun MainApp() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val navController = rememberNavController()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val start = if (currentUser != null) "home" else "signin"
        val selectedDeckFormat =
            remember { mutableStateOf("") }
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
                CardSearchScreen(navController,null,selectedDeckFormat, deckViewModel = DeckListViewModel(),)
            }
            composable("SearchScreenNotDeckBuild") {
                SearchScreenNotDeckBuild(navController,null,selectedDeckFormat, deckViewModel = DeckListViewModel())
            }
            composable("CreateDeckList") {
                CreateDeckList(navController)
            }
            composable("AskAI") {
                ChatScreen(navController)
            }
            composable("ViewDeckList/{deckOwnerID}/{decklistID}")
            { BackStackEntry ->
                ViewDeckList(
                    navController,
                    BackStackEntry.arguments?.getString("deckOwnerID") ?: "",
                    BackStackEntry.arguments?.getString("decklistID") ?: ""
                )
            }
            composable("ShareDeckSearch")
            {
                ShareDeckSearch(navController)
            }
            composable("ImportDecklistScreen")
            {
                ImportDecklistScreen(DeckListViewModel(),navController)
            }
        }
    }
}