package com.cybergoudan.niuma.presentation.apps

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build

/**
 * Loads installed applications list for user selection.
 *
 * Note: On Android 11+ this requires package visibility. We currently request
 * `QUERY_ALL_PACKAGES` for debugging/prototyping.
 */
data class InstalledApp(
  val packageName: String,
  val label: String,
  val isSystemApp: Boolean,
)

object InstalledApps {
  fun load(context: Context): List<InstalledApp> {
    val pm = context.packageManager

    val packageNames: List<String> = try {
      if (Build.VERSION.SDK_INT >= 33) {
        pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
          .map { it.packageName }
      } else {
        @Suppress("DEPRECATION")
        pm.getInstalledPackages(0).map { it.packageName }
      }
    } catch (_: Throwable) {
      // fallback
      pm.getInstalledApplications(PackageManager.GET_META_DATA).map { it.packageName }
    }

    val apps = packageNames
      .asSequence()
      .filter { it != context.packageName }
      .mapNotNull { pkg ->
        val ai = try {
          pm.getApplicationInfo(pkg, 0)
        } catch (_: Throwable) {
          null
        } ?: return@mapNotNull null

        val label = runCatching { pm.getApplicationLabel(ai).toString() }.getOrDefault(pkg)
        InstalledApp(
          packageName = pkg,
          label = label,
          isSystemApp = (ai.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
        )
      }
      .sortedWith(compareBy<InstalledApp> { it.isSystemApp }.thenBy { it.label.lowercase() })
      .toList()

    return apps
  }
}
