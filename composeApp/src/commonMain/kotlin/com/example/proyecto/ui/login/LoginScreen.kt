package com.example.proyecto.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.ui.HuertaInput
import com.example.proyecto.ui.theme.GreenPrimary
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Spa, null, tint = GreenPrimary, modifier = Modifier.size(40.dp))
        }

        Spacer(Modifier.height(24.dp))
        // TEXTOS CAMBIADOS A RECURSOS
        Text(stringResource(Res.string.login_title), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text(stringResource(Res.string.login_subtitle), color = MaterialTheme.colorScheme.secondary)

        Spacer(Modifier.height(40.dp))

        HuertaInput(email, { email = it }, stringResource(Res.string.login_user_hint), Icons.Filled.Email)
        Spacer(Modifier.height(16.dp))
        HuertaInput(password, { password = it }, stringResource(Res.string.login_pass_hint), Icons.Filled.Lock, isPassword = true)

        Text(
            stringResource(Res.string.login_forgot),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.align(Alignment.End).padding(top = 10.dp),
            fontSize = 12.sp
        )

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = onLoginSuccess,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(stringResource(Res.string.login_btn), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
        ) {
            Text("G   " + stringResource(Res.string.login_google))
        }
    }
}