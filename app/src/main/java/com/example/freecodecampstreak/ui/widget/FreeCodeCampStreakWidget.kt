package com.example.freecodecampstreak.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.example.freecodecampstreak.R
import com.example.freecodecampstreak.repository.FreeCodeCampRepository
import kotlinx.coroutines.*

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [FreeCodeCampStreakWidgetConfigureActivity]
 */
class FreeCodeCampStreakWidget : AppWidgetProvider() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
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
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val userName = loadTitlePref(context, appWidgetId)
    val views = RemoteViews(context.packageName, R.layout.free_code_camp_streak_widget)
    CoroutineScope(Dispatchers.IO).launch {
        val repo = FreeCodeCampRepository()
        val status = repo.getChallengeStatus(userName)

        views.setTextViewText(R.id.appwidget_text, status)

        withContext(Dispatchers.Main) {
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}