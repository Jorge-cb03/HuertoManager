package com.example.proyecto.ui.profile

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.proyecto.ui.HuertaInput
import com.example.proyecto.ui.garden.GardenViewModel
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import com.example.proyecto.util.MediaManager
import org.jetbrains.compose.resources.stringResource
import huertomanager.composeapp.generated.resources.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    viewModel: GardenViewModel = koinViewModel()
) {
    val usuario by viewModel.usuarioActivo.collectAsState()

    var userName by remember(usuario) { mutableStateOf(usuario?.nombre ?: "Usuario") }
    var userEmail by remember(usuario) { mutableStateOf(usuario?.email ?: "usuario@email.com") }
    var profilePhotoBytes by remember(usuario) { mutableStateOf(usuario?.fotoPerfil) }

    var showEditNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }
    var showPhotoOptions by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val launcher = MediaManager.rememberLauncher { bytes ->
        if (bytes != null) {
            profilePhotoBytes = bytes
            showPhotoOptions = false
            viewModel.guardarPerfil(userName, userEmail, bytes)
        }
    }

    // --- DISEÑO PRINCIPAL ---
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // Fondo Decorativo Superior (Degradado sutil)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(GreenPrimary.copy(alpha = 0.8f), GreenPrimary.copy(alpha = 0.1f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp)) // Espacio para la barra de estado

            // --- FOTO DE PERFIL PRO ---
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier
                        .size(140.dp)
                        .border(4.dp, MaterialTheme.colorScheme.background, CircleShape)
                        .clip(CircleShape)
                        .clickable { showPhotoOptions = true },
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 8.dp
                ) {
                    if (profilePhotoBytes != null) {
                        val bitmap = BitmapFactory.decodeByteArray(profilePhotoBytes, 0, profilePhotoBytes!!.size)
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            null,
                            modifier = Modifier.padding(30.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }

                // Botón de cámara flotante
                Surface(
                    shape = CircleShape,
                    color = GreenPrimary,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .padding(bottom = 8.dp, end = 8.dp)
                        .size(40.dp)
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                        .clickable { showPhotoOptions = true }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- INFORMACIÓN DEL USUARIO ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = {
                    tempName = userName
                    showEditNameDialog = true
                }) {
                    Icon(Icons.Default.Edit, null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                }
            }

            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(40.dp))

            // --- SECCIÓN DE AJUSTES ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "CONFIGURACIÓN",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )

                // Tarjeta de Ajustes
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        // Modo Oscuro
                        SettingItem(
                            icon = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            title = stringResource(if (isDarkTheme) Res.string.pref_color_D else Res.string.pref_color_C),
                            iconColor = if (isDarkTheme) Color(0xFF9FA8DA) else Color(0xFFFFB74D),
                            trailingContent = {
                                Switch(
                                    checked = isDarkTheme,
                                    onCheckedChange = onToggleTheme,
                                    colors = SwitchDefaults.colors(checkedThumbColor = GreenPrimary, checkedTrackColor = GreenPrimary.copy(0.5f))
                                )
                            }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                        // Acerca de
                        SettingItem(
                            icon = Icons.Default.Info,
                            title = stringResource(Res.string.about_title),
                            iconColor = Color(0xFF81C784),
                            onClick = { navController.navigate(AppScreens.About) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Botón Cerrar Sesión (Estilo Diferente)
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, RedDanger.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RedDanger)
                ) {
                    Icon(Icons.Default.Logout, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(stringResource(Res.string.profile_logout), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }

    // --- DIÁLOGO DE EDICIÓN DE NOMBRE ---
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text(stringResource(Res.string.profile_edit_title)) },
            text = { HuertaInput(tempName, { tempName = it }, "Nuevo nombre", Icons.Default.Person) },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempName.isNotBlank()) {
                            userName = tempName
                            viewModel.guardarPerfil(userName, userEmail, profilePhotoBytes)
                            showEditNameDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { showEditNameDialog = false }) { Text("Cancelar") } },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // --- MENÚ DE FOTO PRO (ESTILO DIARIO) ---
    if (showPhotoOptions) {
        Dialog(onDismissRequest = { showPhotoOptions = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.PhotoCamera, null, tint = GreenPrimary, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(Res.string.profile_change_photo),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(24.dp))

                    // Botón Cámara (Estilo Primario)
                    Button(
                        onClick = { launcher.launchCamera(); showPhotoOptions = false },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.CameraAlt, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Hacer Foto", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(12.dp))

                    // Botón Galería (Estilo Outlined)
                    OutlinedButton(
                        onClick = { launcher.launchGallery(); showPhotoOptions = false },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Icon(Icons.Outlined.Image, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Abrir Galería")
                    }

                    Spacer(Modifier.height(20.dp))

                    TextButton(onClick = { showPhotoOptions = false }) {
                        Text("Cancelar", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // --- DIÁLOGO DE CIERRE DE SESIÓN ---
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Icon(Icons.Default.Warning, null, tint = RedDanger) },
            title = { Text(stringResource(Res.string.dialog_logout_title)) },
            text = { Text(stringResource(Res.string.dialog_logout_msg), textAlign = TextAlign.Center) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        navController.navigate(AppScreens.Login) { popUpTo(AppScreens.Home) { inclusive = true } }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger),
                    shape = RoundedCornerShape(12.dp)
                ) { Text(stringResource(Res.string.dialog_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text(stringResource(Res.string.dialog_cancel)) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

// Componente auxiliar para filas de ajustes
@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    iconColor: Color,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }

        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        }
    }
}