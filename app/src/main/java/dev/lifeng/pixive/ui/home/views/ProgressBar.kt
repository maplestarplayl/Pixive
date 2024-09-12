package dev.lifeng.pixive.ui.home.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toColorInt

class ProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    private var progress: Int = 0  // 进度值 (0 - 100)
    // 定义画笔
    private val paintBackground = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 4f
        color = "#FFF0F0F4".toColorInt()
        isAntiAlias = true
    }
    private val paintFill = Paint().apply {
        style = Paint.Style.FILL
        color = "#98E4FF".toColorInt()
        isAntiAlias = true
    }
    private val rectF = RectF()
    private val progressRectF = RectF()
    // 动画中改变的圆角半径

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val screenWidth = width
        val screenHeight = height
        val top = screenHeight * 0.9f  // 使用屏幕高度的75%作为上边界
        val bottom = top + (screenHeight * 0.02f)  // 进度条的高度为屏幕高度的2%
        val left = screenWidth * 0.3f  // 使用屏幕宽度的10%作为左边界
        val right = screenWidth * 0.7f  // 使用屏幕宽度的90%作为右边界
        val relativeWidth = right - left
        rectF.set(left, top, right, bottom)
        canvas.drawRoundRect(rectF, height / 2f, height / 2f, paintBackground)

        // 根据进度画填充
        val progressWidth = (progress / 100f) * (relativeWidth)
        progressRectF.set(left, top, left + progressWidth, bottom)
        canvas.drawRoundRect(progressRectF, height / 2f, height / 2f, paintFill)
    }
    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }
}