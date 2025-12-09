package com.example.watchnext

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.watchnext.ui.theme.WatchNextTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Local UI model that tracks votes
data class VotableMovie(
    val movie: ImdbTitle,
    val voteCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VotingScreen(
    navController: NavHostController
) {
    val votableMovies = remember { mutableStateListOf<VotableMovie>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorState = remember { mutableStateOf<String?>(null) }

    // Load movies to vote on
    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) {
                MovieApi.service.getCastTitles("nm0000190")
            }

            // If later you add a real "status" like "To Watch", you can filter here.
            votableMovies.clear()
            votableMovies.addAll(
                response.map { movie ->
                    VotableMovie(movie = movie, voteCount = 0)
                }
            )
        } catch (e: Exception) {
            errorState.value = e.localizedMessage ?: "Unknown error"
        } finally {
            isLoading.value = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Top Picks") }
            )
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

                isLoading.value -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                votableMovies.isEmpty() -> {
                    Text(
                        text = "No movies to vote on.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    // Sort by voteCount descending
                    val sortedList = votableMovies.sortedByDescending { it.voteCount }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(sortedList) { item ->
                            val isTop =
                                sortedList.firstOrNull()?.movie == item.movie &&
                                        item.voteCount > 0

                            VotingRow(
                                votableMovie = item,
                                isTop = isTop,
                                onUpvote = {
                                    val index = votableMovies.indexOfFirst {
                                        it.movie == item.movie
                                    }
                                    if (index != -1) {
                                        val current = votableMovies[index]
                                        votableMovies[index] = current.copy(
                                            voteCount = current.voteCount + 1
                                        )
                                    }
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
fun VotingRow(
    votableMovie: VotableMovie,
    isTop: Boolean,
    onUpvote: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isTop) {
                        Text(
                            text = "👑",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }

                    Text(
                        text = votableMovie.movie.primaryTitle ?: "(No title)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Votes: ${votableMovie.voteCount}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(onClick = onUpvote) {
                Text("Upvote")
            }
        }
    }
}


