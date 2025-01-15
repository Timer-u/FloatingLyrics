package com.wzvideni.floatinglyrics.network.model

import androidx.compose.runtime.Stable

@Stable
data class Lyric(
    val timeline: String,
    val millisecond: Int,
    val lyricsList: MutableList<String>,
)