package dev.odaridavid.smoovie.movies.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.movies.ReviewUiModel
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import dev.odaridavid.smoovie.utils.previewReviews
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.reviews_anonymous
import smoovie.composeapp.generated.resources.reviews_read_more
import smoovie.composeapp.generated.resources.reviews_section_title
import smoovie.composeapp.generated.resources.reviews_show_less

@Composable
internal fun ReviewsSection(
    reviews: List<ReviewUiModel>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.reviews_section_title),
            style = MaterialTheme.typography.titleMedium,
        )
        reviews.forEach { review ->
            ReviewCard(review = review)
        }
    }
}

@Composable
private fun ReviewCard(review: ReviewUiModel) {
    var expanded by rememberSaveable(review.id) { mutableStateOf(false) }
    var hasOverflow by rememberSaveable(review.id) { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .animateContentSize(
                        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                    ),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            ReviewHeader(review = review)
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (expanded) Int.MAX_VALUE else COLLAPSED_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { layout ->
                    if (!expanded && layout.hasVisualOverflow) {
                        hasOverflow = true
                    }
                },
            )
            if (hasOverflow) {
                TextButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(
                        text =
                            stringResource(
                                if (expanded) Res.string.reviews_show_less else Res.string.reviews_read_more,
                            ),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewHeader(review: ReviewUiModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = review.author.ifBlank { stringResource(Res.string.reviews_anonymous) },
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (review.rating.isNotBlank()) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = "★ ${review.rating}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    if (review.date.isNotBlank()) {
        Text(
            text = review.date,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private const val COLLAPSED_MAX_LINES = 4

// region Previews

@PreviewLightDark
@Composable
private fun ReviewsSectionPreview() {
    SmoovieTheme {
        ReviewsSection(
            reviews = previewReviews,
            modifier = Modifier.padding(16.dp),
        )
    }
}

// endregion
