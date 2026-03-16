package com.cybergoudan.niuma.domain.model

/**
 * A logical app key (used in settings) and its default Android package name.
 *
 * Note: package names can differ across channels/regions, so treat these as defaults.
 */
data class MonitoredApp(
  val key: String,
  val labelZh: String,
  val defaultPackageName: String,
)
