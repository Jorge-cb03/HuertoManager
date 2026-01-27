package com.example.proyecto.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.proyecto.ui.navigation.AppScreens
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

sealed class BottomNavItem(val resource: StringResource, val icon: ImageVector, val route: String) {
    object Home : BottomNavItem(Res.string.menu_home, Icons.Default.Home, AppScreens.Home)
    // FIX: Navegamos a "garden/0" para que no pete buscando el argumento
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
            // Comprobamos si la ruta actual empieza por "garden" para marcarlo como seleccionado
            val isSelected = currentRoute == item.route || (item is BottomNavItem.Garden && currentRoute?.startsWith("garden") == true)

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = title) },
                label = { Text(title) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        // FIX: Esto evita que el Home se quede pillado al limpiar la pila correctamente
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}