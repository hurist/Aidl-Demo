package com.example.aidlcustomer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.os.Process
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.aidl.Callback
import com.example.aidl.CallbackEntity
import com.example.aidl.IMyAidlInterface
import com.example.aidl.PriceCallback
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    var aidlInterface: IMyAidlInterface? = null

    private val deathRecipient: IBinder.DeathRecipient = object :IBinder.DeathRecipient {
        override fun binderDied() {
            aidlInterface?.let {
                aidlInterface?.asBinder()?.unlinkToDeath(this, 0)
                aidlInterface = null
                this@MainActivity.unbindService(this@MainActivity.connection)
            }
        }

    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            aidlInterface = IMyAidlInterface.Stub.asInterface(service)
            service?.linkToDeath(deathRecipient, 0)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            unbindService(this)
        }

    }

    val priceCallback = object : PriceCallback.Stub() {
        override fun onPriceChange(price: String?) {
            Log.d(this::class.simpleName, "onPriceChange: $price")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        bindService(connection)


        findViewById<Button>(R.id.button).setOnClickListener {
            thread {
                if (aidlInterface == null || aidlInterface?.asBinder()?.isBinderAlive == false) {
                    Log.d(this::class.simpleName, "onCreate: binder is null or dead")
                    bindService(connection)
                    return@thread
                }
                Log.d(this::class.simpleName, "onCreate: MyPid: ${Process.myPid()} ServiceId: ${aidlInterface?.pid}")
                aidlInterface?.pay(object :Callback.Stub() {
                    override fun call(entity: CallbackEntity?) {
                        Looper.prepare()
                        Log.d(this@MainActivity::class.simpleName, "call: ${entity?.code} ${entity?.message}")
                        Toast.makeText(this@MainActivity, "${entity?.code} ${entity?.message}", Toast.LENGTH_LONG).show()
                        Looper.loop()
                    }
                })
                aidlInterface?.registerCallback(priceCallback)
            }

        }

    }



    private fun bindService(connection: ServiceConnection) {
        val intent = Intent("com.example.aidl.pay_service")
        intent.`package` = "com.example.aidl"
        bindService(intent, connection, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        aidlInterface?.unregisterCallback(priceCallback)
        unbindService(connection)
    }
}