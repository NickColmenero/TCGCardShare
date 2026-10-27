package com.nickcolmenero.tcgcardshare.homepage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nickcolmenero.tcgcardshare.MainApp
import com.nickcolmenero.tcgcardshare.ui.theme.TCGCardShareTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomepageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TCGCardShareTheme { MainApp() }
        }
    }
}
