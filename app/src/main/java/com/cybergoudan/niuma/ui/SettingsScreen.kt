package com.cybergoudan.niuma.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cybergoudan.niuma.data.AppSettings
import com.cybergoudan.niuma.presentation.apps.InstalledApps
import com.cybergoudan.niuma.presentation.state.DefaultSnapshot
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  val s by AppSettings.snapshotFlow(context).collectAsState(initial = DefaultSnapshot)

  var query by remember { mutableStateOf("") }
  var hideSystem by remember { mutableStateOf(true) }

  val apps = remember { InstalledApps.load(context) }
  val filtered = apps
    .asSequence()
    .filter { if (hideSystem) !it.isSystemApp else true }
    .filter { if (query.isBlank()) true else it.label.contains(query, ignoreCase = true) || it.packageName.contains(query, ignoreCase = true) }
    .toList()

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
          value = if (s.hourlyRate == 0.0) "" else trimNumber(s.hourlyRate),
          onValueChange = { v ->
            val d = v.toDoubleOrNull()
            if (d != null) scope.launch { AppSettings.setHourlyRate(context, d) }
            if (v.isBlank()) scope.launch { AppSettings.setHourlyRate(context, 0.0) }
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
          supportingText = { Text("达到阈值后开始提醒（避免刷屏：约每+10分钟提醒一次）") }
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
        Text("选择要统计的 App（娱乐）", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
          value = query,
          onValueChange = { query = it },
          singleLine = true,
          placeholder = { Text("搜索应用名称/包名") },
        )

        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("隐藏系统应用", modifier = Modifier.weight(1f))
          Switch(checked = hideSystem, onCheckedChange = { hideSystem = it })
        }

        Spacer(Modifier.height(8.dp))
        Text(
          "可见应用：${filtered.size} 个 · 已选择：${s.monitoredPackages.size} 个",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(6.dp))

        LazyColumn {
          items(filtered, key = { it.packageName }) { app ->
            val checked = s.monitoredPackages.contains(app.packageName)
            Row(
              modifier = Modifier.padding(vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(app.label, style = MaterialTheme.typography.bodyLarge)
                Text(
                  app.packageName,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Switch(
                checked = checked,
                onCheckedChange = { on ->
                  scope.launch { AppSettings.toggleMonitoredPackage(context, app.packageName, on) }
                }
              )
            }
          }
        }
      }
    }
  }
}

private fun trimNumber(v: Double): String {
  val s = v.toString()
  return if (s.endsWith(".0")) s.dropLast(2) else s
}
