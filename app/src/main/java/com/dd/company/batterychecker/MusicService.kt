package com.dd.company.batterychecker

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.MediaPlayer
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class MusicService : Service() {

    val NOTIFICATION_CHANNEL_ID = "com.example.batterychecker"
    val FOREGROUND_ID = 9999
    val CHANNEL_NAME = "My Background Service"
    val FLAG = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    private val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val x = intent!!.getStringExtra(KEY_ACTION_SERVICE)
        mediaPlayer = MediaPlayer.create(this, R.raw.music)
        if (x == ACTION_START_SERVICE) {
            registerReceiver(broadcastReceiver, intentFilter)
        } else if (x == ACTION_START_MUSIC) {
            mediaPlayer!!.start()
            onFullBattery()
        } else if (x == ACTION_STOP_MUSIC) {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            unregisterReceiver(broadcastReceiver)
        }
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startMyOwnForeground() else startForeground(
            1,
            Notification()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.reset()
    }


    private fun onFullBattery() {
        val intentOpenApp = Intent(this, MainActivity::class.java)
        intentOpenApp.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val intentStopSv = Intent(this, BatteryReceiver::class.java)
        intentStopSv.action = ACTION_STOP_SERVER
        val pendingIntentStopSv =
            PendingIntent.getBroadcast(this, 0, intentStopSv, FLAG)
        val pendingIntentOpenApp =
            PendingIntent.getActivity(this, 0, intentOpenApp, FLAG)
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            NOTIFICATION_CHANNEL_ID
        )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.battery)
            .setContentTitle(getString(R.string.full_battery_notice))
            .setPriority(NotificationManager.IMPORTANCE_MAX)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(pendingIntentOpenApp)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.battery, getString(R.string.cancel_notify), pendingIntentStopSv)
            .build()
        startForeground(2, notification)
    }

    private fun startMyOwnForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_NONE
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
            val percent = SharedPreUtil.getInstance(this).getValue(KEY_PERCENT, 0)
            //Intent mo app
            val intentOpenApp = Intent(this, MainActivity::class.java)
            intentOpenApp.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntentOpenApp = PendingIntent.getActivity(
                this,
                0,
                intentOpenApp,
                FLAG
            )
            //Intent stop service
            val intentStopSv = Intent(this, BatteryReceiver::class.java)
            intentStopSv.action = ACTION_STOP_SERVER
            val pendingIntentStopSv = PendingIntent.getBroadcast(
                this,
                0,
                intentStopSv,
                FLAG
            )
            //create notif
            val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
                this,
                NOTIFICATION_CHANNEL_ID
            )
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.battery)
                .setContentTitle(getString(R.string.percent_target, percent.toString()))
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntentOpenApp)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(
                    R.drawable.battery,
                    getString(R.string.cancel_notify),
                    pendingIntentStopSv
                )
                .build()
            startForeground(2, notification)
        }
    }


    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val percentage = level * 100 / scale
            Log.d("Percent", percentage.toString() + "")
            val sharedPreferences = SharedPreUtil.getInstance(context)
            val percent = sharedPreferences.getValue(KEY_PERCENT, 0)
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val check = sharedPreferences.getValue(KEY_TURN_ON_NOTIFY, false)
            if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                val intentStop = Intent(context, MusicService::class.java)
                intentStop.putExtra(KEY_ACTION_SERVICE, ACTION_STOP_MUSIC)
                stopService(intentStop)
            } else if (percentage >= percent && check && status == BatteryManager.BATTERY_STATUS_CHARGING) {
                val intentStart = Intent(context, MusicService::class.java)
                intentStart.putExtra(KEY_ACTION_SERVICE, ACTION_START_MUSIC)
                sharedPreferences.putValue(KEY_TURN_ON_NOTIFY, false)
                startService(intentStart)
            }
        }
    }
}