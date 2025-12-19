package com.example.watchnext

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController
) {
    val moviesState = remember { mutableStateOf<List<ImdbTitle>?>(null) }
    val errorState = remember { mutableStateOf<String?>(null) }

    // --- Add this block to handle a new movie returned from AddMovieScreen ---
    val newMovie = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<ImdbTitle>("newMovie")

    newMovie?.let {
        moviesState.value = listOf(it) + (moviesState.value ?: emptyList())
        navController.currentBackStackEntry?.savedStateHandle?.remove<ImdbTitle>("newMovie")
    }
    // --------------------------------------------------------------------------

    // Load movies once
    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) {
                MovieApi.service.getCastTitles("nm0000190")
            }
            moviesState.value = response
        } catch (e: Exception) {
            errorState.value = e.localizedMessage ?: "Unknown error"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trending") },
                actions = {
                    TextButton(
                        onClick = { navController.navigate("voting") }
                    ) {
                        Text("Voting")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addMovie") // <-- now navigates to Add Movie screen
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add movie")
            }
        },
        bottomBar = {
            BottomMenuBar(navController = navController)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                errorState.value != null -> {
                    Text(
                        text = "Error: ${errorState.value}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                moviesState.value == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                moviesState.value!!.isEmpty() -> {
                    Text(
                        text = "No movies yet.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(moviesState.value!!) { movie ->
                            MovieRow(
                                movie = movie,
                                onClick = {
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("selectedMovie", movie)
                                    navController.navigate("details")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomMenuBar(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomMenuItem("Trending") { navController.navigate("home") }
                BottomMenuItem("Votes") { navController.navigate("voting") }
                BottomMenuItem("Add") { navController.navigate("addMovie") } // optional
                BottomMenuItem("WatchList") { navController.navigate("watchList") }
                BottomMenuItem("Settings") { navController.navigate("settings") }
            }
        }
    }
}

@Composable
private fun BottomMenuItem(
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun MovieRow(
    movie: ImdbTitle,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            if (movie.primaryImage != null) {
                AsyncImage(
                    model = movie.primaryImage,
                    contentDescription = movie.primaryTitle,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.primaryTitle ?: "(No title)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                val yearText = movie.startYear?.toString() ?: "Unknown year"
                Text(
                    text = yearText,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = movie.description ?: "",
                    maxLines = 2,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(64.dp)
            ) {
                movie.averageRating?.let {
                    Text(
                        text = "★ $it",
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "To Watch",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
