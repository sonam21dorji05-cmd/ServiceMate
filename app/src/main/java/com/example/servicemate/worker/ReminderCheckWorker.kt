package com.example.servicemate.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.servicemate.data.AppDatabase
import com.example.servicemate.util.NotificationHelper

class ReminderCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getInstance(applicationContext)
            val reminderDao = db.reminderDao()
            val vehicleDao = db.vehicleDao()

            val activeReminders = reminderDao.getAllActiveReminders()
            val vehicles = vehicleDao.getAllVehicles().associateBy { it.id }

            for (reminder in activeReminders) {
                if (reminder.notifiedAt != null) continue // already notified once

                val vehicle = vehicles[reminder.vehicleId] ?: continue

                val odometerOverdue = reminder.dueOdometer?.let { vehicle.currentOdometer >= it } ?: false
                val dateOverdue = reminder.dueDateMillis?.let { System.currentTimeMillis() >= it } ?: false

                if (odometerOverdue || dateOverdue) {
                    val wasShown = NotificationHelper.showNotification(
                        applicationContext,
                        reminder.id.toInt(),
                        "Service Due: ${reminder.serviceType}",
                        "${vehicle.make} ${vehicle.model} needs ${reminder.serviceType}"
                    )
                    if (wasShown) {
                        reminderDao.update(reminder.copy(notifiedAt = System.currentTimeMillis()))
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "reminder_check_work"
    }
}