package com.wzvideni.floatinglyrics.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wzvideni.floatinglyrics.ui.basic.PrimaryAnnotatedStringText
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit


@Composable
fun PlayingInfo(
    musicPathState: String,
    lrcPathState: String,
    queryState: String,
    titleState: String,
    artistState: String,
    albumState: String,
    trackNumberState: Int,
    stateState: Int,
    positionState: Int,
    lyricState: String,
    translationState: String,
    lyricsIndexState: Int,
) {
    Column(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PrimaryAnnotatedStringText("查询：", queryState)
        PrimaryAnnotatedStringText("媒体路径：", musicPathState)
        PrimaryAnnotatedStringText("歌词路径：", lrcPathState)
        PrimaryAnnotatedStringText("标题：", titleState)
        PrimaryAnnotatedStringText("艺术家：", artistState)
        PrimaryAnnotatedStringText("专辑：", albumState)
        PrimaryAnnotatedStringText("音轨号：", trackNumberState.toString())
        PrimaryAnnotatedStringText("状态：", convertPlayState(stateState))
        PrimaryAnnotatedStringText("位置：", "${convertMillisecond(positionState)} ms")
        PrimaryAnnotatedStringText("歌词：", lyricState)
        PrimaryAnnotatedStringText("翻译：", translationState)
        PrimaryAnnotatedStringText("歌词索引：", lyricsIndexState.toString())
    }
}

// 转换媒体状态从数字到文字
fun convertPlayState(playingState: Int?) = when (playingState) {
    0 -> "无媒体"
    1 -> "已停止"
    2 -> "已暂停"
    3 -> "播放中"
    else -> "未处理"
}

// 转换毫秒为分秒毫秒形式
fun convertMillisecond(position: Int?): String {

    position?.let {
        val duration = position.milliseconds
        val minutes = duration.toInt(DurationUnit.MINUTES)
        val seconds = duration.toInt(DurationUnit.SECONDS) % 60
        val millisecond = duration.toInt(DurationUnit.MILLISECONDS) % 1000
        return String.format(Locale.getDefault(), "%02d:%02d.%03d", minutes, seconds, millisecond)

    }
    return "00.00:000"
}