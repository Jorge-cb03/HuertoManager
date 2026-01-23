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
// IMPORTANTE: Estos imports para usar los strings
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

// Ahora BottomNavItem guarda una referencia al recurso, no el texto directo
sealed class BottomNavItem(val resource: StringResource, val icon: ImageVector, val route: String) {
    object Garden : BottomNavItem(Res.string.menu_garden, Icons.Default.Eco, AppScreens.Garden)
    object Products : BottomNavItem(Res.string.menu_products, Icons.Default.ShoppingCart, AppScreens.Products)
    object Home : BottomNavItem(Res.string.menu_home, Icons.Default.Home, AppScreens.Home)
    object Diary : BottomNavItem(Res.string.menu_diary, Icons.Default.Edit, AppScreens.Diary)
    object Profile : BottomNavItem(Res.string.menu_profile, Icons.Default.Person, AppScreens.Profile)
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
            val title = stringResource(item.resource) // Obtiene el texto según el idioma del móvil
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = title) },
                label = { Text(title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
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