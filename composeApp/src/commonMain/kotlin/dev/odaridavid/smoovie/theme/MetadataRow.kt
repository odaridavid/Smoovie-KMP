package dev.odaridavid.smoovie.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MetadataRow(
    vararg items: String?,
    modifier: Modifier = Modifier,
) {
    val text = items.filterNot { it.isNullOrBlank() }.joinToString(" · ")
    if (text.isBlank()) return
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}
