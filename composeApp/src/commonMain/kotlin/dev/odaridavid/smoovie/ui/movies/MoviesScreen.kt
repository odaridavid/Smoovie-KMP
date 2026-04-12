package dev.odaridavid.smoovie.ui.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.data.model.Movie
import dev.odaridavid.smoovie.ui.theme.ErrorContent
import dev.odaridavid.smoovie.ui.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import previewMovies
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.app_name

@Composable
fun MoviesScreen(viewModel: MoviesViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    MoviesContent(uiState = uiState, onRetry = viewModel::loadMovies)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoviesContent(
    uiState: MoviesUiState,
    onRetry: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.app_name)) }) },
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
                        onRetry = onRetry,
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

// region Previews

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MoviesLoadingPreview() {
    SmoovieTheme {
        MoviesContent(uiState = MoviesUiState.Loading, onRetry = {})
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MoviesSuccessPreview() {
    SmoovieTheme {
        MoviesContent(uiState = MoviesUiState.Success(previewMovies), onRetry = {})
    }
}

// endregion
