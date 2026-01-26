package com.example.proyecto.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme // Detectar modo oscuro
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // En modo oscuro eliminamos el degradado verde para que sea "de verdad oscuro"
        val backgroundModifier = if (isDark) {
            Modifier.fillMaxSize()
        } else {
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(GreenPrimary, GreenSecondary)))
        }

        Box(
            modifier = backgroundModifier,
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(32.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.Eco, null, modifier = Modifier.size(64.dp), tint = GreenPrimary)
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(Res.string.login_title), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(stringResource(Res.string.login_user_hint), fontSize = 16.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(Res.string.login_pass_hint), fontSize = 16.sp,) },
                        visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passVisible = !passVisible }) {
                                Icon(if (passVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                delay(2000)
                                isLoading = false
                                onLoginSuccess()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        enabled = !isLoading
                    ) {
                        Text(stringResource(Res.string.login_btn))
                    }

                    Spacer(Modifier.height(10.dp))
                    TextButton(onClick = {}) {
                        Text(stringResource(Res.string.login_forgot), color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
                    }
                    // Botón para ir al Registro
                    TextButton(onClick = onNavigateToRegister) {
                        Text(stringResource(Res.string.login_no_account), color = GreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        HuertaLoading(isLoading = isLoading)
    }
}