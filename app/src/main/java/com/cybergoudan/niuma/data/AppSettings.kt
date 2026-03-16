package com.cybergoudan.niuma.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

/**
 * App settings and computed cache stored in Preferences DataStore.
 */
object AppSettings {
  private val KEY_HOURLY_RATE = doublePreferencesKey("hourly_rate")
  private val KEY_DAILY_THRESHOLD_MIN = intPreferencesKey("daily_threshold_min")
  private val KEY_NOTIFY_ENABLED = booleanPreferencesKey("notify_enabled")

  // Which installed apps should be counted as "entertainment".
  private val KEY_MONITORED_PACKAGES = stringSetPreferencesKey("monitored_packages")

  // UX: whether we've already auto-opened the Usage Access settings page at least once.
  private val KEY_USAGE_ACCESS_PROMPTED = booleanPreferencesKey("usage_access_prompted")

  // cache of last computed values
  private val KEY_TODAY_MIN = intPreferencesKey("today_minutes")
  private val KEY_TODAY_COST = doublePreferencesKey("today_cost")
  private val KEY_TODAY_TEXT = stringPreferencesKey("today_text")
  private val KEY_LAST_NOTIFIED_MIN = intPreferencesKey("last_notified_min")

  data class Snapshot(
    val hourlyRate: Double,
    val dailyThresholdMin: Int,
    val notifyEnabled: Boolean,
    val monitoredPackages: Set<String>,
    val usageAccessPrompted: Boolean,
    val todayMinutes: Int,
    val todayCost: Double,
    val todayText: String,
    val lastNotifiedMin: Int,
  )

  fun snapshotFlow(context: Context): Flow<Snapshot> = context.dataStore.data.map { p ->
    Snapshot(
      hourlyRate = p[KEY_HOURLY_RATE] ?: 150.0,
      dailyThresholdMin = p[KEY_DAILY_THRESHOLD_MIN] ?: 120,
      notifyEnabled = p[KEY_NOTIFY_ENABLED] ?: true,
      monitoredPackages = p[KEY_MONITORED_PACKAGES] ?: emptySet(),
      usageAccessPrompted = p[KEY_USAGE_ACCESS_PROMPTED] ?: false,
      todayMinutes = p[KEY_TODAY_MIN] ?: 0,
      todayCost = p[KEY_TODAY_COST] ?: 0.0,
      todayText = p[KEY_TODAY_TEXT] ?: "",
      lastNotifiedMin = p[KEY_LAST_NOTIFIED_MIN] ?: 0,
    )
  }

  suspend fun setHourlyRate(context: Context, value: Double) {
    context.dataStore.edit { it[KEY_HOURLY_RATE] = value.coerceAtLeast(0.0) }
  }

  suspend fun setDailyThresholdMin(context: Context, value: Int) {
    context.dataStore.edit { it[KEY_DAILY_THRESHOLD_MIN] = value.coerceIn(0, 24 * 60) }
  }

  suspend fun setNotifyEnabled(context: Context, value: Boolean) {
    context.dataStore.edit { it[KEY_NOTIFY_ENABLED] = value }
  }

  suspend fun setUsageAccessPrompted(context: Context, value: Boolean) {
    context.dataStore.edit { it[KEY_USAGE_ACCESS_PROMPTED] = value }
  }

  suspend fun setMonitoredPackages(context: Context, packages: Set<String>) {
    context.dataStore.edit { it[KEY_MONITORED_PACKAGES] = packages }
  }

  suspend fun toggleMonitoredPackage(context: Context, pkg: String, enabled: Boolean) {
    context.dataStore.edit { prefs ->
      val cur = prefs[KEY_MONITORED_PACKAGES] ?: emptySet()
      prefs[KEY_MONITORED_PACKAGES] = if (enabled) (cur + pkg) else (cur - pkg)
    }
  }

  suspend fun writeComputedToday(context: Context, minutes: Int, cost: Double, text: String) {
    context.dataStore.edit {
      it[KEY_TODAY_MIN] = minutes
      it[KEY_TODAY_COST] = cost
      it[KEY_TODAY_TEXT] = text
    }
  }

  suspend fun setLastNotifiedMin(context: Context, minutes: Int) {
    context.dataStore.edit { it[KEY_LAST_NOTIFIED_MIN] = minutes }
  }
}
