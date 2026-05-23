package com.warith.app.util

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.*
import com.warith.app.data.repository.LocalSourceRepository
import com.warith.app.ui.widget.WarithWidget
import java.util.concurrent.TimeUnit

class MidnightRotationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val historyManager = HistoryManager(applicationContext)
        if (!historyManager.isAutoRotationEnabled()) {
            return Result.success()
        }

        val repository = LocalSourceRepository(applicationContext, historyManager)
        val sources = repository.getAllSources()

        for (source in sources) {
            repository.getNextEntry(source.sourceId)
        }

        // Trigger widget update
        WarithWidget().updateAll(applicationContext)
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "MidnightRotationWork"

        fun schedule(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<MidnightRotationWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(calculateDelayToMidnight(), TimeUnit.MILLISECONDS)
                .addTag(WORK_NAME)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }

        private fun calculateDelayToMidnight(): Long {
            val now = System.currentTimeMillis()
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = now
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
            return calendar.timeInMillis - now
        }
    }
}
