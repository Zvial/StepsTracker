package com.zzvial.steptracker.custom_views.linear_chart_view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.zzvial.steptracker.R

class LinearChartView(val cont: Context, val attributeSet: AttributeSet): View(cont, attributeSet) {
    private var leftPx: Int = 0
    private var rightPx: Int = width
    private var bottomPx: Int = 0
    private var topPx: Int = height
    private var maxY = 0F
    private var maxX = 0F

    var values: Map<Float, Float>? = mapOf(1F to 25F, 2F to 40F, 3F to 1F, 4F to 11F, 5F to 77F, 6F to 8F, 7F to 18F, 9F to 45F)
    set(value) {
        field = value
        calculateMaxXY()
    }

    init {
        calculateMaxXY()
    }

    @SuppressLint("ResourceAsColor")
    override fun onDraw(canvas: Canvas?) {
        values?.let { vals ->
            prepareBorders()

            val paint = Paint()
            paint.setStyle(Paint.Style.STROKE)
            paint.color = R.color.purple_500
            paint.setStrokeWidth(4F)

            val path = Path()
            val firstEntry = vals.entries.firstOrNull()
            if(firstEntry!=null) {
                path.moveTo(getXPos(firstEntry.key), getYPos(firstEntry.value))
                for (i in 1..vals.entries.size-1) {
                    val curEntry = vals.entries.elementAt(i)
                    path.lineTo(getXPos(curEntry.key), getYPos(curEntry.value))
                }
            }
            canvas?.drawPath(path, paint)
        }
    }

    private fun prepareBorders() {
        leftPx = paddingStart
        bottomPx = height - paddingBottom
        rightPx = width - paddingEnd
        topPx = paddingTop
    }

    private fun calculateMaxXY() {
        maxX = values?.keys?.maxByOrNull { it } ?: 0F
        maxY = values?.values?.maxByOrNull { it } ?: 0F
    }

    private fun getYPos(value: Float): Float {
        val screenHeight = height - paddingBottom - paddingTop

        var yValue = (value / maxY) * screenHeight
        yValue = screenHeight - yValue //инверсия

        yValue += paddingTop

        return yValue
    }

    private fun getXPos(value: Float): Float {
        val screenWidth = width - paddingStart - paddingEnd

        var xValue = (value / maxX) * screenWidth

        xValue += paddingStart

        return xValue
    }
}