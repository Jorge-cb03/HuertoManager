package com.example.proyecto.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Person
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto.di.AppModule
import com.example.proyecto.ui.HuertaLoading
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.GreenSecondary
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    // Inyectamos el nuevo ViewModel
    viewModel: LoginViewModel = viewModel { LoginViewModel(AppModule.authRepository) }
) {
    // Si el usuario ya estaba logueado, saltamos directos (opcional, pero buena UX)
    LaunchedEffect(Unit) {
        if (viewModel.isUserLoggedIn()) {
            onLoginSuccess()
        }
    }

    // Estados UI
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }

    // Observamos el ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.loginError.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(GreenSecondary, GreenPrimary)))) {
        Card(
            modifier = Modifier.align(Alignment.Center).padding(20.dp).fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Filled.Eco, null, tint = GreenPrimary, modifier = Modifier.size(60.dp))
                Spacer(Modifier.height(10.dp))
                Text(stringResource(Res.string.login_title), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                Text(stringResource(Res.string.login_subtitle), fontSize = 14.sp, color = Color.Gray)

                Spacer(Modifier.height(30.dp))

                // CAMPOS DE TEXTO (Visuales por ahora)
                OutlinedTextField(
                    value = user, onValueChange = { user = it },
                    label = { Text(stringResource(Res.string.login_user_hint)) },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = pass, onValueChange = { pass = it },
                    label = { Text(stringResource(Res.string.login_pass_hint)) },
                    singleLine = true,
                    visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passVisible = !passVisible }) {
                            Icon(if (passVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMsg != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(text = errorMsg!!, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(Modifier.height(20.dp))

                // BOTÓN NORMAL (Deshabilitado visualmente para forzar el uso del anónimo en pruebas)
                Button(
                    onClick = { /* Lógica futura de usuario/pass */ },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    enabled = false // Desactivado hasta Fase 4 real
                ) {
                    Text(stringResource(Res.string.login_btn))
                }

                Spacer(Modifier.height(20.dp))
                HorizontalDivider()
                Spacer(Modifier.height(20.dp))

                // --- BOTÓN LOGIN ANÓNIMO (DEV) ---
                OutlinedButton(
                    onClick = {
                        viewModel.loginAnonymously(onSuccess = onLoginSuccess)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenPrimary)
                ) {
                    Icon(Icons.Filled.Person, null)
                    Spacer(Modifier.width(8.dp))
                    Text("ACCESO INVITADO (DEV MODE)")
                }
            }
        }

        HuertaLoading(isLoading = isLoading)
    }
}