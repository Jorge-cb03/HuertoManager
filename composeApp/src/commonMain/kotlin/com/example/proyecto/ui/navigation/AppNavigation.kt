package com.example.proyecto.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// IMPORTS
import com.example.proyecto.ui.components.BottomMenu
import com.example.proyecto.ui.login.LoginScreen
import com.example.proyecto.ui.home.HomeScreen
import com.example.proyecto.ui.products.ProductsScreen
import com.example.proyecto.ui.garden.GardenScreen
import com.example.proyecto.ui.garden.GardenSlotDetailScreen
import com.example.proyecto.ui.diary.DiaryScreen
import com.example.proyecto.ui.profile.ProfileScreen

object AppScreens {
    const val Login = "login"
    const val Home = "home"
    const val Garden = "garden"       // Cuadrícula de Jardineras
    const val Diary = "diary"         // Calendario/Diario
    const val Products = "products"   // Inventario (Herramientas/Semillas)
    const val Profile = "profile"

    const val ProductDetail = "product_detail"
    // Ruta dinámica para el detalle de un hueco
    const val GardenSlotDetail = "garden_slot_detail/{slotName}"
    fun createSlotDetailRoute(slotName: String) = "garden_slot_detail/$slotName"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != AppScreens.Login

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                BottomMenu(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreens.Login,
            modifier = Modifier.padding(innerPadding)
        ) {
            // LOGIN
            composable(AppScreens.Login) {
                LoginScreen(onLoginSuccess = {
                    navController.navigate(AppScreens.Home) { popUpTo(AppScreens.Login) { inclusive = true } }
                })
            }

            // HOME
            composable(AppScreens.Home) { HomeScreen() }

            // JARDINERAS (Tu cuadrícula de 8 huecos)
            composable(AppScreens.Garden) {
                GardenScreen(navController = navController)
                // Nota: Dentro de GardenScreen debes usar:
                // navController.navigate(AppScreens.createSlotDetailRoute("A1"))
                // cuando se haga clic en un hueco lleno.
            }

            // DETALLE DEL HUECO (Timeline del tomate, etc.)
            composable(
                route = AppScreens.GardenSlotDetail,
                arguments = listOf(navArgument("slotName") { type = NavType.StringType })
            ) { backStackEntry ->
                val slotName = backStackEntry.arguments?.getString("slotName") ?: "Desconocido"
                GardenSlotDetailScreen(navController = navController, slotName = slotName)
            }

            // DIARIO (Calendario)
            composable(AppScreens.Diary) { DiaryScreen() }

            // INVENTARIO (Antes productos, ahora herramientas/semillas)
            composable(AppScreens.Products) {
                ProductsScreen(
                    navController = navController,
                    onAddProduct = { /* Navegar a añadir item */ }
                )
            }

            // PERFIL
            composable(AppScreens.Profile) {
                ProfileScreen()
            }
        }
    }
}

@Composable
fun PantallaEnConstruccion(nombre: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pantalla $nombre", color = MaterialTheme.colorScheme.onBackground)
    }
}