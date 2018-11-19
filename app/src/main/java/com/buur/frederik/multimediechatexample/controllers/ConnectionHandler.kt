package com.buur.frederik.multimediechatexample.controllers

import android.content.*
import android.net.ConnectivityManager

class ConnectionHandler : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = cm?.activeNetworkInfo
        val isConnected = activeNetwork?.isConnected == true
        connectivityReceiverListener?.onNetworkChanged(isConnected)

    }

    interface ConnectionReceiverListener {
        fun onNetworkChanged(isConnected: Boolean)
    }

    companion object {

        var connectivityReceiverListener: ConnectionReceiverListener? = null
        var connectivityReceiver: ConnectionHandler? = null

        fun isConnected(context: Context?): Boolean {
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val activeNetwork = cm?.activeNetworkInfo
            return activeNetwork?.isConnected == true
        }

        fun setListener(context: Context, delegate: ConnectionHandler.ConnectionReceiverListener) {
            val intentFilter = IntentFilter()
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)

            connectivityReceiver = ConnectionHandler()
            context.registerReceiver(connectivityReceiver, intentFilter)

            /*register connection status listener*/
            MultiMediaApplication.instance?.setConnectionListener(delegate)
        }

    }
}