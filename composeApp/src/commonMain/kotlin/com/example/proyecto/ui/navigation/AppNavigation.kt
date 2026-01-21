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

// IMPORTS DE TUS PANTALLAS
import com.example.proyecto.ui.components.BottomMenu
import com.example.proyecto.ui.login.LoginScreen
import com.example.proyecto.ui.home.HomeScreen
import com.example.proyecto.ui.products.ProductsScreen
import com.example.proyecto.ui.garden.GardenScreen
import com.example.proyecto.ui.garden.GardenSlotDetailScreen
import com.example.proyecto.ui.diary.DiaryScreen
import com.example.proyecto.ui.profile.ProfileScreen
import com.example.proyecto.ui.products.AddProductScreen
import com.example.proyecto.ui.diary.AddDiaryEntryScreen
import com.example.proyecto.ui.alerts.AlertsScreen // Importa Alerts si lo usas

object AppScreens {
    const val Login = "login"
    const val Home = "home"
    const val Garden = "garden"
    const val Diary = "diary"
    const val Products = "products"
    const val Profile = "profile"

    const val ProductDetail = "product_detail"
    const val GardenSlotDetail = "garden_slot_detail/{slotName}"
    fun createSlotDetailRoute(slotName: String) = "garden_slot_detail/$slotName"

    // NUEVAS RUTAS
    const val AddProduct = "add_product"
    const val AddDiaryEntry = "add_diary_entry"
    const val Alerts = "alerts"
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
            composable(AppScreens.Home) {
                HomeScreen(navController = navController)
            }

            // AVISOS (Nueva)
            composable(AppScreens.Alerts) {
                AlertsScreen(navController = navController)
            }

            // JARDINERAS
            composable(AppScreens.Garden) {
                GardenScreen(navController = navController)
            }

            // DETALLE DEL HUECO
            composable(
                route = AppScreens.GardenSlotDetail,
                arguments = listOf(navArgument("slotName") { type = NavType.StringType })
            ) { backStackEntry ->
                val slotName = backStackEntry.arguments?.getString("slotName") ?: "Desconocido"
                GardenSlotDetailScreen(navController = navController, slotName = slotName)
            }

            // DIARIO (Corregido: Ahora pasamos navController)
            composable(AppScreens.Diary) {
                DiaryScreen(navController = navController)
            }

            // AÑADIR ENTRADA DIARIO (Nueva)
            composable(AppScreens.AddDiaryEntry) {
                AddDiaryEntryScreen(navController = navController)
            }

            // INVENTARIO / PRODUCTOS (Corregido: Ya no pide onAddProduct)
            composable(AppScreens.Products) {
                ProductsScreen(navController = navController)
            }

            // AÑADIR PRODUCTO (Nueva)
            composable(AppScreens.AddProduct) {
                AddProductScreen(navController = navController)
            }

            // PERFIL
            composable(AppScreens.Profile) {
                ProfileScreen()
            }
        }
    }
}