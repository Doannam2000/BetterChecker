package com.dd.company.batterychecker

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.pow

object AdUtils {
    var countInterAddNote = 1
    var countBackInter = 1
    fun initialize(context: Context) {
        MobileAds.initialize(
            context
        ) { initializationStatus ->
            Log.d(
                "dddd",
                "onInitializationComplete: $initializationStatus"
            )
        }
        val testDevices: MutableList<String> = ArrayList()
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR)
        testDevices.add("")
        val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDevices)
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)
    }

    fun Activity.getAdSizeFollowScreen(): AdSize {
        val display = this.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = outMetrics.density
        var adWidthPixels = resources.displayMetrics.widthPixels.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
    }

//    fun showAdsInter(activity: Activity, action: () -> Unit) {
//        if (countInterAddNote >= 3) {
//            countInterAddNote = 1
//            showInterstitialAd(activity) {
//                action.invoke()
//            }
//        } else {
//            countInterAddNote++
//            action.invoke()
//        }
//    }
//
//    fun showAdsInterBackEvent(activity: Activity, action: () -> Unit) {
//        if (countBackInter >= 4) {
//            countBackInter = 1
//            showInterstitialAd(activity) {
//                action.invoke()
//            }
//        } else {
//            countBackInter++
//            action.invoke()
//        }
//    }

    fun showBanner(activity: Activity, viewGroup: ViewGroup) {
        val adRequest = AdRequest.Builder()
            .build()
        if (viewGroup.childCount > 0) return
        val adView = AdView(activity)
        adView.apply {
            adUnitId = BuildConfig.ADMOD_BANNER
//            setAdSize(AdSize.BANNER)
            setAdSize(activity.getAdSizeFollowScreen())
            loadAd(adRequest)
            adListener = object : AdListener(){
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d("dddd", "banner - onAdLoaded: ")
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    Log.d("dddd", "banner - onAdFailedToLoad: ")
                }
            }
        }
        viewGroup.addView(adView)
    }

    private var mInterstitialAd: InterstitialAd? = null
    private var retryAttempt: Int = 0

    fun initAndShowBannerAd(adView: AdView?) {
        adView?.post {
            adView.isEnabled = true
            adView.visibility = View.VISIBLE
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }
    }

//    fun initInterAd(context: Context) {
//        val adRequest = AdRequest.Builder().build()
//        InterstitialAd.load(
//            context,
//            BuildConfig.ADMOD_INTER,
//            adRequest,
//            object : InterstitialAdLoadCallback() {
//                override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                    super.onAdLoaded(interstitialAd)
//                    mInterstitialAd = interstitialAd
//                    retryAttempt = 0
//                }
//
//                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                    super.onAdFailedToLoad(loadAdError)
//                }
//            })
//    }
//
//    private fun showInterstitialAd(activity: Activity, onDone: () -> Unit) {
//        mInterstitialAd?.let {
//            it.show(activity)
//            it.fullScreenContentCallback = object : FullScreenContentCallback() {
//                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
//                    onDone.invoke()
//                    super.onAdFailedToShowFullScreenContent(adError)
//                    initInterAd(activity)
//                }
//
//                override fun onAdShowedFullScreenContent() {
//                    super.onAdShowedFullScreenContent()
//                }
//
//                override fun onAdDismissedFullScreenContent() {
//                    onDone.invoke()
//                    super.onAdDismissedFullScreenContent()
//                    retryAttempt++
//                    val delayMillis = TimeUnit.SECONDS.toMillis(2.0.pow(6.coerceAtMost(retryAttempt).toDouble()).toLong())
//                    CoroutineScope(Dispatchers.Main).launch {
//                        delay(delayMillis)
//                        initInterAd(activity)
//                    }
//                }
//            }
//        } ?: kotlin.run {
//            initInterAd(activity)
//        }
//    }
}