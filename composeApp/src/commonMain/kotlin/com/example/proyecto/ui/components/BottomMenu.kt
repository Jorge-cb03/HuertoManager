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
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import huertomanager.composeapp.generated.resources.*

sealed class BottomNavItem(val resource: StringResource, val icon: ImageVector, val route: String) {
    object Home : BottomNavItem(Res.string.menu_home, Icons.Default.Home, AppScreens.Home)
    object Garden : BottomNavItem(Res.string.menu_garden, Icons.Default.Eco, "garden/0")
    object Diary : BottomNavItem(Res.string.menu_diary, Icons.Default.Edit, AppScreens.Diary)
    object Products : BottomNavItem(Res.string.menu_products, Icons.Default.ShoppingCart, AppScreens.Products)
    object Profile : BottomNavItem(Res.string.menu_profile, Icons.Default.Person, AppScreens.Profile)
}

@Composable
fun BottomMenu(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Garden,
        BottomNavItem.Diary,
        BottomNavItem.Products,
        BottomNavItem.Profile
    )

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val title = stringResource(item.resource)

            // MEJORA: Marcamos el icono como seleccionado incluso si estamos en una sub-pantalla (añadir/editar)
            val isSelected = when (item) {
                BottomNavItem.Home -> currentRoute == AppScreens.Home || currentRoute == AppScreens.Alerts
                BottomNavItem.Garden -> currentRoute?.contains("garden") == true
                BottomNavItem.Diary -> currentRoute?.contains("diary") == true
                BottomNavItem.Products -> currentRoute?.contains("product") == true
                BottomNavItem.Profile -> currentRoute == AppScreens.Profile || currentRoute == AppScreens.About
            }

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = title) },
                label = { Text(title) },
                selected = isSelected,
                onClick = {
                    // NAVEGACIÓN PRIORITARIA
                    navController.navigate(item.route) {
                        // Limpiamos la pila hasta el Home para cerrar cualquier sub-ventana abierta
                        popUpTo(AppScreens.Home) {
                            saveState = false // NO guardamos el estado de la ventana de "añadir/editar"
                        }
                        // Evita duplicar la pantalla si ya estás en la raíz de la sección
                        launchSingleTop = true
                        // NO restauramos el estado para forzar que aparezca la vista principal (lista/calendario)
                        restoreState = false
                    }
                }
            )
        }
    }
}