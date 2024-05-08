package com.mohaberabi.wear.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mohaberabi.core.notification.ActiveRunService
import com.mohaberabi.core.presentation.designsystem_wear.RuniqueWearTheme
import com.mohaberabi.wear.run.presentation.tracker.screen.TrackerScreenRoot


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)


        setContent {


            RuniqueWearTheme {


                TrackerScreenRoot(onServiceToggle = { shoudlStartRunning ->

                    if (shoudlStartRunning) {
                        startService(
                            ActiveRunService.createStartIntent(
                                applicationContext,
                                this::class.java
                            )
                        )
                    } else {
                        startService(ActiveRunService.createStopIntent(applicationContext))

                    }
                })

            }


        }
    }
}


