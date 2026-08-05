package com.walhalla.mtprotolist.activity

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.ads.AdRequest
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.walhalla.compat.ComV19
import com.walhalla.library.AdListener
import com.walhalla.mtprotolist.Config
import com.walhalla.mtprotolist.R
import com.walhalla.mtprotolist.activity.web.WVActivity
import com.walhalla.mtprotolist.databinding.ActivityPagerBinding
import com.walhalla.mtprotolist.dialog.DisclaimerDialog
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.fragment.CompatFragment
import com.walhalla.mtprotolist.utils.CustomTabUtils
import com.walhalla.mtprotolist.utils.TelegramUtils
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import com.walhalla.ui.DLog
import com.walhalla.ui.SharedPref
import com.walhalla.ui.plugins.DialogAbout.aboutDialog
import com.walhalla.ui.plugins.Launcher
import com.walhalla.ui.plugins.Module_U
import es.dmoral.toasty.Toasty
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Angle.Companion.RIGHT
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartyFactory
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Shape.Square
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.xml.KonfettiView
import nl.dionsegijn.konfetti.xml.listeners.OnParticleSystemUpdateListener
import java.util.Arrays
import java.util.concurrent.TimeUnit

class VPager : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    CompatFragment.Callback {


//    private val defaultCustomTabsIntentBuilder: CustomTabsIntent.Builder
//        get() {
//
//
//            val builder = CustomTabsIntent.Builder()
//                .addDefaultShareMenuItem()
//                .setToolbarColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
//                .setShowTitle(true)
//                //.setCloseButtonIcon(backArrow)
////            getBitmapFromVectorDrawable(R.drawable.ic_arrow_back_white_24dp)?.let {
////                builder.setCloseButtonIcon(it)
////            }
//            return builder
//        }


    private lateinit var party: Party
    private lateinit var drawableShape: Shape.DrawableShape

    lateinit var comv19: ComV19
    lateinit var binding: ActivityPagerBinding

    val DAYS_FOR_FLEXIBLE_UPDATE: Int = 4

    lateinit var flexibleListener: InstallStateUpdatedListener
    lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>


    //Updater
    private lateinit var appUpdateManager: AppUpdateManager
    private var updateType = AppUpdateType.IMMEDIATE


    private var prf: PrefManager? = null
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private var customTabsIntent: CustomTabsIntent? = null
    private var var1: SharedPref? = null
    private var info1: ProxyInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        comv19 = ComV19()
        binding = ActivityPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val drawable = ContextCompat.getDrawable(
            applicationContext, R.drawable.ic_heart
        )
        if (drawable != null) {
            drawableShape = Shape.DrawableShape(drawable, true, true)
        }

        val emitterConfig = Emitter(5L, TimeUnit.SECONDS).perSecond(50)
        party = PartyFactory(emitterConfig)
            .angle(270)
            .spread(90)
            .setSpeedBetween(1f, 5f)
            .timeToLive(2000L)
            .shapes(Shape.Rectangle(0.2f), drawableShape)
            .sizes(Size(12, 5f, 0.2f))
            .position(0.0, 0.0, 1.0, 0.0)
            .build()

        features()

        prf = PrefManager(this)
        binding.navView.setNavigationItemSelectedListener(this)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.isDrawerIndicatorEnabled = true
        actionBarDrawerToggle!!.syncState()
        customTabsIntent = CustomTabUtils.customWeb(this)

        //binding.toolbar.setNavigationIcon(R.drawable.ic_action_action);
        val actionBar = this.supportActionBar
        if (actionBar != null) {
//            actionBar.setDisplayUseLogoEnabled(false);
//            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true)
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeButtonEnabled(true);
            binding.toolbar.setSubtitle(DLog.getAppVersion(this))
        }

//        toolbar.post(() -> Module_U.checkUpdate(this));

        //m = new MProtoFragmentPresenter(getContext(), this);


        setToolbarLogo("logo")



