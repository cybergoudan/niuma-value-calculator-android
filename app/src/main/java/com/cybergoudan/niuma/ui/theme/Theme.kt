package com.cybergoudan.niuma.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
  primary = Color(0xFF2E7D32),
  onPrimary = Color(0xFFFFFFFF),
  primaryContainer = Color(0xFFB7F2B9),
  onPrimaryContainer = Color(0xFF08210B),

  secondary = Color(0xFF1E6C63),
  onSecondary = Color(0xFFFFFFFF),
  secondaryContainer = Color(0xFFB4F2E8),
  onSecondaryContainer = Color(0xFF00201C),

  tertiary = Color(0xFF4F5AA8),
  onTertiary = Color(0xFFFFFFFF),
  tertiaryContainer = Color(0xFFDDE1FF),
  onTertiaryContainer = Color(0xFF0A144B),

  error = Color(0xFFBA1A1A),
  onError = Color(0xFFFFFFFF),
  errorContainer = Color(0xFFFFDAD6),
  onErrorContainer = Color(0xFF410002),

  background = Color(0xFFF8FAF4),
  onBackground = Color(0xFF191C19),

  surface = Color(0xFFF8FAF4),
  onSurface = Color(0xFF191C19),
  surfaceVariant = Color(0xFFDEE5DA),
  onSurfaceVariant = Color(0xFF424940),

  outline = Color(0xFF727A70),
  outlineVariant = Color(0xFFC2C9BE),

  inverseSurface = Color(0xFF2E312E),
  inverseOnSurface = Color(0xFFEFF1EC),
  inversePrimary = Color(0xFF8AD98D),

  surfaceTint = Color(0xFF2E7D32),
  scrim = Color(0xFF000000)
)

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFF8AD98D),
  onPrimary = Color(0xFF0D3812),
  primaryContainer = Color(0xFF17521D),
  onPrimaryContainer = Color(0xFFB7F2B9),

  secondary = Color(0xFF84D6CC),
  onSecondary = Color(0xFF003732),
  secondaryContainer = Color(0xFF005049),
  onSecondaryContainer = Color(0xFFB4F2E8),

  tertiary = Color(0xFFB8C4FF),
  onTertiary = Color(0xFF1C276F),
  tertiaryContainer = Color(0xFF34408F),
  onTertiaryContainer = Color(0xFFDDE1FF),

  error = Color(0xFFFFB4AB),
  onError = Color(0xFF690005),
  errorContainer = Color(0xFF93000A),
  onErrorContainer = Color(0xFFFFDAD6),

  background = Color(0xFF101410),
  onBackground = Color(0xFFE0E4DC),

  surface = Color(0xFF101410),
  onSurface = Color(0xFFE0E4DC),
  surfaceVariant = Color(0xFF424940),
  onSurfaceVariant = Color(0xFFC2C9BE),

  outline = Color(0xFF8C9489),
  outlineVariant = Color(0xFF424940),

  inverseSurface = Color(0xFFE0E4DC),
  inverseOnSurface = Color(0xFF2E312E),
  inversePrimary = Color(0xFF2E7D32),

  surfaceTint = Color(0xFF8AD98D),
  scrim = Color(0xFF000000)
)

@Composable
fun NiuMaTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = NiuMaTypography,
    shapes = NiuMaShapes,
  ) {
    Surface(color = MaterialTheme.colorScheme.background) {
      content()
    }
  }
}
