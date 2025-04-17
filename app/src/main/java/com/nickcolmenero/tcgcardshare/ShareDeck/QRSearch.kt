package com.nickcolmenero.tcgcardshare.ShareDeck

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.zxing.integration.android.IntentIntegrator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRSearch(navController: NavController) {
    val context = LocalContext.current as Activity
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val data = handleQRCodeResult(result.resultCode, result.data)
            if (data.isNotEmpty()) {
                val (value1, value2) = parseQRCode(data)
                if (parseQRCode(data) != Pair("Invalid", "QR data")) {
                    navController.navigate("ViewDeckList/$value1/$value2")
                } else {
                    Toast.makeText(context, "Invalid Decklist ID", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Button(onClick = {
            launchQRCodeScanner(context, launcher)
        }) {
            Text(text = "Scan QR Code")
        }
    }

}

fun launchQRCodeScanner(
    activity: Activity,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val integrator = IntentIntegrator(activity).apply {
        setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        setPrompt("Scan a QR Code")
        setCameraId(0)
        setBeepEnabled(true)
        setBarcodeImageEnabled(false)
    }
    val scanIntent = integrator.createScanIntent()
    launcher.launch(scanIntent)
}

fun handleQRCodeResult(resultCode: Int, data: Intent?): String {
    val result = IntentIntegrator.parseActivityResult(resultCode, data)
    return result?.contents ?: ""
}

fun parseQRCode(data: String): Pair<String, String> {
    Log.e("Data is", data)
    Log.e("Data length is", data.length.toString())
    if (data.length != 14) {
        return Pair("Invalid", "QR data")
    }
    val parts = data.split(":")
    return if (parts.size == 2) {
        Pair(parts[0], parts[1])
    } else {
        Pair("Invalid", "QR data")
    }
}