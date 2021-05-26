package com.xdao7.xdweather.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.xdao7.xdweather.R
import com.xdao7.xdweather.utils.bitmapResize
import com.xdao7.xdweather.utils.dp2px
import com.xdao7.xdweather.utils.getCurrentTime
import java.text.DecimalFormat
import kotlin.math.cos
import kotlin.math.sin

class SunAnimtorView : BaseScrollAnimtorView {

    /**
     * 控件宽度
     */
    private var viewWidth = 0

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

    /**
     * 总时间
     */
    private var totalMinute = 0f

    /**
     * 当前时间减去日出时间后的总数
     */
    private var needMinute = 0f

    /**
     * 根据所给的时间算出来的百分占比
     */
    private var percentage = 0f
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

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.SunView).apply {
                circleColor = getColor(
                    R.styleable.SunView_sun_circle_color,
                    ContextCompat.getColor(context, R.color.colorDefault)
                )
                fontColor = getColor(
                    R.styleable.SunView_sun_font_color,
                    ContextCompat.getColor(context, R.color.colorDefault)
                )
                radius =
                    dp2px(context, getInteger(R.styleable.SunView_sun_circle_radius, 10).toFloat())
                fontSize = getDimension(R.styleable.SunView_sun_font_size, 12f)
                fontSize = dp2px(context, fontSize).toFloat()
                isSun = getBoolean(R.styleable.SunView_type, true)
                recycle()
            }
        }

        marginTop = dp2px(context, 30f)

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = fontColor
            textSize = fontSize
            textAlign = Paint.Align.CENTER
        }

        imagePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isDither = true
        }

        timePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.black)
            textSize = fontSize
            textAlign = Paint.Align.CENTER
        }

        pathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.black)
            isDither = true
            color = circleColor
            style = Paint.Style.STROKE
        }

        sunIcon = bitmapResize(
            BitmapFactory.decodeResource(resources, R.drawable.ic_sun),
            dp2px(context, 18f).toFloat(),
            dp2px(context, 18f).toFloat()
        )
        moonIcon = bitmapResize(
            BitmapFactory.decodeResource(resources, R.drawable.ic_moon),
            dp2px(context, 18f).toFloat(),
            dp2px(context, 18f).toFloat()
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = measuredWidth
        positionX = (viewWidth / 2 - radius - dp2px(context, 9f)).toFloat()
        positionY = radius.toFloat()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(
            changed,
            viewWidth / 2 - radius,
            marginTop,
            viewWidth / 2 + radius,
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
            (viewWidth / 2 - radius).toFloat(),
            marginTop.toFloat(),
            (viewWidth / 2 + radius).toFloat(),
            (radius * 2 + marginTop).toFloat()
        )
        canvas.apply {
            drawArc(rectF, 180f, 180f, true, pathPaint)
            save()
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
                (viewWidth / 2 - radius + dp2px(context, 8f)).toFloat(),
                (radius + dp2px(context, 16f) + marginTop).toFloat(),
                textPaint
            )
            drawText(
                startTime,
                (viewWidth / 2 - radius + dp2px(context, 8f)).toFloat(),
                (radius + dp2px(context, 32f) + marginTop).toFloat(),
                timePaint
            )
            drawText(
                set,
                (viewWidth / 2 + radius - dp2px(context, 8f)).toFloat(),
                (radius + dp2px(context, 16f) + marginTop).toFloat(),
                textPaint
            )
            drawText(
                endTime,
                (viewWidth / 2 + radius - dp2px(context, 8f)).toFloat(),
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

        totalMinute = calculateTime(startHour, startMin, endHour, endMin, false)
        needMinute =
            if (isSun && (currentHour > endHour || (currentHour == endHour && currentMin >= endMin))) {
                calculateTime(startHour, startMin, endHour, endMin, true)
            } else {
                calculateTime(startHour, startMin, currentHour, currentMin, true)
            }
        percentage = formatTime(totalMinute, needMinute).toFloat()
        currentAngle = 180 * percentage
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
        if (isNeedAnimator) {
            isNeedAnimator = false
            ValueAnimator.ofFloat(0f, currentAngle).run {
                this.duration = 2000L
                addUpdateListener { animation ->
                    animatorAngle = animation.animatedValue as Float
                    invalidateView()
                }
                start()
            }
        }
    }

    /**
     * 动态修正太阳、月亮位置
     */
    private fun invalidateView() {
        positionX = (viewWidth / 2 - (radius * cos((animatorAngle) * Math.PI / 180)) - dp2px(
            context,
            10f
        )).toFloat()
        positionY = (radius - (radius * sin((animatorAngle) * Math.PI / 180)) + dp2px(
            context,
            18f
        )).toFloat()
        invalidate()
    }
}