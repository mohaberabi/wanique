package com.mohaberabi.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.mohaberabi.core.presentation.ui.util.formatted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.koin.android.ext.android.inject
import kotlin.time.Duration

class ActiveRunService : Service() {

    private val notificationManager by lazy {
        getSystemService<NotificationManager>()!!
    }

    private var servicesScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val elapsedTime by inject<StateFlow<Duration>>()

    private val baseNotification by lazy {
        NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        ).setSmallIcon(com.mohaberabi.core.presentation.designsystem.R.drawable.logo)
            .setContentTitle("Active Run")

    }


    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_START -> {

                val activityClass = intent.getStringExtra(EXTRA_ACTIVITY_CLASS)
                    ?: throw IllegalArgumentException("No class provided ")
                start(Class.forName(activityClass))
            }


            ACTION_STOP -> {
                stop()
            }

        }
        return START_STICKY
    }

    fun stop() {
        stopSelf()
        _isServiceActive.update { false }
        servicesScope.cancel()
        servicesScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    }

    private fun start(
        activityClass: Class<*>
    ) {
        if (!_isServiceActive.value) {
            _isServiceActive.update { true }
            createNotificationChannel()

            val activityIntent = Intent(
                applicationContext,
                activityClass,
            ).apply {
                data = ACTIVE_RUN_DEEP_LINK.toUri()
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

            val activityPendingIntent = TaskStackBuilder.create(applicationContext)
                .run {
                    addNextIntentWithParentStack(activityIntent)
                    getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
                }
            val notification = baseNotification
                .setContentText("00:00:00")
                .setContentIntent(activityPendingIntent)
                .build()
            startForeground(1, notification)
            updateNotification()
        }
    }

    private fun updateNotification() {
        elapsedTime.onEach { time ->
            val notification = baseNotification
                .setContentText(time.formatted())
                .build()
            notificationManager.notify(1, notification)
        }.launchIn(servicesScope)

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Active Run",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null


    companion object {
        private val _isServiceActive = MutableStateFlow(false)
        val isServiceActive = _isServiceActive.asStateFlow()

        private const val CHANNEL_ID = "ACTIVE_RUN_CHANNEL"
        const val ACTIVE_RUN_DEEP_LINK = "runique://active_run"

        private const val EXTRA_ACTIVITY_CLASS = "EXTRA_ACTIVITY_CLASS"

        private const val ACTION_START = "ACTION_START"
        private const val ACTION_STOP = "ACTION_STOP"

        fun createStartIntent(context: Context, activityClass: Class<*>): Intent {
            return Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_ACTIVITY_CLASS, activityClass.name)
            }
        }

        fun createStopIntent(context: Context): Intent {
            return Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_STOP
            }
        }
    }


}