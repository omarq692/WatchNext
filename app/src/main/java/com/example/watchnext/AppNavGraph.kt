package com.example.watchnext

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.Text

@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {
        // 1) AUTH SCREEN
        composable("auth") {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        // Clear auth from back stack so user can't go back to it
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        // 2) HOME SCREEN
        composable("home") {
            HomeScreen(navController = navController)
        }

        // 3) DETAILS SCREEN
        composable("details") { backStackEntry ->
            // We stored the movie in the previous back stack entry's SavedStateHandle
            val movie =
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<ImdbTitle>("selectedMovie")

            if (movie != null) {
                MovieDetailScreen(
                    navController = navController,
                    movie = movie
                )
            } else {
                // Fallback UI so it doesn't crash if movie is missing
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No movie selected.")
                }
            }
        }

        // 4) VOTING SCREEN
        composable("voting") {
            VotingScreen(navController = navController)
        }
    }
}


