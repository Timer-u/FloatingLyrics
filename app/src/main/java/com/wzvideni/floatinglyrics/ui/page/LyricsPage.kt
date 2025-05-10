package com.wzvideni.floatinglyrics.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wzvideni.floatinglyrics.network.model.Lyric
import com.wzvideni.floatinglyrics.ui.basic.PrimaryIcon
import com.wzvideni.floatinglyrics.ui.basic.PrimaryText
import com.wzvideni.floatinglyrics.ui.basic.lyrics.CurrentLyricsItem
import com.wzvideni.floatinglyrics.ui.basic.lyrics.LyricsItem
import com.wzvideni.floatinglyrics.viewmodel.PlayingStateViewModel

// 歌词页面
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsPage(
    playingStateViewModel: PlayingStateViewModel,
    onClickToSave: (lyricList: List<Lyric>) -> Unit,
) {
    val lyricList by playingStateViewModel.lyricsList.collectAsState()
    val lyricListState = rememberLazyListState()
    val lyricIndexState by playingStateViewModel.lyricIndex.collectAsState()

    LaunchedEffect(lyricIndexState) {
        if (lyricIndexState > 5) {
            lyricListState.animateScrollToItem(lyricIndexState - 4)
        } else if (lyricIndexState >= 0) {
            lyricListState.scrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { PrimaryText("歌词信息") },
                actions = {
                    IconButton(onClick = {
                        onClickToSave(lyricList)
                    }) {
                        PrimaryIcon(Icons.Rounded.Save)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (lyricList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "歌词列表为空",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = lyricListState,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(lyricList) { index, lyrics ->
                    if (lyrics.lyricsList.size == 2) {
                        Spacer(modifier = Modifier.height(30.dp))
                    } else {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    if (index == lyricIndexState) {
                        CurrentLyricsItem(lyrics.lyricsList)
                    } else {
                        LyricsItem(lyrics.lyricsList)
                    }
                }
            }
        }
    }
}