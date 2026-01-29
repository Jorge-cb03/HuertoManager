package com.example.proyecto.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// --- IMPORTS DE TODAS LAS PANTALLAS ---
import com.example.proyecto.ui.components.BottomMenu
import com.example.proyecto.ui.login.LoginScreen
import com.example.proyecto.ui.login.RegisterScreen
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
import com.example.proyecto.ui.diary.DiaryDetailScreen

object AppScreens {
    // Rutas simples
    const val Login = "login"
    const val Register = "register"
    const val Home = "home"
    const val Diary = "diary"
    const val Products = "products"
    const val Profile = "profile"
    const val Alerts = "alerts"
    const val About = "about"
    const val AddProduct = "add_product"

    // Rutas con argumentos (FIX: Soporte para índice de jardinera)
    const val DiaryDetail = "diary_detail/{taskId}"
    fun createDiaryDetailRoute(taskId: Long) = "diary_detail/$taskId"
    const val Garden = "garden/{gardenIndex}"
    fun createGardenRoute(index: Int) = "garden/$index"
    const val ProductDetail = "product_detail/{productId}"
    fun createProductDetailRoute(productId: String) = "product_detail/$productId"

    const val GardenSlotDetail = "garden_slot_detail/{slotName}"
    fun createSlotDetailRoute(slotName: String) = "garden_slot_detail/$slotName"

    const val AddDiaryEntry = "add_diary_entry/{dateMillis}?taskId={taskId}&title={title}&desc={desc}"
    fun createAddDiaryRoute(dateMillis: Long) = "add_diary_entry/$dateMillis"
}

// ... (manten los imports igual)

@Composable
fun AppNavigation(
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != AppScreens.Login && currentRoute != AppScreens.Register

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { if (showBottomBar) BottomMenu(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreens.Login,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. LOGIN
            composable(AppScreens.Login) {
                LoginScreen(
                    onLoginSuccess = { navController.navigate(AppScreens.Home) { popUpTo(AppScreens.Login) { inclusive = true } } },
                    onNavigateToRegister = { navController.navigate(AppScreens.Register) }
                )
            }

            // 2. REGISTRO
            composable(AppScreens.Register) {
                RegisterScreen(
                    onRegisterSuccess = { navController.navigate(AppScreens.Home) { popUpTo(AppScreens.Register) { inclusive = true } } },
                    onBackToLogin = { navController.popBackStack() }
                )
            }

            composable(AppScreens.Home) { HomeScreen(navController) }
            composable(AppScreens.Alerts) { AlertsScreen(navController) }

            // 5. JARDINERA
            composable(
                route = AppScreens.Garden,
                arguments = listOf(navArgument("gardenIndex") { type = NavType.IntType })
            ) { backStackEntry ->
                val index = backStackEntry.arguments?.getInt("gardenIndex") ?: 0
                GardenScreen(navController = navController, initialGardenIndex = index)
            }

            // 6. DETALLE HUECO (ID Real)
            composable(
                route = AppScreens.GardenSlotDetail,
                arguments = listOf(navArgument("slotName") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("slotName") ?: "0"
                GardenSlotDetailScreen(navController = navController, bancalId = id)
            }

            composable(AppScreens.Diary) { DiaryScreen(navController) }

            // 8. AÑADIR ENTRADA DIARIO (Corregido sin parámetros fantasma)
            composable(
                route = AppScreens.AddDiaryEntry,
                arguments = listOf(
                    navArgument("dateMillis") { type = NavType.LongType },
                    navArgument("taskId") { nullable = true; defaultValue = null },
                    navArgument("title") { nullable = true; defaultValue = null },
                    navArgument("desc") { nullable = true; defaultValue = null }
                )
            ) { backStackEntry ->
                AddDiaryEntryScreen(
                    navController = navController,
                    initialDateMillis = backStackEntry.arguments?.getLong("dateMillis") ?: 0L,
                    taskId = backStackEntry.arguments?.getString("taskId"),
                    initialTitle = backStackEntry.arguments?.getString("title"),
                    initialDesc = backStackEntry.arguments?.getString("desc")
                )
            }

            // 8. DETALLE DIARIO
            composable(
                route = AppScreens.DiaryDetail, // Debe ser "diary_detail/{taskId}"
                arguments = listOf(navArgument("taskId") { type = NavType.LongType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
                DiaryDetailScreen(navController, taskId)
            }

            composable(AppScreens.Products) { ProductsScreen(navController) }

            // 10. AÑADIR PRODUCTO (Limpio)
            composable(AppScreens.AddProduct) { AddProductScreen(navController = navController) }

            // 11. DETALLE PRODUCTO
            composable(
                route = AppScreens.ProductDetail,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: "0"
                ProductDetailScreen(navController = navController, productId = productId)
            }

            composable(AppScreens.Profile) {
                ProfileScreen(navController = navController, isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme)
            }

            composable(AppScreens.About) { AboutScreen(navController = navController) }
        }
    }
}