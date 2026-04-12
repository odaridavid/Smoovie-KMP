package dev.odaridavid.smoovie.ui.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.data.model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(viewModel: MoviesViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Smoovie") }) },
    ) { padding ->
        Box(
            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
        ) {
            when (val state = uiState) {
                is MoviesUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is MoviesUiState.Success -> {
                    MoviesList(movies = state.movies)
                }

                is MoviesUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = viewModel::loadMovies,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }
}

@Composable
private fun MoviesList(movies: List<Movie>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(movies, key = { it.id }) { movie ->
            MovieCard(movie = movie)
        }
    }
}

@Composable
private fun MovieCard(movie: Movie) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = movie.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                text = movie.overview,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "★ ${movie.voteAverage}",
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = movie.releaseDate,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "Failed to load movies", style = MaterialTheme.typography.titleMedium)
        Text(text = message, style = MaterialTheme.typography.bodySmall)
        Button(onClick = onRetry) { Text("Retry") }
    }
}
