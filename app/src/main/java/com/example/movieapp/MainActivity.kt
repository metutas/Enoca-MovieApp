package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource // ÇOKLU DİL İÇİN GEREKLİ
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                MovieAppNavigation()
            }
        }
    }
}

@Composable
fun MovieAppNavigation() {
    val navController = rememberNavController()
    val viewModel: MovieViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            MovieAppScreen(viewModel, onMovieClick = { movieId ->
                navController.navigate("detail/$movieId")
            })
        }
        composable(
            "detail/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0

            val allMovies = viewModel.nowPlayingMovies.value + viewModel.popularMovies.value +
                    viewModel.topRatedMovies.value + viewModel.upcomingMovies.value
            val movie = allMovies.find { it.id == movieId }

            movie?.let { MovieDetailScreen(it, onBack = { navController.popBackStack() }) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieAppScreen(movieViewModel: MovieViewModel, onMovieClick: (Int) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.app_name), // TÜRKÇE/İNGİLİZCE OTOMATİK DEĞİŞECEK
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                if (it.isNotEmpty()) movieViewModel.search(it)
            },
            label = { Text(stringResource(id = R.string.search_hint)) }, // TÜRKÇE/İNGİLİZCE OTOMATİK DEĞİŞECEK
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        )

        if (searchQuery.isNotEmpty()) {
            LazyColumn {
                items(movieViewModel.searchResults.value) { movie ->
                    MovieListItem(movie, onClick = { onMovieClick(movie.id) })
                }
            }
        } else {
            LazyColumn {
                item { MovieSection(stringResource(id = R.string.now_playing), movieViewModel.nowPlayingMovies.value, onMovieClick) }
                item { MovieSection(stringResource(id = R.string.popular), movieViewModel.popularMovies.value, onMovieClick) }
                item { MovieSection(stringResource(id = R.string.top_rated), movieViewModel.topRatedMovies.value, onMovieClick) }
                item { MovieSection(stringResource(id = R.string.upcoming), movieViewModel.upcomingMovies.value, onMovieClick) }
            }
        }
    }
}

@Composable
fun MovieSection(title: String, movies: List<Movie>, onMovieClick: (Int) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(movies) { movie ->
                MovieCard(movie, onClick = { onMovieClick(movie.id) })
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Card(modifier = Modifier.width(130.dp).height(220.dp).clickable { onClick() }) {
        Column {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
                contentDescription = null,
                modifier = Modifier.height(180.dp).fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(text = movie.title, modifier = Modifier.padding(4.dp), maxLines = 1, fontSize = 12.sp)
        }
    }
}

@Composable
fun MovieListItem(movie: Movie, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() }) {
        AsyncImage(model = "https://image.tmdb.org/t/p/w200${movie.poster_path}", contentDescription = null, modifier = Modifier.size(60.dp))
        Text(text = movie.title, modifier = Modifier.padding(start = 16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(movie: Movie, onBack: () -> Unit) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.movie_detail)) }, // TÜRKÇE/İNGİLİZCE
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/original${movie.poster_path}",
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(300.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = movie.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)

            // YAYIN TARİHİ VE PUAN KISMI DA DİLE BAĞLANDI
            Text(text = "${stringResource(id = R.string.release_date)} ${movie.release_date}", fontSize = 14.sp, color = Color.Gray)
            Text(text = "${stringResource(id = R.string.rating)} ${movie.vote_average}", fontSize = 14.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = movie.overview, fontSize = 16.sp)
        }
    }
}