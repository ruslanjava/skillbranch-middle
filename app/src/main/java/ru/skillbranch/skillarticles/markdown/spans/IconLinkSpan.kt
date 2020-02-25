package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting

class IconLinkSpan(

        private val linkDrawable: Drawable,

        @ColorInt
        private val iconColor: Int,

        @Px
        private val padding: Float,

        @ColorInt
        private val textColor: Int,

        dotWidth: Float = 6f

) : ReplacementSpan() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var iconSize = 0

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var textWidth = 0f

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var path = Path()

    override fun draw(
            canvas: Canvas,
            text: CharSequence,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint
    ) {

    }

}