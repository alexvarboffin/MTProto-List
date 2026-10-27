package com.walhalla.mtprotocompose.update

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.slapps.cupertino.CupertinoAlertDialog
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.cancel
import com.slapps.cupertino.default
import com.walhalla.mtprotocompose.R

private const val TAG = "InAppUpdate"

/** Same threshold as the View app (:mtproto VPager.DAYS_FOR_FLEXIBLE_UPDATE). */
internal const val DAYS_FOR_FLEXIBLE_UPDATE = 4

/** Play "high priority" (0..5, set via the Play Developer API). */
internal const val PRIORITY_FOR_IMMEDIATE_UPDATE = 4

/** Which kind of update to start for the given Play data; null means "do not start anything". */
internal fun chooseUpdateType(
    updateAvailability: Int,
    updatePriority: Int,
    stalenessDays: Int?,
    immediateAllowed: Boolean,
    flexibleAllowed: Boolean,
): Int? {
    if (updateAvailability != UpdateAvailability.UPDATE_AVAILABLE) return null
    if (updatePriority >= PRIORITY_FOR_IMMEDIATE_UPDATE && immediateAllowed) {
        return AppUpdateType.IMMEDIATE
    }
    if (flexibleAllowed && (stalenessDays ?: -1) >= DAYS_FOR_FLEXIBLE_UPDATE) {
        return AppUpdateType.FLEXIBLE
    }
    return null
}

/**
 * Google Play In-App Update for [ComponentActivity].
 *
 * Create it in `onCreate` (after `super.onCreate`). It observes the activity lifecycle:
 *  - onStart/onStop: registers/unregisters the flexible-update install listener;
 *  - onResume: shows the "restart" prompt if an update is DOWNLOADED, resumes an IMMEDIATE
 *    update that is DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS, and (once, if [checkForUpdate])
 *    starts a new update flow.
 * Every Play failure (not installed from Play, no Play Store, ...) is only logged.
 */
class InAppUpdateController(
    private val activity: ComponentActivity,
    private var checkForUpdate: Boolean,
) : DefaultLifecycleObserver {

    /** True while the "update downloaded, restart?" prompt must be visible. */
    var restartPromptVisible by mutableStateOf(false)
        private set

    private val appUpdateManager: AppUpdateManager? = try {
        AppUpdateManagerFactory.create(activity.applicationContext)
    } catch (t: Throwable) {
        Log.w(TAG, "AppUpdateManager unavailable: $t")
        null
    }

    private val launcher: ActivityResultLauncher<IntentSenderRequest> =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            Log.d(TAG, "Update flow result code: ${result.resultCode}")
        }

    private var flexibleStarted = false

    private val installListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADING ->
                Log.d(TAG, "Downloading ${state.bytesDownloaded()}/${state.totalBytesToDownload()}")
            InstallStatus.DOWNLOADED -> restartPromptVisible = true
            InstallStatus.FAILED, InstallStatus.CANCELED -> {
                Log.w(TAG, "Flexible update ended: status=${state.installStatus()} error=${state.installErrorCode()}")
                flexibleStarted = false
            }
            else -> Unit
        }
    }

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        try {
            appUpdateManager?.registerListener(installListener)
        } catch (t: Throwable) {
            Log.w(TAG, "registerListener failed: $t")
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        try {
            appUpdateManager?.unregisterListener(installListener)
        } catch (t: Throwable) {
            Log.w(TAG, "unregisterListener failed: $t")
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        val manager = appUpdateManager ?: return
        try {
            manager.appUpdateInfo
                .addOnSuccessListener { info ->
                    try {
                        handleInfo(manager, info)
                    } catch (t: Throwable) {
                        Log.w(TAG, "handleInfo failed: $t")
                    }
                }
                .addOnFailureListener { e ->
                    // Sideloaded / debug builds / no Play Store: expected, never fatal.
                    Log.w(TAG, "appUpdateInfo failed: $e")
                }
        } catch (t: Throwable) {
            Log.w(TAG, "appUpdateInfo threw: $t")
        }
    }

    private fun handleInfo(manager: AppUpdateManager, info: AppUpdateInfo) {
        // 1) Flexible update already downloaded: ask the user to restart.
        if (info.installStatus() == InstallStatus.DOWNLOADED) {
            restartPromptVisible = true
            return
        }
        // 2) IMMEDIATE update still in progress (e.g. user left the app): resume it.
        if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
            if (!flexibleStarted) {
                startFlow(manager, info, AppUpdateType.IMMEDIATE)
            }
            return
        }
        // 3) New update: only once per activity creation (not after rotation).
        if (!checkForUpdate) return
        checkForUpdate = false
        val type = chooseUpdateType(
            updateAvailability = info.updateAvailability(),
            updatePriority = info.updatePriority(),
            stalenessDays = info.clientVersionStalenessDays(),
            immediateAllowed = info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE),
            flexibleAllowed = info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE),
        ) ?: return
        startFlow(manager, info, type)
    }

    private fun startFlow(manager: AppUpdateManager, info: AppUpdateInfo, type: Int) {
        try {
            val started = manager.startUpdateFlowForResult(
                info,
                launcher,
                AppUpdateOptions.newBuilder(type).build(),
            )
            if (started && type == AppUpdateType.FLEXIBLE) flexibleStarted = true
            Log.d(TAG, "startUpdateFlow type=$type started=$started")
        } catch (t: Throwable) {
            Log.w(TAG, "startUpdateFlowForResult failed: $t")
        }
    }

    /** User tapped "Restart": installs the downloaded update and restarts the app. */
    fun completeUpdate() {
        restartPromptVisible = false
        try {
            appUpdateManager?.completeUpdate()
        } catch (t: Throwable) {
            Log.w(TAG, "completeUpdate failed: $t")
        }
    }

    fun dismissRestartPrompt() {
        restartPromptVisible = false
    }
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun InAppUpdateRestartDialog(
    onRestart: () -> Unit,
    onDismiss: () -> Unit,
) {
    CupertinoAlertDialog(
        onDismissRequest = onDismiss,
        title = { CupertinoText(stringResource(R.string.app_update_downloaded)) },
        buttons = {
            default(onClick = onRestart) {
                CupertinoText(stringResource(R.string.action_restart))
            }
            cancel(onClick = onDismiss) {
                CupertinoText(stringResource(android.R.string.cancel))
            }
        },
    )
}