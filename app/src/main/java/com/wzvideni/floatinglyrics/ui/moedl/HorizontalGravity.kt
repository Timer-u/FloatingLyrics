package com.wzvideni.floatinglyrics.ui.moedl

import android.view.Gravity
import android.widget.TextView
import androidx.compose.runtime.Stable

@Stable
enum class HorizontalGravity {
    Start,
    Center,
    End
}

fun HorizontalGravity.setHorizontalGravity(
    horizontalLyricsTextView: TextView,
    horizontalTranslationTextView: TextView,
) {
    when (this) {
        HorizontalGravity.Center -> {
            horizontalLyricsTextView.gravity = Gravity.CENTER
            horizontalTranslationTextView.gravity = Gravity.CENTER
        }

        HorizontalGravity.Start -> {
            horizontalLyricsTextView.gravity = Gravity.START
            horizontalTranslationTextView.gravity = Gravity.START
        }

        HorizontalGravity.End -> {
            horizontalLyricsTextView.gravity = Gravity.END
            horizontalTranslationTextView.gravity = Gravity.END
        }
    }
}