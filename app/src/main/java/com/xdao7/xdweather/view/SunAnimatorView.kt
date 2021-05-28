package com.xdao7.xdweather.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.xdao7.xdweather.R
import com.xdao7.xdweather.utils.bitmapResize
import com.xdao7.xdweather.utils.dp2px
import com.xdao7.xdweather.utils.getColor
import com.xdao7.xdweather.utils.getCurrentTime
import java.text.DecimalFormat
import kotlin.math.cos
import kotlin.math.sin

class SunAnimatorView : BaseScrollAnimatorView {

    /**
     * 顶部的间隔
     */
    private var marginTop = 0

    /**
     * 圆弧颜色
     */
    private var circleColor = 0

    /**
     * 字体颜色
     */
    private var fontColor = 0

    /**
     * 圆的半径
     */
    private var radius = 0

    /**
     * 当前旋转的角度
     */
    private var currentAngle = 0f

    /**
     * 动画中实时绘制的角度
     */
    private var animatorAngle = 0f

    private var positionX = 0f
    private var positionY = 0f
    private var fontSize = 0f

    private var startTime: String = ""
    private var endTime: String = ""

    private lateinit var textPaint: Paint
    private lateinit var imagePaint: Paint
    private lateinit var timePaint: Paint
    private lateinit var pathPaint: Paint
    private lateinit var rectF: RectF
    private lateinit var sunIcon: Bitmap
    private lateinit var moonIcon: Bitmap
    private var isSun = true
    private var iconWidth = 0f

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(attrs)
    }

    @SuppressLint("Recycle")
    private fun initView(attrs: AttributeSet?) {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.SunAnimatorView).apply {
                circleColor = getColor(
                    R.styleable.SunAnimatorView_sun_circle_color,
                    R.color.colorDefault.getColor()
                )
                fontColor = getColor(
                    R.styleable.SunAnimatorView_sun_font_color,
                    R.color.colorDefault.getColor()
                )
                radius = dp2px(
                    context,
                    getInteger(R.styleable.SunAnimatorView_sun_circle_radius, 10).toFloat()
                )
                fontSize = getDimension(R.styleable.SunAnimatorView_sun_font_size, 12f)
                fontSize = dp2px(context, fontSize).toFloat()
                isSun = getBoolean(R.styleable.SunAnimatorView_type, true)
                recycle()
            }
        }

        marginTop = dp2px(context, 30f)

        textPaint = Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = fontColor
            textSize = fontSize
            textAlign = Paint.Align.CENTER
        }

        imagePaint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        timePaint = Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = R.color.black.getColor()
            textSize = fontSize
            textAlign = Paint.Align.CENTER
        }

        pathPaint = Paint().apply {
            isAntiAlias = true
            color = R.color.black.getColor()
            isDither = true
            color = circleColor
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 0f)
        }

        iconWidth = dp2px(context, 18f).toFloat()
        sunIcon = bitmapResize(
            BitmapFactory.decodeResource(resources, R.drawable.ic_sun),
            iconWidth, iconWidth
        )
        moonIcon = bitmapResize(
            BitmapFactory.decodeResource(resources, R.drawable.ic_moon),
            iconWidth, iconWidth
        )

        animator = ValueAnimator().apply {
            this.duration = 2000L
            addUpdateListener { animation ->
                animatorAngle = animation.animatedValue as Float
                invalidateView()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        positionX = measuredWidth / 2 - radius - iconWidth / 2
        positionY = radius + marginTop - iconWidth / 2
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(
            changed,
            measuredWidth / 2 - radius,
            marginTop,
            measuredWidth / 2 + radius,
            radius * 2 + marginTop
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawSemicircle(canvas)
        drawIconPosition(canvas)
        drawFont(canvas)
    }

    /**
     * 绘制半圆弧线
     */
    private fun drawSemicircle(canvas: Canvas) {
        rectF = RectF(
            (measuredWidth / 2 - radius).toFloat(),
            marginTop.toFloat(),
            (measuredWidth / 2 + radius).toFloat(),
            (radius * 2 + marginTop).toFloat()
        )
        canvas.apply {
            drawArc(rectF, 180f, 180f, false, pathPaint)
            drawLine(
                measuredWidth / 2 - radius - 18f,
                (radius + marginTop).toFloat(),
                measuredWidth / 2 + radius + 18f,
                (radius + marginTop).toFloat(),
                timePaint
            )
        }
    }

    /**
     * 绘制太阳、月亮图片
     */
    private fun drawIconPosition(canvas: Canvas) {
        canvas.apply {
            if (isSun) {
                drawBitmap(sunIcon, positionX, positionY, imagePaint)
            } else {
                drawBitmap(moonIcon, positionX, positionY, imagePaint)
            }
        }
    }

    /**
     * 绘制文字
     */
    private fun drawFont(canvas: Canvas) {
        val rise: String
        val set: String
        if (isSun) {
            rise = resources.getString(R.string.str_sun_rise)
            set = resources.getString(R.string.str_sun_set)
        } else {
            rise = resources.getString(R.string.str_moon_rise)
            set = resources.getString(R.string.str_moon_set)
        }

        canvas.apply {
            drawText(
                rise,
                (measuredWidth / 2 - radius + dp2px(context, 8f)).toFloat(),
                (radius + dp2px(context, 16f) + marginTop).toFloat(),
                textPaint
            )
            drawText(
                startTime,
                (measuredWidth / 2 - radius + dp2px(context, 8f)).toFloat(),
                (radius + dp2px(context, 32f) + marginTop).toFloat(),
                timePaint
            )
            drawText(
                set,
                (measuredWidth / 2 + radius - dp2px(context, 8f)).toFloat(),
                (radius + dp2px(context, 16f) + marginTop).toFloat(),
                textPaint
            )
            drawText(
                endTime,
                (measuredWidth / 2 + radius - dp2px(context, 8f)).toFloat(),
                (radius + dp2px(context, 32f) + marginTop).toFloat(),
                timePaint
            )
        }
    }

    /**
     * 设置时间
     *
     * @param startTime 升起时间（00:00格式）
     * @param endTime 落下时间（00:00格式）
     * @param currentTime 当前时间
     */
    fun setTime(startTime: String, endTime: String, currentTime: String = getCurrentTime()) {
        this.startTime = startTime
        this.endTime = endTime

        val currentTimes = currentTime.split(":")
        val startTimes = startTime.split(":")
        val endTimes = endTime.split(":")

        val currentHour = currentTimes[0].toFloat()
        val currentMin = currentTimes[1].toFloat()
        val startHour = startTimes[0].toFloat()
        val startMin = startTimes[1].toFloat()
        var endHour = endTimes[0].toFloat()
        val endMin = endTimes[1].toFloat()

        if (!isSun && endHour < startHour) {
            endHour += 24
        }

        val totalMinute = calculateTime(startHour, startMin, endHour, endMin, false)
        val needMinute =
            if (isSun && (currentHour > endHour || (currentHour == endHour && currentMin >= endMin))) {
                calculateTime(startHour, startMin, endHour, endMin, true)
            } else {
                calculateTime(startHour, startMin, currentHour, currentMin, true)
            }
        val percentage = formatTime(totalMinute, needMinute).toFloat()
        currentAngle = percentage * 180

        isNeedAnimator = true
        startAnimator()
    }

    /**
     * 根据日出和日落时间计算出一天总共的时间:单位为分钟
     */
    private fun calculateTime(
        startHour: Float,
        startMin: Float,
        endHour: Float,
        endMin: Float,
        isCurrent: Boolean
    ): Float {
        if (isSun) {
            if (startHour > endHour || (startHour == endHour && startMin >= endMin)) {
                return 0f
            }
        } else {
            if (isCurrent) {
                if (startHour > endHour || (startHour == endHour && startMin >= endMin)) {
                    return 0f
                }
            } else {
                if (startHour >= endHour + 24) {
                    return 0f
                }
            }
        }
        return (endHour - startHour - 1) * 60 + (60 - startMin) + endMin
    }

    /**
     * 计算百分比
     */
    private fun formatTime(totalTime: Float, needTime: Float) = if (totalTime == 0f) {
        "0.00"
    } else {
        DecimalFormat("0.00").run {
            format(needTime / totalTime)
        }
    }

    /**
     * 启动动画
     */
    override fun startAnimator() {
        baseAnimator {
            animator.apply {
                setFloatValues(0f, currentAngle)
                start()
            }
        }
    }

    /**
     * 动态修正太阳、月亮位置
     */
    private fun invalidateView() {
        positionX =
            (measuredWidth / 2 - (radius * cos((animatorAngle) * Math.PI / 180)) - iconWidth / 2).toFloat()
        positionY =
            (radius + marginTop - (radius * sin((animatorAngle) * Math.PI / 180)) + -iconWidth / 2).toFloat()
        invalidate()
    }
}