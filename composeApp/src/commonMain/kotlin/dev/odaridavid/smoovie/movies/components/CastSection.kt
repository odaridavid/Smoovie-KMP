package dev.odaridavid.smoovie.movies.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import dev.odaridavid.smoovie.movies.CastMemberUiModel
import dev.odaridavid.smoovie.person.PersonSummaryUiModel
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import previewCast
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.cast_section_title

@Composable
internal fun CastSection(
    cast: List<CastMemberUiModel>,
    modifier: Modifier = Modifier,
    onPersonClick: (PersonSummaryUiModel) -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.cast_section_title),
            style = MaterialTheme.typography.titleMedium,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(cast, key = { it.id }) { member ->
                CastMemberItem(
                    member = member,
                    onClick = {
                        onPersonClick(
                            PersonSummaryUiModel(
                                id = member.id,
                                name = member.name,
                                profileUrl = member.profileUrl,
                            ),
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun CastMemberItem(
    member: CastMemberUiModel,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp).clickable(onClick = onClick),
    ) {
        if (member.profileUrl != null) {
            SubcomposeAsyncImage(
                model = member.profileUrl,
                contentDescription = member.name,
                contentScale = ContentScale.Crop,
                loading = { ProfilePlaceholder() },
                error = { ProfilePlaceholder() },
                modifier =
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape),
            )
        } else {
            ProfilePlaceholder()
        }
        Text(
            text = member.name,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp),
        )
        if (member.character.isNotBlank()) {
            Text(
                text = member.character,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ProfilePlaceholder() {
    Box(
        modifier =
            Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// region Previews

@PreviewLightDark
@Composable
private fun CastSectionPreview() {
    SmoovieTheme {
        CastSection(cast = previewCast)
    }
}

// endregion
