package com.cybergoudan.niuma.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object UsagePollScheduler {
  fun ensureScheduled(context: Context) {
    val req = PeriodicWorkRequestBuilder<UsagePollWorker>(15, TimeUnit.MINUTES)
      .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
      UsagePollWorker.UNIQUE_WORK_NAME,
      ExistingPeriodicWorkPolicy.UPDATE,
      req
    )
  }

  fun runOnceNow(context: Context) {
    val req = OneTimeWorkRequestBuilder<UsagePollWorker>().build()
    WorkManager.getInstance(context).enqueue(req)
  }
}
