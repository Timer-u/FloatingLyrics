package com.wzvideni.floatinglyrics.ui.basic.lyrics

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

// 歌词页面的当前歌词子项
@Composable
fun CurrentLyricsItem(lyricsList: List<String>) {
    when (lyricsList.size) {
        2 -> {
            Column {
                CurrentLyricsText(lyricsList[0])
                CurrentLyricsText(lyricsList[1])
            }
        }

        1 -> {
            CurrentLyricsText(lyricsList[0])
        }
    }
}