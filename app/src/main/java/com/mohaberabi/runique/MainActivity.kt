package com.mohaberabi.runique

import android.content.Intent
import android.os.Bundle
import android.widget.Space
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.mohaberabi.core.presentation.designsystem.theme.RuniqueTheme
import com.mohaberabi.runique.core.navigation.RuniqueNavGraph
import com.mohaberabi.runique.splash.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModel<MainViewModel>()
    private lateinit var splitInstallManager: SplitInstallManager

    private val splitInstallListener = SplitInstallStateUpdatedListener { state ->
        when (state.status()) {
            SplitInstallSessionStatus.INSTALLED -> {
                viewModel.toggleAnalyticsDownloaderDialog(false)
                showToast("Congratulations Analytics is now installed ")

            }

            SplitInstallSessionStatus.DOWNLOADING -> viewModel.toggleAnalyticsDownloaderDialog(true)
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                splitInstallManager.startConfirmationDialogForResult(state, this, 0)
            }

            SplitInstallSessionStatus.INSTALLING -> viewModel.toggleAnalyticsDownloaderDialog(true)
            SplitInstallSessionStatus.FAILED -> {
                viewModel.toggleAnalyticsDownloaderDialog(false)
                showToast("Can not install module")
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.state.isChecking
            }
        }

        splitInstallManager = SplitInstallManagerFactory.create(applicationContext)
        setContent {
            RuniqueTheme {
                if (!viewModel.state.isChecking) {
                    val navController = rememberNavController()
                    RuniqueNavGraph(
                        onActivityClick = {
                            installOrStartAnalyticsFeature()
                        },
                        navController = navController,
                        isLoggedIn = viewModel.state.isLoggedIn
                    )
                    if (viewModel.state.showAnalyticsInstallerDialog) {
                        Dialog(
                            onDismissRequest = { },
                        ) {

                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(16.dp)
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Installing analytics module")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun installOrStartAnalyticsFeature() {

        if (splitInstallManager.installedModules.contains(ANALYTICS_FEATURE)) {
            Intent().setClassName(
                packageName,
                ANALYTICS_PACKAGE
            ).also {
                startActivity(it)
            }
            return
        }
        val request = SplitInstallRequest.newBuilder()
            .addModule(ANALYTICS_FEATURE)
            .build()
        splitInstallManager.startInstall(request).addOnFailureListener {

                ex ->
            ex.printStackTrace()
            showToast("Can not install module ")
        }
    }

    override fun onResume() {
        super.onResume()
        splitInstallManager.registerListener(splitInstallListener)
    }

    override fun onPause() {
        super.onPause()
        splitInstallManager.unregisterListener(splitInstallListener)
    }

    companion object {
        const val ANALYTICS_FEATURE = "ANALYTICS_FEATURE"
        const val ANALYTICS_PACKAGE = "com.mohaberabi.analytics_feature.AnalyticsActivity"
    }

    private fun showToast(message: String) = Toast.makeText(
        applicationContext,
        message,
        Toast.LENGTH_LONG
    ).show()
}

