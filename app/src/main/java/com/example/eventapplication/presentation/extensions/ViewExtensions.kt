package com.example.eventapplication.presentation.extensions

import android.view.View
import android.widget.ImageView
import coil3.load
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
