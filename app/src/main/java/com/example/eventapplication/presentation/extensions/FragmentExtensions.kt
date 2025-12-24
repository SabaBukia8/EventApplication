package com.example.eventapplication.presentation.extensions

import androidx.fragment.app.Fragment
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar


fun Fragment.showSnackbar(
    @StringRes messageResId: Int, 
    duration: Int = Snackbar.LENGTH_LONG,
    @StringRes actionTextResId: Int? = null,
    action: (() -> Unit)? = null
) {
    view?.let {
        val snackbar = Snackbar.make(it, getString(messageResId), duration)
        if (actionTextResId != null && action != null) {
            snackbar.setAction(getString(actionTextResId)) { action.invoke() }
        }
        snackbar.show()
    }
}


fun Fragment.showSnackbar(
    message: String, 
    duration: Int = Snackbar.LENGTH_LONG,
    actionText: String? = null,
    action: (() -> Unit)? = null
) {
    view?.let {
        val snackbar = Snackbar.make(it, message, duration)
        if (actionText != null && action != null) {
            snackbar.setAction(actionText) { action.invoke() }
        }
        snackbar.show()
    }
}
