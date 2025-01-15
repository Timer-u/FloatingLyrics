package com.wzvideni.floatinglyrics.room

import androidx.compose.runtime.Stable
import androidx.room.Entity

// 歌词更新实体
@Stable
@Entity(primaryKeys = ["title", "album"])
data class LyricsUpdate(
    val title: String,
    val album: String,
    val updateDate: Long,
)