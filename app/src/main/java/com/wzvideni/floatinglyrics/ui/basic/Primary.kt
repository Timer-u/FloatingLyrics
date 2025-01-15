package com.wzvideni.floatinglyrics.ui.basic

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryText(
    text: String,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Center,
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}

@Composable
fun PrimaryAnnotatedStringText(
    normalText: String,
    primaryText: String,
    textAlign: TextAlign = TextAlign.Center,
) {
    Text(
        buildAnnotatedString {
            append(normalText)
            withStyle(
                style = SpanStyle(color = MaterialTheme.colorScheme.primary)
            ) {
                append(primaryText)
            }
        },
        textAlign = textAlign
    )

}

@Composable
fun PrimaryIcon(imageVector: ImageVector) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsTextWithPadding(text: String, onClick: () -> Unit, onLongClick: () -> Unit = {}) {
    Text(
        text = text,
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            )
    )
}

