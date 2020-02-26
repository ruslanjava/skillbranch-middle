package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.*
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import ru.skillbranch.skillarticles.markdown.Element

class BlockCodeSpan(

        @ColorInt
        private val textColor: Int,

        @ColorInt
        private val bgColor: Int,

        @Px
        private val cornerRadius: Float,

        @Px
        private val padding: Float,

        private val type: Element.BlockCode.Type

) : ReplacementSpan() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var rect = RectF()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var path = Path()

    private var defaultAscent: Float = 0.0f
    private var defaultDescent: Float = 0.0f

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
        when (type) {
            Element.BlockCode.Type.SINGLE -> {
                paint.forBackground {
                    canvas.drawRoundRect(
                            RectF(0f, top + padding, canvas.width.toFloat(), bottom - padding),
                            cornerRadius,
                            cornerRadius,
                            paint
                    )
                }
            }
            Element.BlockCode.Type.START -> {
                paint.forBackground {
                    path.reset()
                    path.addRoundRect(
                            RectF(
                                    0f,
                                    top + padding,
                                    canvas.width.toFloat(),
                                    bottom.toFloat()
                            ),
                            floatArrayOf(
                                    cornerRadius, cornerRadius, // Top left radius in px
                                    cornerRadius, cornerRadius, // Top right radius in px
                                    0f, 0f, // Bottom right radius in px
                                    0f, 0f // Bottom left radius in px
                            ),
                            Path.Direction.CW
                    )
                    canvas.drawPath(path, paint)
                }
            }
            Element.BlockCode.Type.END -> {
                paint.forBackground {
                    path.reset()
                    path.addRoundRect(
                            RectF(
                                    0f,
                                    top.toFloat(),
                                    canvas.width.toFloat(),
                                    bottom - padding
                            ),
                            floatArrayOf(
                                    0f, 0f,
                                    0f, 0f,
                                    cornerRadius, cornerRadius,
                                    cornerRadius, cornerRadius
                            ),
                            Path.Direction.CW
                    )
                    canvas.drawPath(path, paint)
                }
            }
        }
        paint.forText {
            canvas.drawText(text, 0, text.length, x + padding, y.toFloat(), paint)
        }
    }

    override fun getSize(
            paint: Paint,
            text: CharSequence,
            start: Int,
            end: Int,
            fm: Paint.FontMetricsInt?
    ): Int {

        defaultAscent = paint.ascent() / 0.85f
        defaultDescent = paint.descent() / 0.85f

        if (fm != null) {
            when (type) {
                Element.BlockCode.Type.SINGLE -> {
                    fm.ascent = (defaultAscent * 0.85f - 2 * padding).toInt()
                    fm.descent = (defaultDescent * 0.85f + 2 * padding).toInt()
                }
                Element.BlockCode.Type.START -> {
                    fm.ascent = (defaultAscent * 0.85f - 2 * padding).toInt()
                    fm.descent = (defaultDescent * 0.85f).toInt()
                }
                Element.BlockCode.Type.MIDDLE -> {
                    fm.ascent = (defaultAscent * 0.85f).toInt()
                    fm.descent = (defaultDescent * 0.85f).toInt()
                }
                Element.BlockCode.Type.END -> {
                    fm.ascent = (defaultAscent * 0.85f).toInt()
                    fm.descent = (defaultDescent * 0.85f + 2 * padding).toInt()
                }
            }
        }
        return 0
    }

    private inline fun Paint.forBackground(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style

        color = bgColor
        style = Paint.Style.FILL

        block()
        color = oldColor
        style = oldStyle
    }

    private inline fun Paint.forText(block: () -> Unit) {
        val oldSize = textSize
        val oldStyle = typeface?.style ?: 0
        val oldFont = typeface
        val oldColor = color

        color = textColor
        typeface = Typeface.create(Typeface.MONOSPACE, oldStyle)
        textSize *= 0.85f

        block()

        color = oldColor
        typeface = oldFont
        textSize = oldSize
    }

}