        binding.toolbar.setNavigationOnClickListener { v: View? ->
            if (Config.BuildConfigDEBUG) {
                makeInstaller()
            } else {
                aboutDialog(this)
            }
        }

        //main Fragment
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("MTProto"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("WebProxy"))


        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = TabAdapter(this, supportFragmentManager, binding.tabLayout.tabCount)
        binding.viewPager.setAdapter(adapter)
        binding.viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(binding.tabLayout))
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.setCurrentItem(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        var1 = SharedPref.getInstance(this)


        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
                // handle callback
                if (result.resultCode == RESULT_OK) {
                    DLog.d("Result code: " + result.resultCode);
                    // If the update is canceled or fails,
                    // you can request to start the update again.
                } else if (result.resultCode == RESULT_CANCELED) {
                    DLog.d("Update flow failed! Result code: Canceled!->" + result.resultCode)
                } else {
                    //ActivityResult.RESULT_IN_APP_UPDATE_FAILED:
                }
            }

        //Check Update
//        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
//        // Returns an intent object that you use to check for an update.
//        //val appUpdateInfoTask = appUpdateManager.appUpdateInfo
//        // Create a listener to track request state updates.
//        flexibleListener = InstallStateUpdatedListener { installState ->
//            // (Optional) Provide a download progress bar.
//            if (installState.installStatus() == InstallStatus.DOWNLOADING) {
//                val bytesDownloaded = installState.bytesDownloaded()
//                val totalBytesToDownload = installState.totalBytesToDownload()
//                DLog.d("// Show update progress bar. " + bytesDownloaded + "/" + totalBytesToDownload)
//            } else if (installState.installStatus() == InstallStatus.DOWNLOADED) {
//
//                // After the update is downloaded, show a notification
//                // and request user confirmation to restart the app.
//                popupSnackbarForCompleteUpdateFlexible()
//            }
//
//            // Log installState or install the update.
//        }
//        checkForAppUpdates()
    }

    private fun setToolbarLogo(s: String) {
        try {
            val name = String.format("ic_%1s", s)
            val res = resources.getIdentifier(
                name, "drawable",
                getPackageName()
            )
            binding.toolbar.setNavigationIcon(res)
        } catch (ignored: java.lang.Exception) {
            binding.toolbar.setNavigationIcon(R.drawable.ic_logo)
        }
    }

    private fun checkForAppUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->

            var avaliable = appUpdateInfo.updateAvailability()
            var isUpdateAvailable = avaliable == UpdateAvailability.UPDATE_AVAILABLE;

            val isUpdateAllowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> appUpdateInfo.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> appUpdateInfo.isImmediateUpdateAllowed
                else -> false
            }

            if (isUpdateAvailable && isUpdateAllowed) {
                handleInstallStatus(appUpdateInfo)
            }

            // If the update is downloaded but not installed,
            // notify the user to complete the update.
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdateFlexible()
            } else if (appUpdateInfo.installStatus() == InstallStatus.UNKNOWN) {
                //@handleInstallStatus(appUpdateInfo)
            } else {
                DLog.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + appUpdateInfo.installStatus())
            }


//            if (avaliable == UpdateAvailability.UPDATE_AVAILABLE) {
//                handleInstallStatus(appUpdateInfo)
//            }
        }.addOnFailureListener { e ->
            // Handle the error.
            DLog.d("@WWW@" + e.message)
        }
    }

    private fun popupSnackbarForCompleteUpdateFlexible() {

        DLog.d("===============")

        Snackbar.make(
            binding.coordinator, "An update has just been downloaded.", Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") {
                appUpdateManager.unregisterListener(flexibleListener)
                appUpdateManager.completeUpdate()

//                val intent = Intent(Intent.ACTION_VIEW)
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName))
//                intent.setPackage("com.android.vending")
//                startActivity(intent)
            }
            setActionTextColor(resources.getColor(android.R.color.white))
            show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menu.add("Crash")
