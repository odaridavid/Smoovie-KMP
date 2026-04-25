package dev.odaridavid.smoovie.person

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.movies.components.MovieCard
import dev.odaridavid.smoovie.person.components.TvBadge
import dev.odaridavid.smoovie.shows.TvShowUiModel
import dev.odaridavid.smoovie.shows.components.TvShowCard
import dev.odaridavid.smoovie.theme.ErrorContent
import dev.odaridavid.smoovie.ui.SetStatusBarIcons
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.error_person_detail_failed
import smoovie.composeapp.generated.resources.media_type_movies
import smoovie.composeapp.generated.resources.media_type_tv_shows
import smoovie.composeapp.generated.resources.navigate_back

@Composable
fun PersonFilmographyScreen(
    viewModel: PersonFilmographyViewModel,
    personName: String,
    mediaType: PersonFilmographyMediaType,
    onBack: () -> Unit,
    onMovieClick: (MovieUiModel) -> Unit,
    onTvShowClick: (TvShowUiModel) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    PersonFilmographyContent(
        personName = personName,
        mediaType = mediaType,
        state = state,
        onBack = onBack,
        onRetry = viewModel::load,
        onMovieClick = onMovieClick,
        onTvShowClick = onTvShowClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PersonFilmographyContent(
    personName: String,
    mediaType: PersonFilmographyMediaType,
    state: PersonFilmographyUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onMovieClick: (MovieUiModel) -> Unit = {},
    onTvShowClick: (TvShowUiModel) -> Unit = {},
) {
    SetStatusBarIcons(useDarkIcons = !isSystemInDarkTheme())
    val sectionTitle =
        stringResource(
            when (mediaType) {
                PersonFilmographyMediaType.MOVIE -> Res.string.media_type_movies
                PersonFilmographyMediaType.TV -> Res.string.media_type_tv_shows
            },
        )
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "$personName · $sectionTitle",
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.navigate_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (state) {
                is PersonFilmographyUiState.Loading -> Unit

                is PersonFilmographyUiState.Success -> {
                    val bottomInset =
                        WindowInsets.navigationBars
                            .asPaddingValues()
                            .calculateBottomPadding()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding =
                            PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 16.dp + bottomInset,
                            ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        when (mediaType) {
                            PersonFilmographyMediaType.MOVIE -> {
                                items(state.personDetail.movieFilmography, key = { it.movie.id }) { item ->
                                    MovieCard(
                                        movie = item.movie,
                                        onClick = { onMovieClick(item.movie) },
                                    )
                                }
                            }

                            PersonFilmographyMediaType.TV -> {
                                items(state.personDetail.tvFilmography, key = { it.tvShow.id }) { item ->
                                    Box {
                                        TvShowCard(
                                            tvShow = item.tvShow,
                                            onClick = { onTvShowClick(item.tvShow) },
                                        )
                                        TvBadge(
                                            modifier =
                                                Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(8.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is PersonFilmographyUiState.Error -> {
                    ErrorContent(
                        error = state.error,
                        title = stringResource(Res.string.error_person_detail_failed),
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }
}
