package com.wzvideni.floatinglyrics.ui.moedl

import android.view.Gravity
import android.widget.TextView
import androidx.compose.runtime.Stable

@Stable
enum class VerticalGravity {
    Top,
    Center,
    Bottom
}

fun VerticalGravity.setVerticalGravity(
    verticalLyricsTextView: TextView,
    verticalTranslationTextView: TextView,
) {
    when (this) {
        VerticalGravity.Center -> {
            verticalLyricsTextView.gravity = Gravity.CENTER
            verticalTranslationTextView.gravity = Gravity.CENTER

        }

        VerticalGravity.Top -> {
            verticalLyricsTextView.gravity = Gravity.TOP
            verticalTranslationTextView.gravity = Gravity.TOP
        }

        VerticalGravity.Bottom -> {
            verticalLyricsTextView.gravity = Gravity.BOTTOM
            verticalTranslationTextView.gravity = Gravity.BOTTOM
        }
    }
}