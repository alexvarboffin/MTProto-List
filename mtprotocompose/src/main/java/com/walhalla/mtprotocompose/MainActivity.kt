package com.walhalla.mtprotocompose

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.LocaleChangerAppCompatDelegate
import com.franmontiel.localechanger.LocaleChanger
import com.walhalla.mtprotocompose.ui.MtprotoApp
import com.walhalla.mtprotocompose.update.InAppUpdateController
import com.walhalla.mtprotocompose.update.InAppUpdateRestartDialog

class MainActivity : AppCompatActivity() {
    private var localeChangerDelegate: LocaleChangerAppCompatDelegate? = null
    private lateinit var inAppUpdate: InAppUpdateController

    override fun onCreate(savedInstanceState: Bundle?) {
        applyNightMode()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Play In-App Update; the check starts only on a fresh launch, not after rotation.
        inAppUpdate = InAppUpdateController(this, checkForUpdate = savedInstanceState == null)
        setContent {
            MtprotoApp(overlay = {
                if (inAppUpdate.restartPromptVisible) {
                    InAppUpdateRestartDialog(
                        onRestart = inAppUpdate::completeUpdate,
                        onDismiss = inAppUpdate::dismissRestartPrompt,
                    )
                }
            })
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleChanger.configureBaseContext(newBase))
    }

    override fun getDelegate(): AppCompatDelegate {
        if (localeChangerDelegate == null) {
            localeChangerDelegate = LocaleChangerAppCompatDelegate(super.getDelegate())
        }
        return localeChangerDelegate!!
    }

    fun applyNightMode() {
        val nightMode = mtprotoApp().settingsRepository.isNightMode()
        AppCompatDelegate.setDefaultNightMode(
            if (nightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO,
        )
    }
}
