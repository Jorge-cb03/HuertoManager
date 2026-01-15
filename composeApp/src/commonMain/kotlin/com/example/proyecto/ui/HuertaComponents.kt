package com.example.proyecto.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.ui.theme.*

// Input Redondo (Login)
@Composable
fun HuertaInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector? = null,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = if (icon != null) { { Icon(icon, null) } } else null,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = GreenPrimary,
            unfocusedIndicatorColor = Color.Transparent
        ),
        // Aquí añadirías visualTransformation para password si es necesario
    )
}

// Tarjeta Estándar (Esquinas muy redondeadas)
@Composable
fun HuertaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        onClick = onClick ?: {}
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

// Píldora de Estado (Sano/Enfermo)
@Composable
fun StatusPill(status: String) {
    val isHealthy = status.lowercase() == "sano" || status.lowercase() == "buena"
    val color = if (isHealthy) GreenPrimary else RedDanger
    val bg = color.copy(alpha = 0.2f)

    Surface(
        color = bg,
        shape = RoundedCornerShape(50),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Icon(Icons.Filled.Circle, null, tint = color, modifier = Modifier.size(8.dp))
            Spacer(Modifier.width(6.dp))
            Text(status, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}