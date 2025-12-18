package com.example.watchnext

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

// Optional: Enum for type
enum class ContentType { MOVIE, TV_SHOW }

/**
 * Add Movie / Show screen with bottom nav.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovieScreen(
    navController: NavHostController,
    onAddMovie: (ImdbTitle) -> Unit   // callback to add to HomeScreen list
) {
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ContentType.MOVIE) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Movie / Show") }
            )
        },
        bottomBar = {
            BottomMenuBar(navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown / type selector
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { type = ContentType.MOVIE },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == ContentType.MOVIE)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface
                    )
                ) { Text("Movie") }

                Button(
                    onClick = { type = ContentType.TV_SHOW },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == ContentType.TV_SHOW)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface
                    )
                ) { Text("TV Show") }
            }

            Spacer(Modifier.height(24.dp))

            // Button 1: Add manually
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val newItem = ImdbTitle(
                            id = title,
                            url = null,
                            primaryTitle = title,
                            originalTitle = null,
                            type = type.name.lowercase(),
                            description = null,
                            primaryImage = null,
                            releaseDate = null,
                            startYear = null,
                            runtimeMinutes = null,
                            genres = null,
                            averageRating = null,
                            numVotes = null
                        )
                        // Save to back stack so HomeScreen can pick it up
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("newMovie", newItem)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add Manually")
            }

            // Button 2: Placeholder for OMDb search
            Button(
                onClick = {
                    // TODO: Implement OMDb search
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Search via OMDb (optional)")
            }
        }
    }
}

/**
 * Preview function for AddMovieScreen with bottom nav.
 */
@Preview(showBackground = true)
@Composable
fun AddMovieScreenPreview() {
    val fakeNavController = rememberNavController()
    AddMovieScreen(
        navController = fakeNavController,
        onAddMovie = {} // No-op for preview
    )
}
