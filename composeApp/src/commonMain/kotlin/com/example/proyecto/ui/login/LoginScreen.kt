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
    onGoogleLoginClick: () -> Unit // <--- Nueva lambda
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        val backgroundModifier = if (isDark) Modifier.fillMaxSize()
        else Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(GreenPrimary, GreenSecondary)))

        Box(modifier = backgroundModifier, contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier.padding(32.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Eco, null, modifier = Modifier.size(64.dp), tint = GreenPrimary)
                    Spacer(Modifier.height(16.dp))
                    Text("Bienvenido", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(24.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())

                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
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
                        onClick = { /* Tu lógica de login normal */ },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Text("Entrar")
                    }

                    // --- BOTÓN DE GOOGLE ---
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onGoogleLoginClick,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = GreenPrimary)
                            Spacer(Modifier.width(12.dp))
                            Text("Continuar con Google", color = Color.Black)
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    TextButton(onClick = onNavigateToRegister) {
                        Text("¿No tienes cuenta? Regístrate", color = GreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}