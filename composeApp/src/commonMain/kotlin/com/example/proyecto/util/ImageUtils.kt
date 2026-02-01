package com.example.proyecto.util

import androidx.compose.ui.graphics.ImageBitmap

// Este es el "contrato": decimos que existirá una forma de convertir bytes a imagen
expect fun ByteArray.toImageBitmap(): ImageBitmap?