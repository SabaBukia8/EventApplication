package com.example.eventapplication.presentation.extensions

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import com.example.eventapplication.R

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.setBackgroundColorHex(hexColor: String) {
    try {
        setBackgroundColor(hexColor.toColorInt())
    } catch (e: Exception) {
        setBackgroundColor("#D4D4D4".toColorInt())
    }
}

fun TextView.setTextColorHex(hexColor: String) {
    try {
        setTextColor(hexColor.toColorInt())
    } catch (e: Exception) {
        setTextColor("#171717".toColorInt())
    }
}

fun ImageView.loadImage(
    url: String?,
    placeholderRes: Int = R.drawable.ic_launcher_background,
    errorRes: Int = R.drawable.ic_launcher_background,
) {
    this.load(url) {
        placeholder(placeholderRes)
        error(errorRes)
    }
}
