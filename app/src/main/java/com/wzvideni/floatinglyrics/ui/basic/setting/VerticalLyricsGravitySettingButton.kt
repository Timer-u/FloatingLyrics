package com.wzvideni.floatinglyrics.ui.basic.setting

import android.view.View
import android.widget.TextView
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.wzvideni.floatinglyrics.ui.moedl.VerticalGravity
import com.wzvideni.floatinglyrics.ui.moedl.setVerticalGravity


@Composable
inline fun VerticalLyricsGravitySettingButton(
    verticalGravity: VerticalGravity,
    verticalLyricsTextView: TextView,
    verticalTranslationTextView: TextView,
    crossinline onVerticalGravityChanged: (VerticalGravity) -> Unit,
) {
    Button(
        onClick = {
            if (verticalLyricsTextView.visibility == View.VISIBLE && verticalTranslationTextView.visibility == View.VISIBLE) {
                when (verticalGravity) {
                    // 居中对齐 --> 顶部对齐
                    VerticalGravity.Center -> {
                        // 设置垂直歌词和翻译文字顶部对齐
                        VerticalGravity.Top.setVerticalGravity(
                            verticalLyricsTextView,
                            verticalTranslationTextView
                        )
                        onVerticalGravityChanged(VerticalGravity.Top)
                    }
                    // 顶部对齐 --> 底部对齐
                    VerticalGravity.Top -> {
                        // 设置垂直歌词和翻译文字底部对齐
                        VerticalGravity.Bottom.setVerticalGravity(
                            verticalLyricsTextView,
                            verticalTranslationTextView
                        )
                        onVerticalGravityChanged(VerticalGravity.Bottom)
                    }
                    // 底部对齐 --> 居中对齐
                    VerticalGravity.Bottom -> {
                        // 设置垂直歌词和翻译居中顶部对齐
                        VerticalGravity.Center.setVerticalGravity(
                            verticalLyricsTextView,
                            verticalTranslationTextView
                        )
                        onVerticalGravityChanged(VerticalGravity.Center)
                    }
                }
            }
        }
    ) {
        when (verticalGravity) {
            VerticalGravity.Center -> {
                Text(text = "垂直位置：居中")
            }

            VerticalGravity.Top -> {
                Text(text = "垂直位置：顶部")
            }

            VerticalGravity.Bottom -> {
                Text(text = "垂直位置：底部")
            }
        }
    }
}