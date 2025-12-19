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

// Enum for type
enum class ContentType { MOVIE, TV_SHOW }

/**
 * Reusable segmented selector (Filled = selected, Outlined = unselected)
 */
@Composable
fun SegmentedSelector(
    options: List<String>,
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, text ->
            val selected = index == selectedIndex

            if (selected) {
                Button(
                    onClick = { onSelectedChange(index) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text)
                }
            } else {
                OutlinedButton(
                    onClick = { onSelectedChange(index) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text)
                }
            }
        }
    }
}

/**
 * Add Movie / Show screen with bottom nav.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovieScreen(
    navController: NavHostController,
    onAddMovie: (ImdbTitle) -> Unit
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

            // Segmented Movie / TV Show selector
            SegmentedSelector(
                options = listOf("Movie", "TV Show"),
                selectedIndex = if (type == ContentType.MOVIE) 0 else 1,
                onSelectedChange = { index ->
                    type = if (index == 0)
                        ContentType.MOVIE
                    else
                        ContentType.TV_SHOW
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // Add button
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

                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("newMovie", newItem)

                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add to WatchList")
            }
        }
    }
}

/**
 * Preview
 */
@Preview(showBackground = true)
@Composable
fun AddMovieScreenPreview() {
    val fakeNavController = rememberNavController()
    AddMovieScreen(
        navController = fakeNavController,
        onAddMovie = {}
    )
}