//                .setOnMenuItemClickListener(v -> {
//                    throw new RuntimeException("Test Crash");
//                });
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_refresh) {
            false
        } else if (item.itemId == android.R.id.home) {
            val intent = HomeActivity.newIntent(this)
            startActivity(intent)
            true
        } else if (item.itemId == R.id.action_about) {
            aboutDialog(this)
            true
        } else if (item.itemId == R.id.action_privacy_policy) {
            Launcher.openBrowser(this, Config.URL_PRIVACY_POLICY)
            true
        } else if (item.itemId == R.id.action_rate_app) {
            Launcher.rateUs(this)
            true
        } else if (item.itemId == R.id.action_share_app) {
            Module_U.shareThisApp(this, "")
            true
        } else if (item.itemId == R.id.action_discover_more_app) {
            Module_U.moreApp(this)
            true
        } else if (item.itemId == R.id.action_settings) {
            val i = Intent(applicationContext, ActivitySetting::class.java)
            startActivity(i)
            true
        } else if (item.itemId == R.id.action_feedback) {
            Module_U.feedback(this)
            true
        } else if (item.itemId == R.id.action_mirror) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.action_mirror)
            val sites = arrayOf(
                "https://mtprotolist.blogspot.com",
                "https://protoproxy.blogspot.com",
                "https://proxy-telegram.blogspot.com",
                "https://mtproton.blogspot.com"
            )
            builder.setItems(sites) { dialog: DialogInterface?, which: Int ->
                val site = sites[which]
                Launcher.openBrowser(this, site)
            }
            val dialog = builder.create()
            dialog.show()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
//            case R.string.start_test_again:
//                return false;


//            case R.id.action_exit:
//                this.finish();
//                return true;


