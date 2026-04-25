package dev.odaridavid.smoovie.person.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun TvBadge(modifier: Modifier = Modifier) {
    Text(
        text = "TV",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier =
            modifier
                .background(
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(4.dp),
                ).padding(horizontal = 6.dp, vertical = 2.dp),
    )
}
