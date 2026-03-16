package com.cybergoudan.niuma.data

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object UsageCalculator {
  data class Result(
    val totalMillis: Long,
    val perPackageMillis: Map<String, Long>
  )

  /**
   * Approximate foreground time by pairing ACTIVITY_RESUMED/PAUSED events.
   * This is not perfect but good enough for MVP.
   */
  fun calcTodayForegroundMillis(context: Context, packages: Set<String>): Result {
    if (packages.isEmpty()) return Result(0, emptyMap())

    val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val zone = ZoneId.systemDefault()
    val start = LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli()
    val end = System.currentTimeMillis()

    val events = usm.queryEvents(start, end)
    val map = mutableMapOf<String, Long>()

    // Track last resume per package
    val lastResume = mutableMapOf<String, Long>()

    val event = UsageEvents.Event()
    while (events.hasNextEvent()) {
      events.getNextEvent(event)
      val pkg = event.packageName ?: continue
      if (!packages.contains(pkg)) continue

      when (event.eventType) {
        UsageEvents.Event.ACTIVITY_RESUMED,
        UsageEvents.Event.MOVE_TO_FOREGROUND -> {
          lastResume[pkg] = event.timeStamp
        }

        UsageEvents.Event.ACTIVITY_PAUSED,
        UsageEvents.Event.MOVE_TO_BACKGROUND -> {
          val t0 = lastResume.remove(pkg)
          if (t0 != null && event.timeStamp > t0) {
            val delta = (event.timeStamp - t0).coerceAtMost(6L * 60L * 60L * 1000L) // cap 6h session
            map[pkg] = (map[pkg] ?: 0L) + delta
          }
        }
      }
    }

    // any resumed without a closing event: close at now
    val now = System.currentTimeMillis()
    for ((pkg, t0) in lastResume) {
      if (now > t0) {
        map[pkg] = (map[pkg] ?: 0L) + (now - t0)
      }
    }

    val total = map.values.sum()
    return Result(totalMillis = total, perPackageMillis = map.toMap())
  }
}
