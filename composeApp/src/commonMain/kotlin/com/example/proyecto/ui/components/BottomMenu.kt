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

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    // CORRECCIÓN: Se usa AppScreens.Gardens (plural), que es como se llama en AppNavigation.kt
    object Garden : BottomNavItem("Mi huerta", Icons.Default.Eco, AppScreens.Gardens)
    object Products : BottomNavItem("Productos", Icons.Default.ShoppingCart, AppScreens.Products)
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
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Evita acumular pantallas en la pila al navegar entre pestañas principales
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