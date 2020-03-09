package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.Layout
import android.text.Spanned
import androidx.core.text.getSpans
import ru.skillbranch.skillarticles.extensions.*
import ru.skillbranch.skillarticles.ui.custom.markdown.spans.HeaderSpan
import ru.skillbranch.skillarticles.ui.custom.markdown.spans.SearchSpan

class SearchBgHelper(
    context: Context,
    private val focusListener: (Int) -> Unit
) {

    private val padding: Int = context.dpToIntPx(4)
    private val borderWidth: Int = context.dpToIntPx(1)
    private val radius: Float = context.dpToPx(8)

    private val secondaryColor: Int = context.attrValue(ru.skillbranch.skillarticles.R.attr.colorSecondary)
    private val alphaColor: Int = androidx.core.graphics.ColorUtils.setAlphaComponent(secondaryColor, 160)

    val drawable: Drawable by lazy {
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = FloatArray(8).apply {
                fill(radius, 0, size)
            }
            color = ColorStateList.valueOf(alphaColor)
            setStroke(borderWidth, secondaryColor)
        }
    }

    val drawableLeft: Drawable by lazy {
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = floatArrayOf(
                radius, radius, // top left radius in px
                0f, 0f,         // top right radius in px
                0f, 0f,         // bottom right radius in px
                radius, radius  // bottom left radius in px
            )
            color = ColorStateList.valueOf(alphaColor)
            setStroke(borderWidth, secondaryColor)
        }
    }

    val drawableMiddle: Drawable by lazy {
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            color = ColorStateList.valueOf(alphaColor)
            setStroke(borderWidth, secondaryColor)
        }
    }

    val drawableRight: Drawable by lazy {
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = floatArrayOf(
                0f, 0f,         // top left radius in px
                radius, radius, // top right radius in px
                radius, radius, // bottom right radius in px
                0f, 0f          // bottom left radius in px
            )
            color = ColorStateList.valueOf(alphaColor)
            setStroke(borderWidth, secondaryColor)
        }
    }

    private lateinit var render: SearchBgRender
    private val singleLineRender: SingleLineRender by lazy {
        SingleLineRender(padding, drawable)
    }
    private val multiLineRender: MultiLineRender by lazy {
        MultiLineRender(
            padding,
            drawableLeft,
            drawableMiddle,
            drawableRight
        )
    }

    private lateinit var spans: Array<out SearchSpan>
    private lateinit var headerSpans: Array<out SearchSpan>

    private var spanStart = 0
    private var spanEnd = 0
    private var startLine = 0
    private var endLine = 0

    private var startOffset = 0
    private var endOffset = 0

    private var topExtraPadding = 0
    private var bottomExtraPadding = 0

    fun draw(canvas: Canvas, text: Spanned, layout: Layout) {
        spans = text.getSpans()
        spans.forEach {
            spanStart = text.getSpanStart(it)
            spanEnd = text.getSpanEnd(it)
            startLine = layout.getLineForOffset(spanStart)
            endLine = layout.getLineForOffset(spanEnd)

            val headerSpans = text.getSpans(spanStart, spanEnd, HeaderSpan::class.java)

            topExtraPadding = 0
            bottomExtraPadding = 0

            if (headerSpans.isNotEmpty()) {
                // TODO
                topExtraPadding = if (
                    spanStart in headerSpans[0].firstLineBounds ||
                    spanEnd in headerSpans[0].firstLineBounds
                ) headerSpans[0].topExtraPadding else 0

                bottomExtraPadding = if (
                    spanStart in headerSpans[0].lastLineBounds ||
                    spanEnd in headerSpans[0].lastLineBounds
                ) headerSpans[0].bottomExtraPadding else 0
            }

            startOffset = layout.getPrimaryHorizontal(spanStart).toInt()
            endOffset = layout.getPrimaryHorizontal(spanEnd).toInt()

            render = if (startLine == endLine) singleLineRender else multiLineRender
            render.draw(
                canvas,
                layout,
                startLine,
                endLine,
                startOffset,
                endOffset,
                topExtraPadding,
                bottomExtraPadding
            )
        }
    }

}

abstract class SearchBgRender(
    val padding: Int
) {

    abstract fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        topExtraPadding: Int = 0,
        bottomExtraPadding: Int = 0
    )

    fun getLineTop(layout: Layout, line: Int): Int {
        return layout.getLineTopWithoutPadding(line)
    }

    fun getLineBottom(layout: Layout, line: Int): Int {
        return layout.getLineBottomWithoutPadding(line)
    }

}

class SingleLineRender(
    padding: Int,
    val drawable: Drawable
): SearchBgRender(padding) {

    private var lineTop: Int = 0
    private var lineBottom: Int = 0

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        topExtraPadding: Int,
        bottomExtraPadding: Int
    ) {
        // TODO implement extra padding
        lineTop = getLineTop(layout, startLine) + topExtraPadding
        lineBottom = getLineBottom(layout, startLine) - bottomExtraPadding
        drawable.setBounds(startOffset - padding, lineTop, endOffset + padding, lineBottom)
        drawable.draw(canvas)
    }
}

class MultiLineRender(
    padding: Int,
    val drawableLeft: Drawable,
    val drawableMiddle: Drawable,
    val drawableRight: Drawable
): SearchBgRender(padding) {

    private var lineTop: Int = 0
    private var lineBottom: Int = 0

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        topExtraPadding: Int,
        bottomExtraPadding: Int
    ) {
        // TODO IMPLEMENT ME!!
        lineTop = getLineTop(layout, startLine)
        lineBottom = getLineBottom(layout, startLine)
    }

}

