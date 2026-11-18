package com.hms.player

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Updates are delivered by Google Play. This object only reports the installed version
 * and opens the app's official Google Play page (the old GitHub APK download/install flow was removed).
 */
object Updater {
    fun currentVersion(ctx: Context): String = try {
        ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName ?: "1.0.0"
    } catch (e: Exception) {
        "1.0.0"
    }

    /** Opens this app's Google Play page (Play Store app first, web page as fallback). */
    fun openStorePage(ctx: Context) {
        val id = ctx.packageName
        fun go(uri: String) = ctx.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(uri)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        try {
            go("market://details?id=$id")
        } catch (e: ActivityNotFoundException) {
            try { go("https://play.google.com/store/apps/details?id=$id") } catch (e2: Exception) { /* no browser/store */ }
        }
    }
}
