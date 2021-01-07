package com.example.flutter_app



import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.example.flutter_app.NotificationListener.Companion.NOTIFICATION_PACKAGE_EXTRA
import com.example.flutter_app.NotificationListener.Companion.NOTIFICATION_PACKAGE_MESSAGE
import com.example.flutter_app.NotificationListener.Companion.NOTIFICATION_PACKAGE_NAME
import com.example.flutter_app.NotificationListener.Companion.NOTIFICATION_PACKAGE_TEXT
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugins.GeneratedPluginRegistrant


class MainActivity(): FlutterActivity() {
    private val eventChannelName = "events"
    private var eventSink: EventSink? = null
    val context = this
    var gre = false
    val map = HashMap<String, Any>()
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)


    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        if (permissionGiven() == true) {
            context.startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
            val receiver = NotificationReceiver()
            val intentFilter = IntentFilter()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                intentFilter.addAction(NotificationListener.NOTIFICATION_INTENT)
            }
            context.registerReceiver(receiver, intentFilter)



        }

        EventChannel(flutterEngine?.dartExecutor?.binaryMessenger, eventChannelName).setStreamHandler(
                object: EventChannel.StreamHandler{
                    override fun onListen(arguments: Any?, events: EventSink?) {
                        eventSink = events
                        val listenerIntent = Intent(context, NotificationListener::class.java)
                        context.startService(listenerIntent)

                    }

                    override fun onCancel(arguments: Any?) {

                    }

                }
        )




    }

    internal inner class NotificationReceiver : BroadcastReceiver() {
        val TAG = "NOTIFICATION_RECEIVER"
        override fun onReceive(context: Context, intent: Intent) {
            val packageName =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        intent.getStringExtra(NOTIFICATION_PACKAGE_NAME)
                    } else {
                        TODO("VERSION.SDK_INT < JELLY_BEAN_MR2")
                    }

            val packageMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                intent.getStringExtra(NOTIFICATION_PACKAGE_MESSAGE)
            } else {
                TODO("VERSION.SDK_INT < JELLY_BEAN_MR2")
            }
            val packageText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                intent.getStringExtra(NOTIFICATION_PACKAGE_TEXT)
            } else {
                TODO("VERSION.SDK_INT < JELLY_BEAN_MR2")
            }
            val packageExtra = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                intent.getStringExtra(NOTIFICATION_PACKAGE_EXTRA)
            } else {
                TODO("VERSION.SDK_INT < JELLY_BEAN_MR2")
            }

            map["packageName"] = packageName.toString()
            map["packageMessage"] = packageMessage.toString()
            map["packageText"] = packageText.toString()
            map["packageExtra"] =packageExtra.toString()
            Log.i("hello", packageText.toString())
            this@MainActivity.eventSink?.success(map)
        }

    }

    private fun permissionGiven(): Boolean {

        if(gre == false) {
            gre = true
            return true
        }
            else
            return false
    }





    companion object {
        const val TAG = "NOTIFICATION_PLUGIN"
        private const val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"
        private const val ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"

    }








}