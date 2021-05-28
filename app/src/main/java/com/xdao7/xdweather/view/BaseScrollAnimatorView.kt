package com.xdao7.xdweather.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View

abstract class BaseScrollAnimatorView : View{

    var isNeedAnimator = true
    lateinit var animator: ValueAnimator

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDetachedFromWindow() {
        animator.apply {
            if (isRunning) {
                cancel()
            }
        }
        super.onDetachedFromWindow()
    }

    fun baseAnimator(block: () -> Unit) {
        if (isNeedAnimator) {
            isNeedAnimator = false
            animator.apply {
                if (isRunning) {
                    cancel()
                }
            }
            block()
        }
    }

    abstract fun startAnimator()
}