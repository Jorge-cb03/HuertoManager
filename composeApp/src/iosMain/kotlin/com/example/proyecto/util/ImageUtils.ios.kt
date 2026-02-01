package com.example.proyecto.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

actual fun ByteArray.toImageBitmap(): ImageBitmap? {
    return try {
        // iOS (y el código nativo de Compose) utiliza Skia para procesar imágenes
        Image.makeFromEncoded(this).toComposeImageBitmap()
    } catch (e: Exception) {
        null
    }
}