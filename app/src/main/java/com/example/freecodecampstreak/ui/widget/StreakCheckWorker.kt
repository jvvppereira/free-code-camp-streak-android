package com.example.freecodecampstreak.ui.widget

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.freecodecampstreak.repository.FreeCodeCampRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StreakCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val appWidgetId = inputData.getInt("appWidgetId", -1)
            val checkTime = inputData.getString("checkTime") ?: "12h"

            if (appWidgetId == -1) {
                Result.failure()
            } else {
                val username = loadTitlePref(applicationContext, appWidgetId)
                val repo = FreeCodeCampRepository()
                val isDone = repo.isTodayActivityDone(username)

                if (!isDone) {
                    val helper = NotificationHelper(applicationContext)
                    when (checkTime) {
                        "12h" -> helper.showPendingNotification()
                        "18h" -> helper.showLastChanceNotification()
                    }
                }
                Result.success()
            }
        }
    }
}