package com.example.proyecto.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.ui.HuertaLoading // <--- IMPORTANTE
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.GreenSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }

    // Estado de carga y CoroutineScope
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(GreenSecondary, GreenPrimary)))) {
        // TARJETA CENTRAL
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

                OutlinedTextField(
                    value = user, onValueChange = { user = it },
                    label = { Text(stringResource(Res.string.login_user_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
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

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        // SIMULACIÓN DE CARGA (2 SEGUNDOS)
                        scope.launch {
                            isLoading = true
                            delay(2000) // Espera 2 segundos
                            isLoading = false
                            onLoginSuccess() // Navega al Home
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    enabled = !isLoading // Deshabilitar si carga
                ) {
                    Text(stringResource(Res.string.login_btn))
                }

                Spacer(Modifier.height(10.dp))
                TextButton(onClick = {}) { Text(stringResource(Res.string.login_forgot), color = Color.Gray, fontSize = 12.sp) }
                TextButton(onClick = {}) { Text(stringResource(Res.string.login_no_account), color = GreenPrimary, fontWeight = FontWeight.Bold) }
            }
        }

        // INDICADOR DE CARGA (FLOTANTE)
        HuertaLoading(isLoading = isLoading)
    }
}