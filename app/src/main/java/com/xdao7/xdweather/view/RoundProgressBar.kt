package com.xdao7.xdweather.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.xdao7.xdweather.R
import com.xdao7.xdweather.utils.dp2px
import com.xdao7.xdweather.utils.getColor

class RoundProgressBar : BaseScrollAnimatorView {

    /**
     * 圆弧宽度
     */
    private var strokeWidth = dp2px(context, 8f)

    /**
     * 圆弧开始的角度
     */
    private var startAngle = 135f

    /**
     * 起点角度和终点角度对应的夹角大小
     */
    private var angleSize = 270f

    /**
     * 圆弧背景颜色
     */
    private var arcBgColor = 0

    /**
     * 最大的进度，用于计算进度与夹角的比例
     */
    private var maxProgress = 0f

    /**
     * 当前进度对应的起点角度到当前进度角度夹角的大小
     */
    private var currentAngleSize = 0f

    /**
     * 动画中实时绘制的角度
     */
    private var animatorAngleSize = 0f

    /**
     * 进度圆弧的颜色
     */
    private var progressColor = 0

    /**
     * 文本的颜色
     */
    private var textColor = Color.BLACK

    /**
     * 第一行文本
     */
    lateinit var firstText: String

    /**
     * 第一行文本的字体大小
     */
    private var firstTextSize = 0

    /**
     * 第二行文本
     */
    lateinit var secondText: String

    /**
     * 第二行文本的字体大小
     */
    private var secondTextSize = 0

    /**
     * 进度条最小值
     */
    private var minText = 0

    /**
     * 最小值字体大小
     */
    private var minTextSize = 0

    /**
     * 进度最大值
     */
    private var maxText = 0

    /**
     * 最大值字体大小
     */
    private var maxTextSize = 0

