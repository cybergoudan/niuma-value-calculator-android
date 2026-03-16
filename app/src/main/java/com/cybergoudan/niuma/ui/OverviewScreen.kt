package com.cybergoudan.niuma.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cybergoudan.niuma.data.AppSettings
import com.cybergoudan.niuma.data.UsageAccess
import com.cybergoudan.niuma.worker.UsagePollScheduler
import kotlin.math.roundToInt

@Composable
fun OverviewScreen(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val s by AppSettings.snapshotFlow(context).collectAsState(
    initial = AppSettings.Snapshot(
      hourlyRate = 150.0,
      dailyThresholdMin = 120,
      notifyEnabled = true,
      enableDouyin = true,
      enableKuaishou = false,
      enableBilibili = false,
      enableWeibo = false,
      enableXhs = false,
      todayMinutes = 0,
      todayCost = 0.0,
      todayText = "",
      lastNotifiedMin = 0,
    )
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    val hasAccess = UsageAccess.hasUsageAccess(context)

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
      Column(Modifier.padding(14.dp)) {
        Text("自动统计状态", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Text(
          if (hasAccess) "已授权 Usage Access（自动挡已开启）" else "未授权 Usage Access（无法自动统计）",
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))
        Button(onClick = { openUsageAccessSettings(context) }) {
          Text("去系统设置授权")
        }
      }
    }

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
      Column(Modifier.padding(14.dp)) {
        Text("今日娱乐时长", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        val h = s.todayMinutes / 60
        val m = s.todayMinutes % 60
        Text(
          if (h > 0) "${h}小时${m}分钟" else "${m}分钟",
          style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(
          "折算成本：≈ ${s.todayCost.roundToInt()} 元（按 ${s.hourlyRate.roundToInt()} 元/小时）",
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))
        Button(onClick = { UsagePollScheduler.runOnceNow(context) }) {
          Text("立即刷新")
        }
      }
    }

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
      Column(Modifier.padding(14.dp)) {
        Text("一键复制文案", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Text(
          if (s.todayText.isBlank()) "先点『立即刷新』生成文案" else s.todayText,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))
        Button(
          enabled = s.todayText.isNotBlank(),
          onClick = { copyToClipboard(context, s.todayText) }
        ) {
          Text("复制")
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
