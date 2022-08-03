package com.example.aidl

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.os.Process
import android.os.Process.myPid
import android.os.RemoteCallbackList
import android.util.Log
import android.widget.Toast
import kotlin.concurrent.thread
import kotlin.math.log
import kotlin.random.Random

class PayService : Service() {

    var count = 0

    override fun onCreate() {
        super.onCreate()
        thread {
            while(true) {
                Thread.sleep(1000)
                onPriceChange(Random.nextFloat().toString())
            }
        }
    }

    private fun onPriceChange(price: String) {
        Log.d(this::class.simpleName, "onPriceChange: Service $price")
        val size = remoteCallback.beginBroadcast()
        repeat(size) {
            val item = remoteCallback.getBroadcastItem(it)
            item?.onPriceChange(price)
        }
        remoteCallback.finishBroadcast()
    }

    val remoteCallback = RemoteCallbackList<PriceCallback>()
    private val binder = object :IMyAidlInterface.Stub() {


        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?
        ) {

        }

        override fun pay(callback: Callback) {
            Looper.prepare()
            Log.d(this::class.simpleName, "pay: Success")
            //Toast.makeText(applicationContext, "Pay Success", Toast.LENGTH_LONG).show()
            callback.call(CallbackEntity("Haha", Random.nextInt(1, 100)))
            Looper.loop()
        }

        override fun getPid(): Int {
            return Process.myPid()
        }

        override fun registerCallback(callback: PriceCallback?) {
            Log.d(this@PayService::class.simpleName, "registerCallback: ${callback}")
            remoteCallback.register(callback)
        }

        override fun unregisterCallback(callback: PriceCallback?) {
            Log.d(this@PayService::class.simpleName, "unregisterCallback: ${callback}")
            remoteCallback.unregister(callback)
        }

    }

    override fun onBind(intent: Intent): IBinder? {
        val result = checkCallingOrSelfPermission("com.example.aidl.permission.PAY_PERMISSION")
        if (result == PackageManager.PERMISSION_DENIED) {
            Log.e(this::class.simpleName, "onBind: 客户端没有权限", )
            return null
        }
        return binder
    }
}