package com.wzvideni.floatinglyrics.ui.basic

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

// 设置页面的标题文本
@Composable
fun TitleText(text: String, modifier: Modifier = Modifier) {
    Text(text = text, fontWeight = FontWeight.Bold, modifier = modifier)
}