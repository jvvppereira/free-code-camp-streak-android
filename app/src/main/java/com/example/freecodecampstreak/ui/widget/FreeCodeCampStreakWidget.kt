package com.example.freecodecampstreak.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.freecodecampstreak.R
import com.example.freecodecampstreak.repository.FreeCodeCampRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
class FreeCodeCampStreakWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
            scheduleWorkers(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            cancelWorkers(context, appWidgetId)
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
    }

    private fun scheduleWorkers(context: Context, appWidgetId: Int) {
        val workManager = WorkManager.getInstance(context)
        val username = loadTitlePref(context, appWidgetId)

        val workData12h = androidx.work.Data.Builder()
            .putInt("appWidgetId", appWidgetId)
            .putString("checkTime", "12h")
            .build()

        val workData18h = androidx.work.Data.Builder()
            .putInt("appWidgetId", appWidgetId)
            .putString("checkTime", "18h")
            .build()

        val workRequest12h = PeriodicWorkRequestBuilder<StreakCheckWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(calculateDelayToHour(12), TimeUnit.MILLISECONDS)
            .setInputData(workData12h)
            .addTag("streak_12h_$appWidgetId")
            .build()

        val workRequest18h = PeriodicWorkRequestBuilder<StreakCheckWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(calculateDelayToHour(18), TimeUnit.MILLISECONDS)
            .setInputData(workData18h)
            .addTag("streak_18h_$appWidgetId")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "streak_12h_$appWidgetId",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest12h
        )

        workManager.enqueueUniquePeriodicWork(
            "streak_18h_$appWidgetId",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest18h
        )
    }

    private fun cancelWorkers(context: Context, appWidgetId: Int) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork("streak_12h_$appWidgetId")
        workManager.cancelUniqueWork("streak_18h_$appWidgetId")
    }

    private fun calculateDelayToHour(targetHour: Int): Long {
        val now = java.util.Calendar.getInstance()
        val target = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, targetHour)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }

        if (target.timeInMillis <= now.timeInMillis) {
            target.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }

        return target.timeInMillis - now.timeInMillis
    }
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun updateAppWidget(
    context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int
) {
    val userName = loadTitlePref(context, appWidgetId)
    val views = RemoteViews(context.packageName, R.layout.free_code_camp_streak_widget)

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = "https://www.freecodecamp.org/learn".toUri()
    }
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    views.setOnClickPendingIntent(R.id.appwidget_root, pendingIntent)

    CoroutineScope(Dispatchers.IO).launch {
        val repo = FreeCodeCampRepository()
        val bitmap = repo.getStreakSvg(userName)

        if (bitmap != null) {
            withContext(Dispatchers.Main) {
                views.setImageViewBitmap(R.id.streak_svg_image, bitmap)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}