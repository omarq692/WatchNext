package com.example.watchnext

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    navController: NavHostController,
    movie: ImdbTitle
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = movie.primaryTitle ?: "No title") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomMenuBar(navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Poster
            movie.primaryImage?.let { posterUrl ->
                AsyncImage(
                    model = posterUrl,
                    contentDescription = movie.primaryTitle,
                    modifier = Modifier
                        .fillMaxSize()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(16.dp))
            }

            // Title
            Text(
                text = movie.primaryTitle ?: "(No title)",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            // Year / runtime / genres
            movie.releaseDate?.let {
                Spacer(Modifier.height(4.dp))
                Text("Release date: $it")
            }

            movie.runtimeMinutes?.let {
                Spacer(Modifier.height(4.dp))
                Text("Runtime: ${it} min")
            }

            movie.genres?.takeIf { it.isNotEmpty() }?.let { genres ->
                Spacer(Modifier.height(8.dp))
                Text("Genres: ${genres.joinToString()}")
            }

            Spacer(Modifier.height(16.dp))

            // Description
            Text(
                text = movie.description ?: "No description available.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))

            // “Trailer” / more info – opens IMDb page
            movie.url?.let { imdbUrl ->
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(imdbUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Watch trailer / see more on IMDb")
                }
            }
        }
    }
}
