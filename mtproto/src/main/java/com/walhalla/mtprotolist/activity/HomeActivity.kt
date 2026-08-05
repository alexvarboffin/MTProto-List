package com.walhalla.mtprotolist.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.LocaleChangerAppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.franmontiel.localechanger.LocaleChanger
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.google.android.gms.ads.AdRequest
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.walhalla.compat.ComV19
import com.walhalla.library.AdListener
import com.walhalla.mtprotolist.Config
import com.walhalla.mtprotolist.R
import com.walhalla.mtprotolist.databinding.ActivityMainBinding
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.fragment.CompatFragment
import com.walhalla.mtprotolist.fragment.f1
import com.walhalla.mtprotolist.utils.TelegramUtils
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import com.walhalla.ui.DLog.getAppVersion
import com.walhalla.ui.plugins.DialogAbout.aboutDialog
import com.walhalla.ui.plugins.Launcher.openBrowser
import com.walhalla.ui.plugins.Launcher.rateUs
import com.walhalla.ui.plugins.Module_U
import com.walhalla.ui.plugins.Module_U.feedback
import com.walhalla.ui.plugins.Module_U.moreApp
import com.walhalla.ui.plugins.Module_U.shareThisApp
import es.dmoral.toasty.Toasty

//import nl.walhalla.domain.interactors.AdvertInteractor;
//import nl.walhalla.domain.interactors.impl.AdvertInteractorImpl;
//import nl.walhalla.domain.repository.from_internet.AdvertAdmobRepository;
//import nl.walhalla.domain.repository.from_internet.AdvertConfig;
class HomeActivity : AppCompatActivity(), CompatFragment.Callback {
    private var binding: ActivityMainBinding? = null
    private var doubleBackToExitPressedOnce = false
    private var comv19: ComV19? = null


    //private RateAppModule mRateAppModule;
    //private ViewGroup b1;
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        comv19 = ComV19()
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding!!.getRoot())
        this.setSupportActionBar(binding!!.toolbar)
        features()

        //toolbar.setVisibility(View.GONE);

        //b1 = findViewById(R.id.ad_view);
        val actionBar = this.supportActionBar
        if (actionBar != null) {
//            actionBar.setDisplayUseLogoEnabled(false);
//            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true)
            //            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeButtonEnabled(true);
            binding!!.toolbar.setSubtitle(getAppVersion(this))
        }

        //        toolbar.post(() -> Module_U.checkUpdate(this));
        binding!!.toolbar.setNavigationIcon(R.drawable.ic_logo)
        binding!!.toolbar.setNavigationOnClickListener(View.OnClickListener { v: View? ->
            if (Config.BuildConfigDEBUG) {
                makeInstaller()
            } else {
                aboutDialog(this)
            }
        })

        //        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(view ->
//                .setAction("Action", null).show());
        binding!!.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        if (IS_DRAWER_ENABLED) {
            val toggle = ActionBarDrawerToggle(
                this,
                binding!!.drawerLayout,
                binding!!.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            binding!!.drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
            binding!!.navView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item: MenuItem? ->
                // Handle navigation view item clicks here.
                val id = item!!.itemId

                //                if (id == R.id.nav_home) {
//                    // Handle the camera action
//                } else if (id == R.id.nav_gallery) {
//
//                } else if (id == R.id.nav_slideshow) {
//
//                } else if (id == R.id.nav_tools) {
//
//                } else if (id == R.id.nav_share) {
//
//                } else if (id == R.id.nav_send) {
//
//                }
                //drawer = findViewById(R.id.drawer_layout);
                binding!!.drawerLayout.closeDrawer(GravityCompat.START)
                true
            })
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding!!.activityMainContainer.getId(), f1.newInstance("", ""))
                .commit()

            //            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.activity_main_container, new MtprotoProxyFragment())
//                    .commit();
        }

        //        AdView mAdView = new AdView(this);
//        mAdView.setAdSize(AdSize.BANNER);
//        mAdView.setAdUnitId(getString(R.string.b1));
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

