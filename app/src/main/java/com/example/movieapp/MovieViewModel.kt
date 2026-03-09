package com.example.movieapp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class MovieViewModel : ViewModel() {

    private val apiKey = "f4aa1a5667b30f7dbc8f83f02661e4ed"

    val nowPlayingMovies = mutableStateOf<List<Movie>>(emptyList())
    val popularMovies = mutableStateOf<List<Movie>>(emptyList())
    val topRatedMovies = mutableStateOf<List<Movie>>(emptyList())
    val upcomingMovies = mutableStateOf<List<Movie>>(emptyList())

    var searchResults = mutableStateOf<List<Movie>>(emptyList())


    init {
        fetchAllMovies()
    }

    private fun fetchAllMovies() {
        viewModelScope.launch {
            try {

                nowPlayingMovies.value = RetrofitInstance.api.getNowPlaying(apiKey).results
                popularMovies.value = RetrofitInstance.api.getPopular(apiKey).results
                topRatedMovies.value = RetrofitInstance.api.getTopRated(apiKey).results
                upcomingMovies.value = RetrofitInstance.api.getUpcoming(apiKey).results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }




    fun search(query: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.searchMovies(apiKey, query)
                searchResults.value = response.results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }





}