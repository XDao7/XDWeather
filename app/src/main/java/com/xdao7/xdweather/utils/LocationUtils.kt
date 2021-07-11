package com.xdao7.xdweather.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import com.xdao7.xdweather.XDWeatherApplication
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object LocationUtils {

    private val locationManager =
        XDWeatherApplication.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    /**
     * 获取定位信息
     */
    suspend fun getLocation(timeOut: Long = 2000L, block: (location: Location?) -> Unit) {
        try {
            var hasResult = false
            var bestLocation = getLastKnownLocation()

            val gpsListener = createLocationListener { location ->
                if (isBetterLocation(location, bestLocation)) {
                    hasResult = true
                    block(location)
                }
            }
            val networkListener = createLocationListener { location ->
                if (isBetterLocation(location, bestLocation) && !hasResult) {
                    bestLocation = location
                }
            }

            requestLocationUpdates(gpsListener, LocationManager.GPS_PROVIDER)
            requestLocationUpdates(networkListener, LocationManager.NETWORK_PROVIDER)

            delay(timeOut)
            if (!hasResult) {
                block(bestLocation)
            }
            locationManager.removeUpdates(gpsListener)
            locationManager.removeUpdates(networkListener)
        } catch (t: SecurityException) {
            block(null)
        }
    }

    /**
     * 获取最优定位信息
     */
    private fun isBetterLocation(location: Location?, currentBestLocation: Location?): Boolean {
        if (location == null) {
            return false
        }
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true
        }

        val twoMinutes = 1000 * 60 * 2

        // Check whether the new location fix is newer or older
        val timeDelta = location.time - currentBestLocation.time
        val isSignificantlyNewer: Boolean = timeDelta > twoMinutes
        val isSignificantlyOlder: Boolean = timeDelta < -twoMinutes
        val isNewer = timeDelta > 0

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true
            // If the new location is more than two minutes older, it must be
            // worse
        } else if (isSignificantlyOlder) {
            return false
        }

        // Check whether the new location fix is more or less accurate
        val accuracyDelta = (location.accuracy - currentBestLocation.accuracy).toInt()
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        // Check if the old and new location are from the same provider
        val isFromSameProvider = location.provider == currentBestLocation.provider

        // Not significantly newer or older, so check for Accuracy
        if (isMoreAccurate) {
            // If more accurate return true
            return true
        } else if (isNewer && !isLessAccurate) {
            // Same accuracy but newer, return true
            return true
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            // Accuracy is less (not much though) but is new, so if from same
            // provider return true
            return true
        }
        return false
    }

    /**
     * 获取上一次已知的位置信息
     */
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        var location: Location? = null
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (isBetterLocation(networkLocation, location)) {
                location = networkLocation
            }
        }
        return location
    }

    /**
     * 创建定位信息监听
     */
    private fun createLocationListener(block: (location: Location) -> Unit) =
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                block(location)
                locationManager.removeUpdates(this)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

    /**
     * 注册定位信息监听
     */
    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates(listener: LocationListener, provider: String) {
        if (locationManager.isProviderEnabled(provider)) {
            locationManager.requestLocationUpdates(
                provider,
                60000,
                100f,
                listener,
                Looper.getMainLooper()
            )
        }
    }
}