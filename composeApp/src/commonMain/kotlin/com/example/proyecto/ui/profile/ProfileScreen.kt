package com.example.proyecto.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.theme.GreenPrimary
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@Composable
fun ProfileScreen() {
    var notifRiego by remember { mutableStateOf(true) }
    var notifClima by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cabecera Perfil
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(Modifier.size(100.dp).clip(CircleShape).background(Color.Gray))
            IconButton(
                onClick = {},
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape).size(30.dp)
            ) {
                Icon(Icons.Filled.Edit, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        Text("Juan Pérez", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text("Agricultor Pro • Huerto Los Andes", color = GreenPrimary)

        Spacer(Modifier.height(30.dp))

        // Datos Personales - TRADUCIDO
        Text(stringResource(Res.string.profile_section_personal), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(10.dp))
        HuertaCard {
            Text(stringResource(Res.string.profile_name_label), fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
            Text("Juan Pérez", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Divider(Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.onSurface.copy(0.1f))
            Text(stringResource(Res.string.profile_email_label), fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
            Text("juan@huerta.app", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
        }

        Spacer(Modifier.height(20.dp))

        // Preferencias - TRADUCIDO
        Text(stringResource(Res.string.profile_section_prefs), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(10.dp))

        HuertaCard {
            SettingRow(Icons.Filled.WaterDrop, stringResource(Res.string.pref_irrigation), Color(0xFF4287f5), notifRiego) { notifRiego = it }
            Divider(Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.onSurface.copy(0.1f))
            SettingRow(Icons.Filled.WbSunny, stringResource(Res.string.pref_weather), Color(0xFFf5a742), notifClima) { notifClima = it }
            Divider(Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.onSurface.copy(0.1f))
            SettingRow(Icons.Filled.Email, stringResource(Res.string.pref_newsletter), GreenPrimary, true) {}
        }
    }
}

@Composable
fun SettingRow(icon: ImageVector, text: String, iconColor: Color, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).clip(CircleShape).background(iconColor.copy(0.2f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(text, color = MaterialTheme.colorScheme.onBackground)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GreenPrimary
            )
        )
    }
}