package com.wzvideni.floatinglyrics.ui.basic.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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

// QQ音乐搜索子项
@Composable
inline fun QQMusicSearchItem(
    qqMusicInfo: MusicInfo,
    crossinline clickable: () -> Unit,
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { clickable() }) {
        Row {
            // 专辑封面可能为空
            if (qqMusicInfo.musicId != "") {
                // 使用AsyncImage请求网络图片
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://y.qq.com/music/photo_new/T002R800x800M000${qqMusicInfo.albumInfo}.jpg")
                        .memoryCachePolicy(CachePolicy.ENABLED) // 启动内存缓存
                        .diskCachePolicy(CachePolicy.ENABLED) // 启动磁盘缓存
                        .networkCachePolicy(CachePolicy.ENABLED) // 启动网络缓存
                        .scale(Scale.FIT)
                        .build(),
                    modifier = Modifier.size(128.dp),
                    contentDescription = null
                )
            } else {
                Box(modifier = Modifier.size(128.dp), contentAlignment = Alignment.Center) {
                    Text(text = "无专辑封面")
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.height(128.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${qqMusicInfo.title} - ${
                        qqMusicInfo.singerString
                    }",
                    fontSize = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(5.dp))
                // 专辑可能为空
                if (qqMusicInfo.album != "") {
                    Text(
                        text = qqMusicInfo.album,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(text = "专辑未知", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "QQ音乐ID：${qqMusicInfo.musicId}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
