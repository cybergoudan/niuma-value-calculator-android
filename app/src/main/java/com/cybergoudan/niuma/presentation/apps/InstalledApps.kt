package com.cybergoudan.niuma.presentation.apps

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

data class InstalledApp(
  val packageName: String,
  val label: String,
  val isSystemApp: Boolean,
)

object InstalledApps {
  fun load(context: Context): List<InstalledApp> {
    val pm = context.packageManager
    val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
    return apps
      .asSequence()
      .filter { it.packageName != context.packageName }
      .map { ai ->
        InstalledApp(
          packageName = ai.packageName,
          label = runCatching { pm.getApplicationLabel(ai).toString() }.getOrDefault(ai.packageName),
          isSystemApp = (ai.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
        )
      }
      .sortedWith(compareBy<InstalledApp> { it.isSystemApp }.thenBy { it.label.lowercase() })
      .toList()
  }
}
