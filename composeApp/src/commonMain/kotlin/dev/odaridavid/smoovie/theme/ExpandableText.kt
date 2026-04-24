package dev.odaridavid.smoovie.theme

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import smoovie.composeapp.generated.resources.Res
import smoovie.composeapp.generated.resources.action_read_more
import smoovie.composeapp.generated.resources.action_show_less

private const val DEFAULT_COLLAPSED_MAX_LINES = 4

@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    collapsedMaxLines: Int = DEFAULT_COLLAPSED_MAX_LINES,
) {
    var expanded by rememberSaveable(text) { mutableStateOf(false) }
    var hasOverflow by rememberSaveable(text) { mutableStateOf(false) }

    Column(
        modifier =
            modifier.animateContentSize(
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            ),
    ) {
        Text(
            text = text,
            style = style,
            color = color,
            maxLines = if (expanded) Int.MAX_VALUE else collapsedMaxLines,
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
                            if (expanded) Res.string.action_show_less else Res.string.action_read_more,
                        ),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ExpandableTextShortPreview() {
    SmoovieTheme {
        ExpandableText(
            text = "A brief description.",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@PreviewLightDark
@Composable
private fun ExpandableTextLongPreview() {
    SmoovieTheme {
        ExpandableText(
            text =
                "Breaking Bad follows the transformation of Walter White, a high school chemistry teacher diagnosed " +
                    "with terminal lung cancer, who partners with a former student to manufacture and sell crystal " +
                    "methamphetamine to secure his family's financial future. Set in Albuquerque, New Mexico, the " +
                    "series is a character-driven examination of how seemingly small choices cascade into moral " +
                    "catastrophe across five seasons.",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
