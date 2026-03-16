package com.cybergoudan.niuma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.cybergoudan.niuma.ui.NiuMaApp
import com.cybergoudan.niuma.ui.theme.NiuMaTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
    setContent {
      NiuMaTheme {
        NiuMaApp()
      }
    }
  }
}
