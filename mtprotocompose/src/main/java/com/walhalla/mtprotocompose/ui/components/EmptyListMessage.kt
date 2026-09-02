package com.walhalla.mtprotocompose.ui.components

import android.content.ComponentName
import android.content.Intent
import android.app.Activity
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.walhalla.mtprotocompose.R
import com.walhalla.ui.plugins.Module_U
import java.util.Locale

@Composable
fun EmptyListMessage(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val uriHandler = LocalUriHandler.current
    val part1 = stringResource(R.string.part1).lowercase(Locale.getDefault())
    val part2 = stringResource(R.string.part2).lowercase(Locale.getDefault())
    val body = stringResource(R.string.msg_no_notes1)
    val lower = body.lowercase(Locale.getDefault())

    val linkColor = MaterialTheme.colorScheme.primary
    val annotated = remember(body, part1, part2, linkColor) {
        buildAnnotatedString {
            append(body)
            val start1 = lower.indexOf(part1)
            if (start1 >= 0) {
                addStringAnnotation("network", "network", start1, start1 + part1.length)
                addStyle(
                    SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
                    start1,
                    start1 + part1.length,
                )
            }
            val start2 = lower.indexOf(part2)
            if (start2 >= 0) {
                addStringAnnotation("data", "data", start2, start2 + part2.length)
                addStyle(
                    SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
                    start2,
                    start2 + part2.length,
                )
            }
        }
    }

    ClickableText(
        text = annotated,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        onClick = { offset ->
            annotated.getStringAnnotations("network", offset, offset).firstOrNull()?.let {
                activity?.let { act -> Module_U.actionWirelessSettings(act) }
                return@ClickableText
            }
            annotated.getStringAnnotations("data", offset, offset).firstOrNull()?.let {
                try {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        component = ComponentName(
                            "com.android.settings",
                            "com.android.settings.Settings\$DataUsageSummaryActivity",
                        )
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {
                    activity?.let { act -> Module_U.actionWirelessSettings(act) }
                }
            }
        },
    )
}

@Composable
fun PlayServicesHint(
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val linkText = stringResource(R.string.msg_no_notes2)
    ClickableText(
        text = AnnotatedString(linkText),
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
        ),
        onClick = {
            uriHandler.openUri("market://details?id=com.google.android.gms")
        },
    )
}
