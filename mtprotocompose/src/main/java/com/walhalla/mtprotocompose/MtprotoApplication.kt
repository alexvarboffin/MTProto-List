package com.walhalla.mtprotocompose



import android.app.Activity

import android.app.Application

import android.content.Context

import android.content.res.Configuration

import android.os.Bundle

import androidx.annotation.NonNull

import androidx.lifecycle.DefaultLifecycleObserver

import androidx.lifecycle.LifecycleOwner

import androidx.lifecycle.ProcessLifecycleOwner

import androidx.multidex.MultiDexApplication

import com.franmontiel.localechanger.LocaleChanger

import com.google.android.gms.ads.AdRequest

import com.google.android.gms.ads.MobileAds

import com.google.android.gms.ads.RequestConfiguration

import com.google.firebase.FirebaseApp

import com.google.firebase.database.FirebaseDatabase

import com.onesignal.OneSignal

import com.walhalla.mtproto.shared.repository.ProxyRepository

import com.walhalla.mtprotocompose.data.DisclaimerStore

import com.walhalla.mtprotocompose.data.LockedItemsStore

import com.walhalla.mtprotocompose.data.SettingsRepository

import com.walhalla.ui.DLog

import com.walhalla.utils.AdmobAdsIds

import com.walhalla.utils.RewardManager

import com.walhalla.wads.AppOpenAdManager

import com.walhalla.wads.OnShowAdCompleteListener

import java.util.Locale



class MtprotoApplication : MultiDexApplication(), DefaultLifecycleObserver, Application.ActivityLifecycleCallbacks {

    lateinit var repository: ProxyRepository

        private set

    lateinit var lockedItemsStore: LockedItemsStore

        private set

    lateinit var disclaimerStore: DisclaimerStore

        private set

    lateinit var settingsRepository: SettingsRepository

        private set



    private var appOpenAdManager: AppOpenAdManager? = null

    private var currentActivity: Activity? = null



    override fun onCreate() {

        registerActivityLifecycleCallbacks(this)



        val locales = resources.getStringArray(R.array.lang_values)

            .map { Locale(it) }

        LocaleChanger.initialize(this, locales)



        super<MultiDexApplication>.onCreate()

        FirebaseApp.initializeApp(this)

        try {

            FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        } catch (e: Exception) {

            DLog.handleException(e)

        }



        if (BuildConfig.DEBUG) {

            MobileAds.setRequestConfiguration(

                RequestConfiguration.Builder()

                    .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))

                    .build(),

            )

        }

        MobileAds.initialize(this) {}



        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        appOpenAdManager = AppOpenAdManager(getString(R.string.run1))



        repository = ProxyRepository.create()

        lockedItemsStore = LockedItemsStore(this)

        disclaimerStore = DisclaimerStore(this)

        settingsRepository = SettingsRepository(this)



        RewardManager.instance?.init(

            AdmobAdsIds(

                null,

                null,

                getString(R.string.rewardedId),

            ),

        )

        RewardManager.instance?.loadRewardAd(this)



        OneSignal.initWithContext(this, ONE_SIGNAL_APP_ID)

    }



    fun showAdIfAvailable(activity: Activity, onComplete: () -> Unit) {

        appOpenAdManager?.showAdIfAvailable(

            activity,

            object : OnShowAdCompleteListener {

                override fun onShowAdComplete() {

                    onComplete()

                }



                override fun adAdDismissedBackPressed() {

                    onComplete()

                }

            },

        ) ?: onComplete()

    }



    override fun onStart(@NonNull owner: LifecycleOwner) {

        val activity = currentActivity

        if (activity != null && appOpenAdManager?.isShowingAd != true) {

            appOpenAdManager?.showAdIfAvailable(activity)

        }

    }



    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit



    override fun onActivityStarted(@NonNull activity: Activity) {

        if (activity !is com.google.android.gms.ads.AdActivity) {

            currentActivity = activity

        }

    }



    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit



    override fun onConfigurationChanged(newConfig: Configuration) {

        super<MultiDexApplication>.onConfigurationChanged(newConfig)

        LocaleChanger.onConfigurationChanged()

    }



    companion object {

        private const val ONE_SIGNAL_APP_ID = "f23d47a4-921b-4439-9c3f-3abfecea8419"

    }

}



fun Context.mtprotoApp(): MtprotoApplication = applicationContext as MtprotoApplication


