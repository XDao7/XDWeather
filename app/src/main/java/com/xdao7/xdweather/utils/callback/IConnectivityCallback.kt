package com.xdao7.xdweather.utils.callback

import android.net.Network




interface IConnectivityCallback {
    /**
     * 网络可用回调
     *
     * @param network Network
     */
    fun onAvailable(network: Network?)

    /**
     * 网络丢失回调
     *
     * @param network Network
     */
    fun onLost(network: Network?)
}