package com.wzvideni.floatinglyrics.ui.basic.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.size.Scale

import com.wzvideni.floatinglyrics.network.model.MusicInfo

// 网易云音乐搜索子项
@Composable
inline fun WyyMusicSearchItem(
    wyyMusicInfo: MusicInfo,
    crossinline clickable: () -> Unit,
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            clickable()
        }) {
        Row {
            // 使用AsyncImage请求网络图片
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(wyyMusicInfo.albumInfo)
                    .memoryCachePolicy(CachePolicy.ENABLED) // 启动内存缓存
                    .diskCachePolicy(CachePolicy.ENABLED) // 启动磁盘缓存
                    .networkCachePolicy(CachePolicy.ENABLED) // 启动网络缓存
                    .scale(Scale.FIT)
                    .build(),
                modifier = Modifier.size(128.dp),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.height(128.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${wyyMusicInfo.title} - ${wyyMusicInfo.singerString}",
                    fontSize = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = wyyMusicInfo.album,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "网易云音乐ID：${wyyMusicInfo.musicId}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}