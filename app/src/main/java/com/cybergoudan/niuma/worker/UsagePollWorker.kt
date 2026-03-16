package com.cybergoudan.niuma.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cybergoudan.niuma.data.AppSettings
import com.cybergoudan.niuma.data.UsageAccess
import com.cybergoudan.niuma.data.UsageCalculator
import kotlinx.coroutines.flow.first
import kotlin.math.roundToInt

class UsagePollWorker(
  appContext: Context,
  params: WorkerParameters
) : CoroutineWorker(appContext, params) {

  companion object {
    /** Name for unique periodic WorkManager schedule. */
    const val UNIQUE_WORK_NAME = "usage_poll"
  }

  override suspend fun doWork(): Result {
    val ctx = applicationContext
    val s = AppSettings.snapshotFlow(ctx).first()

    if (!UsageAccess.hasUsageAccess(ctx)) {
      return Result.success()
    }

    // If user hasn't selected apps yet, do nothing (avoid surprising stats).
    val monitored = s.monitoredPackages
    if (monitored.isEmpty()) {
      AppSettings.writeComputedToday(ctx, minutes = 0, cost = 0.0, text = "")
      return Result.success()
    }

    val usage = UsageCalculator.calcTodayForegroundMillis(ctx, monitored)
    val minutes = (usage.totalMillis / 60000.0).roundToInt()
    val cost = (minutes / 60.0) * s.hourlyRate

    val text = buildCopyText(minutes = minutes, hourlyRate = s.hourlyRate, cost = cost)
    AppSettings.writeComputedToday(ctx, minutes = minutes, cost = cost, text = text)

    if (s.notifyEnabled && s.dailyThresholdMin > 0 && minutes >= s.dailyThresholdMin) {
      if (minutes - s.lastNotifiedMin >= 10) {
        Notifier.notify(
          ctx,
          title = "牛马提醒：今天已经刷了 ${minutes} 分钟",
          body = text
        )
        AppSettings.setLastNotifiedMin(ctx, minutes)
      }
    }

    return Result.success()
  }

  private fun buildCopyText(minutes: Int, hourlyRate: Double, cost: Double): String {
    val h = minutes / 60
    val m = minutes % 60
    val timeStr = if (h > 0) "${h}小时${m}分钟" else "${m}分钟"
    val costInt = cost.roundToInt()
    val rateInt = hourlyRate.roundToInt()
    return "牛马价值计算器APP\n你的时间价值：1小时≈${rateInt}元\n你今天娱乐 ${timeStr}，约烧掉 ${costInt} 元。"
  }
}
