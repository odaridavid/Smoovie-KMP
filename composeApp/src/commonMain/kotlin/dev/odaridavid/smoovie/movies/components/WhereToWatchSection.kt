package dev.odaridavid.smoovie.movies.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import dev.odaridavid.smoovie.shared.WatchProviderUiModel
import dev.odaridavid.smoovie.theme.SmoovieTheme
import dev.odaridavid.smoovie.utils.previewWatchProviders
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.action_view_all
import smoovie.composeapp.generated.resources.where_to_watch_provider_logo_description
import smoovie.composeapp.generated.resources.where_to_watch_rent_buy_label
import smoovie.composeapp.generated.resources.where_to_watch_section_title
import smoovie.composeapp.generated.resources.where_to_watch_stream_label

@Composable
internal fun WhereToWatchSection(
    streamingProviders: List<WatchProviderUiModel>,
    rentBuyProviders: List<WatchProviderUiModel>,
    link: String?,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.where_to_watch_section_title),
                style = MaterialTheme.typography.titleMedium,
            )
            if (!link.isNullOrBlank()) {
                TextButton(onClick = { uriHandler.openUri(link) }) {
                    Text(stringResource(Res.string.action_view_all))
                }
            }
        }
        if (streamingProviders.isNotEmpty()) {
            ProviderRow(
                label = stringResource(Res.string.where_to_watch_stream_label),
                providers = streamingProviders,
            )
        }
        if (rentBuyProviders.isNotEmpty()) {
            ProviderRow(
                label = stringResource(Res.string.where_to_watch_rent_buy_label),
                providers = rentBuyProviders,
            )
        }
    }
}

@Composable
private fun ProviderRow(
    label: String,
    providers: List<WatchProviderUiModel>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(providers, key = { it.name }) { provider ->
                ProviderItem(provider = provider)
            }
        }
    }
}

@Composable
private fun ProviderItem(provider: WatchProviderUiModel) {
    if (provider.logoUrl != null) {
        SubcomposeAsyncImage(
            model = provider.logoUrl,
            contentDescription = stringResource(Res.string.where_to_watch_provider_logo_description, provider.name),
            contentScale = ContentScale.Crop,
            loading = { ProviderLogoPlaceholder() },
            error = { ProviderLogoPlaceholder() },
            modifier =
                Modifier
                    .size(PROVIDER_LOGO_SIZE)
                    .clip(RoundedCornerShape(8.dp)),
        )
    } else {
        ProviderLogoPlaceholder()
    }
}

@Composable
private fun ProviderLogoPlaceholder() {
    Box(
        modifier =
            Modifier
                .size(PROVIDER_LOGO_SIZE)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}

private val PROVIDER_LOGO_SIZE = 52.dp

// region Previews

@PreviewLightDark
@Composable
private fun WhereToWatchSectionPreview() {
    SmoovieTheme {
        WhereToWatchSection(
            streamingProviders = previewWatchProviders,
            rentBuyProviders = previewWatchProviders.take(2),
            link = null,
            modifier = Modifier.padding(16.dp),
        )
    }
}

// endregion
