package com.example.servicemate.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.Dao
import androidx.room.Query
import com.example.servicemate.data.model.MaintenanceRecord

@Dao
interface MaintenanceDao {
    @Insert
    suspend fun insert(record: MaintenanceRecord): Long

    @Query("SELECT * FROM maintenance_records WHERE vehicleId = :vehicleId ORDER BY serviceDateMillis DESC")
    fun getRecordsForVehicle(vehicleId: Long): LiveData<List<MaintenanceRecord>>
}
