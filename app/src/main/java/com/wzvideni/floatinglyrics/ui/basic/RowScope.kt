package com.wzvideni.floatinglyrics.ui.basic

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun RowScope.FullSpacer() {
    Spacer(Modifier.weight(1f))
}

@Composable
inline fun CenterVerticallyRow(crossinline content: @Composable (RowScope.() -> Unit)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        content()
    }
}



