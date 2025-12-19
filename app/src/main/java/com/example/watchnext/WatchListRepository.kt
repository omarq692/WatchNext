package com.example.watchnext

import androidx.compose.runtime.mutableStateListOf

object WatchListRepository {
    // Global watchlist that all screens can see
    val watchList = mutableStateListOf<ImdbTitle>()

    fun addMovie(movie: ImdbTitle) {
        // Avoid duplicates by id
        if (watchList.none { it.id == movie.id }) {
            watchList.add(0, movie)
        }
    }

    fun removeMovie(movie: ImdbTitle) {
        watchList.removeAll { it.id == movie.id }
    }
}