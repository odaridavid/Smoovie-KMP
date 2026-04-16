package dev.odaridavid.smoovie.movies.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.theme.SmoovieTheme

private const val SHIMMER_CAST_COUNT = 5

@Composable
internal fun ShimmerMovieDetail(modifier: Modifier = Modifier) {
    val brush = rememberShimmerBrush()
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Metadata chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier =
                    Modifier
                        .width(72.dp)
                        .height(32.dp)
                        .background(brush, MaterialTheme.shapes.small),
            )
            Box(
                modifier =
                    Modifier
                        .width(64.dp)
                        .height(32.dp)
                        .background(brush, MaterialTheme.shapes.small),
            )
            Box(
                modifier =
                    Modifier
                        .width(96.dp)
                        .height(32.dp)
                        .background(brush, MaterialTheme.shapes.small),
            )
        }

        // Overview text lines
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(brush, MaterialTheme.shapes.small),
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(0.95f)
                        .height(14.dp)
                        .background(brush, MaterialTheme.shapes.small),
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(0.8f)
                        .height(14.dp)
                        .background(brush, MaterialTheme.shapes.small),
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(0.6f)
                        .height(14.dp)
                        .background(brush, MaterialTheme.shapes.small),
            )
        }

        // Cast section
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Section title
            Box(
                modifier =
                    Modifier
                        .width(48.dp)
                        .height(18.dp)
                        .background(brush, MaterialTheme.shapes.small),
            )
            // Cast row
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(SHIMMER_CAST_COUNT) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(80.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(64.dp)
                                    .background(brush, CircleShape),
                        )
                        Box(
                            modifier =
                                Modifier
                                    .width(56.dp)
                                    .height(10.dp)
                                    .background(brush, MaterialTheme.shapes.small),
                        )
                        Box(
                            modifier =
                                Modifier
                                    .width(48.dp)
                                    .height(10.dp)
                                    .background(brush, MaterialTheme.shapes.small),
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ShimmerMovieDetailPreview() {
    SmoovieTheme {
        ShimmerMovieDetail()
    }
}
