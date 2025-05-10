package com.wzvideni.floatinglyrics.ui.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wzvideni.floatinglyrics.ui.basic.PrimaryIcon
import com.wzvideni.floatinglyrics.ui.basic.PrimaryText
import com.wzvideni.floatinglyrics.ui.basic.search.QQMusicSearchItem
import com.wzvideni.floatinglyrics.ui.basic.search.WyyMusicSearchItem
import com.wzvideni.floatinglyrics.viewmodel.PlayingStateViewModel

// 搜索页面
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(
    playingStateViewModel: PlayingStateViewModel,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onClickToRequestQQMusicLyric: (String) -> Unit,
    onClickToRequestWyyMusicLyric: (String) -> Unit,
) {

    val qqMusicSearchResultList by playingStateViewModel.qqMusicSearchResultList.collectAsState()

    val wyyMusicSearchResultList by playingStateViewModel.wyyMusicSearchResultList.collectAsState()


    val pullRefreshState =
        rememberPullRefreshState(refreshing = isRefreshing, onRefresh = { onRefresh() })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { PrimaryText("歌词搜索") },
                actions = {
                    IconButton(onClick = {
                        onRefresh()
                    }) {
                        PrimaryIcon(Icons.Rounded.Cached)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(qqMusicSearchResultList.size) { index: Int ->
                    QQMusicSearchItem(qqMusicSearchResultList[index]) {
                        onClickToRequestQQMusicLyric(qqMusicSearchResultList[index].musicId)
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    if (index < wyyMusicSearchResultList.size) {
                        WyyMusicSearchItem(wyyMusicSearchResultList[index]) {
                            onClickToRequestWyyMusicLyric(wyyMusicSearchResultList[index].musicId)
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
            if (qqMusicSearchResultList.isEmpty() && wyyMusicSearchResultList.isEmpty()) {
                Text(
                    text = "未搜索到结果",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            // 下滑刷新指示器，必须让其位于其他界面组件的后面，不然会导致刷新动画显示在其他组件的下面而无法被看见
            PullRefreshIndicator(
                isRefreshing,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }
}