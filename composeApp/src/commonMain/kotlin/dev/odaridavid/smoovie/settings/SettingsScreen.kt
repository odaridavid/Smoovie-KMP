package dev.odaridavid.smoovie.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.settings.components.RegionPickerSheet
import dev.odaridavid.smoovie.theme.SmoovieTheme
import dev.odaridavid.smoovie.ui.SetStatusBarIcons
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.app_name
import smoovie.composeapp.generated.resources.settings_app_version_format
import smoovie.composeapp.generated.resources.settings_region_label
import smoovie.composeapp.generated.resources.settings_region_unset
import smoovie.composeapp.generated.resources.settings_title
import smoovie.composeapp.generated.resources.settings_tmdb_attribution
import smoovie.composeapp.generated.resources.settings_tmdb_powered_by_short
import smoovie.composeapp.generated.resources.tmdb_logo

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val state by viewModel.state.collectAsState()
    SettingsContent(
        state = state,
        version = appVersionInfo(),
        onRegionSelected = viewModel::onRegionSelected,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsContent(
    state: SettingsUiState,
    version: AppVersion,
    onRegionSelected: (Region) -> Unit,
) {
    SetStatusBarIcons(useDarkIcons = !isSystemInDarkTheme())
    var isRegionSheetVisible by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(Res.string.settings_title)) },
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            RegionRow(
                selectedRegion = state.selectedRegion,
                onClick = { isRegionSheetVisible = true },
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            TmdbAttribution()
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(modifier = Modifier.weight(1f))

            AppVersionFooter(version = version)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (isRegionSheetVisible) {
        RegionPickerSheet(
            regions = state.regions,
            selectedRegionCode = state.selectedRegion?.code,
            onRegionSelected = { region ->
                onRegionSelected(region)
                isRegionSheetVisible = false
            },
            onDismiss = { isRegionSheetVisible = false },
        )
    }
}

@Composable
private fun RegionRow(
    selectedRegion: Region?,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Public,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.settings_region_label),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = selectedRegion?.displayName ?: stringResource(Res.string.settings_region_unset),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = selectedRegion?.code ?: "—",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun TmdbAttribution() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.settings_tmdb_powered_by_short),
                style = MaterialTheme.typography.titleSmall,
            )
            Image(
                painter = painterResource(Res.drawable.tmdb_logo),
                contentDescription = "TMDB",
                modifier = Modifier.height(14.dp),
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(Res.string.settings_tmdb_attribution),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AppVersionFooter(version: AppVersion) {
    val versionLine =
        stringResource(Res.string.app_name) + " · " +
            stringResource(Res.string.settings_app_version_format, version.name, version.code)
    Text(
        text = versionLine,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
    )
}

private val previewVersion = AppVersion(name = "1.0", code = "1")

@PreviewLightDark
@Composable
private fun SettingsContentDefaultPreview() {
    SmoovieTheme {
        SettingsContent(
            state = SettingsUiState(selectedRegion = null),
            version = previewVersion,
            onRegionSelected = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun SettingsContentSelectedPreview() {
    SmoovieTheme {
        SettingsContent(
            state = SettingsUiState(selectedRegion = SUPPORTED_REGIONS.first { it.code == "DE" }),
            version = previewVersion,
            onRegionSelected = {},
        )
    }
}
