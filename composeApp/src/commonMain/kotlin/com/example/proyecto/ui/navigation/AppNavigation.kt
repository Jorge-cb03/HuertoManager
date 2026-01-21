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

// --- IMPORTS DE TODAS TUS PANTALLAS ---
import com.example.proyecto.ui.components.BottomMenu
import com.example.proyecto.ui.login.LoginScreen
import com.example.proyecto.ui.home.HomeScreen
import com.example.proyecto.ui.products.ProductsScreen
import com.example.proyecto.ui.products.ProductDetailScreen
import com.example.proyecto.ui.products.AddProductScreen
import com.example.proyecto.ui.garden.GardenScreen
import com.example.proyecto.ui.garden.GardenSlotDetailScreen
import com.example.proyecto.ui.diary.DiaryScreen
import com.example.proyecto.ui.diary.AddDiaryEntryScreen
import com.example.proyecto.ui.profile.ProfileScreen
import com.example.proyecto.ui.profile.AboutScreen
import com.example.proyecto.ui.alerts.AlertsScreen

object AppScreens {
    // Rutas simples
    const val Login = "login"
    const val Home = "home"
    const val Garden = "garden"
    const val Diary = "diary"
    const val Products = "products"
    const val Profile = "profile"
    const val Alerts = "alerts"
    const val About = "about"
    const val AddProduct = "add_product"

    // Rutas con argumentos (Detalles y Formularios)
    const val ProductDetail = "product_detail/{productId}"
    fun createProductDetailRoute(productId: String) = "product_detail/$productId"

    const val GardenSlotDetail = "garden_slot_detail/{slotName}"
    fun createSlotDetailRoute(slotName: String) = "garden_slot_detail/$slotName"

    const val AddDiaryEntry = "add_diary_entry/{dateMillis}"
    fun createAddDiaryRoute(dateMillis: Long) = "add_diary_entry/$dateMillis"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Ocultar menú inferior solo en el Login
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
            // 1. LOGIN
            composable(AppScreens.Login) {
                LoginScreen(onLoginSuccess = {
                    navController.navigate(AppScreens.Home) { popUpTo(AppScreens.Login) { inclusive = true } }
                })
            }

            // 2. HOME
            composable(AppScreens.Home) {
                HomeScreen(navController = navController)
            }

            // 3. AVISOS (ALERTS)
            composable(AppScreens.Alerts) {
                AlertsScreen(navController = navController)
            }

            // 4. JARDINERA (GRID)
            composable(AppScreens.Garden) {
                GardenScreen(navController = navController)
            }

            // 5. DETALLE HUECO JARDINERA
            composable(
                route = AppScreens.GardenSlotDetail,
                arguments = listOf(navArgument("slotName") { type = NavType.StringType })
            ) { backStackEntry ->
                val slotName = backStackEntry.arguments?.getString("slotName") ?: "Desconocido"
                GardenSlotDetailScreen(navController = navController, slotName = slotName)
            }

            // 6. DIARIO (CALENDARIO)
            composable(AppScreens.Diary) {
                DiaryScreen(navController = navController)
            }

            // 7. AÑADIR ENTRADA DIARIO
            composable(
                route = AppScreens.AddDiaryEntry,
                arguments = listOf(navArgument("dateMillis") { type = NavType.LongType })
            ) { backStackEntry ->
                val dateMillis = backStackEntry.arguments?.getLong("dateMillis") ?: 0L
                AddDiaryEntryScreen(navController = navController, initialDateMillis = dateMillis)
            }

            // 8. PRODUCTOS (INVENTARIO)
            composable(AppScreens.Products) {
                ProductsScreen(navController = navController)
            }

            // 9. AÑADIR PRODUCTO
            composable(AppScreens.AddProduct) {
                AddProductScreen(navController = navController)
            }

            // 10. DETALLE PRODUCTO
            composable(
                route = AppScreens.ProductDetail,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: "0"
                ProductDetailScreen(navController = navController, productId = productId)
            }

            // 11. PERFIL
            composable(AppScreens.Profile) {
                ProfileScreen(navController = navController)
            }

            // 12. ACERCA DE
            composable(AppScreens.About) {
                AboutScreen(navController = navController)
            }
        }
    }
}