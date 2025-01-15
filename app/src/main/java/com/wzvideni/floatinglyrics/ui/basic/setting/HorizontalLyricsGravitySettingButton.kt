package com.wzvideni.floatinglyrics.ui.basic.setting

import android.view.View
import android.widget.TextView
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.wzvideni.floatinglyrics.ui.moedl.HorizontalGravity
import com.wzvideni.floatinglyrics.ui.moedl.setHorizontalGravity


@Composable
inline fun HorizontalLyricsGravitySettingButton(
    horizontalGravity: HorizontalGravity,
    horizontalLyricsTextView: TextView,
    horizontalTranslationTextView: TextView,
    crossinline onHorizontalGravityChanged: (HorizontalGravity) -> Unit,
) {
    Button(
        onClick = {
            if (horizontalLyricsTextView.visibility == View.VISIBLE && horizontalTranslationTextView.visibility == View.VISIBLE) {
                when (horizontalGravity) {
                    // 居中对齐 --> 头部对齐
                    HorizontalGravity.Center -> {
                        // 设置水平歌词和翻译文字头部对齐
                        HorizontalGravity.Start.setHorizontalGravity(
                            horizontalLyricsTextView,
                            horizontalTranslationTextView
                        )
                        onHorizontalGravityChanged(HorizontalGravity.Start)
                    }
                    // 头部对齐 --> 尾部对齐
                    HorizontalGravity.Start -> {
                        // 设置水平歌词和翻译文字尾部对齐
                        HorizontalGravity.End.setHorizontalGravity(
                            horizontalLyricsTextView,
                            horizontalTranslationTextView
                        )
                        onHorizontalGravityChanged(HorizontalGravity.End)
                    }
                    // 尾部对齐 --> 居中对齐
                    HorizontalGravity.End -> {
                        // 设置水平歌词和翻译文字居中对齐
                        HorizontalGravity.Center.setHorizontalGravity(
                            horizontalLyricsTextView,
                            horizontalTranslationTextView
                        )
                        onHorizontalGravityChanged(HorizontalGravity.Center)
                    }
                }
            }
        }
    ) {

        when (horizontalGravity) {
            HorizontalGravity.Center -> {
                Text(text = "水平位置：居中")
            }

            HorizontalGravity.Start -> {
                Text(text = "水平位置：靠左")
            }

            HorizontalGravity.End -> {
                Text(text = "水平位置：靠右")
            }
        }
    }
}