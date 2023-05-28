package ru.otus.views.finish

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.ViewGroup
import ru.otus.views.utils.PaintCloudsHelper
import ru.otus.views.R
import java.lang.Integer.max

class ChartInfoLayout @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
) : ViewGroup(context, attrs)  {

    private val paintCloudsHelper = PaintCloudsHelper()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        require(childCount == 2)

        val chart = getChildAt(0)
        val title = getChildAt(1)

        title.measure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST),
        )

        chart.measure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize((chart.layoutParams as LayoutParams).height),
                MeasureSpec.EXACTLY
            ),
        )

        val forceTop = (chart.layoutParams as LayoutParams).forceTop

        if (chart.measuredWidth > chart.measuredHeight || forceTop) {
            setMeasuredDimension(
                max(title.measuredWidth, chart.measuredWidth),
                title.measuredHeight + chart.measuredHeight,
            )
        } else {
            setMeasuredDimension(
                max(title.measuredWidth, chart.measuredWidth),
                chart.measuredHeight,
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
            title.layout(l, b - title.measuredHeight, l + title.measuredWidth, b)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        paintCloudsHelper.paint(this, canvas)
        super.dispatchDraw(canvas)
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    class LayoutParams(context: Context, attrs: AttributeSet) :
        ViewGroup.LayoutParams(context, attrs) {
        var forceTop: Boolean = false

        init {
            val typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.ChartInfoLayout_Layout)
            forceTop =
                typedArray.getBoolean(R.styleable.ChartInfoLayout_Layout_layout_forceTop, false)
            typedArray.recycle()
        }
    }
}