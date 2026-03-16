package com.cybergoudan.niuma.ui

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NiuMaApp() {
  val context = LocalContext.current

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Text("牛马价值计算器", style = MaterialTheme.typography.headlineMedium)
    Text(
      "先授权 Usage Access，才能自动统计抖音等 App 的使用时长。",
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Card(
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
      Column(Modifier.padding(14.dp)) {
        Text("自动挡：Usage Access 权限", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Text(
          "状态：${if (hasUsageAccess(context)) "已授权" else "未授权"}",
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))
        Button(onClick = { openUsageAccessSettings(context) }) {
          Text("去系统设置授权")
        }
      }
    }

    Card(
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
      Column(Modifier.padding(14.dp)) {
        Text("时间价值（示例）", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Text("1 小时 = 150 元", style = MaterialTheme.typography.headlineSmall)
        Text(
          "后续：收入/工作时长/支出记录后自动计算。",
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Card(
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
      Column(Modifier.padding(14.dp)) {
        Text("提醒文案（示例，可复制）", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Text("你刚刚刷抖音 2 小时，相当于烧掉了 300 元。")
      }
    }
  }
}

private fun hasUsageAccess(context: Context): Boolean {
  val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
  val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    appOps.unsafeCheckOpNoThrow(
      AppOpsManager.OPSTR_GET_USAGE_STATS,
      android.os.Process.myUid(),
      context.packageName
    )
  } else {
    @Suppress("DEPRECATION")
    appOps.checkOpNoThrow(
      AppOpsManager.OPSTR_GET_USAGE_STATS,
      android.os.Process.myUid(),
      context.packageName
    )
  }
  return mode == AppOpsManager.MODE_ALLOWED
}

private fun openUsageAccessSettings(context: Context) {
  context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  })
}
