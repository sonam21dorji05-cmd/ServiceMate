package com.example.servicemate.data.repository

import androidx.lifecycle.LiveData
import com.example.servicemate.data.dao.ReminderDao
import com.example.servicemate.data.model.Reminder
import com.example.servicemate.util.ServiceIntervals

class ReminderRepository(private val reminderDao: ReminderDao) {

    fun getActiveRemindersForOwner(ownerId: Long): LiveData<List<Reminder>> =
        reminderDao.getActiveRemindersForOwner(ownerId)

    /**
     * Called whenever a maintenance record is logged. Marks any previous
     * open reminder for this vehicle+serviceType as done, then schedules
     * the next one based on the service interval table.
     */
    suspend fun upsertReminderForService(
        vehicleId: Long,
        serviceType: String,
        fromOdometer: Int,
        fromDateMillis: Long
    ) {
        reminderDao.completeExistingReminders(vehicleId, serviceType)

        val interval = ServiceIntervals.get(serviceType)
        val dueDateMillis = fromDateMillis + interval.days.toLong() * 24 * 60 * 60 * 1000
        val dueOdometer = fromOdometer + interval.miles

        reminderDao.insert(
            Reminder(
                vehicleId = vehicleId,
                serviceType = serviceType,
                dueDateMillis = dueDateMillis,
                dueOdometer = dueOdometer
            )
        )
    }
}