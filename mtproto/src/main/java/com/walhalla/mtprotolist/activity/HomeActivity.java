package com.walhalla.mtprotolist.activity;

import android.content.Context;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;

import android.os.Bundle;

import com.franmontiel.localechanger.LocaleChanger;
import com.franmontiel.localechanger.utils.ActivityRecreationHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.LocaleChangerAppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import com.walhalla.compat.ComV19;
import com.walhalla.library.AdListener;
import com.walhalla.mtprotolist.Config;
import com.walhalla.mtprotolist.R;
import com.walhalla.mtprotolist.databinding.ActivityMainBinding;
import com.walhalla.mtprotolist.entity.MtprotoProxy;
import com.walhalla.mtprotolist.fragment.CompatFragment;

import com.walhalla.mtprotolist.fragment.f1;

import com.walhalla.mtprotolist.utils.TelegramUtils;
import com.walhalla.mtprotolist.webproxy.ProxyInfo;

import com.walhalla.ui.DLog;
import com.walhalla.ui.observer.RateAppModule;
import com.walhalla.ui.plugins.Launcher;
import com.walhalla.ui.plugins.Module_U;


import android.view.Menu;
import android.widget.ProgressBar;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

//import nl.walhalla.domain.interactors.AdvertInteractor;
//import nl.walhalla.domain.interactors.impl.AdvertInteractorImpl;
//import nl.walhalla.domain.repository.from_internet.AdvertAdmobRepository;
//import nl.walhalla.domain.repository.from_internet.AdvertConfig;


public class HomeActivity extends AppCompatActivity
        implements CompatFragment.Callback {


    private static final boolean IS_DRAWER_ENABLED = false;
    private static final int REQUEST_INVITE = 147;
    private ActivityMainBinding binding;
    private boolean doubleBackToExitPressedOnce = false;
    private ComV19 comv19;


    //private RateAppModule mRateAppModule;


    //private ViewGroup b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        comv19 = new ComV19();
        this.binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());
        this.setSupportActionBar(binding.toolbar);
        features();
        //toolbar.setVisibility(View.GONE);

        //b1 = findViewById(R.id.ad_view);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setDisplayUseLogoEnabled(false);
//            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeButtonEnabled(true);
            binding.toolbar.setSubtitle(DLog.getAppVersion(this));
        }

//        toolbar.post(() -> Module_U.checkUpdate(this));
        binding.toolbar.setNavigationIcon(R.drawable.ic_logo);
        binding.toolbar.setNavigationOnClickListener(v -> {
            if (Config.BuildConfigDEBUG) {
                makeInstaller();
            } else {
                Module_U.aboutDialog(this);
            }
        });

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(view ->
//                .setAction("Action", null).show());

        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        if (IS_DRAWER_ENABLED) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            binding.drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            binding.navView.setNavigationItemSelectedListener(item -> {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

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
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(binding.activityMainContainer.getId(), f1.newInstance("", ""))
                    .commit();

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

    private void features() {
//        mRateAppModule = new RateAppModule(this);
//        getLifecycle().addObserver(mRateAppModule);


        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."

        if (Config.BuildConfigDEBUG) {
            binding.adView.setAdListener(new AdListener(binding.adView));
        }
        // Start loading the ad in the background.
        binding.adView.loadAd(new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("28964E2506C9A8C6400A9E8FF42D3486")
                .build());
    }

//    @Override
//    public void onBackPressed() {
//        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            binding.drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(count > 0);
        }
        if (count > 0) {
            getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {//count == 0


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
                    moveTaskToBack(true);
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                //Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
                Toasty.custom(this, R.string.press_again_to_exit,
                        comv19.getDrawable(this, R.drawable.ic_info),
                        R.color.colorPrimaryDark,
                        android.R.color.white, Toasty.LENGTH_SHORT, true, true).show();
                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 1200);
            } else {
                // Если текущий фрагмент не является первым, вернитесь на первый фрагмент
                //@@@ viewPager2.setCurrentItem(0, true); // Установите первый фрагмент в ViewPager2
            }
        }
    }

    private boolean isFirstPage() {
        return true;
    }



    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
//        menu.add("Crash")
//                .setOnMenuItemClickListener(v -> {
//                    throw new RuntimeException("Test Crash");
//                });
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            return false;
        } else if (item.getItemId() == android.R.id.home) {
            Intent intent = HomeActivity.newIntent(this);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_about) {
            Module_U.aboutDialog(this);
            return true;
        } else if (item.getItemId() == R.id.action_privacy_policy) {
            Launcher.openBrowser(this, Config.URL_PRIVACY_POLICY);
            return true;
        } else if (item.getItemId() == R.id.action_rate_app) {
            Launcher.rateUs(this);
            return true;
        } else if (item.getItemId() == R.id.action_share_app) {
            Module_U.shareThisApp(this, "");
            return true;
        } else if (item.getItemId() == R.id.action_discover_more_app) {
            Module_U.moreApp(this);
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), ActivitySetting.class);
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.action_feedback) {
            Module_U.feedback(this);
            return true;
        } else if (item.getItemId() == R.id.action_mirror) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.action_mirror);
            String[] sites = {
                    "https://mtprotolist.blogspot.com",
                    "https://protoproxy.blogspot.com",
                    "https://proxy-telegram.blogspot.com",
                    "https://mtproton.blogspot.com"
            };
            builder.setItems(sites, (dialog, which) -> {
                String site = sites[which];
                Launcher.openBrowser(HomeActivity.this, site);
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
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

    @Override
    public void showMessage(String message) {
        //Snackbar.make(findViewById(R.id.toolbar), message, Snackbar.LENGTH_LONG);
        if (binding.coordinator != null) {
            Snackbar snackbar = Snackbar
                    .make(binding.coordinator//findViewById(R.id.ad_view)
                            , message, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.parseColor("#ff000000"))
                    .setAction(android.R.string.ok, null);
            snackbar.show();
        }
    }


    @Override
    public void onAttach(String tag, boolean b) {

    }

    @Override
    public void checkAnswer(int cursor, String string) {

    }

    @Override
    public void showLoader() {
        //@@binding.p.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        //@@progressBar.setVisibility(View.GONE);
    }

    @Override
    public void makeInstaller() {
        TelegramUtils.makeInstaller(this);
    }

    @Override
    public void handleProxy(MtprotoProxy data) {
        TelegramUtils.handleProxy(this, data);
    }

    @Override
    public void handleProxy0(ProxyInfo data) {
    }

    @Override
    public void rewardExplode() {

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
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        if (mRateAppModule != null) {
//            mRateAppModule.appReloadedHandler();
//        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        if (binding.adView != null) {
            binding.adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityRecreationHelper.onResume(this);
        if (binding.adView != null) {
            binding.adView.resume();
        }
    }

    //Attach new locale for activity
    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleChanger.configureBaseContext(newBase);
        super.attachBaseContext(newBase);
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (binding.adView != null) {
            binding.adView.destroy();
        }
        ActivityRecreationHelper.onDestroy(this);
        super.onDestroy();
    }

    //lang
    private LocaleChangerAppCompatDelegate localeChangerAppCompatDelegate;

    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        if (localeChangerAppCompatDelegate == null) {
            localeChangerAppCompatDelegate = new LocaleChangerAppCompatDelegate(super.getDelegate());
        }
        return localeChangerAppCompatDelegate;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }
}
