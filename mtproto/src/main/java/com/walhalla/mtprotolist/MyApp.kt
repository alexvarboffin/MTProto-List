package com.walhalla.mtprotolist

//import saschpe.android.customtabs.CustomTabsActivityLifecycleCallbacks

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.franmontiel.localechanger.LocaleChanger
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.walhalla.ui.BuildConfig
import com.walhalla.ui.DLog
import com.walhalla.utils.AdmobAdsIds
import com.walhalla.utils.RewardManager
import com.walhalla.wads.AppOpenAdManager
import com.walhalla.wads.OnShowAdCompleteListener
import java.util.Locale


/**
 * The Application class that manages AppOpenManager.
 */
class MyApp : MultiDexApplication(), DefaultLifecycleObserver, ActivityLifecycleCallbacks {
    var location: Location? = null
    private var appOpenAdManager: AppOpenAdManager? = null
    private var currentActivity: Activity? = null

    companion object {
        private const val ACTIVITY_MOVES_TO_FOREGROUND_HANDLE = true
    }


    val OAI: String = "f23d47a4-921b-4439-9c3f-3abfecea8419"

    override fun onCreate() {
        registerActivityLifecycleCallbacks(this)//We can attach other activities


        val arr = resources.getStringArray(R.array.lang_values)
        //        List<Locale> SUPPORTED_LOCALES = Arrays.asList(
////                    new Locale("en", "US"),
////                    new Locale("es", "ES"),
////                    new Locale("fr", "FR"),
////                    new Locale("ar", "JO")
//                new Locale("en"), new Locale("ru"), new Locale("uk"),
////                    new Locale("az")
//                new Locale("uz"));
        val SUPPORTED_LOCALES: MutableList<Locale> = ArrayList()
        val size = arr.size
        for (i in 0 until size) {
            SUPPORTED_LOCALES.add(Locale(arr[i]))
        }
        LocaleChanger.initialize( /*getApplicationContext()*/this, SUPPORTED_LOCALES)
        super<MultiDexApplication>.onCreate()
        FirebaseApp.initializeApp(this)


//        MobileAds.initialize(this, initializationStatus -> {
//            //getString(R.string.app_id)
//        });

        if (Config.BuildConfigDEBUG) {
            val testDevices: MutableList<String> = ArrayList()
            testDevices.add(AdRequest.DEVICE_ID_EMULATOR)

            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)
        }

        //MobileAds.initialize(this, getString(R.string.app_id));
        MobileAds.initialize(this) { initializationStatus: InitializationStatus ->
            val map = initializationStatus.adapterStatusMap
            for ((key, value) in map) {
                DLog.d("--> $key $value")
            }
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

//        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
//            /**
//             * LifecycleObserver method that shows the app open ad when the app moves to foreground.
//             */
//            //    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//            //    protected void onMoveToForeground() {
//            //        // Show the ad (if available) when the app moves to foreground.
//            //        appOpenAdManager.showAdIfAvailable(currentActivity);
//            //    }
//            override fun onStart(@NonNull owner: LifecycleOwner) {
//
//
//                if (ACTIVITY_MOVES_TO_FOREGROUND_HANDLE) { // Show the ad (if available) when the app moves to foreground.
//                    if (noneNull(currentActivity) && !appOpenAdManager!!.isShowingAd) {
//                        appOpenAdManager!!.showAdIfAvailable(currentActivity)
//                    }
//                    DLog.d("{}{}{}{}$currentActivity")
//                }
//            }
//        })

        appOpenAdManager = AppOpenAdManager(getString(R.string.run1))
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        } catch (e: Exception) {
            DLog.handleException(e)
        }
        //printHashKey(this);


        // Preload custom tabs service for improved performance
        // This is optional but recommended
//        registerActivityLifecycleCallbacks(
//            CustomTabsActivityLifecycleCallbacks()
//        )

        val m0 = AdmobAdsIds(
            null,//getString(R.string.admob_native_id),
            null,  //getString(R.string.admob_inter_id),
            getString(R.string.rewardedId)
        )

        val w = RewardManager.instance
        w?.init(m0)

