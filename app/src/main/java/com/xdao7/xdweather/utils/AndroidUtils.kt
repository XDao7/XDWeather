package com.xdao7.xdweather.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.xdao7.xdweather.R
import com.xdao7.xdweather.XDWeatherApplication
import java.util.*

fun String.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(XDWeatherApplication.context, this, duration).show()
}

fun Int.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(XDWeatherApplication.context, this, duration).show()
}

fun View.showSnackbar(
    text: String,
    actionText: String? = null,
    duration: Int = Snackbar.LENGTH_LONG,
    block: (() -> Unit)? = null
) {
    val snackbar = Snackbar.make(this, text, duration)
    snackbar.setActionTextColor(R.color.colorCity5.getColor())
    if (actionText != null && block != null) {
        snackbar.setAction(actionText) {
            block()
        }
    }
    snackbar.show()
}

fun View.showSnackbar(
    resId: Int,
    actionResId: Int? = null,
    duration: Int = Snackbar.LENGTH_LONG,
    block: (() -> Unit)? = null
) {
    val snackbar = Snackbar.make(this, resId, duration)
    snackbar.setActionTextColor(R.color.colorCity5.getColor())
    if (actionResId != null && block != null) {
        snackbar.setAction(actionResId) {
            block()
        }
    }
    snackbar.show()
}

fun Int.getColor(): Int = ContextCompat.getColor(XDWeatherApplication.context, this)

inline fun <reified T> startActivity(context: Context, block: Intent.() -> Unit) {
    val intent = Intent(context, T::class.java)
    intent.block()
    context.startActivity(intent)
}

inline fun <reified T> startActivityForResult(
    activity: Activity,
    requestCode: Int,
    block: Intent.() -> Unit
) {
    val intent = Intent(activity, T::class.java)
    intent.block()
    activity.startActivityForResult(intent, requestCode)
}

fun setFullScreenMode(window: Window) {
    window.apply {
        decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = Color.TRANSPARENT
    }
}

fun isNetworkAvailable(): Boolean {
    val connectivityManager =
        XDWeatherApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
}

fun dp2px(context: Context, dpValue: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

fun bitmapResize(src: Bitmap, px: Float, py: Float): Bitmap = Matrix().run {
    postScale(px / src.width, py / src.height)
    Bitmap.createBitmap(src, 0, 0, src.width, src.height, this, true)
}

fun getCurrentTime(): String {
    val calender = Calendar.getInstance()
    return "${calender.get(Calendar.HOUR_OF_DAY)}:${calender.get(Calendar.MINUTE)}"
}

fun getWeek() = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
    1 -> R.string.str_day_of_week_1
    2 -> R.string.str_day_of_week_2
    3 -> R.string.str_day_of_week_3
    4 -> R.string.str_day_of_week_4
    5 -> R.string.str_day_of_week_5
    6 -> R.string.str_day_of_week_6
    7 -> R.string.str_day_of_week_7
    else -> R.string.str_day_of_week_1
}

fun getStatusBarHeight(): Int {
    var statusBarHeight = 0
    val resourceId = XDWeatherApplication.context.resources.getIdentifier(
        "status_bar_height",
        "dimen",
        "android"
    )
    if (resourceId > 0) {
        statusBarHeight = XDWeatherApplication.context.resources.getDimensionPixelSize(resourceId)
    }
    return statusBarHeight
}