    private lateinit var rectF: RectF
    private lateinit var pathPaint: Paint
    private lateinit var textPaint: Paint
    private lateinit var bounds: Rect

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initViews(attrs)
    }

    @SuppressLint("Recycle")
    private fun initViews(attrs: AttributeSet?) {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.RoundProgressBar).apply {
                maxProgress = getFloat(R.styleable.RoundProgressBar_round_max_progress, 100f)
                arcBgColor =
                    getColor(
                        R.styleable.RoundProgressBar_round_bg_color,
                        R.color.colorPlace.getColor()
                    )
                strokeWidth = dp2px(
                    context,
                    getDimension(R.styleable.RoundProgressBar_round_stroke_width, 6f)
                )
                firstText =
                    getString(R.styleable.RoundProgressBar_round_first_text).toString()
                textColor =
                    getColor(
                        R.styleable.RoundProgressBar_round_text_color,
                        R.color.colorDefault.getColor()
                    )
                firstTextSize = dp2px(
                    context,
                    getDimension(R.styleable.RoundProgressBar_round_first_text_size, 12f)
                )
                secondText =
                    getString(R.styleable.RoundProgressBar_round_second_text).toString()
                secondTextSize = dp2px(
                    context,
                    getDimension(R.styleable.RoundProgressBar_round_second_text_size, 25f)
                )
                minText = getInteger(R.styleable.RoundProgressBar_round_min_text, 0)
                minTextSize = dp2px(
                    context,
                    getDimension(R.styleable.RoundProgressBar_round_min_text_size, 12f)
                )
                maxText = getInteger(R.styleable.RoundProgressBar_round_max_text, 100)
                maxTextSize = dp2px(
                    context,
                    getDimension(R.styleable.RoundProgressBar_round_max_text_size, 12f)
                )
                angleSize = getFloat(R.styleable.RoundProgressBar_round_angle_size, 270f)
                startAngle = getFloat(R.styleable.RoundProgressBar_round_start_angle, 135f)
                recycle()
            }
        }

        rectF = RectF()
        bounds = Rect()
        progressColor = R.color.colorHumidity.getColor()
        pathPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = this@RoundProgressBar.strokeWidth.toFloat()
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
        }

        textPaint = Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        animator = ValueAnimator().apply {
            this.duration = 2000L
            addUpdateListener { animator ->
                animatorAngleSize = animator.animatedValue as Float
                invalidate()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2

        rectF.apply {
            left = strokeWidth.toFloat()
            top = strokeWidth.toFloat()
            right = (centerX * 2 - strokeWidth).toFloat()
            bottom = (centerX * 2 - strokeWidth).toFloat()
        }

        drawArcBg(canvas)
        drawArcProgress(canvas)
        drawFirstText(canvas, centerX)
        drawSecondText(canvas, centerX)
        drawMinText(canvas)
        drawMaxText(canvas)
    }

    /**
     * 画最外层的圆弧
     */
    private fun drawArcBg(canvas: Canvas) {
        pathPaint.color = arcBgColor
        canvas.drawArc(rectF, startAngle, angleSize, false, pathPaint)
    }

    /**
     * 画进度
     */
    private fun drawArcProgress(canvas: Canvas) {
        pathPaint.color = progressColor
        canvas.drawArc(rectF, startAngle, animatorAngleSize, false, pathPaint)
    }

    /**
     * 绘制第一级文本
     */
    private fun drawFirstText(canvas: Canvas, centerX: Int) {
        if (firstText.isEmpty()) {
            return
        }
        canvas.apply {
            textPaint.apply {
                color = textColor
                textSize = firstTextSize.toFloat()
                getTextBounds(firstText, 0, firstText.length, bounds)
            }
            drawText(
                firstText,
                centerX.toFloat(),
                (bounds.height() / 2 + height * 3 / 10).toFloat(),
                textPaint
            )
        }
    }

    /**
     * 绘制第二级文本
     */
    private fun drawSecondText(canvas: Canvas, centerX: Int) {
        canvas.apply {
            textPaint.apply {
                color = progressColor
                textSize = secondTextSize.toFloat()
                getTextBounds(secondText, 0, secondText.length, bounds)
            }
            val textY = if (firstText.isEmpty()) {
                ((height + bounds.height()) / 2).toFloat()
            } else {
                (height * 7 / 15 + bounds.height()).toFloat()
            }
            drawText(
                secondText,
                centerX.toFloat(),
                textY,
                textPaint
            )
        }
    }

    /**
     * 绘制最小值文本
     */
    private fun drawMinText(canvas: Canvas) {
        canvas.apply {
            textPaint.apply {
                color = textColor
                textSize = minTextSize.toFloat()
                getTextBounds(maxText.toString(), 0, maxText.toString().length, bounds)
            }
            val maxWidth = bounds.width()
            textPaint.getTextBounds(minText.toString(), 0, minText.toString().length, bounds)
            val minWidth = bounds.width()
            drawText(
                minText.toString(),
                rectF.left + maxWidth - minWidth / 2,
                rectF.bottom + 8,
                textPaint
            )
        }
    }

    /**
     * 绘制最大值文本
     */
    private fun drawMaxText(canvas: Canvas) {
        canvas.apply {
            textPaint.apply {
                color = textColor
                textSize = maxTextSize.toFloat()
                getTextBounds(maxText.toString(), 0, maxText.toString().length, bounds)
            }
            drawText(
                maxText.toString(),
                rectF.right - bounds.width(),
                rectF.bottom + 8,
                textPaint
            )
        }
    }

    /**
     * 设置当前进度
     *
     * @param progress
     */
    fun setProgress(progress: Float) {
        if (progress < 0) {
            throw IllegalArgumentException("Progress value can not be less than 0")
        }
        val currentProgress = if (progress > maxProgress) {
            maxProgress
        } else {
            progress
        }

        val size = currentProgress / maxProgress
        currentAngleSize = angleSize * size

        isNeedAnimator = true
        startAnimator(null)
    }

    fun chooseColor(level: Int) {
        progressColor = when (level) {
            1 -> R.color.colorAirLevel1.getColor()
            2 -> R.color.colorAirLevel2.getColor()
            3 -> R.color.colorAirLevel3.getColor()
            4 -> R.color.colorAirLevel4.getColor()
            5 -> R.color.colorAirLevel5.getColor()
            6 -> R.color.colorAirLevel6.getColor()
            else -> R.color.colorHumidity.getColor()
        }
    }

    /**
     * 启动动画
     */
    override fun startAnimator(scrollRect: Rect?) {
        baseAnimator(scrollRect) {
            animator.apply {
                setFloatValues(0f, currentAngleSize)
                start()
            }
        }
    }
}