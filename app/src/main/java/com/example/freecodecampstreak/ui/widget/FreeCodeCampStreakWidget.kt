package com.example.freecodecampstreak.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.example.freecodecampstreak.R
import com.example.freecodecampstreak.repository.FreeCodeCampRepository
import kotlinx.coroutines.*
import androidx.core.net.toUri

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [FreeCodeCampStreakWidgetConfigureActivity]
 */
class FreeCodeCampStreakWidget : AppWidgetProvider() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {}

    override fun onDisabled(context: Context) {}
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun updateAppWidget(
    context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int
) {
    val userName = loadTitlePref(context, appWidgetId)
    val views = RemoteViews(context.packageName, R.layout.free_code_camp_streak_widget)
    CoroutineScope(Dispatchers.IO).launch {
        val repo = FreeCodeCampRepository()

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = "https://www.freecodecamp.org/learn".toUri()
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.appwidget_root, pendingIntent)

        val streakData = repo.getStreakData(userName)

        views.setTextViewText(R.id.text_status_message, streakData.status)
        views.setTextViewText(R.id.text_streaks_count, "${streakData.count} days")

        val dayViewIds = listOf(
            R.id.day1,
            R.id.day2,
            R.id.day3,
            R.id.day4,
            R.id.day5,
            R.id.day6,
            R.id.day7
        )
        streakData.last7Days.forEachIndexed { index, dayStatus ->
            val drawable = if (dayStatus.haveDone)
                R.drawable.shape_dot_filled
            else
                R.drawable.shape_dot_outline

            views.setImageViewResource(dayViewIds[index], drawable)
        }

        withContext(Dispatchers.Main) {
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}