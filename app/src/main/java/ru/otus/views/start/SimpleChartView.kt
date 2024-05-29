package ru.otus.views.start

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.ViewGroup
import ru.otus.views.R
import ru.otus.views.finish.ChartInfoLayout
import ru.otus.views.utils.PaintCloudsHelper
import kotlin.math.max

class SimpleChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet
) : ViewGroup(context, attrs) {

    private val paintCloudsHelper = PaintCloudsHelper()
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        require(childCount == 2)

        val wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)

        val chart = getChildAt(0)
        val title = getChildAt(1)

        title.measure(
            MeasureSpec.makeMeasureSpec(wSize, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(hSize, MeasureSpec.AT_MOST)
        )
        chart.measure(
            MeasureSpec.makeMeasureSpec(wSize, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize((chart.layoutParams as LayoutParams).height),
                MeasureSpec.EXACTLY
            ),
        )

        val forceTop = (chart.layoutParams as LayoutParams).forceTop

        if (chart.measuredWidth > chart.measuredHeight || forceTop) {
            setMeasuredDimension(
                max(title.measuredWidth, chart.measuredWidth),
                chart.measuredHeight + title.measuredHeight
            )
        } else {
            setMeasuredDimension(
                max(title.measuredWidth, chart.measuredWidth),
                chart.measuredHeight
            )
        }
    }
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        require(childCount == 2)

        val chart = getChildAt(0)
        val title = getChildAt(1)

        val forceTop = (chart.layoutParams as LayoutParams).forceTop

        if (chart.measuredWidth > chart.measuredHeight || forceTop) {
            title.layout(l, t, l + title.measuredWidth, t + title.measuredHeight)
            chart.layout(l, t + title.measuredHeight, l + chart.measuredWidth, b)
        } else {
            chart.layout(l, t, l + chart.measuredWidth, t + chart.measuredHeight)
            title.layout(l,b - title.measuredHeight, l + title.measuredWidth, b)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        paintCloudsHelper.paint(this, canvas)
        super.dispatchDraw(canvas)
    }

    // Ошибка была в том, что параметр attrs не может быть нулабельным
    // Теперь если высота больше чем ширина, и стоит параметр layout_forceTop=true,
    // то отрисовываться будет также, как при ширине большей, чем высота (сначала текст, потом чарт)
    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    class LayoutParams(context: Context, attrs: AttributeSet)
        : ViewGroup.LayoutParams(context, attrs) {

            var forceTop = false

            init {
                val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleChartView_Layout)
                forceTop = typedArray.getBoolean(R.styleable.SimpleChartView_Layout_layout_forceTop, false)
                typedArray.recycle()
            }
    }
}