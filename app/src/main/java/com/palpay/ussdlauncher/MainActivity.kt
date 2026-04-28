package com.palpay.ussdlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.palpay.ussdlauncher.ui.navigation.AppNavigation
import com.palpay.ussdlauncher.ui.theme.UssdLauncherTheme
import com.palpay.ussdlauncher.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val initialServiceKey: String? = if (
            intent?.action == "com.palpay.ussdlauncher.ACTION_OPEN_PROVIDER"
        ) {
            intent.getStringExtra("serviceKey")
        } else {
            null
        }

        setContent {
            val viewModel: MainViewModel = viewModel()
            val themePreference by viewModel.themePreference.collectAsState()
            UssdLauncherTheme(themePreference = themePreference) {
                val snackbarHostState = remember { SnackbarHostState() }
                val snackbarMessage by viewModel.snackbarMessage.collectAsState()

                LaunchedEffect(snackbarMessage) {
                    snackbarMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearSnackbar()
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        viewModel = viewModel,
                        initialServiceKey = initialServiceKey
                    )
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        Snackbar(snackbarData = data)
                    }
                }
            }
        }
    }
}
