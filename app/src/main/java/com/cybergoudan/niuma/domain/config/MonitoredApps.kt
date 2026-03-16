package com.cybergoudan.niuma.domain.config

import com.cybergoudan.niuma.domain.model.MonitoredApp

/** Default monitored apps (can be adjusted later to select from installed apps). */
object MonitoredApps {
  val Douyin = MonitoredApp("douyin", "抖音", "com.ss.android.ugc.aweme")
  val Kuaishou = MonitoredApp("kuaishou", "快手", "com.smile.gifmaker")
  val Bilibili = MonitoredApp("bilibili", "B 站", "tv.danmaku.bili")
  val Weibo = MonitoredApp("weibo", "微博", "com.sina.weibo")
  val Xhs = MonitoredApp("xhs", "小红书", "com.xingin.xhs")

  val all: List<MonitoredApp> = listOf(Douyin, Kuaishou, Bilibili, Weibo, Xhs)
}
