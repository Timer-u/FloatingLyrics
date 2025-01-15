package com.wzvideni.floatinglyrics.ui.basic.setting

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wzvideni.floatinglyrics.ui.basic.CenterVerticallyRow
import com.wzvideni.floatinglyrics.ui.basic.FullSpacer

@Composable
inline fun AutoSearchLyrics(
    isEnableState: Boolean,
    crossinline onCheckedChange: (Boolean) -> Unit,
) {
    CenterVerticallyRow {
        Text(
            text = "自动搜索歌词",
            modifier = Modifier.padding(start = 10.dp)
        )
        FullSpacer()
        Switch(
            checked = isEnableState,
            onCheckedChange = { onCheckedChange(it) },
            modifier = Modifier.padding(end = 10.dp)
        )
    }
}


@Composable
inline fun AutoSearchPriority(
    isQQMusicPriorityAutoSearchState: Boolean,
    crossinline onClick: () -> Unit,
) {
    CenterVerticallyRow {
        Text(
            text = "自动搜索优先级",
            modifier = Modifier.padding(start = 10.dp)
        )
        FullSpacer()
        Text(
            text = if (isQQMusicPriorityAutoSearchState) "QQ音乐" else "网易云音乐",
            textAlign = TextAlign.Center
        )
        IconButton(
            onClick = { onClick() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "KeyboardArrowRight"
            )
        }
        Text(
            text = if (isQQMusicPriorityAutoSearchState) "网易云音乐" else "QQ音乐",
            modifier = Modifier
                .padding(end = 10.dp),
            textAlign = TextAlign.Center
        )
    }
}
