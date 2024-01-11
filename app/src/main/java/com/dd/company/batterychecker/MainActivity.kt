package com.dd.company.batterychecker

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.dd.company.batterychecker.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val mBatteryReceiver = BatteryReceiver()
    private val mIntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    var percent = 80

    override fun initView() {
        getBatteryCapacity()
    }

    override fun initData() {
        AdUtils.initialize(this)
        AdUtils.showBanner(this, binding.adView)
    }

    override fun initListener() {
        binding.btnSetting.setOnClickListener(View.OnClickListener {
            val dialog = Dialog(this@MainActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_setup)
            dialog.setCanceledOnTouchOutside(false)
            val window = dialog.window
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val tvPin = dialog.findViewById<TextView>(R.id.tvPercent)
            val sbPin = dialog.findViewById<SeekBar>(R.id.sbPercent)
            val btnOK = dialog.findViewById<Button>(R.id.btnOk)
            val btnHuy = dialog.findViewById<Button>(R.id.btnCancel)
            tvPin.text = sbPin.progress.toString() + "%"
            sbPin.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    percent = progress
                    tvPin.text = "$progress%"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
            btnOK.setOnClickListener {
                SharedPreUtil.getInstance(this).apply {
                    putValue(KEY_PERCENT, percent)
                    putValue(KEY_TURN_ON_NOTIFY, true)
                }

                binding.tvNotice.setText(getString(R.string.percent_target, percent.toString()))
                val intent = Intent(this@MainActivity, MusicService::class.java)
                intent.putExtra(KEY_ACTION_SERVICE, ACTION_START_SERVICE)
                ContextCompat.startForegroundService(this@MainActivity, intent)
                dialog.dismiss()
            }
            btnHuy.setOnClickListener { dialog.cancel() }
            dialog.show()
        })
        binding.btnCancelMusic.setOnClickListener { //            mBatteryReceiver.getRequest(0,false);
            val i = Intent(this@MainActivity, MusicService::class.java)
            Toast.makeText(
                this@MainActivity,
                getString(R.string.cancel_notify_success),
                Toast.LENGTH_SHORT
            ).show()
            stopService(i)
            getBatteryCapacity()
        }
    }

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }


    fun getBatteryCapacity() {
        val mPowerProfile: Any
        var batteryCapacity = 0.0
        val POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile"
        // Khởi tạo BatteryManager
        val batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager

        // Lấy thông tin về mức pin hiện tại
        val batteryCapacityCurrent =
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                .getConstructor(Context::class.java)
                .newInstance(this)
            batteryCapacity = Class.forName(POWER_PROFILE_CLASS)
                .getMethod("getBatteryCapacity")
                .invoke(mPowerProfile) as Double
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (batteryCapacity == 0.0) {
            binding.tvNotice.text = getString(R.string.get_battery_infor_failed)
        } else {
            binding.tvNotice.text =
                getString(R.string.current_battery_status) + " " + batteryCapacityCurrent / 1000 + "/" + batteryCapacity.toInt() + "mAh"
        }
    }


    override fun onResume() {
        super.onResume()
        registerReceiver(mBatteryReceiver, mIntentFilter)
    }

    override fun onPause() {
        super.onPause()
        registerReceiver(mBatteryReceiver, mIntentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        registerReceiver(mBatteryReceiver, mIntentFilter)
    }

    override fun onStop() {
        super.onStop()
        registerReceiver(mBatteryReceiver, mIntentFilter)
    }

}