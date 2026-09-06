package com.example.servicemate.data.repository

import androidx.lifecycle.LiveData
import com.example.servicemate.data.dao.MaintenanceDao
import com.example.servicemate.data.model.MaintenanceRecord

class MaintenanceRepository(private val maintenanceDao: MaintenanceDao) {

    fun getRecordsForVehicle(vehicleId: Long): LiveData<List<MaintenanceRecord>> =
        maintenanceDao.getRecordsForVehicle(vehicleId)

    suspend fun addRecord(record: MaintenanceRecord): Long = maintenanceDao.insert(record)
}