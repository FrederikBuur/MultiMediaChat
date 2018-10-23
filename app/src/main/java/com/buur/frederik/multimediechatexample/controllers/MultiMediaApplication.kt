package com.buur.frederik.multimediechatexample.controllers

import android.app.Application
import com.buur.frederik.multimediechatexample.BuildConfig
import com.squareup.leakcanary.LeakCanary
import io.realm.Realm
import io.realm.RealmConfiguration
import io.socket.client.IO
import io.socket.client.Socket
import java.lang.RuntimeException
import java.net.URISyntaxException

class MultiMediaApplication: Application() {

    var socket: Socket? = null

    override fun onCreate() {
        super.onCreate()

        setupLeakCanary()
        setupSocketIOConnection()
        setupRealm()

    }

    private fun setupLeakCanary() {
        if (BuildConfig.DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return
            }
            LeakCanary.install(this)
        }
    }

    private fun setupSocketIOConnection() {
        try {
            socket = IO.socket(ServiceGenerator.MM_BASE_URL)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }

    private fun setupRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(config)
    }

}