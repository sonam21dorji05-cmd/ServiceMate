package com.example.servicemate.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.servicemate.data.model.Reminder

@Dao
interface ReminderDao {
    @Insert
    suspend fun insert(reminder: Reminder): Long

    @Update
    suspend fun update(reminder: Reminder): Int

    @Query("""
        SELECT reminders.* FROM reminders
        INNER JOIN vehicles ON reminders.vehicleId = vehicles.id
        WHERE vehicles.ownerId = :ownerId AND reminders.isCompleted = 0
        ORDER BY reminders.dueDateMillis ASC
    """)
    fun getActiveRemindersForOwner(ownerId: Long): LiveData<List<Reminder>>

    @Query("""
        SELECT reminders.* FROM reminders
        INNER JOIN vehicles ON reminders.vehicleId = vehicles.id
        WHERE reminders.isCompleted = 0
    """)
    suspend fun getAllActiveReminders(): List<Reminder>

    @Query("SELECT * FROM reminders WHERE vehicleId = :vehicleId AND isCompleted = 0")
    fun getActiveRemindersForVehicle(vehicleId: Long): LiveData<List<Reminder>>

    @Query("""
        SELECT * FROM reminders WHERE vehicleId = :vehicleId
    """)
    suspend fun getAllRemindersForVehicle(vehicleId: Long): List<Reminder>

    @Query("""
        UPDATE reminders SET isCompleted = 1
        WHERE vehicleId = :vehicleId AND serviceType = :serviceType AND isCompleted = 0
    """)
    suspend fun completeExistingReminders(vehicleId: Long, serviceType: String)
}