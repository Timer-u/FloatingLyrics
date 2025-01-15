package com.wzvideni.floatinglyrics.ui.moedl

import android.view.View
import android.widget.TextView
import androidx.compose.runtime.Stable

@Stable
enum class Located {
    Horizontal,
    Vertical,
    Both
}

fun Located.setLocated(
    horizontalLyricsTextView: TextView,
    horizontalTranslationTextView: TextView,
    verticalLyricsTextView: TextView,
    verticalTranslationTextView: TextView,
) {
    when (this) {
        Located.Horizontal -> {
            horizontalLyricsTextView.visibility = View.VISIBLE
            horizontalTranslationTextView.visibility = View.VISIBLE
            verticalLyricsTextView.visibility = View.GONE
            verticalTranslationTextView.visibility = View.GONE
        }

        Located.Vertical -> {
            horizontalLyricsTextView.visibility = View.GONE
            horizontalTranslationTextView.visibility = View.GONE
            verticalLyricsTextView.visibility = View.VISIBLE
            verticalTranslationTextView.visibility = View.VISIBLE
        }

        Located.Both -> {
            horizontalLyricsTextView.visibility = View.VISIBLE
            horizontalTranslationTextView.visibility = View.VISIBLE
            verticalLyricsTextView.visibility = View.VISIBLE
            verticalTranslationTextView.visibility = View.VISIBLE
        }
    }
}
