package com.wzvideni.floatinglyrics.ui.basic.lyrics

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

// 歌词页面的歌词子项
@Composable
fun LyricsItem(lyricsList: List<String>) {
    when (lyricsList.size) {
        2 -> {
            Column {
                LyricsText(lyricsList[0])
                LyricsText(lyricsList[1])

            }
        }

        1 -> {
            LyricsText(lyricsList[0])
        }
    }
}