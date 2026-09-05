package com.example.servicemate.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = Vehicle::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val serviceType: String,
    val dueDateMillis: Long?,     // nullable: reminder can be date-based
    val dueOdometer: Int?,        // nullable: or odometer-based (or both)
    val isCompleted: Boolean = false,
    val notifiedAt: Long? = null  // tracks if a notification was already sent, to avoid duplicates
)
