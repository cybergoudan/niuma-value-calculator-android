package com.cybergoudan.niuma.data

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build

/**
 * Usage Access ("usage stats") permission detection.
 *
 * Notes:
 * - `PACKAGE_USAGE_STATS` is a special app access: it is NOT grantable via runtime permissions.
 * - Some OEM ROMs may return MODE_DEFAULT even when access is enabled, so we do a best-effort
 *   probe using UsageStatsManager.
 */
object UsageAccess {
  fun hasUsageAccess(context: Context): Boolean {
    // 1) Check AppOps first
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      appOps.unsafeCheckOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(),
        context.packageName
      )
    } else {
      @Suppress("DEPRECATION")
      appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(),
        context.packageName
      )
    }
    if (mode == AppOpsManager.MODE_ALLOWED) return true

    // 2) Best-effort probe: if we can query non-empty stats, we have access.
    return try {
      val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
      val end = System.currentTimeMillis()
      val start = end - 60L * 60L * 1000L // last 1h
      val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
      !stats.isNullOrEmpty()
    } catch (_: Throwable) {
      false
    }
  }
}
