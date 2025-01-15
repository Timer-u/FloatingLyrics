package com.wzvideni.floatinglyrics.ui.basic.setting

import android.widget.TextView
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.wzvideni.floatinglyrics.ui.moedl.Located
import com.wzvideni.floatinglyrics.ui.moedl.setLocated


@Composable
inline fun LyricsLocatedSettingButton(
    locatedState: Located,
    horizontalLyricsTextView: TextView,
    horizontalTranslationTextView: TextView,
    verticalLyricsTextView: TextView,
    verticalTranslationTextView: TextView,
    crossinline onLocatedStateChanged: (Located) -> Unit,
) {
    Button(
        onClick = {
            when (locatedState) {
                Located.Horizontal -> {
                    Located.Vertical.setLocated(
                        horizontalLyricsTextView = horizontalLyricsTextView,
                        horizontalTranslationTextView = horizontalTranslationTextView,
                        verticalLyricsTextView = verticalLyricsTextView,
                        verticalTranslationTextView = verticalTranslationTextView
                    )
                    onLocatedStateChanged(Located.Vertical)
                }

                Located.Vertical -> {
                    Located.Both.setLocated(
                        horizontalLyricsTextView = horizontalLyricsTextView,
                        horizontalTranslationTextView = horizontalTranslationTextView,
                        verticalLyricsTextView = verticalLyricsTextView,
                        verticalTranslationTextView = verticalTranslationTextView
                    )
                    onLocatedStateChanged(Located.Both)
                }

                Located.Both -> {
                    Located.Horizontal.setLocated(
                        horizontalLyricsTextView = horizontalLyricsTextView,
                        horizontalTranslationTextView = horizontalTranslationTextView,
                        verticalLyricsTextView = verticalLyricsTextView,
                        verticalTranslationTextView = verticalTranslationTextView
                    )
                    onLocatedStateChanged(Located.Horizontal)
                }
            }
        }
    ) {
        when (locatedState) {
            Located.Horizontal -> {
                Text(text = "当前显示：水平")
            }

            Located.Vertical -> {
                Text(text = "当前显示：垂直")
            }

            Located.Both -> {
                Text(text = "当前显示：水平和垂直")
            }
        }
    }
}