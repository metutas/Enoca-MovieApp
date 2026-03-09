package com.example.movieapp

// Bu sınıf API'den gelen ana listeyi tutar
data class MovieResponse(
    val results: List<Movie>
)

// Bu sınıf her bir filmin detaylarını tutar
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val release_date: String,
    val vote_average: Double,
    val genre_ids: List<Int> //
)