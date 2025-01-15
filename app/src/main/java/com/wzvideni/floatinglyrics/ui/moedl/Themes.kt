package com.wzvideni.floatinglyrics.ui.moedl

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector

@Stable
sealed class Themes(
    val theme: String,
    val icon: ImageVector,
) {
    data object System : Themes("System", Icons.Rounded.BrightnessAuto)
    data object Light : Themes("Light", Icons.Rounded.LightMode)
    data object Dark : Themes("Dark", Icons.Rounded.DarkMode)
}