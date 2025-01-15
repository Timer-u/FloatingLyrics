package com.wzvideni.floatinglyrics.ui.basic.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.wzvideni.floatinglyrics.ui.basic.CenterVerticallyRow
import com.wzvideni.floatinglyrics.ui.basic.FullSpacer
import com.wzvideni.floatinglyrics.ui.isDarkTheme
import com.wzvideni.floatinglyrics.ui.moedl.Themes
import kotlin.random.Random


@Composable
inline fun ColorPicker(
    selectedColor: Int,
    themeState: Themes,
    crossinline onClick: (Int) -> Unit,
) {
    val colorList = if (isDarkTheme(themeState))
        listOf(
            Color.White,
            Color.LightGray,
            Color.Red,
            Color.Magenta,
            Color.Blue,
            Color.Cyan,
            Color.Green,
            Color.Yellow
        )
    else
        listOf(
            Color.Black,
            Color.LightGray,
            Color.Red,
            Color.Magenta,
            Color.Blue,
            Color.Cyan,
            Color.Green,
            Color.Yellow
        )
    CenterVerticallyRow {
        Box(
            modifier = Modifier
                .size(40.dp)
                .padding(3.dp)
                .background(color = Color(selectedColor), shape = CircleShape)
                .clickable {
                    onClick(Color(randomColor()).toArgb())
                }
        )

        FullSpacer()
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            colorList.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(3.dp)
                        .background(color = color, shape = CircleShape)
                        .clickable { onClick(color.toArgb()) }
                )
            }
        }
    }
}

// 生成16进制随机颜色值
fun randomColor(): Int {
    val r = Random.nextInt(256)
    val g = Random.nextInt(256)
    val b = Random.nextInt(256)
    // 合成一个32位的整数，其中前8位是alpha值，后24位是RGB值。
    return 0xff000000.toInt() or (r shl 16) or (g shl 8) or b
}