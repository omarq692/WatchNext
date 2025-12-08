package com.example.watchnext

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable(route = "login") {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable(route = "home") {
            HomeScreen(navController = navController)
        }

        // 🔥 NEW: details screen route
        composable(route = "details") {
            // We stored the movie in the previous back stack entry’s savedStateHandle
            val movie = navController
                .previousBackStackEntry
                ?.savedStateHandle
                ?.get<ImdbTitle>("selectedMovie")

            if (movie != null) {
                MovieDetailScreen(
                    movie = movie,
                    onBack = { navController.popBackStack() }
                )
            } else {
                // simple fallback so it won't crash if something goes wrong
                androidx.compose.material3.Text("No movie selected.")
            }
        }
    }
}