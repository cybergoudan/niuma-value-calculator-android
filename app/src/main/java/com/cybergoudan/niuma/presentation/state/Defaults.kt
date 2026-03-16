package com.cybergoudan.niuma.presentation.state

import com.cybergoudan.niuma.data.AppSettings

/** Default state used before DataStore emits the first value. */
val DefaultSnapshot = AppSettings.Snapshot(
  hourlyRate = 150.0,
  dailyThresholdMin = 120,
  notifyEnabled = true,
  monitoredPackages = emptySet(),
  usageAccessPrompted = false,
  todayMinutes = 0,
  todayCost = 0.0,
  todayText = "",
  lastNotifiedMin = 0,
)
