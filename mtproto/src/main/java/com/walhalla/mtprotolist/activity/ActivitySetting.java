package com.walhalla.mtprotolist.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.franmontiel.localechanger.LocaleChanger;
import com.franmontiel.localechanger.utils.ActivityRecreationHelper;

import com.walhalla.mtprotolist.Config;
import com.walhalla.mtprotolist.LocalePersistor;
import com.walhalla.mtprotolist.R;

import com.walhalla.ui.DLog;
import com.walhalla.ui.plugins.Launcher;
import com.walhalla.ui.plugins.Module_U;

import java.util.Calendar;
import java.util.Locale;


public class ActivitySetting extends AppCompatActivity {

    private AppCompatDelegate mDelegate;

    private View parent_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar(findViewById(R.id.toolbar));
        initToolbar();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }


    /**
     * Binds a preference's summary to its value. More specifically, when the preference's value is changed.
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), "")
        );
    }

    /**
     * A preference value change listener that updates the preference's summary to reflect its new value.
     */
    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0 ? listPreference.getEntries()[index] : null
            );

        } else {
            // For all other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    private void initToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.action_settings);
        }

        // for system bar in lollipop
//        ShareManager.systemBarLolipop(this);
//        ShareManager.setActionBarColor(this, actionBar);
//        actionBar.setSubtitle(Locale.getDefault().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * Support for Activity : DO NOT CODE BELOW ----------------------------------------------------
     */

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
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

//    @Override
//    protected void onDestroy() {
//        getDelegate().onDestroy();
//        super.onDestroy();
//    }

//    public void invalidateOptionsMenu() {
//        getDelegate().invalidateOptionsMenu();
//    }
//
//    public AppCompatDelegate getDelegate() {
//        if (mDelegate == null) {
//            mDelegate = AppCompatDelegate.create(this, null);
//        }
//        return mDelegate;
//    }


    public static class SettingsFragment extends PreferenceFragmentCompat {


        private static final String KEY_GOOGLE_PLAY = "Google Play";

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.setting_notification);
            //parent_view = findViewById(android.R.id.content);

            //sharedPref = new SharedPref(getApplicationContext());

            //bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_ringtone)));
            final Preference key_more = findPreference("key_more");
            if (key_more != null) {
                key_more.setSummary(KEY_GOOGLE_PLAY);
                key_more.setOnPreferenceClickListener(preference -> {
                    Module_U.moreApp(getContext());
                    return false;
                });
            }
            final Preference key_about = findPreference("key_about");
            if (key_about != null) {
                String summary = getString(R.string.app_name) + " " + DLog.getAppVersion(getContext());
                key_about.setSummary(summary);
//                key_about.setOnPreferenceClickListener(preference -> {
//                    Module_U.moreApp(getContext());
//                    return false;
//                });
            }
            final Preference preference3 = findPreference("pref_copyright");
            if (preference3 != null) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                String tmp = getString(R.string.pref_summary_copyright);
                preference3.setSummary(String.format(tmp, year));
//                preference3.setOnPreferenceClickListener(preference -> {
//                    Module_U.moreApp(getContext());
//                    return false;
//                });
            }

            final Preference preference1 = findPreference("pref_title_term");
            if (preference1 != null) {
                preference1.setSummary(Config.URL_PRIVACY_POLICY);
                preference1.setOnPreferenceClickListener(preference -> {
                    Launcher.openBrowser(getContext(), Config.URL_PRIVACY_POLICY);
                    return false;
                });
            }
            final Preference preference2 = findPreference("key_rate");
            if (preference2 != null) {
                preference2.setSummary(KEY_GOOGLE_PLAY);
                preference2.setOnPreferenceClickListener(preference -> {
                    Launcher.rateUs(getContext());
                    return false;
                });
            }
            final ListPreference keyLang = findPreference(LocalePersistor.KEY_LANGUAGE);
            String[] arr = getResources().getStringArray(R.array.lang_values);
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            int size = arr.length;
            for (int i = 0; i < size; i++) {
                sb.append(arr[i]);
                if (i < size - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            if (keyLang != null) {
                keyLang.setSummary(sb.toString());
                //keyLang.setValueIndex(0);
                keyLang.setOnPreferenceChangeListener((preference, newValue) -> {

                    int index = keyLang.findIndexOfValue(newValue.toString());

                    if (index != -1) {
                        //setNewLocale((AppCompatActivity) getActivity(), newValue.toString()); //LocaleChanger.RUSSIAN
                        LocaleChanger.setLocale(new Locale(newValue.toString()));
                        ActivityRecreationHelper.recreate(getActivity(), true);
                    }
                    return true;
                });
            }
        }


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityRecreationHelper.onResume(this);
//        currentLocale.setText(Locale.getDefault().toString());
//        date.setText(DateProvider.provideSystemLocaleFormattedDate());

    }


    @Override
    protected void onDestroy() {
        ActivityRecreationHelper.onDestroy(this);
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleChanger.configureBaseContext(newBase);
        super.attachBaseContext(newBase);
    }


//    private static void setNewLocale(AppCompatActivity context, @LocaleChanger.LocaleDef String language) {
//        LocaleChanger.setLocale(context, language);
//        ActivityRecreationHelper.recreate(context, true);
//
////        Intent intent = context.getIntent();
////        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
//
//
//        //Toast.makeText(getBaseContext(), lang.getEntries()[index], Toast.LENGTH_LONG).show();
////        try {
//////                            LocaleChanger.onConfigurationChanged(new Locale(newValue.toString()));
//////                            ActivityRecreationHelper.recreate(getActivity(), false);
////            //Reload activity
////            context.finish();
////            context.overridePendingTransition(0, 0);
////            context.startActivity(context.getIntent());
////            context.overridePendingTransition(0, 0);
////
////            //@@@ShareManager.restartApplication(getActivity());
////
////        } catch (Exception e) {
////            handleException(context, e);
////        }
//    }
}
