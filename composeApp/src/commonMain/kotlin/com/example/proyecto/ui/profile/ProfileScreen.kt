package com.example.proyecto.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.navigation.NavController
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@Composable
fun ProfileScreen(navController: NavController) {
    var userName by remember { mutableStateOf("Juan Pérez") }
    var userEmail by remember { mutableStateOf("juan@huerta.app") }
    var notifRiego by remember { mutableStateOf(true) }
    var notifClima by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) } // Estado nuevo
    var tempName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cabecera
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(Modifier.size(100.dp).clip(CircleShape).background(Color.Gray))
            IconButton(
                onClick = { tempName = userName; showEditDialog = true },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape).size(30.dp)
            ) { Icon(Icons.Filled.Edit, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp)) }
        }
        Spacer(Modifier.height(10.dp))
        Text(userName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text("Agricultor Pro • Huerto Los Andes", color = GreenPrimary)

        Spacer(Modifier.height(30.dp))

        // Datos Personales
        Text(stringResource(Res.string.profile_section_personal), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(10.dp))
        HuertaCard {
            Text(stringResource(Res.string.profile_name_label), fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
            Text(userName, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            HorizontalDivider(Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.onSurface.copy(0.1f))
            Text(stringResource(Res.string.profile_email_label), fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
            Text(userEmail, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
        }

        Spacer(Modifier.height(20.dp))

        // Preferencias
        Text(stringResource(Res.string.profile_section_prefs), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(10.dp))

        HuertaCard {
            SettingRow(Icons.Filled.WaterDrop, stringResource(Res.string.pref_irrigation), Color(0xFF4287f5), notifRiego) { notifRiego = it }
            HorizontalDivider(Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.onSurface.copy(0.1f))
            SettingRow(Icons.Filled.WbSunny, stringResource(Res.string.pref_weather), Color(0xFFf5a742), notifClima) { notifClima = it }
            HorizontalDivider(Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.onSurface.copy(0.1f))
            SettingRow(Icons.Filled.Email, stringResource(Res.string.pref_newsletter), GreenPrimary, true) {}

            HorizontalDivider(Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.onSurface.copy(0.1f))
            Row(modifier = Modifier.fillMaxWidth().clickable { navController.navigate(AppScreens.About) }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(36.dp).clip(CircleShape).background(Color.Gray.copy(0.2f)), contentAlignment = Alignment.Center) { Icon(Icons.Filled.Info, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) }
                    Spacer(Modifier.width(12.dp))
                    Text(stringResource(Res.string.about_title), color = MaterialTheme.colorScheme.onBackground)
                }
                Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurface.copy(0.3f))
            }
        }

        Spacer(Modifier.height(30.dp))

        // BOTÓN LOGOUT (NUEVO)
        TextButton(
            onClick = { showLogoutDialog = true },
            colors = ButtonDefaults.textButtonColors(contentColor = RedDanger)
        ) {
            Icon(Icons.Filled.Logout, null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(Res.string.profile_logout))
        }

        Spacer(Modifier.height(30.dp))
    }

    // DIÁLOGO EDICIÓN
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(Res.string.profile_edit_title)) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Filled.CameraAlt, null, tint = MaterialTheme.colorScheme.onSurfaceVariant); Text(stringResource(Res.string.profile_change_photo), fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = tempName, onValueChange = { tempName = it }, label = { Text(stringResource(Res.string.profile_edit_name_hint)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { Button(onClick = { if (tempName.isNotBlank()) userName = tempName; showEditDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) { Text(stringResource(Res.string.profile_edit_save)) } },
            dismissButton = { TextButton(onClick = { showEditDialog = false }) { Text(stringResource(Res.string.profile_edit_cancel)) } }
        )
    }

    // DIÁLOGO LOGOUT (NUEVO)
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(Res.string.dialog_logout_title)) },
            text = { Text(stringResource(Res.string.dialog_logout_msg)) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        // Navegar al Login y borrar historial
                        navController.navigate(AppScreens.Login) {
                            popUpTo(AppScreens.Home) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                ) { Text(stringResource(Res.string.dialog_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text(stringResource(Res.string.dialog_cancel)) }
            }
        )
    }
}

@Composable
fun SettingRow(icon: ImageVector, text: String, iconColor: Color, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).clip(CircleShape).background(iconColor.copy(0.2f)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(12.dp))
            Text(text, color = MaterialTheme.colorScheme.onBackground)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GreenPrimary))
    }
}