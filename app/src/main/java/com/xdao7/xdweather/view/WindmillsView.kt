package com.xdao7.xdweather.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.xdao7.xdweather.R
import com.xdao7.xdweather.utils.getColor

class WindmillsView : View {

    /**
     * 叶片的长度
     */
    private var bladeRadius = 0f

    /**
     * 风车叶片旋转中心x
     */
    private var centerY = 0f

    /**
     * 风车叶片旋转中心y
     */
    private var centerX = 0f

    /**
     * 风车旋转中心点圆的半径
     */
    private var pivotRadius = 0f
    private val paint = Paint()

    /**
     * 风车旋转时叶片偏移的角度
     */
    private var offsetAngle = 0f
    private val path = Path()

    /**
     * 控件颜色
     */
    private var color = Color.WHITE

    private lateinit var animator: ValueAnimator

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initViews(attrs)
    }

    private fun initViews(attrs: AttributeSet?) {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.WindmillsView).apply {
                color = getColor(
                    R.styleable.WindmillsView_windColor,
                    R.color.colorWindmills.getColor()
                )
                recycle()
            }
        }

        paint.apply {
            isAntiAlias = true
            color = this@WindmillsView.color
            style = Paint.Style.FILL
        }

        animator = ValueAnimator().apply {
            duration = 3600L
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener { animator ->
                offsetAngle = animator.animatedValue as Float
                invalidate()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        centerX = (measuredWidth / 2).toFloat()
        centerY = centerX

        pivotRadius = (measuredWidth / 40).toFloat()
        bladeRadius = centerY - 2 * pivotRadius
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawPivot(canvas)
        drawWindBlade(canvas)
        drawPillar(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startRotate()
    }

    override fun onDetachedFromWindow() {
        stopRotate()
        super.onDetachedFromWindow()
    }

    /**
     * 画扇叶旋转的中心
     */
    private fun drawPivot(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, pivotRadius, paint)
    }

    /**
     * 画扇叶
     */
    private fun drawWindBlade(canvas: Canvas) {
        paint.pathEffect = CornerPathEffect(15f)
        canvas.apply {
            save()
            rotate(offsetAngle, centerX, centerY)
            path.apply {
                reset()
                moveTo(centerX, centerY - pivotRadius)
                lineTo(centerX, centerY - pivotRadius - bladeRadius)
                lineTo(centerX + pivotRadius * 2, pivotRadius + bladeRadius * 2 / 3)
                close()
            }

            drawPath(path, paint)
            rotate(120f, centerX, centerY)
            drawPath(path, paint)
            rotate(120f, centerX, centerY)
            drawPath(path, paint)
            restore()
        }
    }

    /**
     * 画底部支柱
     */
    private fun drawPillar(canvas: Canvas) {
        paint.pathEffect = CornerPathEffect(5f)
        canvas.apply {
            path.apply {
                reset()
                moveTo(centerX - pivotRadius / 2, centerY + pivotRadius)
                lineTo(centerX + pivotRadius / 2, centerY + pivotRadius)
                lineTo(centerX + pivotRadius, measuredHeight - pivotRadius * 2)
                lineTo(centerX - pivotRadius, measuredHeight - pivotRadius * 2)
                close()
            }
            drawPath(path, paint)
        }
    }

    private fun startRotate() {
        animator.apply {
            setFloatValues(0f, 360f)
            start()
        }
    }

    private fun stopRotate() {
        animator.apply {
            if (isRunning) {
                cancel()
            }
        }
    }
}