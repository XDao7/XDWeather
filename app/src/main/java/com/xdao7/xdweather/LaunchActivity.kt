package com.xdao7.xdweather

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import com.xdao7.xdweather.databinding.ActivityLaunchBinding
import com.xdao7.xdweather.utils.showToast

class LaunchActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 0
    }

    private lateinit var binding: ActivityLaunchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        binding.groupText.visibility = View.VISIBLE

        checkPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    startWeather(true)
                } else {
                    R.string.str_location_permissions_denied.showToast()
                    startWeather(false)
                }
                return
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startWeather(true)
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                R.string.str_location_permissions_denied.showToast()
                startWeather(false)
            }

            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun startWeather(locationPermission: Boolean) {
        Handler(Looper.getMainLooper()).postDelayed({
            WeatherActivity.actionStart(this@LaunchActivity, locationPermission)
            finish()
        }, 1500)
    }
}