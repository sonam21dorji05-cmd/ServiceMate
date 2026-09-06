package com.example.servicemate.data.repository

import androidx.lifecycle.LiveData
import com.example.servicemate.data.dao.VehicleDao
import com.example.servicemate.data.model.Vehicle

class VehicleRepository(private val vehicleDao: VehicleDao) {

    fun getVehiclesForOwner(ownerId: Long): LiveData<List<Vehicle>> =
        vehicleDao.getVehiclesForOwner(ownerId)

    suspend fun addVehicle(vehicle: Vehicle): Long = vehicleDao.insert(vehicle)

    suspend fun getVehicleById(id: Long): Vehicle? = vehicleDao.getById(id)

    suspend fun updateVehicle(vehicle: Vehicle) = vehicleDao.update(vehicle)

    suspend fun deleteVehicle(vehicle: Vehicle) = vehicleDao.delete(vehicle)
}