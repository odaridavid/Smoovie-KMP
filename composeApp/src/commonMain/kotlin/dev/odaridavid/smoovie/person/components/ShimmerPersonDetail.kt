package dev.odaridavid.smoovie.person.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.odaridavid.smoovie.theme.SmoovieTheme
import dev.odaridavid.smoovie.theme.rememberShimmerBrush

private val PHOTO_SIZE = 160.dp
private const val SHIMMER_FILMOGRAPHY_COUNT = 3

@Composable
internal fun ShimmerPersonDetail(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
    val brush = rememberShimmerBrush()
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HeaderFrame(onBack = onBack) {
            Box(
                modifier =
                    Modifier
                        .size(PHOTO_SIZE)
                        .background(brush, CircleShape),
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(0.5f)
                        .height(24.dp)
                        .background(brush, MaterialTheme.shapes.small),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier =
                        Modifier
                            .width(96.dp)
                            .height(32.dp)
                            .background(brush, MaterialTheme.shapes.extraLarge),
                )
                Box(
                    modifier =
                        Modifier
                            .width(72.dp)
                            .height(32.dp)
                            .background(brush, MaterialTheme.shapes.extraLarge),
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
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
                            .fillMaxWidth(0.85f)
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
        }
        Box(
            modifier =
                Modifier
                    .padding(horizontal = 16.dp)
                    .width(140.dp)
                    .height(20.dp)
                    .background(brush, MaterialTheme.shapes.small),
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(SHIMMER_FILMOGRAPHY_COUNT) {
                ShimmerFilmographyCard(brush = brush)
            }
        }
    }
}

@Composable
private fun ShimmerFilmographyCard(brush: Brush) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.height(140.dp)) {
            Box(
                modifier =
                    Modifier
                        .width(96.dp)
                        .fillMaxHeight()
                        .background(brush),
            )
            Column(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier =
                            Modifier
                                .width(180.dp)
                                .height(18.dp)
                                .background(brush, MaterialTheme.shapes.small),
                    )
                    Box(
                        modifier =
                            Modifier
                                .width(220.dp)
                                .height(12.dp)
                                .background(brush, MaterialTheme.shapes.small),
                    )
                    Box(
                        modifier =
                            Modifier
                                .width(140.dp)
                                .height(12.dp)
                                .background(brush, MaterialTheme.shapes.small),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier =
                            Modifier
                                .width(60.dp)
                                .height(14.dp)
                                .background(brush, MaterialTheme.shapes.small),
                    )
                    Box(
                        modifier =
                            Modifier
                                .width(80.dp)
                                .height(14.dp)
                                .background(brush, MaterialTheme.shapes.small),
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ShimmerPersonDetailPreview() {
    SmoovieTheme {
        ShimmerPersonDetail()
    }
}
