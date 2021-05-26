package com.xdao7.xdweather.utils

import android.content.Context
import android.net.*


class NetworkUtils {

    companion object {

        private val callbackList: MutableMap<Any, IConnectivityCallback> = HashMap()
        private val networkCallback = object : ConnectivityManager.NetworkCallback() {
            /*
             * 网络可用的回调
             */
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                for (callback in callbackList.values) {
                    callback.onAvailable(network)
                }
            }

            /*
             * 在网络失去连接的时候回调，但是如果是一个生硬的断开，他可能不回调
             */
            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
            }

            /*
             * 网络丢失的回调
             */
            override fun onLost(network: Network) {
                super.onLost(network)
                for (callback in callbackList.values) {
                    callback.onLost(network)
                }
            }

            /*
             * 按照官方注释的解释，是指如果在超时时间内都没有找到可用的网络时进行回调
             */
            override fun onUnavailable() {
                super.onUnavailable()
            }

            /*
             * 按照官方的字面意思是，当我们的网络的某个能力发生了变化回调，那么也就是说可能会回调多次
             * 之后在仔细的研究
             */
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
            }

            /*
             * 当建立网络连接时，回调连接的属性
             */
            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties)
            }

            /*
             * 当访问指定的网络被阻止或解除阻塞时调用
             */
            override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                super.onBlockedStatusChanged(network, blocked)
            }
        }

        /**
         * 在application中调用
         */
        fun register(context: Context) {
            val builder = NetworkRequest.Builder()
            val request = builder.build()
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }

        fun addNetworkCallback(tag: Any, call: IConnectivityCallback) {
            callbackList[tag] = call
        }

        fun removeNetworkCallback(tag: Any) {
            callbackList[tag]
        }
    }

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
}