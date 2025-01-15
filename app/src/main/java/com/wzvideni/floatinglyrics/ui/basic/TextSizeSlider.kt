package com.wzvideni.floatinglyrics.ui.basic

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
inline fun TextSizeSlider(
    textSize: Float,
    crossinline onValueChange: (Float) -> Unit,
) {
    CenterVerticallyRow {
        Text(
            modifier = Modifier.padding(start = 5.dp, end = 5.dp),
            text = textSize.toInt().toString()
        )
        FullSpacer()
        Slider(
            value = textSize,
            onValueChange = { onValueChange(it) },
            modifier = Modifier.padding(end = 10.dp),
            valueRange = 10f..30f,
            steps = 20,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        )
    }
}