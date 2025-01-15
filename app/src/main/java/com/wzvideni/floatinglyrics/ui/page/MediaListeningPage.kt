package com.wzvideni.floatinglyrics.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wzvideni.floatinglyrics.ui.PlayingInfo
import com.wzvideni.floatinglyrics.ui.UpdateDialog
import com.wzvideni.floatinglyrics.ui.basic.PrimaryIcon
import com.wzvideni.floatinglyrics.ui.basic.PrimaryText
import com.wzvideni.floatinglyrics.viewmodel.PlayingStateViewModel
import com.wzvideni.floatinglyrics.viewmodel.UpdateViewModel


// 媒体监听页面
@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun MediaListeningPage(
    playingStateViewModel: PlayingStateViewModel,
    updateViewModel: UpdateViewModel,
    crossinline addView: () -> Unit,
    crossinline onClickTopIcon: () -> Unit,
    crossinline onClickToListen: () -> Unit,
    crossinline onClickToFinish: () -> Unit,
) {
    val musicPathState by playingStateViewModel.musicPath.collectAsState()
    val lrcPathState by playingStateViewModel.lyricsPath.collectAsState()
    val queryState by playingStateViewModel.query.collectAsState()
    val titleState by playingStateViewModel.title.collectAsState()
    val artistState by playingStateViewModel.artist.collectAsState()
    val albumState by playingStateViewModel.album.collectAsState()
    val trackNumberState by playingStateViewModel.trackNumber.collectAsState()
    val stateState by playingStateViewModel.state.collectAsState()
    val positionState by playingStateViewModel.position.collectAsState()
    val lyricState by playingStateViewModel.lyric.collectAsState()
    val translationState by playingStateViewModel.translation.collectAsState()
    val lyricsIndexState by playingStateViewModel.lyricIndex.collectAsState()
    val verticalScrollState = rememberScrollState()

    // 媒体监听状态
    val mediaListenerState by playingStateViewModel.mediaListenerState.collectAsState()

    // 处理设备配置变更导致的问题（悬浮窗被移除、悬浮歌词样式被重置）
    if (mediaListenerState) {
        addView()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { PrimaryText("监听信息") },
                actions = {
                    IconButton(onClick = { onClickTopIcon() }) {
                        PrimaryIcon(Icons.Rounded.AutoAwesome)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(verticalScrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 正在播放的音乐信息
            PlayingInfo(
                musicPathState,
                lrcPathState,
                queryState,
                titleState,
                artistState,
                albumState,
                trackNumberState,
                stateState,
                positionState,
                lyricState,
                translationState,
                lyricsIndexState
            )
            if (mediaListenerState) {
                Button(
                    onClick = { onClickToFinish() }
                ) {
                    Text(text = "停止媒体监听")
                }
            } else {
                Button(onClick = { onClickToListen() }) {
                    Text(text = "启动媒体监听")
                }
            }
            UpdateDialog(updateViewModel)
        }
    }
}