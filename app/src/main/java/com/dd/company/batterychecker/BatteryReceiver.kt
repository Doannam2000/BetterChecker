package com.dd.company.batterychecker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

class BatteryReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action == ACTION_STOP_SERVER) {
            val intent1 = Intent(context, MusicService::class.java)
            context!!.stopService(intent1)
        }
        if (action != null && action == Intent.ACTION_BATTERY_CHANGED) {
            val statusLabel: TextView = (context as MainActivity).findViewById(R.id.tvStatus)
            val percentageLabel: TextView = context.findViewById(R.id.tvPercent)
            val batteryImage: ImageView = context.findViewById(R.id.imgPercent)
            // Status
            val status = intent!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            var message = ""
            when (status) {
                BatteryManager.BATTERY_STATUS_FULL -> message =
                    context.getString(R.string.full_battery)
                BatteryManager.BATTERY_STATUS_CHARGING -> message =
                    context.getString(R.string.charging)
                BatteryManager.BATTERY_STATUS_DISCHARGING -> message =
                    context.getString(R.string.not_charging)
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> message =
                    context.getString(R.string.not_charging)
                BatteryManager.BATTERY_STATUS_UNKNOWN -> message =
                    context.getString(R.string.unknow)
            }
            statusLabel.text = message
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val percentage = level * 100 / scale
            percentageLabel.text = "$percentage%"
            context.getBatteryCapacity()
            // Image
            val res = context.getResources()
            if (percentage >= 85) {
                batteryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.b100))
            } else if (85 > percentage && percentage >= 65) {
                batteryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.b75))
            } else if (65 > percentage && percentage >= 40) {
                batteryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.b50))
            } else if (40 > percentage && percentage >= 15) {
                batteryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.b25))
            } else {
                batteryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.b0))
            }
        }
    }
}