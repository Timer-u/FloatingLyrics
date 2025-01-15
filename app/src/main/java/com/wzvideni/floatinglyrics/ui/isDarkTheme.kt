package com.wzvideni.floatinglyrics.ui;

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.wzvideni.floatinglyrics.ui.moedl.Themes

@Composable
fun isDarkTheme(themes: Themes): Boolean {
    return when (themes) {
        Themes.Light -> false
        Themes.Dark -> true
        else -> isSystemInDarkTheme()
    }
}
