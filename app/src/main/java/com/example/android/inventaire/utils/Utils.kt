package com.example.android.inventaire.utils

import android.view.View

class Utils {

    fun View.VisibleOrGone(visible: Boolean) {
        visibility = if(visible) View.VISIBLE else View.GONE
    }
}