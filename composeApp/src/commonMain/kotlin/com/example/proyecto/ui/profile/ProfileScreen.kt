package com.example.proyecto.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape // FIX: Importación añadida
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
import com.example.proyecto.ui.HuertaInput
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import com.example.proyecto.util.MediaManager
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@Composable
fun ProfileScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    var userName by remember { mutableStateOf("Jorge") }
    var userEmail by remember { mutableStateOf("jorge@ejemplo.com") }

    var showEditNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }

    var profilePhotoBytes by remember { mutableStateOf<ByteArray?>(null) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val launcher = MediaManager.rememberLauncher { bytes ->
        if (bytes != null) profilePhotoBytes = bytes
        showPhotoOptions = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.profile_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(30.dp))

        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { showPhotoOptions = true },
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(Icons.Default.Person, null, modifier = Modifier.padding(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(
                shape = CircleShape,
                color = GreenPrimary,
                modifier = Modifier
                    .size(36.dp)
                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
            ) {
                IconButton(onClick = { showPhotoOptions = true }) {
                    Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(userName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = {
                tempName = userName
                showEditNameDialog = true
            }) {
                Icon(Icons.Default.Edit, "Editar", tint = GreenPrimary, modifier = Modifier.size(20.dp))
            }
        }
        Text(userEmail, color = MaterialTheme.colorScheme.secondary)

        Spacer(Modifier.height(40.dp))

        Text(
            stringResource(Res.string.profile_section_prefs),
            style = MaterialTheme.typography.labelLarge,
            color = GreenPrimary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(10.dp))

        HuertaCard {
            SettingRow(
                icon = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                text = stringResource(if (isDarkTheme) Res.string.pref_color_D else Res.string.pref_color_C),
                iconColor = if (isDarkTheme) Color(0xFFBB86FC) else Color(0xFFFFB300),
                checked = isDarkTheme,
                onCheckedChange = onToggleTheme
            )
            // FIX: Parámetro alpha eliminado y movido al color
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(AppScreens.About) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(12.dp))
                Text(stringResource(Res.string.about_title))
            }
        }

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RedDanger.copy(alpha = 0.1f),
                contentColor = RedDanger
            ),
            shape = RoundedCornerShape(12.dp) // FIX: Ahora la referencia es válida
        ) {
            Icon(Icons.Default.Logout, null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(Res.string.profile_logout), fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(100.dp))
    }

    // DIÁLOGOS...
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text(stringResource(Res.string.profile_edit_title)) },
            text = {
                HuertaInput(tempName, { tempName = it }, stringResource(Res.string.profile_edit_name_hint), Icons.Default.Person)
            },
            confirmButton = {
                Button(onClick = { if(tempName.isNotBlank()) userName = tempName; showEditNameDialog = false }) {
                    Text(stringResource(Res.string.profile_edit_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) { Text(stringResource(Res.string.profile_edit_cancel)) }
            }
        )
    }

    if (showPhotoOptions) {
        AlertDialog(
            onDismissRequest = { showPhotoOptions = false },
            title = { Text(stringResource(Res.string.profile_change_photo)) },
            text = {
                Column {
                    ListItem(headlineContent = { Text("Cámara") }, leadingContent = { Icon(Icons.Default.PhotoCamera, null) }, modifier = Modifier.clickable { launcher.launchCamera() })
                    ListItem(headlineContent = { Text("Galería") }, leadingContent = { Icon(Icons.Default.PhotoLibrary, null) }, modifier = Modifier.clickable { launcher.launchGallery() })
                }
            },
            confirmButton = { TextButton(onClick = { showPhotoOptions = false }) { Text(stringResource(Res.string.dialog_cancel)) } }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(Res.string.dialog_logout_title)) },
            text = { Text(stringResource(Res.string.dialog_logout_msg)) },
            confirmButton = {
                Button(onClick = {
                    showLogoutDialog = false
                    navController.navigate(AppScreens.Login) { popUpTo(AppScreens.Home) { inclusive = true } }
                }, colors = ButtonDefaults.buttonColors(containerColor = RedDanger)) {
                    Text(stringResource(Res.string.dialog_confirm))
                }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text(stringResource(Res.string.dialog_cancel)) } }
        )
    }
}

@Composable
fun SettingRow(icon: ImageVector, text: String, iconColor: Color, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).clip(CircleShape).background(iconColor.copy(0.2f)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(12.dp))
            Text(text, color = MaterialTheme.colorScheme.onBackground)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = GreenPrimary, checkedTrackColor = GreenPrimary.copy(0.5f)))
    }
}