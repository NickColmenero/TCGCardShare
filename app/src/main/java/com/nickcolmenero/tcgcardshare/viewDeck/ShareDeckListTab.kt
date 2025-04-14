package com.nickcolmenero.tcgcardshare.viewDeck

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun ShareDeckListTab(navController: NavController, deckOwnerID: String, decklistID: String) {
    val qrCodeContent = "$deckOwnerID:$decklistID"
    val qrBitmap = generateQRCode(qrCodeContent)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            Text(
                "Deck Owner's ID: $deckOwnerID \n Decklist ID: $decklistID",
                style = MaterialTheme.typography.titleLarge
            )
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            qrBitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = "QR Code for deck sharing",
                    modifier = Modifier
                )
            }
        }
    }
}


fun generateQRCode(content: String): ImageBitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 800, 800)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[y * width + x] =
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            }
        }
        val bitmap = android.graphics.Bitmap.createBitmap(
            width,
            height,
            android.graphics.Bitmap.Config.ARGB_8888
        )
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
