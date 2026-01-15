package com.example.proyecto.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.proyecto.ui.navigation.AppScreens

// Asegúrate de que las rutas ("home", "garden", "products"...) coinciden con AppScreens
sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Garden : BottomNavItem("Mi huerta", Icons.Default.Eco, AppScreens.Garden)
    object Products : BottomNavItem("Productos", Icons.Default.ShoppingCart, AppScreens.Products) // <--- ESTA ES LA CLAVE
    object Home : BottomNavItem("Inicio", Icons.Default.Home, AppScreens.Home)
    object Diary : BottomNavItem("Diario", Icons.Default.Edit, AppScreens.Diary)
    object Profile : BottomNavItem("Perfil", Icons.Default.Person, AppScreens.Profile)
}

@Composable
fun BottomMenu(navController: NavController) {
    val items = listOf(
        BottomNavItem.Garden,
        BottomNavItem.Products,
        BottomNavItem.Home,
        BottomNavItem.Diary,
        BottomNavItem.Profile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) }, // Puedes quitar esto si quieres solo iconos
                selected = currentRoute == item.route,
                onClick = {
                    // --- CORRECCIÓN AQUÍ ---
                    // Antes teníamos un IF que bloqueaba products. Lo hemos quitado.
                    // Ahora TODOS los botones navegan.

                    if (currentRoute != item.route) { // Evita recargar si ya estás ahí
                        navController.navigate(item.route) {
                            // Esto hace que al dar atrás vuelvas al Home
                            popUpTo(AppScreens.Home) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}