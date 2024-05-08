package com.mohaberabi.analytics_feature

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.splitcompat.SplitCompat
import com.mohaberabi.anaylitcs.presentation.di.analyticsPresentationModule
import com.mohaberabi.anayltics.data.di.analyticsModule
import com.mohaberabi.core.presentation.designsystem.theme.RuniqueTheme
import com.plcoding.analytics.presentation.AnalyticsDashboardScreenRoot
import org.koin.core.context.loadKoinModules

class AnalyticsActivity : ComponentActivity() {


    override fun onCreate(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onCreate(savedInstanceState, persistentState)
        loadKoinModules(
            listOf(
                analyticsModule,
                analyticsPresentationModule,
            )
        )

        SplitCompat.installActivity(this)

        setContent {
            RuniqueTheme {

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "analyticsDashboard"
                ) {
                    composable("analyticsDashboard") {
                        AnalyticsDashboardScreenRoot(
                            onBackClick = {
                                finish()
                            },
                        )
                    }
                }
            }
        }
    }
}