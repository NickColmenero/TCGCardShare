package com.nickcolmenero.tcgcardshare.Settings

import android.content.res.Resources.Theme
import android.widget.Switch
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable

@Composable
fun ChangeSettings(darkTheme: Theme){
    Switch(
        checked = isSystemInDarkTheme(),
        onCheckedChange = { },
    )
}