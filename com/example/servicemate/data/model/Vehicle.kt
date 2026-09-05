package com.example.servicemate.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "vehicles",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Vehicle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerId: Long,
    val make: String,
    val model: String,
    val year: Int,
    val licensePlate: String,
    val currentOdometer: Int
)