        if (!TextUtils.isEmpty(OAI)) {
            // OneSignal Initialization
//            OneSignal.startInit(this)
//                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                    //.setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
//                    .unsubscribeWhenNotificationsAreDisabled(true)
//                    .autoPromptLocation(true)
//                    .init();

            // OneSignal Initialization

            OneSignal.initWithContext(this, OAI)
            // Enable verbose OneSignal logging to debug issues if needed.
            //OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE)

            //OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.DEBUG);
//            DLog.d("OneSignal Initialization");
//            OneSignal.startInit(this)
//                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                    //.setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
//                    //--.unsubscribeWhenNotificationsAreDisabled(true)
//                    //.autoPromptLocation(true)
//                    .init();

//            OneSignal.unsubscribeWhenNotificationsAreDisabled(false);
//            OSDeviceState device = OneSignal.getDeviceState();
//            if (device != null) {
//                String email = device.getEmailAddress();
//                String emailId = device.getEmailUserId();
//                String pushToken = device.getPushToken();
//                String userId = device.getUserId();
//
//                boolean enabled = device.areNotificationsEnabled();
//                boolean subscribed = device.isSubscribed();
//                boolean pushDisabled = device.isPushDisabled();
//
//                DLog.d("[" + enabled + "] " + subscribed + " " + pushDisabled);
//                DLog.d(String.valueOf(device.toJSONObject()));

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getApplicationContext(),
//                            new String[]{POST_NOTIFICATIONS}, 1);
//                }
//            }
//            val id: String = OneSignal.getUser().getOnesignalId()
//            DLog.d("@@@@@@@@@@$id")
        }
    }

    //    public static void printHashKey(Context pContext) {
    //        //if (BuildConfig.DEBUG) {
    //            try {
    //                PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
    //                for (Signature signature : info.signatures) {
    //                    MessageDigest md = MessageDigest.getInstance("SHA");
    //                    md.update(signature.toByteArray());
    //                    String hashKey = new String(Base64.encode(md.digest(), 0));
    //                    Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
    //                }
    //            } catch (NoSuchAlgorithmException e) {
    //                Log.e(TAG, "printHashKey()", e);
    //            } catch (Exception e) {
    //                Log.e(TAG, "printHashKey()", e);
    //            }
    //        }
    //}


    private fun noneNull(currentActivity: Activity?): Boolean {
        DLog.d("@@@@" + (currentActivity == null));
        return currentActivity != null
    }

    /**
     * ActivityLifecycleCallback methods.
     */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}


    override fun onCreate(owner: LifecycleOwner) {

    }

    override fun onStart(@NonNull owner: LifecycleOwner) {


        if (ACTIVITY_MOVES_TO_FOREGROUND_HANDLE) { // Show the ad (if available) when the app moves to foreground.
            if (noneNull(currentActivity) && !appOpenAdManager!!.isShowingAd) {
                appOpenAdManager!!.showAdIfAvailable(currentActivity)
            }
            DLog.d("{}{}{}{}$currentActivity")
        }
    }

//    override fun onActivityStarted(activity: Activity) {
//        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
//        // SDK or another activity class implemented by a third party mediation partner. Updating the
//        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
//        // one that shows the ad.
//        if (!appOpenAdManager!!.isShowingAd) {
//            currentActivity = activity
//        }
//    }

    override fun onActivityStarted(@NonNull activity: Activity) {
        if (activity is com.google.android.gms.ads.AdActivity) {
        } else {
            //###activity.SplRunActivity
            //###activity.MainActivity
            //###activity.StickerInfoActivity
            //###com.google.android.gms.ads.AdActivity

            // An ad activity is started when an ad is showing, which could be AdActivity class from Google
            // SDK or another activity class implemented by a third party mediation partner. Updating the
            // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
            // one that shows the ad.
            currentActivity = activity
        }


        if (BuildConfig.DEBUG) {
            Toast.makeText(activity, "###" + activity.getLocalClassName(), Toast.LENGTH_SHORT)
                .show()
            DLog.d("[*]" + activity.getLocalClassName() + "," + currentActivity)
        }

    }

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

    /**
     * Shows an app open ad.
     *
     * @param activity                 the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    fun showAdIfAvailable(
        activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener
    ) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager!!.showAdIfAvailable(activity, onShowAdCompleteListener)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleChanger.onConfigurationChanged()
        //Log.d(TAG, "onConfigurationChanged: ");
    }


}
