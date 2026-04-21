package dev.odaridavid.smoovie.person

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.movies.components.MovieCard
import dev.odaridavid.smoovie.theme.ErrorContent
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import previewPersonDetailUiModel
import previewPersonSummaryUiModel
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.error_person_detail_failed
import smoovie.composeapp.generated.resources.filmography_section_title
import smoovie.composeapp.generated.resources.navigate_back
import smoovie.composeapp.generated.resources.person_known_for_format

@Composable
fun PersonDetailScreen(
    viewModel: PersonDetailViewModel,
    person: PersonSummaryUiModel,
    onBack: () -> Unit,
    onMovieClick: (MovieUiModel) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    PersonDetailContent(
        person = person,
        state = state,
        onBack = onBack,
        onRetry = viewModel::loadPersonDetail,
        onMovieClick = onMovieClick,
    )
}

@Composable
internal fun PersonDetailContent(
    person: PersonSummaryUiModel,
    state: PersonDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onMovieClick: (MovieUiModel) -> Unit = {},
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        when (state) {
            is PersonDetailUiState.Loading -> {
                SummaryHeader(person = person, onBack = onBack)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is PersonDetailUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding =
                        PaddingValues(
                            bottom =
                                WindowInsets.navigationBars
                                    .asPaddingValues()
                                    .calculateBottomPadding() + 16.dp,
                        ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        DetailHeader(
                            detail = state.personDetail,
                            onBack = onBack,
                        )
                    }
                    if (state.personDetail.filmography.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(Res.string.filmography_section_title),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                        items(state.personDetail.filmography, key = { it.movie.id }) { entry ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                MovieCard(
                                    movie = entry.movie,
                                    onClick = { onMovieClick(entry.movie) },
                                )
                            }
                        }
                    }
                }
            }

            is PersonDetailUiState.Error -> {
                SummaryHeader(person = person, onBack = onBack)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    ErrorContent(
                        message = stringResource(Res.string.error_person_detail_failed),
                        onRetry = onRetry,
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryHeader(
    person: PersonSummaryUiModel,
    onBack: () -> Unit,
) {
    HeaderFrame(onBack = onBack) {
        PersonPhoto(profileUrl = person.profileUrl, name = person.name)
        Text(
            text = person.name,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailHeader(
    detail: PersonDetailUiModel,
    onBack: () -> Unit,
) {
    HeaderFrame(onBack = onBack) {
        PersonPhoto(profileUrl = detail.profileUrl, name = detail.name)
        Text(
            text = detail.name,
            style = MaterialTheme.typography.headlineSmall,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (detail.knownForDepartment.isNotBlank()) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(stringResource(Res.string.person_known_for_format, detail.knownForDepartment))
                    },
                )
            }
            if (detail.birthday.isNotBlank()) {
                AssistChip(onClick = {}, label = { Text(detail.birthday) })
            }
            if (detail.placeOfBirth.isNotBlank()) {
                AssistChip(onClick = {}, label = { Text(detail.placeOfBirth) })
            }
        }
        if (detail.biography.isNotBlank()) {
            Text(
                text = detail.biography,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun HeaderFrame(
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            content()
        }
        IconButton(
            onClick = onBack,
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(4.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.navigate_back),
            )
        }
    }
}

@Composable
private fun PersonPhoto(
    profileUrl: String?,
    name: String,
) {
    Box(modifier = Modifier.size(HEADER_IMAGE_SIZE)) {
        if (profileUrl != null) {
            SubcomposeAsyncImage(
                model = profileUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                loading = { ProfilePlaceholder() },
                error = { ProfilePlaceholder() },
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
            )
        } else {
            ProfilePlaceholder()
        }
    }
}

@Composable
private fun ProfilePlaceholder() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private val HEADER_IMAGE_SIZE = 160.dp

// region Previews

@PreviewLightDark
@Composable
private fun PersonDetailLoadingPreview() {
    SmoovieTheme {
        PersonDetailContent(
            person = previewPersonSummaryUiModel,
            state = PersonDetailUiState.Loading,
            onBack = {},
            onRetry = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun PersonDetailSuccessPreview() {
    SmoovieTheme {
        PersonDetailContent(
            person = previewPersonSummaryUiModel,
            state = PersonDetailUiState.Success(previewPersonDetailUiModel),
            onBack = {},
            onRetry = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun PersonDetailErrorPreview() {
    SmoovieTheme {
        PersonDetailContent(
            person = previewPersonSummaryUiModel,
            state = PersonDetailUiState.Error("Network error"),
            onBack = {},
            onRetry = {},
        )
    }
}

// endregion
