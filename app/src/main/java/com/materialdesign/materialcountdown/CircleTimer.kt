package com.materialdesign.materialcountdown

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

/**
 * TODO: document your custom view class.
 */
class CircleTimer : View {

    lateinit var paintFill: Paint
    lateinit var paintBorder: Paint

    var colorFill: Int = 0
    var colorBorder: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {
        colorFill = Color.MAGENTA
        colorBorder = Color.GRAY
        val array = context.obtainStyledAttributes(attrs, R.styleable.CircleTimer)
        colorFill = attrs.let { array.getColor(R.styleable.CircleTimer_android_fillColor, colorFill) }
        colorBorder = attrs.let { array.getColor(R.styleable.CircleTimer_android_color, colorBorder) }
        array.recycle()

        paintFill = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFill.style = Paint.Style.FILL

        paintBorder = Paint(Paint.ANTI_ALIAS_FLAG)
        paintBorder.style = Paint.Style.STROKE
        paintBorder.strokeWidth = 16f
        println("init() attrs color $colorFill")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = min(measuredHeight, measuredWidth)
        setMeasuredDimension(size, size)
    }

    fun setFillColor(color: Int) {
        colorFill = color
        invalidate()
        println("Invalidate called")
    }

    fun setBorderColor(color: Int) {
        colorBorder = color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width - (paddingLeft + paddingRight)
        val height = height - (paddingTop + paddingBottom)

        val cx = ((width / 2) + paddingStart).toFloat()
        val cy = ((height / 2) + paddingTop).toFloat()
        val radius = (min(width, height) / 2).toFloat()

        paintFill.color = colorFill
        paintBorder.color = colorBorder

        canvas.drawCircle(cx, cy, radius, paintFill)
        canvas.drawCircle(cx, cy, radius, paintBorder)
    }
}
