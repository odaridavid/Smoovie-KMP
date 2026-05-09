package dev.odaridavid.smoovie.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.settings.Region
import dev.odaridavid.smoovie.settings.SUPPORTED_REGIONS
import dev.odaridavid.smoovie.theme.SmoovieTheme
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.settings_region_sheet_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RegionPickerSheet(
    regions: List<Region>,
    selectedRegionCode: String?,
    onRegionSelected: (Region) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        ) {
            Text(
                text = stringResource(Res.string.settings_region_sheet_title),
                style = MaterialTheme.typography.titleLarge,
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 12.dp),
            )
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                items(regions, key = { it.code }) { region ->
                    val isSelected = region.code == selectedRegionCode
                    ListItem(
                        modifier = Modifier.clickable { onRegionSelected(region) },
                        headlineContent = { Text(region.displayName) },
                        supportingContent = { Text(region.code) },
                        trailingContent = {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun RegionPickerSheetPreview() {
    SmoovieTheme {
        RegionPickerSheet(
            regions = SUPPORTED_REGIONS,
            selectedRegionCode = "DE",
            onRegionSelected = {},
            onDismiss = {},
        )
    }
}
