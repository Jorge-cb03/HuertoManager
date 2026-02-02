package com.example.proyecto.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.ui.HuertaLoading
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.GreenSecondary
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import huertomanager.composeapp.generated.resources.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onGoogleLoginClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (isLoading) {
        // CORRECCIÓN: Pasamos el parámetro isLoading = true
        HuertaLoading(isLoading = true)
    } else {
        // Fondo con degradado suave
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (isSystemInDarkTheme())
                            listOf(Color(0xFF1E1E1E), Color(0xFF121212))
                        else
                            listOf(GreenSecondary.copy(alpha = 0.3f), Color.White)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // LOGO / ICONO
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(Modifier.height(24.dp))

                // TÍTULO
                Text(
                    text = stringResource(Res.string.login_title),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
                Text(
                    text = stringResource(Res.string.login_subtitle),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(Modifier.height(40.dp))

                // FORMULARIO
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(stringResource(Res.string.login_user_hint)) },
                            leadingIcon = { Icon(Icons.Default.Email, null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(Res.string.login_pass_hint)) },
                            leadingIcon = { Icon(Icons.Default.Lock, null) },
                            singleLine = true,
                            visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passVisible = !passVisible }) {
                                    Icon(if (passVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        // Olvidaste contraseña
                        TextButton(
                            onClick = { /* Acción recuperar */ },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(stringResource(Res.string.login_forgot), fontSize = 12.sp, color = GreenPrimary)
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                // Simular login
                                isLoading = true
                                // Aquí iría la lógica real
                                onLoginSuccess()
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            Text(stringResource(Res.string.login_btn), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // BOTÓN GOOGLE
                OutlinedButton(
                    onClick = onGoogleLoginClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = null, // Estilo limpio
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Icono Google (simulado con AccountCircle si no tienes el drawable)
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.Unspecified)
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(Res.string.login_google), color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // REGISTRO
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(Res.string.login_no_account), color = MaterialTheme.colorScheme.onSurface)
                    TextButton(onClick = onNavigateToRegister) {
                        Text(stringResource(Res.string.register_btn), color = GreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}