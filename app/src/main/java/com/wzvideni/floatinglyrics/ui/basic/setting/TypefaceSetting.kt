package com.wzvideni.floatinglyrics.ui.basic.setting

import android.graphics.Typeface
import android.widget.TextView
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wzvideni.floatinglyrics.ui.basic.CenterVerticallyRow


@Composable
inline fun TypefaceSetting(
    lyricsTypefaceState: Int,
    translationTypefaceState: Int,
    horizontalLyricsTextView: TextView,
    horizontalTranslationTextView: TextView,
    verticalLyricsTextView: TextView,
    verticalTranslationTextView: TextView,
    crossinline lyricsTypefaceStateChanged: (Int) -> Unit,
    crossinline translationTypefaceStateChanged: (Int) -> Unit,
) {
    CenterVerticallyRow {
        Button(
            onClick = {
                when (lyricsTypefaceState) {
                    // 标准
                    0 -> {
                        horizontalLyricsTextView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD)
                        verticalLyricsTextView.setTypeface(
                            Typeface.SANS_SERIF,
                            Typeface.BOLD
                        )
                        lyricsTypefaceStateChanged(1)
                    }
                    // 加粗
                    1 -> {
                        horizontalLyricsTextView.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC)
                        verticalLyricsTextView.setTypeface(
                            Typeface.SANS_SERIF,
                            Typeface.ITALIC
                        )
                        lyricsTypefaceStateChanged(2)
                    }
                    // 斜体
                    2 -> {
                        horizontalLyricsTextView.setTypeface(
                            Typeface.SANS_SERIF,
                            Typeface.BOLD_ITALIC
                        )
                        verticalLyricsTextView.setTypeface(
                            Typeface.SANS_SERIF,
                            Typeface.BOLD_ITALIC
                        )
                        lyricsTypefaceStateChanged(3)
                    }
                    // 加粗+斜体
                    3 -> {
                        horizontalLyricsTextView.setTypeface(
                            Typeface.SANS_SERIF,
                            Typeface.NORMAL
                        )
                        verticalLyricsTextView.setTypeface(
                            Typeface.SANS_SERIF,
                            Typeface.NORMAL
                        )
                        lyricsTypefaceStateChanged(0)
                    }
                }
            }
        ) {
            when (lyricsTypefaceState) {
                0 -> {
                    Text(text = "歌词字体样式：标准")
                }

                1 -> {
                    Text(text = "歌词字体样式：加粗")
                }

                2 -> {
                    Text(text = "歌词字体样式：斜体")
                }

                3 -> {
                    Text(text = "歌词字体样式：加粗+斜体")
                }
            }
        }
        Spacer(modifier = Modifier.width(5.dp))
        Button(
            onClick = {
                when (translationTypefaceState) {
                    // 标准
                    0 -> {
                        horizontalTranslationTextView.setTypeface(
                            Typeface.SANS_SERIF,
                            Typeface.BOLD
                        )
                        verticalTranslationTextView.setTypeface(
                            Typeface.SANS_SERIF,
                            Typeface.BOLD
                        )
                        translationTypefaceStateChanged(1)
                    }
                    // 加粗
                    1 -> {
                        horizontalTranslationTextView.setTypeface(
                            Typeface.SANS_SERIF,
                            Typeface.ITALIC
                        )
                        verticalTranslationTextView.setTypeface(
                            Typeface.SANS_SERIF,
                            Typeface.ITALIC
                        )
                        translationTypefaceStateChanged(2)
                    }
                    // 斜体
                    2 -> {
                        horizontalTranslationTextView.setTypeface(
                            Typeface.SANS_SERIF,
                            Typeface.BOLD_ITALIC
                        )
                        verticalTranslationTextView.setTypeface(
                            Typeface.SANS_SERIF,
                            Typeface.BOLD_ITALIC
                        )
                        translationTypefaceStateChanged(3)
                    }
                    // 加粗+斜体
                    3 -> {
                        horizontalTranslationTextView.setTypeface(
                            Typeface.SANS_SERIF,
                            Typeface.NORMAL
                        )
                        verticalTranslationTextView.setTypeface(
                            Typeface.SANS_SERIF,
                            Typeface.NORMAL
                        )
                        translationTypefaceStateChanged(0)
                    }
                }
            }
        ) {
            when (translationTypefaceState) {
                0 -> {
                    Text(text = "翻译字体样式：标准")
                }

                1 -> {
                    Text(text = "翻译字体样式：加粗")
                }

                2 -> {
                    Text(text = "翻译字体样式：斜体")
                }

                3 -> {
                    Text(text = "翻译字体样式：加粗+斜体")
                }
            }
        }
    }
}