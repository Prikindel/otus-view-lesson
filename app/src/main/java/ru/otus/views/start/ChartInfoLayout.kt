package ru.otus.views.start

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import ru.otus.views.R
import ru.otus.views.TAG
import ru.otus.views.px
import kotlin.math.min

class ChartInfoLayout @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null
) : View(context, attr) {

    private val list = ArrayList<Int>()
    private var maxValue = 0
    private var barWidth = 50.px.toFloat()
    private lateinit var paintBaseFill : Paint
    private lateinit var paintDangerFill : Paint
    private var threshold: Int = Int.MAX_VALUE
    private lateinit var paintStroke : Paint
    private val rect = RectF()

    init {
        if (isInEditMode) {
            setValues(listOf(60, 20, 40, 81, 45, 30, 70))
        }

        val typedArray = context.obtainStyledAttributes(attr, R.styleable.ChartInfoLayout)
        val threshold = typedArray.getInteger(R.styleable.ChartInfoLayout_threshold, 80)
        val barWidth = typedArray.getDimension(R.styleable.ChartInfoLayout_barWidth, 50.px.toFloat())
        val dangerColor = typedArray.getColor(R.styleable.ChartInfoLayout_dangerColor, Color.RED)
        typedArray.recycle()

        setup(Color.GREEN, dangerColor, threshold, barWidth)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        val wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)

        when (wMode) {
            MeasureSpec.EXACTLY -> {
                Log.i(TAG, "EXACTLY $wSize $hSize ")
                setMeasuredDimension(wSize, hSize)
            }
            MeasureSpec.AT_MOST -> {
                Log.i(TAG, "AT_MOST $wSize $hSize ")
                val newW = min((list.size * barWidth).toInt(), wSize)
                setMeasuredDimension(newW, hSize)
            }
            MeasureSpec.UNSPECIFIED -> {
                Log.i(TAG, "UNSPECIFIED $wSize $hSize ")
                setMeasuredDimension((list.size * barWidth).toInt(), hSize)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (list.size == 0) return

        val widthPerView = width.toFloat() / list.size
        var currentX = 0f
        val heightPerValue = height.toFloat() / maxValue

        for (item in list) {
            rect.set(
                currentX,
                (height - heightPerValue * item),
                (currentX + widthPerView),
                height.toFloat(),
            )
            canvas.drawRect(rect, if (item > threshold) paintDangerFill else paintBaseFill)
            canvas.drawRect(rect, paintStroke)
            currentX += widthPerView
        }
    }

    fun setValues(values : List<Int>) {
        list.clear()
        list.addAll(values)
        maxValue = list.max()

        requestLayout()
        invalidate()
    }

    fun setThreshold(threshold : Int) {
        this.threshold = threshold

        requestLayout()
        invalidate()
    }

    private fun setup(baseColor: Int, dangerColor: Int, threshold : Int, barWidth : Float) {
        paintBaseFill = Paint().apply {
            color = baseColor
            style = Paint.Style.FILL
        }
        paintDangerFill = Paint().apply {
            color = dangerColor
            style = Paint.Style.FILL
        }
        paintStroke = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 2.0f
        }
        this.threshold = threshold
        this.barWidth = barWidth
    }
}