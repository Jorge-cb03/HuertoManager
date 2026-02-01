package com.example.proyecto.util

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

// Aquí le decimos a Android CÓMO hacer esa conversión
actual fun ByteArray.toImageBitmap(): ImageBitmap? {
    return try {
        BitmapFactory.decodeByteArray(this, 0, size).asImageBitmap()
    } catch (e: Exception) {
        null
    }
}