package com.cybergoudan.niuma

import android.app.Application
import com.cybergoudan.niuma.worker.UsagePollScheduler

/**
 * App entry: schedule periodic usage polling once per process start.
 */
class NiuMaApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    UsagePollScheduler.ensureScheduled(this)
  }
}
