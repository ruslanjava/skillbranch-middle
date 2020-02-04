package ru.skillbranch.skillarticles.extensions

import android.view.View
import android.view.ViewGroup

fun View.setMarginOptionally(top: Int? = null, bottom: Int? = null, start: Int? = null, end : Int? = null) {
    val layoutParams = this.layoutParams as ViewGroup.LayoutParams
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        top?.let { layoutParams.topMargin = it }
        bottom?.let { layoutParams.bottomMargin = it }
        start?.let { layoutParams.marginStart = it }
        end?.let { layoutParams.marginEnd = it }
        setLayoutParams(layoutParams)
    }
}