//            case R.id.action_more_app_01:
//                Module_U.moreApp(this, getString(R.string.p_more_app_01));
//                return true;
//
//            case R.id.action_more_app_02:
//                Module_U.moreApp(this, getString(R.string.p_more_app_02));
//                return true;

        //action_how_to_use_app
        //action_support_developer

        //return super.onOptionsItemSelected(item);
    }

    //    private void replaceFragment(Fragment fragment) {
    //        try {
    //            //CURRENT_TAG = fragment.getClass().getSimpleName();
    //
    //            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    //            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
    //            //fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
    //            fragmentTransaction.replace(R.id.container, fragment);
    //            fragmentTransaction.addToBackStack(null);
    //            fragmentTransaction.commit();
    //        } catch (IllegalStateException e) {
    //            DLog.d(e.getMessage());
    //        }
    //    }
    private var doubleBackToExitPressedOnce = false


    //    @Override
    //    public void onBackPressed() {
    //        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
    //            binding.drawerLayout.closeDrawer(GravityCompat.START);
    //        } else {
    //            super.onBackPressed();
    //        }
    //    }

    private fun isFirstPage(): Boolean {
//        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.viewPagerContainer);
//        return (currentFragment instanceof Fragment1);
        val currentPosition: Int = binding.viewPager.currentItem
        return currentPosition == 0
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(count > 0)
        }
        if (count > 0) {
            supportFragmentManager.popBackStack(
                supportFragmentManager.getBackStackEntryAt(0).id,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        } else { //count == 0


//                Dialog
//                new AlertDialog.Builder(this)
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setTitle("Leaving this App?")
//                        .setMessage("Are you sure you want to close this application?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//
//                        })
//                        .setNegativeButton("No", null)
//                        .show();
            //super.onBackPressed();
            if (isFirstPage()) {
                if (doubleBackToExitPressedOnce) {
//                super.onBackPressed();
//                return;

                    // Move the task containing the SubdomainActivity to the back of the activity stack, instead of
                    // destroying it. Therefore, SubdomainActivity will be shown when the user switches back to the app.
                    moveTaskToBack(true)
                    return
                }
                doubleBackToExitPressedOnce = true
                //Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
                Toasty.custom(
                    this, R.string.press_again_to_exit,
                    comv19.getDrawable(this, R.drawable.ic_info),
                    R.color.colorPrimaryDark,
                    android.R.color.white, Toasty.LENGTH_SHORT, true, true
                ).show()
                Handler().postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 1200)
            } else {
                // Если текущий фрагмент не является первым, вернитесь на первый фрагмент
                binding.viewPager.setCurrentItem(
                    0,
                    true
                ); // Установите первый фрагмент в ViewPager2
            }
        }
    }

    private fun backPressedToast() {
        //View view = findViewById(R.id.cLayout);
        //val view = findViewById<View>(android.R.id.content)
        if (binding.coordinator == null) {
            Toast.makeText(this, R.string.press_again_to_exit, Toast.LENGTH_SHORT).show()
        } else {
            Snackbar.make(binding.coordinator, R.string.press_again_to_exit, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        //        if (menuItem.getItemId() == R.id.nav_today) {
//            startActivity(new Intent(this, QuoteOfTheDayActivity.class));
//        } else if (menuItem.getItemId() == R.id.nav_premium) {
//            startActivity(new Intent(this, PrimeActivity.class));
//        } else if (menuItem.getItemId() == R.id.nav_favotite) {
//            startActivity(new Intent(this, FavoriteActivity.class));
//        } else if (menuItem.getItemId() == R.id.nav_rate) {
//            try {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
//            } catch (ActivityNotFoundException ex) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
//            }
//        } else if (menuItem.getItemId() == R.id.nav_share) {
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("text/plain");
//            String shareBodyText = "https://play.google.com/store/apps/details?id=" + getPackageName();
//            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
//            intent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
//            startActivity(Intent.createChooser(intent, "share via"));
//        } else if (menuItem.getItemId() == R.id.nav_contact) {
//            Intent i = new Intent(Intent.ACTION_SEND);
//            i.setType("message/rfc822");
//            i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.app_name)});
//            i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
//            i.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name));
//            try {
//                startActivity(Intent.createChooser(i, "Send mail..."));
//            } catch (ActivityNotFoundException ex) {
//                Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//            }
//        } else if (menuItem.getItemId() == R.id.nav_about) {
//            showAboutDialog();
//        } else if (menuItem.getItemId() == R.id.nav_settings) {
//
//            startActivity(new Intent(this, SettingsActivity.class));
//        } else if (menuItem.getItemId() == R.id.nav_insta) {
//
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("http://www.instagram.com/" + Config.INSTAGRAM));
//            startActivity(browserIntent);
//        } else if (menuItem.getItemId() == R.id.nav_night) {
//            return false;
//        }
        return false
    }

    private fun showAboutDialog() {
//        final Dialog dialog = new Dialog(VPager.this, R.style.DialogCustomTheme);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        dialog.setContentView(R.layout.layout_about);
//
//        Button dialog_btn = dialog.findViewById(R.id.btn_done);
//        dialog_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
    }

    override fun onResume() {
        super.onResume()
//        if (binding.konfettiView.isActive()) {
//            binding.konfettiView.reset()
//        }
        initCheck()


        //Only for immediate
//        if (updateType == AppUpdateType.IMMEDIATE) {
//            appUpdateManager.appUpdateInfo
//                .addOnSuccessListener { info ->
//
//                    // If the update is downloaded but not installed,
//                    // notify the user to complete the update.
////                if (info.installStatus() == InstallStatus.DOWNLOADED) {
////                    popupSnackbarForCompleteUpdate()
////                } else if (info.installStatus() == InstallStatus.UNKNOWN) {
////                    handleInstallStatus(info)
////                }
//                    if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
//                        DLog.d("// WWW If an in-app update is already running, resume the update.")
//                        appUpdateManager.startUpdateFlowForResult(
//                            info,
//                            activityResultLauncher,
//                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
//                        )
//                    }
//                }.addOnFailureListener { e ->
//                    DLog.d("@@@@@" + e)
//                }
//        }

    }

    private fun handleInstallStatus(appUpdateInfo: AppUpdateInfo) {
        var avaliable = appUpdateInfo.updateAvailability()
        if (avaliable == UpdateAvailability.UPDATE_AVAILABLE) {

            //type = AppUpdateType.FLEXIBLE

//            if ((appUpdateInfo.clientVersionStalenessDays() ?: -1) >= DAYS_FOR_FLEXIBLE_UPDATE
//                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
//            ) {
//                // Request the update.
//                type = AppUpdateType.FLEXIBLE
//            }

            if (
            // This example applies an immediate update. To apply a flexible update
            // instead, pass in AppUpdateType.FLEXIBLE
            // appUpdateInfo.isUpdateTypeAllowed()
                appUpdateInfo.isUpdateTypeAllowed(updateType)

            ) {


                DLog.d(
                    "// Request the update. "
                            + appUpdateInfo.clientVersionStalenessDays()
                            + ", " + appUpdateInfo.updatePriority()
                )




                if (updateType == AppUpdateType.FLEXIBLE) {
                    // Before starting an update, register a listener for updates.
                    appUpdateManager.registerListener(flexibleListener)
                }
//                appUpdateManager
//                    .startUpdateFlowForResult(
//                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
//                    appUpdateInfo,
//                    // an activity result launcher registered via registerForActivityResult
//                    activityResultLauncher,
//                    // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
//                    // flexible updates.
//                    AppUpdateOptions.newBuilder(updateType).build()
//                )


                appUpdateManager
                    .startUpdateFlowForResult(
                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,
                        updateType,
                        this,
                        123
                    )
            }

//            if (appUpdateInfo.updatePriority() >= 4 /* high priority */
//                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//                // Request an immediate update.
//            }


        } else {
            DLog.d("@@aa@@" + (avaliable == UpdateAvailability.UPDATE_NOT_AVAILABLE))
        }

    }

    private fun initCheck() {
        if (prf!!.loadNightModeState() == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    override fun showMessage(message: String) {
        val snackbar = Snackbar
            .make(binding.coordinator, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.parseColor("#ff000000"))
            .setAction(android.R.string.ok, null)
        snackbar.show()
    }

    override fun onAttach(tag: String, b: Boolean) {}
    override fun checkAnswer(cursor: Int, string: String) {}
    override fun showLoader() {
        //progressBar.setVisibility(View.VISIBLE);
    }

    override fun hideLoader() {
        //progressBar.setVisibility(View.GONE);
    }

    override fun makeInstaller() {
        TelegramUtils.makeInstaller(this)
    }

    public override fun onStart() {
        super.onStart()
        agreeHandler(this)
    }

    private fun agreeHandler(activity: AppCompatActivity?) {
        if (activity != null) {
            val fm = activity.supportFragmentManager
            fm.setFragmentResultListener(
                DisclaimerDialog.REQUEST_KEY,  //getViewLifecycleOwner() - fragment
                activity
            ) { requestKey: String?, result: Bundle ->
                val var0 = result.containsKey(DisclaimerDialog.KEY_AGREE_VALUE)
                if (var0) {
                    val value = result.getBoolean(DisclaimerDialog.KEY_AGREE_VALUE)
                    if (value) {
                        //Toast.makeText(activity, "@@" + this.proxyUrl, Toast.LENGTH_SHORT).show();
                        var1!!.licenseAgree(true)
                        //startGitHubProjectCustomTab(proxyUrl)
                        updateRecyclerViewItemBackground(info1!!)

                    }
                }
            }
        }
    }

    private fun updateRecyclerViewItemBackground(info: ProxyInfo) {
//        try {
//            String format;
//            format = data.proxyUrl.trim();
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(format));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            this.getApplicationContext().startActivity(intent);//info(activity,format);
//        } catch (android.content.ActivityNotFoundException anfe) {
//            Toast.makeText(this, "Browser not found", Toast.LENGTH_SHORT).show();
//        }

        //customTabsIntentLaunchUrl(this.proxyUrl);

        startActivity(WVActivity.newInstance(this, info.proxyUrl, info.type));

        //startGitHubProjectCustomTab(proxyUrl)
    }

//    private fun startGitHubProjectCustomTab(proxyUrl: String?) {
//        // Apply some fancy animation to show off
//        val customTabsIntent = defaultCustomTabsIntentBuilder
////            .setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left)
////            .setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right)
//            .build()
//        // This is optional but recommended
//        CustomTabsHelper.addKeepAliveExtra(this, customTabsIntent.intent)
//
//        // This is where the magic happens...
//        CustomTabsHelper.openCustomTab(
//            this,
//            customTabsIntent,
//            Uri.parse(proxyUrl),
//            WebViewFallback()
//        )
//    }

    override fun handleProxy0(data: ProxyInfo) {
        info1 = data
        val licenseAgree = var1!!.licenseAgree()
        if (!licenseAgree) {
            val dialog: DialogFragment = DisclaimerDialog()
            dialog.show(supportFragmentManager, DisclaimerDialog::class.java.getSimpleName())
        } else {
            updateRecyclerViewItemBackground(info1!!)
        }
    }

    override fun rewardExplode() {
        //binding.konfettiView.start(party);
        explode()
    }

    fun customTabsIntentLaunchUrl(url: String) {
        try {
            customTabsIntent!!.launchUrl(this, Uri.parse(url.trim { it <= ' ' }))
        } catch (e: Exception) {
            DLog.handleException(e)
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun handleProxy(data: MtprotoProxy) {
        TelegramUtils.handleProxy(this, data)
    }


    //Features
    private fun features() {
//        mRateAppModule = new RateAppModule(this);
//        getLifecycle().addObserver(mRateAppModule);


        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        if (Config.BuildConfigDEBUG) {
            binding.adView.adListener = AdListener(binding.adView)
        }
        // Start loading the ad in the background.
        binding.adView.loadAd(
            AdRequest.Builder() //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("28964E2506C9A8C6400A9E8FF42D3486")
                .build()
        )
    }


    fun explode() {
        val emitterConfig = Emitter(100L, TimeUnit.MILLISECONDS)
            .max(100)
        binding.konfettiView.onParticleSystemUpdateListener = (object :
            OnParticleSystemUpdateListener {
            override fun onParticleSystemStarted(konfettiView: KonfettiView, party: Party, i: Int) {
            }

            override fun onParticleSystemEnded(konfettiView: KonfettiView, party: Party, i: Int) {
            }
        })
        binding.konfettiView.start(
            PartyFactory(emitterConfig)
                .spread(360)
                .shapes(Arrays.asList<Shape>(Square, Shape.Circle, drawableShape))
                .colors(mutableListOf<Int>(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(0f, 30f)
                .position(Position.Relative(0.5, 0.3))
                .build()
        )
    }

    fun parade() {
        val emitterConfig = Emitter(5, TimeUnit.SECONDS).perSecond(30)
        binding.konfettiView.start(
            PartyFactory(emitterConfig)
                .angle(RIGHT - 45)
                .spread(Spread.SMALL)
                .shapes(Arrays.asList<Shape>(Square, Shape.Circle, drawableShape))
                .colors(mutableListOf<Int>(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(10f, 30f)
                .position(Position.Relative(0.0, 0.5))
                .build(),
            PartyFactory(emitterConfig)
                .angle(Angle.LEFT + 45)
                .spread(Spread.SMALL)
                .shapes(Arrays.asList<Shape>(Square, Shape.Circle, drawableShape))
                .colors(mutableListOf<Int>(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(10f, 30f)
                .position(Position.Relative(1.0, 0.5))
                .build()
        )
    }

    fun rain() {
        val emitterConfig = Emitter(5, TimeUnit.SECONDS).perSecond(100)
        binding.konfettiView.start(
            PartyFactory(emitterConfig)
                .angle(Angle.BOTTOM)
                .spread(Spread.ROUND)
                .shapes(Arrays.asList<Shape>(Square, Shape.Circle, drawableShape))
                .colors(mutableListOf<Int>(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(0f, 15f)
                .position(Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0)))
                .build()
        )
    }
}
