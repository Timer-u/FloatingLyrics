package com.wzvideni.floatinglyrics.ui.basic.setting

import android.content.UriPermission
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wzvideni.floatinglyrics.ui.basic.CenterVerticallyRow
import com.wzvideni.floatinglyrics.ui.basic.FullSpacer


// 设置歌词文件保存路径
@Composable
inline fun LyricsFileSavePath(
    persistedUriPermissionsList: List<UriPermission>,
    crossinline onClickToRemovePath: (uri: Uri) -> Unit,
    crossinline onClickToAddPath: () -> Unit,
) {
    Column {
        persistedUriPermissionsList.forEach { uriPermission: UriPermission ->
            CenterVerticallyRow {
                Text(
                    text = uriPermission.uri.pathSegments[1],
                    modifier = Modifier.padding(start = 10.dp)
                )
                FullSpacer()
                IconButton(onClick = { onClickToRemovePath(uriPermission.uri) }) {
                    Icon(imageVector = Icons.Rounded.Remove, contentDescription = null)
                }
            }
        }
        CenterVerticallyRow {
            Text(
                text = "添加新的目录",
                modifier = Modifier.padding(start = 10.dp)
            )
            FullSpacer()
            IconButton(onClick = { onClickToAddPath() }) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
            }
        }
    }
}