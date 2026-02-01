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

import com.example.proyecto.ui.components.BottomMenu
import com.example.proyecto.ui.login.LoginScreen
import com.example.proyecto.ui.login.RegisterScreen
import com.example.proyecto.ui.home.HomeScreen
import com.example.proyecto.ui.products.*
import com.example.proyecto.ui.garden.*
import com.example.proyecto.ui.diary.*
import com.example.proyecto.ui.profile.*
import com.example.proyecto.ui.alerts.AlertsScreen

object AppScreens {
    const val Login = "login"
    const val Register = "register"
    const val Home = "home"
    const val Diary = "diary"
    const val Products = "products"
    const val Profile = "profile"
    const val Alerts = "alerts"
    const val About = "about"
    const val AddProduct = "add_product"

    // RUTAS CON PARÁMETROS
    const val Garden = "garden/{gardenId}"
    fun createGardenRoute(id: Long) = "garden/$id"

    const val GardenSlotDetail = "garden_slot_detail/{bancalId}"
    fun createSlotDetailRoute(id: String) = "garden_slot_detail/$id"

    const val ProductDetail = "product_detail/{productId}"
    fun createProductDetailRoute(id: String) = "product_detail/$id"

    const val AddDiaryEntry = "add_diary_entry/{dateMillis}"
    fun createAddDiaryRoute(dateMillis: Long) = "add_diary_entry/$dateMillis"
}

@Composable
fun AppNavigation(isDarkTheme: Boolean, onToggleTheme: (Boolean) -> Unit) {
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
            composable(AppScreens.Login) {
                LoginScreen(
                    onLoginSuccess = { navController.navigate(AppScreens.Home) { popUpTo(AppScreens.Login) { inclusive = true } } },
                    onNavigateToRegister = { navController.navigate(AppScreens.Register) },
                    onGoogleLoginClick = { }
                )
            }
            composable(AppScreens.Register) {
                RegisterScreen(onRegisterSuccess = { navController.navigate(AppScreens.Home) { popUpTo(AppScreens.Register) { inclusive = true } } }, onBackToLogin = { navController.popBackStack() })
            }

            composable(AppScreens.Home) { HomeScreen(navController) }
            composable(AppScreens.Alerts) { AlertsScreen(navController) }

            composable(
                route = AppScreens.Garden,
                arguments = listOf(navArgument("gardenId") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("gardenId") ?: 0L
                GardenScreen(navController = navController, initialGardenId = id)
            }

            composable(
                route = AppScreens.GardenSlotDetail,
                arguments = listOf(navArgument("bancalId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("bancalId") ?: "0"
                GardenSlotDetailScreen(navController = navController, bancalId = id)
            }

            composable(AppScreens.Diary) { DiaryScreen(navController) }
            composable(
                route = AppScreens.AddDiaryEntry,
                arguments = listOf(navArgument("dateMillis") { type = NavType.LongType })
            ) { backStackEntry ->
                AddDiaryEntryScreen(navController = navController, initialDateMillis = backStackEntry.arguments?.getLong("dateMillis") ?: 0L)
            }

            composable(AppScreens.Products) { ProductsScreen(navController) }
            composable(AppScreens.AddProduct) { AddProductScreen(navController) }
            composable(
                route = AppScreens.ProductDetail,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("productId") ?: "0"
                ProductDetailScreen(navController = navController, productId = id)
            }

            composable(AppScreens.Profile) { ProfileScreen(navController, isDarkTheme, onToggleTheme) }
            composable(AppScreens.About) { AboutScreen(navController) }
        }
    }
}