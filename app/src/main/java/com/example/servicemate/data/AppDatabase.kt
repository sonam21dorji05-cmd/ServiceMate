package com.example.servicemate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.servicemate.data.dao.MaintenanceDao
import com.example.servicemate.data.dao.ReminderDao
import com.example.servicemate.data.dao.UserDao
import com.example.servicemate.data.dao.VehicleDao
import com.example.servicemate.data.model.MaintenanceRecord
import com.example.servicemate.data.model.Reminder
import com.example.servicemate.data.model.User
import com.example.servicemate.data.model.Vehicle

@Database(
    entities = [User::class, Vehicle::class, MaintenanceRecord::class, Reminder::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "servicemate_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
