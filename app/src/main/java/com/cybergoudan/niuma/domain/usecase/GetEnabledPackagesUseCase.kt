package com.cybergoudan.niuma.domain.usecase

import com.cybergoudan.niuma.data.AppSettings
import com.cybergoudan.niuma.domain.config.MonitoredApps

/** Returns the package names to include in usage calculation for the given settings snapshot. */
object GetEnabledPackagesUseCase {
  fun invoke(s: AppSettings.Snapshot): Set<String> = buildSet {
    if (s.enableDouyin) add(MonitoredApps.Douyin.defaultPackageName)
    if (s.enableKuaishou) add(MonitoredApps.Kuaishou.defaultPackageName)
    if (s.enableBilibili) add(MonitoredApps.Bilibili.defaultPackageName)
    if (s.enableWeibo) add(MonitoredApps.Weibo.defaultPackageName)
    if (s.enableXhs) add(MonitoredApps.Xhs.defaultPackageName)
  }
}
