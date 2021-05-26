package com.xdao7.xdweather.view

import android.content.Context
import android.util.AttributeSet
import android.view.View

open class BaseScrollAnimtorView : View{

    var isNeedAnimator = true

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    open fun startAnimator() {

    }
}