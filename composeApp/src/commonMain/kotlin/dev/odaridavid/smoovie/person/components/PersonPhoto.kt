package dev.odaridavid.smoovie.person.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage

@Composable
internal fun PersonPhoto(
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
