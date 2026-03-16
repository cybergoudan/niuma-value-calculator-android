package com.cybergoudan.niuma.presentation.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cybergoudan.niuma.data.AppSettings
import com.cybergoudan.niuma.data.UsageAccess
import com.cybergoudan.niuma.presentation.share.PosterShare
import com.cybergoudan.niuma.presentation.state.DefaultSnapshot
import com.cybergoudan.niuma.worker.UsagePollScheduler
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun OverviewScreen(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  val s by AppSettings.snapshotFlow(context).collectAsState(initial = DefaultSnapshot)

  val hasAccess = UsageAccess.hasUsageAccess(context)

  // Auto-open permission settings once (no button on home).
  LaunchedEffect(hasAccess, s.usageAccessPrompted) {
    if (!hasAccess && !s.usageAccessPrompted) {
      AppSettings.setUsageAccessPrompted(context, true)
      openUsageAccessSettings(context)
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .padding(18.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Text(
        "今日娱乐成本",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      val cost = s.todayCost.roundToInt()
      Text(
        "¥ $cost",
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.SemiBold
      )

      val h = s.todayMinutes / 60
      val m = s.todayMinutes % 60
      Text(
        if (h > 0) "娱乐时长：${h}小时${m}分钟" else "娱乐时长：${m}分钟",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Text(
        "时间价值：1小时≈${s.hourlyRate.roundToInt()}元",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(Modifier.height(6.dp))

      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            when {
              !hasAccess -> "需要在系统『使用情况访问』中授权（已自动打开一次）"
              s.monitoredPackages.isEmpty() -> "先去设置里选择要统计的 App"
              else -> "已开启自动统计，可点击刷新"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Button(onClick = { UsagePollScheduler.runOnceNow(context) }) {
            Text("立即刷新")
          }

          Button(
            enabled = s.todayText.isNotBlank(),
            onClick = { copyToClipboard(context, s.todayText) }
          ) {
            Text("复制文案")
          }

          Button(
            onClick = { PosterShare.sharePoster(context, s) }
          ) {
            Text("分享海报")
          }
        }
      }
    }
  }
}

private fun openUsageAccessSettings(context: Context) {
  context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  })
}

private fun copyToClipboard(context: Context, text: String) {
  val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  cm.setPrimaryClip(ClipData.newPlainText("牛马价值", text))
}
