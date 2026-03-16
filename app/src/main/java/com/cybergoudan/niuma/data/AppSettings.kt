package com.cybergoudan.niuma.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object AppSettings {
  private val KEY_HOURLY_RATE = doublePreferencesKey("hourly_rate")
  private val KEY_DAILY_THRESHOLD_MIN = intPreferencesKey("daily_threshold_min")
  private val KEY_NOTIFY_ENABLED = booleanPreferencesKey("notify_enabled")

  // individual toggles for common apps
  private val KEY_APP_DOUYIN = booleanPreferencesKey("app_douyin")
  private val KEY_APP_KUAISHOU = booleanPreferencesKey("app_kuaishou")
  private val KEY_APP_BILIBILI = booleanPreferencesKey("app_bilibili")
  private val KEY_APP_WEIBO = booleanPreferencesKey("app_weibo")
  private val KEY_APP_XHS = booleanPreferencesKey("app_xhs")

  // cache of last computed values
  private val KEY_TODAY_MIN = intPreferencesKey("today_minutes")
  private val KEY_TODAY_COST = doublePreferencesKey("today_cost")
  private val KEY_TODAY_TEXT = stringPreferencesKey("today_text")
  private val KEY_LAST_NOTIFIED_MIN = intPreferencesKey("last_notified_min")

  data class Snapshot(
    val hourlyRate: Double,
    val dailyThresholdMin: Int,
    val notifyEnabled: Boolean,
    val enableDouyin: Boolean,
    val enableKuaishou: Boolean,
    val enableBilibili: Boolean,
    val enableWeibo: Boolean,
    val enableXhs: Boolean,
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
      enableDouyin = p[KEY_APP_DOUYIN] ?: true,
      enableKuaishou = p[KEY_APP_KUAISHOU] ?: false,
      enableBilibili = p[KEY_APP_BILIBILI] ?: false,
      enableWeibo = p[KEY_APP_WEIBO] ?: false,
      enableXhs = p[KEY_APP_XHS] ?: false,
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

  suspend fun setAppEnabled(context: Context, key: String, value: Boolean) {
    val prefKey = when (key) {
      "douyin" -> KEY_APP_DOUYIN
      "kuaishou" -> KEY_APP_KUAISHOU
      "bilibili" -> KEY_APP_BILIBILI
      "weibo" -> KEY_APP_WEIBO
      "xhs" -> KEY_APP_XHS
      else -> throw IllegalArgumentException("unknown app key: $key")
    }
    context.dataStore.edit { it[prefKey] = value }
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