//        AdvertAdmobRepository repository = AdvertAdmobRepository.getInstance(new AdvertConfig() {
//            @Override
//            public String application_id() {
//                return null;
//            }
//
//            @Override
//            public SparseArray<String> banner_ad_unit_id() {
//                SparseArray<String> arr = new SparseArray<>();
//                arr.put(R.id.b1, getString(R.string.b1));
//                return arr;
//            }
//
//            @Override
//            public String interstitial_ad_unit_id() {
//                return null;
//            }
//        });
//
//        this.getLifecycle().addObserver(repository);
//
//        AdvertInteractorImpl interactor = new AdvertInteractorImpl(
//                BackgroundExecutor.getInstance(),
//                MainThreadImpl.getInstance(), repository);
//        interactor.selectView(b1, new AdvertInteractor.Callback<View>() {
//            @Override
//            public void onMessageRetrieved(int id, View message) {
//                try {
//                    //viewGroup.removeView(message);
//                    if (message.getParent() != null) {
//                        ((ViewGroup) message.getParent()).removeView(message);
//                    }
//                    b1.addView(message);
//                } catch (Exception ignore) {
//                }
//            }
//
//            @Override
//            public void onRetrievalFailed(String error) {
//                M.d( "onRetrievalFailed: ");
//            }
//        });

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        if (Config.BuildConfigDEBUG) {
//            SharedPreferences prefs = getSharedPreferences(KEY_TKT_LOADER, MODE_PRIVATE);
//            prefs.edit().putBoolean(Config.KEY_CLIPBOARD_MONITOR, true).apply();
//            Toast.makeText(this, "@@@", Toast.LENGTH_SHORT).show();
//            ClipboardMonitor.startClipboardMonitor(this);
//        }
    }

    private fun features() {
//        mRateAppModule = new RateAppModule(this);
//        getLifecycle().addObserver(mRateAppModule);


        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."


        if (Config.BuildConfigDEBUG) {
            binding!!.adView.adListener = AdListener(binding!!.adView)
        }
        // Start loading the ad in the background.
        binding!!.adView.loadAd(
            AdRequest.Builder() //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("28964E2506C9A8C6400A9E8FF42D3486")
                .build()
        )
    }

    //    @Override
    //    public void onBackPressed() {
    //        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
    //            binding.drawerLayout.closeDrawer(GravityCompat.START);
    //        } else {
    //            super.onBackPressed();
    //        }
    //    }
    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (getSupportActionBar() != null) {
            getSupportActionBar()!!.setHomeButtonEnabled(count > 0)
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


            if (this.isFirstPage) {
                if (doubleBackToExitPressedOnce) {
//                super.onBackPressed();
//                return;

                    // Move the task containing the SubdomainActivity to the back of the activity stack, instead of
                    // destroying it. Therefore, SubdomainActivity will be shown when the user switches back to the app.

                    moveTaskToBack(true)
                    return
                }

                this.doubleBackToExitPressedOnce = true
                //Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
                Toasty.custom(
                    this, R.string.press_again_to_exit,
                    comv19!!.getDrawable(this, R.drawable.ic_info),
                    R.color.colorPrimaryDark,
                    android.R.color.white, Toasty.LENGTH_SHORT, true, true
                ).show()
                Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 1200)
            } else {
                // Если текущий фрагмент не является первым, вернитесь на первый фрагмент
                //@@@ viewPager2.setCurrentItem(0, true); // Установите первый фрагмент в ViewPager2
            }
        }
    }

    private val isFirstPage: Boolean
        get() = true


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menu.add("Crash")
//                .setOnMenuItemClickListener(v -> {
//                    throw new RuntimeException("Test Crash");
//                });
        getMenuInflater().inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_refresh) {
            return false
        } else if (item.itemId == android.R.id.home) {
            val intent: Intent = newIntent(this)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.action_about) {
            aboutDialog(this)
            return true
        } else if (item.itemId == R.id.action_privacy_policy) {
            openBrowser(this, Config.URL_PRIVACY_POLICY)
            return true
        } else if (item.itemId == R.id.action_rate_app) {
            rateUs(this)
            return true
        } else if (item.itemId == R.id.action_share_app) {
            shareThisApp(this, "")
            return true
        } else if (item.itemId == R.id.action_discover_more_app) {
            moreApp(this)
            return true
        } else if (item.itemId == R.id.action_settings) {
            val i = Intent(getApplicationContext(), ActivitySetting::class.java)
            startActivity(i)
            return true
        } else if (item.itemId == R.id.action_feedback) {
            feedback(this)
            return true
        } else if (item.itemId == R.id.action_mirror) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.action_mirror)
            val sites = arrayOf<String>(
                "https://mtprotolist.blogspot.com",
                "https://protoproxy.blogspot.com",
                "https://proxy-telegram.blogspot.com",
                "https://mtproton.blogspot.com"
            )
            builder.setItems(
                sites,
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    val site = sites[which]
                    openBrowser(this@HomeActivity, site)
                })
            val dialog = builder.create()
            dialog.show()
            return true
        } else {
            return super.onOptionsItemSelected(item)
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

    override fun showMessage(message: String) {
        //Snackbar.make(findViewById(R.id.toolbar), message, Snackbar.LENGTH_LONG);
        if (binding!!.coordinator != null) {
            val snackbar = Snackbar
                .make(
                    binding!!.coordinator,  //findViewById(R.id.ad_view)
                    message, Snackbar.LENGTH_LONG
                )
                .setBackgroundTint(Color.parseColor("#ff000000"))
                .setAction(android.R.string.ok, null)
            snackbar.show()
        }
    }


    override fun onAttach(tag: String?, b: Boolean) {
    }

    override fun checkAnswer(cursor: Int, string: String?) {
    }

    override fun showLoader() {
        //@@binding.p.setVisibility(View.VISIBLE);
    }

    override fun hideLoader() {
        //@@progressBar.setVisibility(View.GONE);
    }

    override fun makeInstaller() {
        TelegramUtils.makeInstaller(this)
    }

    override fun handleProxy(data: MtprotoProxy) {
        TelegramUtils.handleProxy(this, data)
    }

    override fun handleProxy0(data: ProxyInfo?) {
    }

    override fun rewardExplode() {
    }


    //    private void onInviteClicked() {
    //        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.app_name))
    //                .setMessage("Hey my friend check out this app" +
    //                        (char) 10 + RateMeDialog.GOOGLE_PLAY_CONSTANT + getPackageName() +
    //                        (char) 10)
    //                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
    //                .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
    //                .setCallToActionText(getString(R.string.invitation_cta))
    //                .build();
    //        startActivityForResult(intent, REQUEST_INVITE);
    //    }
    //
    //    @Override
    //    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //        super.onActivityResult(requestCode, resultCode, data);
    //        DLog.d( "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
    //
    //        if (requestCode == REQUEST_INVITE) {
    //            if (resultCode == RESULT_OK) {
    //                // Get the invitation IDs of all sent messages
    //                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
    //                for (String id : ids) {
    //                    DLog.d( "onActivityResult: sent invitation " + id);
    //                }
    //            } else {
    //                // Sending failed or it was canceled, show failure message to the user
    //                // ...
    //            }
    //        }
    //    }
    //features
    override fun onSaveInstanceState(outState: Bundle) {
//        if (mRateAppModule != null) {
//            mRateAppModule.appReloadedHandler();
//        }
        super.onSaveInstanceState(outState)
    }

    public override fun onPause() {
        if (binding!!.adView != null) {
            binding!!.adView.pause()
        }
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        ActivityRecreationHelper.onResume(this)
        if (binding!!.adView != null) {
            binding!!.adView.resume()
        }
    }

    //Attach new locale for activity
    override fun attachBaseContext(newBase: Context?) {
        var newBase = newBase
        newBase = LocaleChanger.configureBaseContext(newBase)
        super.attachBaseContext(newBase)
    }

    /**
     * Called before the activity is destroyed
     */
    public override fun onDestroy() {
        if (binding!!.adView != null) {
            binding!!.adView.destroy()
        }
        ActivityRecreationHelper.onDestroy(this)
        super.onDestroy()
    }

    //lang
    private var localeChangerAppCompatDelegate: LocaleChangerAppCompatDelegate? = null

    override fun getDelegate(): AppCompatDelegate {
        if (localeChangerAppCompatDelegate == null) {
            localeChangerAppCompatDelegate = LocaleChangerAppCompatDelegate(super.getDelegate())
        }
        return localeChangerAppCompatDelegate!!
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        getDelegate().onPostCreate(savedInstanceState)
    }

    override fun onPostResume() {
        super.onPostResume()
        getDelegate().onPostResume()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        getDelegate().onConfigurationChanged(newConfig)
    }

    override fun onStop() {
        super.onStop()
        getDelegate().onStop()
    }

    companion object {
        private const val IS_DRAWER_ENABLED = false
        private const val REQUEST_INVITE = 147
        fun newIntent(context: Context?): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }
    }
}
