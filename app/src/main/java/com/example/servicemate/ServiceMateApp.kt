package com.example.servicemate

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.servicemate.util.NotificationHelper
import com.example.servicemate.worker.ReminderCheckWorker
import java.util.concurrent.TimeUnit

class ServiceMateApp : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        scheduleReminderCheck()
        runReminderCheckNow()
    }

    private fun scheduleReminderCheck() {
        val request = PeriodicWorkRequestBuilder<ReminderCheckWorker>(24, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            ReminderCheckWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    // Also runs one check immediately on app start, so reminders don't wait
    // up to 24 hours to first appear (useful for testing/demoing too).
    private fun runReminderCheckNow() {
        val request = OneTimeWorkRequestBuilder<ReminderCheckWorker>().build()
        WorkManager.getInstance(this).enqueue(request)
    }
}