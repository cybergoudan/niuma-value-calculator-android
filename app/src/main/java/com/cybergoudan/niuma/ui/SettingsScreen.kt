package com.cybergoudan.niuma.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cybergoudan.niuma.data.AppSettings
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

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
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
      Column(Modifier.padding(14.dp)) {
        Text("时间价值（元/小时）", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
          value = if (s.hourlyRate == 0.0) "" else s.hourlyRate.toString().trimEnd('0').trimEnd('.'),
          onValueChange = { v ->
            val d = v.toDoubleOrNull()
            if (d != null) {
              scope.launch { AppSettings.setHourlyRate(context, d) }
            } else if (v.isBlank()) {
              scope.launch { AppSettings.setHourlyRate(context, 0.0) }
            }
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          supportingText = { Text("例如：150") }
        )
      }
    }

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
      Column(Modifier.padding(14.dp)) {
        Text("提醒阈值（分钟）", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
          value = s.dailyThresholdMin.toString(),
          onValueChange = { v ->
            val i = v.toIntOrNull()
            if (i != null) scope.launch { AppSettings.setDailyThresholdMin(context, i) }
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          supportingText = { Text("达到这个分钟数后开始提醒（每增长约10分钟提醒一次）") }
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("开启通知提醒", modifier = Modifier.weight(1f))
          Switch(
            checked = s.notifyEnabled,
            onCheckedChange = { on -> scope.launch { AppSettings.setNotifyEnabled(context, on) } }
          )
        }
      }
    }

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
      Column(Modifier.padding(14.dp)) {
        Text("统计哪些 App（娱乐）", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        AppToggle("抖音", s.enableDouyin) { on -> scope.launch { AppSettings.setAppEnabled(context, "douyin", on) } }
        AppToggle("快手", s.enableKuaishou) { on -> scope.launch { AppSettings.setAppEnabled(context, "kuaishou", on) } }
        AppToggle("B 站", s.enableBilibili) { on -> scope.launch { AppSettings.setAppEnabled(context, "bilibili", on) } }
        AppToggle("微博", s.enableWeibo) { on -> scope.launch { AppSettings.setAppEnabled(context, "weibo", on) } }
        AppToggle("小红书", s.enableXhs) { on -> scope.launch { AppSettings.setAppEnabled(context, "xhs", on) } }
        Spacer(Modifier.height(4.dp))
        Text(
          "提示：不同渠道包名可能不同，后续我会加『从已安装应用中选择』。",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          style = MaterialTheme.typography.bodySmall
        )
      }
    }
  }
}

@Composable
private fun AppToggle(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
  Row(
    modifier = Modifier.padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(label, modifier = Modifier.weight(1f))
    Switch(checked = checked, onCheckedChange = onChecked)
  }
}
