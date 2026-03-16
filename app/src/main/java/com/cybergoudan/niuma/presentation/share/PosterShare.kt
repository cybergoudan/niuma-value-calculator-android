package com.cybergoudan.niuma.presentation.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import androidx.core.content.FileProvider
import com.cybergoudan.niuma.data.AppSettings
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/**
 * Generates a simple but designed poster image and shares it.
 */
object PosterShare {

  fun createPosterBitmap(context: Context, s: AppSettings.Snapshot): Bitmap {
    val w = 1080
    val h = 1920
    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)

    // Background gradient
    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    bgPaint.shader = LinearGradient(
      0f, 0f, w.toFloat(), h.toFloat(),
      intArrayOf(0xFF0F172A.toInt(), 0xFF064E3B.toInt(), 0xFF0B1220.toInt()),
      floatArrayOf(0f, 0.55f, 1f),
      Shader.TileMode.CLAMP
    )
    canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

    // Glow circles
    val glow = Paint(Paint.ANTI_ALIAS_FLAG)
    glow.color = 0x33FFFFFF
    canvas.drawCircle(w * 0.18f, h * 0.22f, 220f, glow)
    glow.color = 0x22FFFFFF
    canvas.drawCircle(w * 0.82f, h * 0.40f, 320f, glow)

    val card = Paint(Paint.ANTI_ALIAS_FLAG)
    card.color = 0x14FFFFFF
    val stroke = Paint(Paint.ANTI_ALIAS_FLAG)
    stroke.style = Paint.Style.STROKE
    stroke.strokeWidth = 3f
    stroke.color = 0x22FFFFFF

    val r = RectF(72f, 260f, w - 72f, h - 260f)
    canvas.drawRoundRect(r, 48f, 48f, card)
    canvas.drawRoundRect(r, 48f, 48f, stroke)

    val title = Paint(Paint.ANTI_ALIAS_FLAG)
    title.color = 0xFFFFFFFF.toInt()
    title.textSize = 64f
    title.isFakeBoldText = true

    val sub = Paint(Paint.ANTI_ALIAS_FLAG)
    sub.color = 0xCCFFFFFF.toInt()
    sub.textSize = 36f

    val big = Paint(Paint.ANTI_ALIAS_FLAG)
    big.color = 0xFFFFFFFF.toInt()
    big.textSize = 120f
    big.isFakeBoldText = true

    val label = Paint(Paint.ANTI_ALIAS_FLAG)
    label.color = 0xB3FFFFFF.toInt()
    label.textSize = 34f

    val small = Paint(Paint.ANTI_ALIAS_FLAG)
    small.color = 0x99FFFFFF.toInt()
    small.textSize = 30f

    val topX = 120f
    var y = 380f

    canvas.drawText("牛马价值计算器APP", topX, y, title)
    y += 70f
    canvas.drawText("把时间换算成钱：看见你的隐形成本", topX, y, sub)

    y += 150f
    canvas.drawText("今日娱乐成本", topX, y, label)

    y += 130f
    val cost = s.todayCost.roundToInt()
    canvas.drawText("¥ $cost", topX, y, big)

    y += 90f
    val minutes = s.todayMinutes
    val h2 = minutes / 60
    val m2 = minutes % 60
    val timeStr = if (h2 > 0) "${h2}小时${m2}分钟" else "${m2}分钟"
    canvas.drawText("娱乐时长：$timeStr", topX, y, label)

    y += 64f
    canvas.drawText("时间价值：1小时≈${s.hourlyRate.roundToInt()}元", topX, y, label)

    y += 120f
    val body = s.todayText.ifBlank { "授权后刷新即可生成文案" }
    drawMultiline(canvas, body, topX, y, w - 240f, small, lineGap = 16f)

    val ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    canvas.drawText("生成时间：$ts", topX, h - 320f, small)

    return bmp
  }

  fun sharePoster(context: Context, s: AppSettings.Snapshot) {
    val bmp = createPosterBitmap(context, s)

    val dir = File(context.cacheDir, "share").apply { mkdirs() }
    val file = File(dir, "niuma_poster_${System.currentTimeMillis()}.png")
    FileOutputStream(file).use { out ->
      bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "image/png"
      putExtra(Intent.EXTRA_STREAM, uri)
      putExtra(Intent.EXTRA_TEXT, "牛马价值计算器APP")
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "分享海报"))
  }

  private fun drawMultiline(
    canvas: Canvas,
    text: String,
    x: Float,
    y: Float,
    maxWidth: Float,
    paint: Paint,
    lineGap: Float,
  ) {
    var yy = y
    val words = text.replace("\n", " \n ").split(Regex("\\s+"))
    var line = ""
    for (w in words) {
      if (w == "\\n") {
        canvas.drawText(line.trim(), x, yy, paint)
        yy += paint.textSize + lineGap
        line = ""
        continue
      }
      val test = if (line.isEmpty()) w else "$line $w"
      if (paint.measureText(test) <= maxWidth) {
        line = test
      } else {
        canvas.drawText(line.trim(), x, yy, paint)
        yy += paint.textSize + lineGap
        line = w
      }
    }
    if (line.isNotBlank()) canvas.drawText(line.trim(), x, yy, paint)
  }
}
