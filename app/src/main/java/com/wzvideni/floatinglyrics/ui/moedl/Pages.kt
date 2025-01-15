package com.wzvideni.floatinglyrics.ui.moedl

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lyrics
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector

// 底部导航栏导航页面
@Stable
sealed class Pages(
    val route: String,
    val name: String,
    val defaultIcon: ImageVector,
    val selectedIcon: ImageVector,
) {
    data object Home : Pages("Home", "主页", Icons.Outlined.Home, Icons.Rounded.Home)
    data object Lyrics : Pages("Lyrics", "歌词", Icons.Outlined.Lyrics, Icons.Rounded.Lyrics)
    data object Search : Pages("Search", "搜索", Icons.Outlined.Search, Icons.Rounded.Search)
    data object Setting : Pages("Setting", "设置", Icons.Outlined.Settings, Icons.Rounded.Settings)
}